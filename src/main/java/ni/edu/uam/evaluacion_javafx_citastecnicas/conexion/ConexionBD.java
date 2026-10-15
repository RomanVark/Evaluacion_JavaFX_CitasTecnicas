package ni.edu.uam.evaluacion_javafx_citastecnicas.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConexionBD {
    private ConexionBD() {}

    private static String entorno(String clave, String defecto) {
        String valor = System.getenv(clave);
        return valor == null || valor.isBlank() ? defecto : valor;
    }

    public static Connection getConnection() throws SQLException {
        String clave = System.getenv("DB_PASSWORD");
        if (clave == null || clave.isBlank()) {
            throw new SQLException("Configura DB_PASSWORD en las variables de entorno de IntelliJ.", "08001");
        }
        return DriverManager.getConnection(
                entorno("DB_URL", "jdbc:postgresql://localhost:5432/gestion_citas"),
                entorno("DB_USER", "postgres"),
                clave);
    }

    public static void comprobarConexion() throws SQLException {
        try (Connection conexion = getConnection();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT 1")) {
            if (!resultado.next() || resultado.getInt(1) != 1) {
                throw new SQLException("PostgreSQL no respondió correctamente a SELECT 1.");
            }
        }
    }
}