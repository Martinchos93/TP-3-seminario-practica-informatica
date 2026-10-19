package ar.edu.siglo21.tribu.consola.persistencia;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;
import ar.edu.siglo21.tribu.consola.modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AlumnoDao implements Repositorio<Alumno> {

    @Override
    public ListaEnlazada<Alumno> listarTodos() {
        ListaEnlazada<Alumno> alumnos = new ListaEnlazada<>();
        String sql = "SELECT id, nombre, apellido, email FROM alumno ORDER BY id";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                alumnos.agregar(new Alumno(filas.getInt("id"), filas.getString("nombre"),
                        filas.getString("apellido"), filas.getString("email")));
            }
        } catch (SQLException | ValidacionException e) {
            throw new PersistenciaException("Error al leer los alumnos", e);
        }
        return alumnos;
    }

    @Override
    public void guardar(Alumno alumno) {
        String sql = "INSERT INTO alumno (nombre, apellido, email) VALUES (?, ?, ?)";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, alumno.getNombre());
            sentencia.setString(2, alumno.getApellido());
            sentencia.setString(3, alumno.getEmail());
            sentencia.executeUpdate();
            try (ResultSet clave = sentencia.getGeneratedKeys()) {
                if (clave.next()) {
                    alumno.setId(clave.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el alumno", e);
        }
    }
}
