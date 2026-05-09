package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AsignacionDAO;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AsignacionController implements Initializable {

    @FXML private TableView<ObservableList<String>> tblMascotas;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaId;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaNombre;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaRaza;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaTamano;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaEnergia;

    @FXML private TableView<ObservableList<String>> tblMascotasConCasa;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaConCasaId;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaConCasaNombre;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaConCasaRaza;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaConCasaTamano;
    @FXML private TableColumn<ObservableList<String>, String> colMascotaConCasaEnergia;
    @FXML private Button btnQuitarAsignacion;

    @FXML private TableView<ObservableList<String>> tblCasas;
    @FXML private TableColumn<ObservableList<String>, String> colCasaId;
    @FXML private TableColumn<ObservableList<String>, String> colCasaTamanos;
    @FXML private TableColumn<ObservableList<String>, String> colCasaDistrito;

    @FXML private Label lblMascotaSeleccionada;
    @FXML private Button btnBuscarCasas;
    @FXML private Button btnAsignar;

    private final AsignacionDAO dao = AsignacionDAO.getInstance();
    private ObservableList<String> mascotaSeleccionada;
    private ObservableList<String> casaSeleccionada;
    private ObservableList<String> mascotaConCasaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarMascotas();

        // Al seleccionar mascota, habilita el botón de buscar
        tblMascotas.getSelectionModel().selectedItemProperty().addListener((obs, old, nueva) -> {
            mascotaSeleccionada = nueva;
            casaSeleccionada = null;
            tblCasas.getItems().clear();
            btnAsignar.setDisable(true);
            if (nueva != null) {
                lblMascotaSeleccionada.setText("Mascota: " + nueva.get(1) + " (ID: " + nueva.get(0) + ")");
                btnBuscarCasas.setDisable(false);
            }
        });

        // Al seleccionar casa, habilita el botón de asignar
        tblCasas.getSelectionModel().selectedItemProperty().addListener((obs, old, nueva) -> {
            casaSeleccionada = nueva;
            btnAsignar.setDisable(nueva == null);
        });

        btnBuscarCasas.setDisable(true);
        btnAsignar.setDisable(true);

        //Cargar mascotas con casa
        cargarMascotasConCasa();
        btnQuitarAsignacion.setDisable(true);

        tblMascotasConCasa.getSelectionModel().selectedItemProperty().addListener((obs, old, nueva) -> {
            mascotaConCasaSeleccionada = nueva;
            btnQuitarAsignacion.setDisable(nueva == null);
        });
    }

    private void configurarColumnas() {
        colMascotaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(0)));
        colMascotaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(1)));
        colMascotaRaza.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(2)));
        colMascotaTamano.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(3)));
        colMascotaEnergia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(4)));

        colMascotaConCasaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(0)));
        colMascotaConCasaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(1)));
        colMascotaConCasaRaza.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(2)));
        colMascotaConCasaTamano.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(3)));
        colMascotaConCasaEnergia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(4)));

        colCasaId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(0)));
        colCasaTamanos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(1)));
        colCasaDistrito.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(2)));
    }

    private void cargarMascotas() {
        try {
            tblMascotas.setItems(dao.getMascotasSinCasa());
        } catch (Exception e) {
            mostrarError("Error al cargar mascotas: " + e.getMessage());
        }
    }

    private void cargarMascotasConCasa() {
        try {
            tblMascotasConCasa.setItems(dao.getMascotasConCasa());
        } catch (Exception e) {
            mostrarError("Error al cargar mascotas con casa: " + e.getMessage());
        }
    }

    @FXML
    private void buscarCasasCompatibles() {
        if (mascotaSeleccionada == null) return;
        try {
            int idPet = Integer.parseInt(mascotaSeleccionada.get(0));
            ObservableList<ObservableList<String>> casas = dao.getCasasCompatibles(idPet);
            tblCasas.setItems(casas);
            if (casas.isEmpty()) {
                mostrarInfo("No hay casas cuna compatibles para esta mascota.");
            }
        } catch (Exception e) {
            mostrarError("Error al buscar casas: " + e.getMessage());
        }
    }

    @FXML
    private void asignarMascota() {
        if (mascotaSeleccionada == null || casaSeleccionada == null) return;

        int idPet = Integer.parseInt(mascotaSeleccionada.get(0));
        int idCasa = Integer.parseInt(casaSeleccionada.get(0));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Asignar a " + mascotaSeleccionada.get(1) +
                " a la casa cuna ID " + idCasa + "?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    dao.asignarMascotaCasa(idPet, idCasa);
                    mostrarInfo("Mascota asignada exitosamente!");
                    cargarMascotas(); //Refrescar la tabla
                    cargarMascotasConCasa(); //Refrescar nueva tabla
                    tblCasas.getItems().clear();
                    lblMascotaSeleccionada.setText("Ninguna seleccionada");
                    btnBuscarCasas.setDisable(true);
                    btnAsignar.setDisable(true);
                } catch (Exception e) {
                    mostrarError("Error al asignar: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void quitarAsignacion() {
        if (mascotaConCasaSeleccionada == null) return;

        int idPet = Integer.parseInt(mascotaConCasaSeleccionada.get(0));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Quitar a " + mascotaConCasaSeleccionada.get(1) + " de su casa cuna?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    dao.quitarAsignacion(idPet);
                    mostrarInfo("Asignación eliminada exitosamente.");
                    cargarMascotas();
                    cargarMascotasConCasa();
                    btnQuitarAsignacion.setDisable(true);
                } catch (Exception e) {
                    mostrarError("Error al quitar asignación: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void cancelar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Admin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
