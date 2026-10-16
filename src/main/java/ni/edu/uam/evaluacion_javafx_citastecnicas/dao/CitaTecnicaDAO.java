package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;

import ni.edu.uam.evaluacion_javafx_citastecnicas.conexion.ConexionBD;
import ni.edu.uam.evaluacion_javafx_citastecnicas.modelo.CitaTecnica;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CitaTecnicaDAO {
    private static final String CONSULTA =
            "SELECT ct.id,ct.cliente_id,cl.nombres || ' ' || cl.apellidos AS nombre_cliente," +
            "ct.fecha,ct.hora,ct.tipo_servicio,ct.descripcion,ct.estado " +
            "FROM cita_tecnica ct JOIN cliente cl ON cl.id=ct.cliente_id";

    public List<CitaTecnica> listar() throws SQLException { return filtrar(null, null); }

    public List<CitaTecnica> filtrar(String estado, LocalDate fecha) throws SQLException {
        // Solo se concatenan fragmentos SQL constantes, nunca datos del usuario.
        StringBuilder sql = new StringBuilder(CONSULTA).append(" WHERE 1=1");
        if (estado != null && !estado.isBlank()) sql.append(" AND ct.estado=?");
        if (fecha != null) sql.append(" AND ct.fecha=?");
        sql.append(" ORDER BY ct.fecha DESC, ct.hora DESC, ct.id DESC");
        List<CitaTecnica> citas = new ArrayList<>();
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            int indice = 1;
            if (estado != null && !estado.isBlank()) ps.setString(indice++, estado);
            if (fecha != null) ps.setDate(indice, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(new CitaTecnica(rs.getInt("id"), rs.getInt("cliente_id"),
                            rs.getString("nombre_cliente"), rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora").toLocalTime(), rs.getString("tipo_servicio"),
                            rs.getString("descripcion"), rs.getString("estado")));
                }
            }
        }
        return citas;
    }

    public int insertar(CitaTecnica cita) throws SQLException {
        String sql = "INSERT INTO cita_tecnica (cliente_id,fecha,hora,tipo_servicio,descripcion,estado) VALUES (?,?,?,?,?,?)";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, cita);
            return ps.executeUpdate();
        }
    }

    public int actualizar(CitaTecnica cita) throws SQLException {
        String sql = "UPDATE cita_tecnica SET cliente_id=?,fecha=?,hora=?,tipo_servicio=?,descripcion=?,estado=? WHERE id=?";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, cita);
            ps.setInt(7, cita.getId());
            return ps.executeUpdate();
        }
    }

    private void asignar(PreparedStatement ps, CitaTecnica cita) throws SQLException {
        ps.setInt(1, cita.getClienteId());
        ps.setDate(2, Date.valueOf(cita.getFecha()));
        ps.setTime(3, Time.valueOf(cita.getHora()));
        ps.setString(4, cita.getTipoServicio());
        ps.setString(5, cita.getDescripcion());
        ps.setString(6, cita.getEstado());
    }

    public int eliminar(int id) throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM cita_tecnica WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
