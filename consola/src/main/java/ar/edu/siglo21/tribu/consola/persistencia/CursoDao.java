package ar.edu.siglo21.tribu.consola.persistencia;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;
import ar.edu.siglo21.tribu.consola.excepciones.ReglaNegocioException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;
import ar.edu.siglo21.tribu.consola.modelo.Academia;
import ar.edu.siglo21.tribu.consola.modelo.Curso;
import ar.edu.siglo21.tribu.consola.modelo.EstadoCurso;
import ar.edu.siglo21.tribu.consola.modelo.Leccion;
import ar.edu.siglo21.tribu.consola.modelo.LeccionCuestionario;
import ar.edu.siglo21.tribu.consola.modelo.LeccionTexto;
import ar.edu.siglo21.tribu.consola.modelo.LeccionVideo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class CursoDao implements Repositorio<Curso> {

    private final ListaEnlazada<Academia> academias;

    public CursoDao(ListaEnlazada<Academia> academias) {
        this.academias = academias;
    }

    @Override
    public ListaEnlazada<Curso> listarTodos() {
        ListaEnlazada<Curso> cursos = new ListaEnlazada<>();
        String sqlCursos = "SELECT id, codigo, titulo, precio, estado, academia_id FROM curso ORDER BY id";
        String sqlLecciones = "SELECT id, orden, titulo, tipo, duracion_segundos, cantidad_palabras, cantidad_preguntas "
                + "FROM leccion WHERE curso_id = ? ORDER BY orden";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement consultaCursos = conexion.prepareStatement(sqlCursos);
             PreparedStatement consultaLecciones = conexion.prepareStatement(sqlLecciones);
             ResultSet filas = consultaCursos.executeQuery()) {
            while (filas.next()) {
                Curso curso = new Curso(filas.getInt("id"), filas.getString("codigo"), filas.getString("titulo"),
                        filas.getBigDecimal("precio"), EstadoCurso.valueOf(filas.getString("estado")),
                        buscarAcademia(filas.getInt("academia_id")));
                consultaLecciones.setInt(1, curso.getId());
                try (ResultSet lecciones = consultaLecciones.executeQuery()) {
                    while (lecciones.next()) {
                        curso.agregarLeccion(crearLeccion(lecciones));
                    }
                }
                cursos.agregar(curso);
            }
        } catch (SQLException | ValidacionException | ReglaNegocioException e) {
            throw new PersistenciaException("Error al leer los cursos", e);
        }
        return cursos;
    }

    private Leccion crearLeccion(ResultSet fila) throws SQLException, ValidacionException {
        int id = fila.getInt("id");
        int orden = fila.getInt("orden");
        String titulo = fila.getString("titulo");
        return switch (fila.getString("tipo")) {
            case "VIDEO" -> new LeccionVideo(id, titulo, orden, fila.getInt("duracion_segundos"));
            case "TEXTO" -> new LeccionTexto(id, titulo, orden, fila.getInt("cantidad_palabras"));
            case "CUESTIONARIO" -> new LeccionCuestionario(id, titulo, orden, fila.getInt("cantidad_preguntas"));
            default -> throw new ValidacionException("Tipo de lección desconocido: " + fila.getString("tipo"));
        };
    }

    private Academia buscarAcademia(int id) throws ValidacionException {
        for (Academia academia : academias) {
            if (academia.getId() == id) {
                return academia;
            }
        }
        throw new ValidacionException("No existe la academia " + id);
    }

    @Override
    public void guardar(Curso curso) {
        String sqlCurso = "INSERT INTO curso (codigo, titulo, precio, estado, academia_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement sentencia = conexion.prepareStatement(sqlCurso, Statement.RETURN_GENERATED_KEYS)) {
                sentencia.setString(1, curso.getCodigo());
                sentencia.setString(2, curso.getTitulo());
                sentencia.setBigDecimal(3, curso.getPrecio());
                sentencia.setString(4, curso.getEstado().name());
                sentencia.setInt(5, curso.getAcademia().getId());
                sentencia.executeUpdate();
                try (ResultSet clave = sentencia.getGeneratedKeys()) {
                    if (clave.next()) {
                        curso.setId(clave.getInt(1));
                    }
                }
                for (Leccion leccion : curso.getLecciones()) {
                    insertarLeccion(conexion, curso.getId(), leccion);
                }
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el curso", e);
        }
    }

    public void agregarLeccion(Curso curso, Leccion leccion) {
        try (Connection conexion = ConexionBD.abrir()) {
            insertarLeccion(conexion, curso.getId(), leccion);
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la lección", e);
        }
    }

    public void actualizarEstado(Curso curso) {
        String sql = "UPDATE curso SET estado = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, curso.getEstado().name());
            sentencia.setInt(2, curso.getId());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el curso", e);
        }
    }

    private void insertarLeccion(Connection conexion, int cursoId, Leccion leccion) throws SQLException {
        String sql = "INSERT INTO leccion (curso_id, orden, titulo, tipo, duracion_segundos, cantidad_palabras, cantidad_preguntas) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, cursoId);
            sentencia.setInt(2, leccion.getOrden());
            sentencia.setString(3, leccion.getTitulo());
            sentencia.setString(4, leccion.getTipo());
            sentencia.setNull(5, Types.INTEGER);
            sentencia.setNull(6, Types.INTEGER);
            sentencia.setNull(7, Types.INTEGER);
            if (leccion instanceof LeccionVideo video) {
                sentencia.setInt(5, video.getDuracionSegundos());
            } else if (leccion instanceof LeccionTexto texto) {
                sentencia.setInt(6, texto.getCantidadPalabras());
            } else if (leccion instanceof LeccionCuestionario cuestionario) {
                sentencia.setInt(7, cuestionario.getCantidadPreguntas());
            }
            sentencia.executeUpdate();
            try (ResultSet clave = sentencia.getGeneratedKeys()) {
                if (clave.next()) {
                    leccion.setId(clave.getInt(1));
                }
            }
        }
    }
}
