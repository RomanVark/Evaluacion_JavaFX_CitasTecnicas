package ni.edu.uam.evaluacion_javafx_citastecnicas;

import ni.edu.uam.evaluacion_javafx_citastecnicas.conexion.ConexionBD;
import ni.edu.uam.evaluacion_javafx_citastecnicas.controlador.ClienteController;
import ni.edu.uam.evaluacion_javafx_citastecnicas.controlador.CitaTecnicaController;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.Alertas;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.ErroresSQL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    @FXML private StackPane contenedor;
    @FXML private Label lblEstado;
    private Node vistaClientes;
    private Node vistaCitas;
    private ClienteController clienteController;
    private CitaTecnicaController citaController;
    private boolean conectado = false;

    @FXML
    private void initialize() {
        Platform.runLater(this::reintentarConexion);
    }

    @FXML
    private void reintentarConexion() {
        try {
            ConexionBD.comprobarConexion();
            conectado = true;
            lblEstado.setText("● Conectado a PostgreSQL | gestion_citas");
            lblEstado.setStyle("-fx-text-fill: #087a57;");
            // Se recargan solo las vistas que ya existen para no abrir pantallas duplicadas.
            if (clienteController != null) clienteController.recargarTabla();
            if (citaController != null) {
                citaController.recargarClientes();
                citaController.recargarTabla();
            }
            if (contenedor.getChildren().isEmpty() || vistaClientes == null) mostrarClientes();
        } catch (SQLException error) {
            conectado = false;
            lblEstado.setText("● Sin conexión con PostgreSQL");
            lblEstado.setStyle("-fx-text-fill: #b42318;");
            // Se ocultan los datos antiguos: no se presenta información como si fuera actual.
            contenedor.getChildren().setAll(new Label(
                    "No hay conexión. Inicia PostgreSQL y pulsa Reintentar conexión."));
            vistaClientes = null;
            vistaCitas = null;
            clienteController = null;
            citaController = null;
            Alertas.error(ErroresSQL.mensaje(error));
        }
    }

    @FXML
    private void mostrarClientes() {
        if (!conectado) { Alertas.error("Primero restablece la conexión con PostgreSQL."); return; }
        try {
            if (vistaClientes == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("clientes-view.fxml"));
                vistaClientes = loader.load();
                clienteController = loader.getController();
            } else clienteController.recargarTabla();
            contenedor.getChildren().setAll(vistaClientes);
        } catch (IOException error) {
            Alertas.error("No se pudo cargar la pantalla de clientes: " + error.getMessage());
        }
    }

    @FXML
    private void mostrarCitas() {
        if (!conectado) { Alertas.error("Primero restablece la conexión con PostgreSQL."); return; }
        try {
            if (vistaCitas == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("citas-view.fxml"));
                vistaCitas = loader.load();
                citaController = loader.getController();
            } else {
                citaController.recargarClientes();
                citaController.recargarTabla();
            }
            contenedor.getChildren().setAll(vistaCitas);
        } catch (IOException error) {
            Alertas.error("No se pudo cargar la pantalla de citas: " + error.getMessage());
        }
    }
}
