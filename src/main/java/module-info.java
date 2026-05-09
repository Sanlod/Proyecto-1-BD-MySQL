module com.example.bdbconsultas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;
    requires argon2;


    opens com.example.bdbconsultas to javafx.fxml;
    exports com.example.bdbconsultas;
}