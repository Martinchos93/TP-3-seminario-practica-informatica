package ar.edu.siglo21.tribu.consola.persistencia;

import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {

    private static final String URL = leer("DB_URL",
            "jdbc:mysql://localhost:3308/tribu_consola?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Argentina/Cordoba");
    private static final String USUARIO = leer("DB_USER", "tribu");
    private static final String CLAVE = leer("DB_PASSWORD", "tribu");

    private ConexionBD() {
    }

    public static Connection abrir() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a MySQL en " + URL, e);
        }
    }

    private static String leer(String variable, String porDefecto) {
        String valor = System.getenv(variable);
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }
}
