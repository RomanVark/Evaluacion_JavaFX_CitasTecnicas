module ni.edu.uam.evaluacion_javafx_citastecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens ni.edu.uam.evaluacion_javafx_citastecnicas to javafx.fxml;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.controlador to javafx.fxml;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.modelo to javafx.base;

    exports ni.edu.uam.evaluacion_javafx_citastecnicas;
}