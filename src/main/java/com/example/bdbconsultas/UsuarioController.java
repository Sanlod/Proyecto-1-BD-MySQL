package com.example.bdbconsultas;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UsuarioController {


    //Navegación entre ventanas de usuario

    public void switchVolver(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/LogIn.fxml" , event);
    }

    public void switchDonar(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Donar.fxml" , event);
    }

    public void switchReportar(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Reportar.fxml" , event);
    }

    public void switchEstadisticasAdopciones(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/EstadisticasAdopcion.fxml" , event);
    }

    public void switchEstadisticasDonacion(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/EstadisticasDonacion.fxml" , event);
    }

    public void switchEstadisticasMatches(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/EstadisticasMatches.fxml" , event);
    }

    public void switchEstadisticas(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Estadisticas.fxml" , event);
    }



    public void cambiarEscena(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
