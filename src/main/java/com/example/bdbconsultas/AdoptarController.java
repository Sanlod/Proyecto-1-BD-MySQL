package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AdopcionesDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.beans.property.SimpleStringProperty;
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
    @FXML private TableView<ObservableList<String>> tblMascotas;
    @FXML private TableColumn<ObservableList<String>, String> colPetId, colPetNombre;
    @FXML private TableView<ObservableList<String>> tblAdoptantes;
    @FXML private TableColumn<ObservableList<String>, String> colAdopId, colAdopNombre;
    @FXML private VBox vboxPreguntas;
    @FXML private Button btnRegistrar;
    @FXML private Button btnVolver;

    private byte[] fotoBytes = null;
    private byte[] fotoNuevaBytes = null;
    private ObservableList<ObservableList<String>> datosMascotas;
    private ObservableList<ObservableList<String>> datosAdoptantes;
    private ObservableList<ObservableList<String>> datosPreguntas;

    // Campos generados dinámicamente para las respuestas
    private final List<TextField> camposRespuesta = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablas();
        cargarDatosTablas();
        cargarPreguntas();
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
    private void configurarTablas() {
        colPetId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colPetNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));

        colAdopId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colAdopNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
    }

    private void cargarDatosTablas() {
        try {
            datosMascotas = MascotasDAO.getMascotasEnAdopcion();
            tblMascotas.setItems(datosMascotas);

            datosAdoptantes = AdopcionesDAO.getAdoptantes();
            tblAdoptantes.setItems(datosAdoptantes);
        } catch (Exception e) {
            mostrarError("Error al cargar tablas: " + e.getMessage());
        }
    }
    @FXML
    private void onRegistrar() {
        ObservableList<String> mascotaSeleccionada = tblMascotas.getSelectionModel().getSelectedItem();
        ObservableList<String> adoptanteSeleccionado = tblAdoptantes.getSelectionModel().getSelectedItem();

        if (mascotaSeleccionada == null || adoptanteSeleccionado == null) {
            mostrarError("Debe seleccionar una mascota y un adoptante de las tablas.");
            return;
        }

        for (TextField campo : camposRespuesta) {
            if (campo.getText().trim().isEmpty()) {
                mostrarError("Debe responder todas las preguntas.");
                return;
            }
        }
        String idMascota = mascotaSeleccionada.get(0);
        String idAdoptante = adoptanteSeleccionado.get(0);
        String createdBy = LogInController.loggedUser;

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
        tblMascotas.getSelectionModel().clearSelection();
        tblAdoptantes.getSelectionModel().clearSelection();
        camposRespuesta.forEach(TextField::clear);
        fotoBytes = null;
        fotoNuevaBytes = null;

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


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}