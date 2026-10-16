package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;

import ni.edu.uam.evaluacion_javafx_citastecnicas.conexion.ConexionBD;
import ni.edu.uam.evaluacion_javafx_citastecnicas.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt("id"), rs.getString("nombres"),
                rs.getString("apellidos"), rs.getString("telefono"),
                rs.getString("correo"), rs.getBoolean("activo"));
    }

    public List<Cliente> listar() throws SQLException {
        return consultar("SELECT id,nombres,apellidos,telefono,correo,activo FROM cliente ORDER BY id", null);
    }

    public List<Cliente> buscar(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) return listar();
        String sql = "SELECT id,nombres,apellidos,telefono,correo,activo FROM cliente "
                + "WHERE nombres ILIKE ? OR apellidos ILIKE ? OR correo ILIKE ? ORDER BY id";
        return consultar(sql, "%" + texto.trim() + "%");
    }

    private List<Cliente> consultar(String sql, String patron) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (patron != null) {
                ps.setString(1, patron);
                ps.setString(2, patron);
                ps.setString(3, patron);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) clientes.add(mapear(rs));
            }
        }
        return clientes;
    }

    public int insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nombres,apellidos,telefono,correo,activo) VALUES (?,?,?,?,?)";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, cliente);
            return ps.executeUpdate();
        }
    }

    public int actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nombres=?,apellidos=?,telefono=?,correo=?,activo=? WHERE id=?";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, cliente);
            ps.setInt(6, cliente.getId());
            return ps.executeUpdate();
        }
    }

    private void asignar(PreparedStatement ps, Cliente cliente) throws SQLException {
        ps.setString(1, cliente.getNombres());
        ps.setString(2, cliente.getApellidos());
        ps.setString(3, cliente.getTelefono());
        ps.setString(4, cliente.getCorreo());
        ps.setBoolean(5, cliente.isActivo());
    }

    public int eliminar(int id) throws SQLException {
        // FK ON DELETE RESTRICT impide eliminar un cliente con citas.
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM cliente WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
