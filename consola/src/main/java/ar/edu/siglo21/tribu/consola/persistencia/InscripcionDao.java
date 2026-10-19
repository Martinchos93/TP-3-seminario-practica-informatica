package ar.edu.siglo21.tribu.consola.persistencia;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;
import ar.edu.siglo21.tribu.consola.modelo.Alumno;
import ar.edu.siglo21.tribu.consola.modelo.Curso;
import ar.edu.siglo21.tribu.consola.modelo.EstadoInscripcion;
import ar.edu.siglo21.tribu.consola.modelo.Inscripcion;
import ar.edu.siglo21.tribu.consola.modelo.Leccion;
import ar.edu.siglo21.tribu.consola.modelo.Pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class InscripcionDao implements Repositorio<Inscripcion> {

    private final ListaEnlazada<Alumno> alumnos;
    private final ListaEnlazada<Curso> cursos;

    public InscripcionDao(ListaEnlazada<Alumno> alumnos, ListaEnlazada<Curso> cursos) {
        this.alumnos = alumnos;
        this.cursos = cursos;
    }

    @Override
    public ListaEnlazada<Inscripcion> listarTodos() {
        ListaEnlazada<Inscripcion> inscripciones = new ListaEnlazada<>();
        String sqlInscripciones = "SELECT id, alumno_id, curso_id, estado, fecha FROM inscripcion ORDER BY id";
        String sqlProgreso = "SELECT l.orden FROM progreso_leccion p JOIN leccion l ON l.id = p.leccion_id "
                + "WHERE p.inscripcion_id = ? ORDER BY p.fecha, p.id";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement consulta = conexion.prepareStatement(sqlInscripciones);
             PreparedStatement consultaProgreso = conexion.prepareStatement(sqlProgreso);
             ResultSet filas = consulta.executeQuery()) {
            while (filas.next()) {
                Curso curso = buscarCurso(filas.getInt("curso_id"));
                Inscripcion inscripcion = new Inscripcion(filas.getInt("id"), buscarAlumno(filas.getInt("alumno_id")),
                        curso, EstadoInscripcion.valueOf(filas.getString("estado")),
                        filas.getTimestamp("fecha").toLocalDateTime());
                consultaProgreso.setInt(1, inscripcion.getId());
                try (ResultSet progreso = consultaProgreso.executeQuery()) {
                    while (progreso.next()) {
                        inscripcion.restaurarLeccionCompletada(curso.buscarLeccionPorOrden(progreso.getInt("orden")));
                    }
                }
                inscripciones.agregar(inscripcion);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al leer las inscripciones", e);
        }
        return inscripciones;
    }

    @Override
    public void guardar(Inscripcion inscripcion) {
        String sql = "INSERT INTO inscripcion (alumno_id, curso_id, estado, fecha) VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, inscripcion.getAlumno().getId());
            sentencia.setInt(2, inscripcion.getCurso().getId());
            sentencia.setString(3, inscripcion.getEstado().name());
            sentencia.setTimestamp(4, Timestamp.valueOf(inscripcion.getFecha()));
            sentencia.executeUpdate();
            try (ResultSet clave = sentencia.getGeneratedKeys()) {
                if (clave.next()) {
                    inscripcion.setId(clave.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la inscripción", e);
        }
    }

    public void registrarPago(Pago pago) {
        String sqlPago = "INSERT INTO pago (inscripcion_id, medio, detalle, importe, estado, motivo_rechazo, fecha) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement sentencia = conexion.prepareStatement(sqlPago, Statement.RETURN_GENERATED_KEYS)) {
                sentencia.setInt(1, pago.getInscripcion().getId());
                sentencia.setString(2, pago.getMedio());
                sentencia.setString(3, pago.getDetalle());
                sentencia.setBigDecimal(4, pago.getImporte());
                sentencia.setString(5, pago.getEstado().name());
                sentencia.setString(6, pago.getMotivoRechazo());
                sentencia.setTimestamp(7, Timestamp.valueOf(pago.getFecha()));
                sentencia.executeUpdate();
                try (ResultSet clave = sentencia.getGeneratedKeys()) {
                    if (clave.next()) {
                        pago.setId(clave.getInt(1));
                    }
                }
                actualizarEstado(conexion, pago.getInscripcion());
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el pago", e);
        }
    }

    public void registrarAvance(Inscripcion inscripcion, Leccion leccion) {
        String sql = "INSERT INTO progreso_leccion (inscripcion_id, leccion_id, fecha) VALUES (?, ?, ?)";
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setInt(1, inscripcion.getId());
                sentencia.setInt(2, leccion.getId());
                sentencia.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                sentencia.executeUpdate();
                actualizarEstado(conexion, inscripcion);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el avance", e);
        }
    }

    public void eliminarAvance(Inscripcion inscripcion, Leccion leccion) {
        String sql = "DELETE FROM progreso_leccion WHERE inscripcion_id = ? AND leccion_id = ?";
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setInt(1, inscripcion.getId());
                sentencia.setInt(2, leccion.getId());
                sentencia.executeUpdate();
                actualizarEstado(conexion, inscripcion);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al deshacer el avance", e);
        }
    }

    private void actualizarEstado(Connection conexion, Inscripcion inscripcion) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement("UPDATE inscripcion SET estado = ? WHERE id = ?")) {
            sentencia.setString(1, inscripcion.getEstado().name());
            sentencia.setInt(2, inscripcion.getId());
            sentencia.executeUpdate();
        }
    }

    private Alumno buscarAlumno(int id) {
        for (Alumno alumno : alumnos) {
            if (alumno.getId() == id) {
                return alumno;
            }
        }
        throw new PersistenciaException("No existe el alumno " + id, null);
    }

    private Curso buscarCurso(int id) {
        for (Curso curso : cursos) {
            if (curso.getId() == id) {
                return curso;
            }
        }
        throw new PersistenciaException("No existe el curso " + id, null);
    }
}
