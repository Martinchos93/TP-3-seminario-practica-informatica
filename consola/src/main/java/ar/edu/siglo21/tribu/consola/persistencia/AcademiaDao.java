package ar.edu.siglo21.tribu.consola.persistencia;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;
import ar.edu.siglo21.tribu.consola.modelo.Academia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AcademiaDao implements Repositorio<Academia> {

    @Override
    public ListaEnlazada<Academia> listarTodos() {
        ListaEnlazada<Academia> academias = new ListaEnlazada<>();
        String sql = "SELECT id, razon_social, cuit FROM academia ORDER BY id";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                academias.agregar(new Academia(filas.getInt("id"), filas.getString("razon_social"), filas.getString("cuit")));
            }
        } catch (SQLException | ValidacionException e) {
            throw new PersistenciaException("Error al leer las academias", e);
        }
        return academias;
    }

    @Override
    public void guardar(Academia academia) {
        String sql = "INSERT INTO academia (razon_social, cuit) VALUES (?, ?)";
        try (Connection conexion = ConexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, academia.getRazonSocial());
            sentencia.setString(2, academia.getCuit());
            sentencia.executeUpdate();
            try (ResultSet clave = sentencia.getGeneratedKeys()) {
                if (clave.next()) {
                    academia.setId(clave.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la academia", e);
        }
    }
}
