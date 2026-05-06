package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AdopcionesDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdoptarController implements Initializable {

    @FXML private ComboBox<String> cmbMascota;
    @FXML private ComboBox<String> cmbAdoptante;
    @FXML private VBox vboxPreguntas;
    @FXML private Button btnRegistrar;
    @FXML private Button btnVolver;
    @FXML private Button btnFoto;
    @FXML private Button btnFotoNueva;
    @FXML private Label lblFoto;
    @FXML private Label lblFotoNueva;

    private byte[] fotoBytes = null;
    private byte[] fotoNuevaBytes = null;
    private ObservableList<ObservableList<String>> datosMascotas;
    private ObservableList<ObservableList<String>> datosAdoptantes;
    private ObservableList<ObservableList<String>> datosPreguntas;

    // Campos generados dinámicamente para las respuestas
    private final List<TextField> camposRespuesta = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombos();
        cargarPreguntas();
    }

    private void cargarCombos() {
        try {
            datosMascotas = AdopcionesDAO.getMascotas();
            datosAdoptantes = AdopcionesDAO.getAdoptantes();
            for (ObservableList<String> fila : datosMascotas)
                cmbMascota.getItems().add(fila.get(1));
            for (ObservableList<String> fila : datosAdoptantes)
                cmbAdoptante.getItems().add(fila.get(1));
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarPreguntas() {
        try {
            datosPreguntas = AdopcionesDAO.getPreguntas(); // SP_LISTAR_PREGUNTAS
            vboxPreguntas.getChildren().clear();
            camposRespuesta.clear();

            for (ObservableList<String> pregunta : datosPreguntas) {
                Label lbl = new Label(pregunta.get(1)); // nombre de la pregunta
                TextField txt = new TextField();
                txt.setPromptText("Respuesta...");
                vboxPreguntas.getChildren().addAll(lbl, txt);
                camposRespuesta.add(txt);
            }
        } catch (Exception e) {
            mostrarError("Error al cargar preguntas: " + e.getMessage());
        }
    }

    @FXML
    private void onRegistrar() {
        int idxMascota   = cmbMascota.getSelectionModel().getSelectedIndex();
        int idxAdoptante = cmbAdoptante.getSelectionModel().getSelectedIndex();

        if (idxMascota < 0 || idxAdoptante < 0) {
            mostrarError("Debe seleccionar mascota y adoptante."); return;
        }
        for (TextField campo : camposRespuesta) {
            if (campo.getText().trim().isEmpty()) {
                mostrarError("Debe responder todas las preguntas."); return;
            }
        }

        String idMascota   = datosMascotas.get(idxMascota).get(0);
        String idAdoptante = datosAdoptantes.get(idxAdoptante).get(0);
        String createdBy   = "SYSTEM";

        try {
            int idRequest = AdopcionesDAO.registrarRequest(idMascota, idAdoptante, createdBy);

            if (idRequest == -1) {
                mostrarError("No se pudo registrar la solicitud."); return;
            }

            for (int i = 0; i < datosPreguntas.size(); i++) {
                AdopcionesDAO.registrarRespuesta(
                        datosPreguntas.get(i).get(0),
                        String.valueOf(idRequest),
                        camposRespuesta.get(i).getText().trim(),
                        createdBy);
            }

            mostrarInfo("Solicitud de adopción registrada. Pendiente de aprobación.");
            limpiar();

        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void limpiar() {
        cmbMascota.getSelectionModel().clearSelection();
        cmbAdoptante.getSelectionModel().clearSelection();
        camposRespuesta.forEach(TextField::clear);
        fotoBytes = null;
        fotoNuevaBytes = null;
        lblFoto.setText("Sin foto");
        lblFotoNueva.setText("Sin foto");
    }
    @FXML
    private void onVolver() {
        ((Stage) btnVolver.getScene().getWindow()).close();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setContentText(msg); a.show();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.show();
    }
    @FXML
    private void onSeleccionarFoto() {
        fotoBytes = seleccionarImagen(lblFoto);
    }

    @FXML
    private void onSeleccionarFotoNueva() {
        fotoNuevaBytes = seleccionarImagen(lblFotoNueva);
    }

    private byte[] seleccionarImagen(Label lblIndicador) {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter(
                        "Imágenes", "*.png", "*.jpg", "*.jpeg"));
        java.io.File archivo = fc.showOpenDialog(btnFoto.getScene().getWindow());
        if (archivo != null) {
            try {
                byte[] bytes = java.nio.file.Files.readAllBytes(archivo.toPath());
                lblIndicador.setText(archivo.getName());
                return bytes;
            } catch (Exception e) {
                mostrarError("Error al leer imagen: " + e.getMessage());
            }
        }
        return null;
    }


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}