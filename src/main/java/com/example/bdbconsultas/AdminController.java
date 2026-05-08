package com.example.bdbconsultas;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {
    //Navegación entre ventanas de admin

    public void cambiarEscena(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void switchVolver(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/LogIn.fxml" , event);
    }
    public void switchDonaciones(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Donaciones.fxml",event);
    }
    public void switchCatalogos(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Catalogos.fxml" , event);
    }
    public void switchAdopciones(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Adopciones.fxml" , event);
    }
    public void switchMatches(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Matches.fxml" , event);
    }
    public void switchPersonas(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Persona.fxml" , event);
    }
    public void switchRegistrarMascota(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/RegistrarMascota.fxml" , event);
    }
    public void Recompensas(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/RecompensaAdmin.fxml" , event);
    }
    public void switchBitacora(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Bitacora.fxml" , event);
    }
    public void switchRegistrarCasaCuna(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/RegistrarCasaCuna.fxml", event);
    }
}
