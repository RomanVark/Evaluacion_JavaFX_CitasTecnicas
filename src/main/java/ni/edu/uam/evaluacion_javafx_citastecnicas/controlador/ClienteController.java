package ni.edu.uam.evaluacion_javafx_citastecnicas.controlador;

import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.modelo.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.Alertas;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.ErroresSQL;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ClienteController {
    private static final Pattern CORREO =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}$");
    private final ClienteDAO dao = new ClienteDAO();

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtBuscar;
    @FXML private CheckBox chkActivo;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, Boolean> colActivo;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, antes, actual) -> seleccionar(actual));
        chkActivo.setSelected(true);
        recargarTabla();
    }

    private void seleccionar(Cliente cliente) {
        if (cliente == null) return;
        txtNombres.setText(cliente.getNombres());
        txtApellidos.setText(cliente.getApellidos());
        txtTelefono.setText(cliente.getTelefono());
        txtCorreo.setText(cliente.getCorreo());
        chkActivo.setSelected(cliente.isActivo());
    }

    private Cliente leerFormulario(int id) {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        if (nombres.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || correo.isEmpty())
            throw new IllegalArgumentException("Todos los campos del cliente son obligatorios.");
        if (nombres.length() > 80 || apellidos.length() > 80)
            throw new IllegalArgumentException("Nombres y apellidos admiten máximo 80 caracteres.");
        if (telefono.length() > 20 || correo.length() > 120)
            throw new IllegalArgumentException("El teléfono admite 20 caracteres y el correo 120.");
        if (!CORREO.matcher(correo).matches())
            throw new IllegalArgumentException("Escribe un correo válido, por ejemplo usuario@correo.com.");
        return new Cliente(id, nombres, apellidos, telefono, correo, chkActivo.isSelected());
    }

    @FXML
    private void guardar() {
        try {
            Cliente cliente = leerFormulario(0);
            if (dao.insertar(cliente) == 1) {
                recargarTabla();
                limpiarFormulario();
                Alertas.exito("Cliente registrado correctamente.");
            } else Alertas.error("No se insertó el cliente.");
        } catch (IllegalArgumentException error) {
            Alertas.error(error.getMessage());
        } catch (SQLException error) {
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void actualizar() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { Alertas.error("Selecciona un cliente de la tabla."); return; }
        try {
            Cliente cliente = leerFormulario(seleccionado.getId());
            if (dao.actualizar(cliente) == 1) {
                recargarTabla();
                limpiarFormulario();
                Alertas.exito("Cliente actualizado correctamente.");
            } else Alertas.error("El cliente ya no existe en la base de datos.");
        } catch (IllegalArgumentException error) {
            Alertas.error(error.getMessage());
        } catch (SQLException error) {
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void eliminar() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { Alertas.error("Selecciona un cliente de la tabla."); return; }
        if (!Alertas.confirmar("¿Eliminar al cliente " + seleccionado.getNombreCompleto() + "?")) return;
        try {
            if (dao.eliminar(seleccionado.getId()) == 1) {
                recargarTabla();
                limpiarFormulario();
                Alertas.exito("Cliente eliminado de forma permanente.");
            } else Alertas.error("El cliente ya no existe.");
        } catch (SQLException error) {
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void buscar() { recargarTabla(); }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        recargarTabla();
    }

    public void recargarTabla() {
        try {
            tablaClientes.setItems(FXCollections.observableArrayList(dao.buscar(txtBuscar.getText())));
        } catch (SQLException error) {
            tablaClientes.getItems().clear();
            Alertas.error("No se pudo consultar clientes. " + ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void limpiarFormulario() {
        tablaClientes.getSelectionModel().clearSelection();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        chkActivo.setSelected(true);
    }
}
