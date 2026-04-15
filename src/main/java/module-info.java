module com.example.bdbconsultas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;


    opens com.example.bdbconsultas to javafx.fxml;
    exports com.example.bdbconsultas;
}