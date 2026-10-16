package ni.edu.uam.evaluacion_javafx_citastecnicas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene escena = new Scene(loader.load(), 1200, 780);
        escena.getStylesheets().add(HelloApplication.class.getResource("app.css").toExternalForm());
        stage.setTitle("TecnoAsistencia Nicaragua | Gestión de citas técnicas");
        stage.setMinWidth(980);
        stage.setMinHeight(650);
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
