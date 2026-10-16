package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.sql.SQLException;

public final class ErroresSQL {
    private ErroresSQL() {}

    public static String mensaje(SQLException error) {
        String estado = error.getSQLState();
        if ("23505".equals(estado)) return "Ya existe un cliente con ese correo electrónico.";
        if ("23001".equals(estado) || "23503".equals(estado)) return "El registro tiene datos relacionados: no puede eliminarse un cliente con citas, ni asociar una cita a un cliente inexistente.";
        if ("23514".equals(estado)) return "El estado de la cita no es uno de los permitidos.";
        if ("23502".equals(estado)) return "Faltan datos obligatorios.";
        if ("22001".equals(estado)) return "Uno de los textos supera el tamaño permitido.";
        if (estado != null && estado.startsWith("08")) return "No fue posible conectar con PostgreSQL. Comprueba que el servidor esté iniciado, la base exista y las credenciales locales sean correctas.";
        return "No se pudo completar la operación en la base de datos. SQLState: " + (estado == null ? "desconocido" : estado) + ".";
    }
}