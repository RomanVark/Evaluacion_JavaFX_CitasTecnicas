package ni.edu.uam.evaluacion_javafx_citastecnicas.controlador;

import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.CitaTecnicaDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.modelo.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.modelo.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.Alertas;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.ErroresSQL;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

public class CitaTecnicaController {
    private static final List<String> ESTADOS =
            List.of("Pendiente", "En proceso", "Finalizada", "Cancelada");
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private final CitaTecnicaDAO dao = new CitaTecnicaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtServicio;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private DatePicker filtroFecha;
    @FXML private TableView<CitaTecnica> tablaCitas;
    @FXML private TableColumn<CitaTecnica, Integer> colId;
    @FXML private TableColumn<CitaTecnica, String> colCliente;
    @FXML private TableColumn<CitaTecnica, LocalDate> colFecha;
    @FXML private TableColumn<CitaTecnica, LocalTime> colHora;
    @FXML private TableColumn<CitaTecnica, String> colServicio;
    @FXML private TableColumn<CitaTecnica, String> colEstado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        cmbEstado.setItems(FXCollections.observableArrayList(ESTADOS));
        cmbEstado.setValue("Pendiente");
        filtroEstado.setItems(FXCollections.observableArrayList("Todos", "Pendiente",
                "En proceso", "Finalizada", "Cancelada"));
        filtroEstado.setValue("Todos");
        tablaCitas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> seleccionar(actual));
        recargarClientes();
        recargarTabla();
    }

    public void recargarClientes() {
        Cliente anterior = cmbCliente.getValue();
        Integer anteriorId = anterior == null ? null : anterior.getId();
        try {
            List<Cliente> clientes = clienteDAO.listar();
            cmbCliente.setItems(FXCollections.observableArrayList(clientes));
            seleccionarClienteId(anteriorId);
        } catch (SQLException error) {
            cmbCliente.getItems().clear();
            cmbCliente.setValue(null);
            Alertas.error("No se pudo consultar clientes. " + ErroresSQL.mensaje(error));
        }
    }

    private void seleccionarClienteId(Integer id) {
        if (id == null) { cmbCliente.setValue(null); return; }
        cmbCliente.getItems().stream().filter(c -> c.getId() == id).findFirst()
                .ifPresentOrElse(cmbCliente::setValue, () -> cmbCliente.setValue(null));
    }

    private void seleccionar(CitaTecnica cita) {
        if (cita == null) return;
        seleccionarClienteId(cita.getClienteId());
        dpFecha.setValue(cita.getFecha());
        txtHora.setText(cita.getHora().format(FORMATO_HORA));
        txtServicio.setText(cita.getTipoServicio());
        txtDescripcion.setText(cita.getDescripcion());
        cmbEstado.setValue(cita.getEstado());
    }

    private CitaTecnica leerFormulario(int id) {
        Cliente cliente = cmbCliente.getValue();
        if (cliente == null || cmbCliente.getItems().stream().noneMatch(c -> c.getId() == cliente.getId()))
            throw new IllegalArgumentException("Selecciona un cliente existente en la lista.");
        LocalDate fecha = dpFecha.getValue();
        if (fecha == null) throw new IllegalArgumentException("Selecciona la fecha de la cita.");
        LocalTime hora;
        try { hora = LocalTime.parse(txtHora.getText().trim(), FORMATO_HORA); }
        catch (DateTimeParseException error) {
            throw new IllegalArgumentException("Ingresa una hora válida en formato HH:mm, por ejemplo 09:30.");
        }
        String servicio = txtServicio.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        if (servicio.isEmpty() || descripcion.isEmpty())
            throw new IllegalArgumentException("Servicio y descripción son obligatorios.");
        if (servicio.length() > 80 || descripcion.length() > 255)
            throw new IllegalArgumentException("El servicio permite 80 caracteres y la descripción 255.");
        String estado = cmbEstado.getValue();
        if (estado == null || !ESTADOS.contains(estado))
            throw new IllegalArgumentException("El estado debe ser Pendiente, En proceso, Finalizada o Cancelada.");
        return new CitaTecnica(id, cliente.getId(), cliente.getNombreCompleto(),
                fecha, hora, servicio, descripcion, estado);
    }

    @FXML
    private void guardar() {
        try {
            if (dao.insertar(leerFormulario(0)) == 1) {
                recargarTabla();
                limpiarFormulario();
                Alertas.exito("Cita técnica registrada correctamente.");
            } else Alertas.error("No se insertó la cita.");
        } catch (IllegalArgumentException error) {
            Alertas.error(error.getMessage());
        } catch (SQLException error) {
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void actualizar() {
        CitaTecnica seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) { Alertas.error("Selecciona una cita de la tabla."); return; }
        try {
            if (dao.actualizar(leerFormulario(seleccionada.getId())) == 1) {
                recargarTabla();
                limpiarFormulario();
                Alertas.exito("Cita actualizada correctamente.");
            } else Alertas.error("La cita ya no existe.");
        } catch (IllegalArgumentException error) {
            Alertas.error(error.getMessage());
        } catch (SQLException error) {
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void eliminar() {
        CitaTecnica seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) { Alertas.error("Selecciona una cita de la tabla."); return; }
        if (!Alertas.confirmar("¿Eliminar la cita #" + seleccionada.getId() + "?")) return;
        try {
            if (dao.eliminar(seleccionada.getId()) == 1) {
                recargarTabla();
                limpiarFormulario();
                Alertas.exito("Cita eliminada permanentemente.");
            } else Alertas.error("La cita ya no existe.");
        } catch (SQLException error) {
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void aplicarFiltros() { recargarTabla(); }

    @FXML
    private void limpiarFiltros() {
        filtroEstado.setValue("Todos");
        filtroFecha.setValue(null);
        recargarTabla();
    }

    public void recargarTabla() {
        String estado = filtroEstado.getValue();
        if ("Todos".equals(estado)) estado = null;
        try {
            tablaCitas.setItems(FXCollections.observableArrayList(
                    dao.filtrar(estado, filtroFecha.getValue())));
        } catch (SQLException error) {
            tablaCitas.getItems().clear();
            Alertas.error("No se pudo consultar citas. " + ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void limpiarFormulario() {
        tablaCitas.getSelectionModel().clearSelection();
        cmbCliente.setValue(null);
        dpFecha.setValue(null);
        txtHora.clear();
        txtServicio.clear();
        txtDescripcion.clear();
        cmbEstado.setValue("Pendiente");
    }
}
