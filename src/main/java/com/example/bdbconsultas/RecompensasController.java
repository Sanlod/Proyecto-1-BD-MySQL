package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.RecompensasDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RecompensasController implements Initializable {

    @FXML private ComboBox<String> cmbMascota;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<String> cmbMoneda;
    @FXML private TableView<ObservableList<String>> tblRecompensas;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colMonto;
    @FXML private TableColumn<ObservableList<String>, String> colMoneda;
    @FXML private TableColumn<ObservableList<String>, String> colMascota;
    @FXML private Label lblTotal;
    @FXML private Button btnVolver;

    private ObservableList<ObservableList<String>> datosMascotas;
    private ObservableList<ObservableList<String>> datosMonedas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarMascotas();
        cargarMonedas();
        onConsultar();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colMonto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colMoneda.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colMascota.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
    }

    private void cargarMascotas() {
        try {
            datosMascotas = MascotasDAO.getMascotas();
            ObservableList<String> nombres = FXCollections.observableArrayList();
            for (ObservableList<String> fila : datosMascotas)
                nombres.add(fila.get(1));
            cmbMascota.setItems(nombres);
        } catch (Exception e) {
            mostrarError("Error al cargar mascotas: " + e.getMessage());
        }
    }

    private void cargarMonedas() {
        try {
            datosMonedas = MascotasDAO.getMonedas();
            ObservableList<String> nombres = FXCollections.observableArrayList();
            for (ObservableList<String> fila : datosMonedas)
                nombres.add(fila.get(1));
            cmbMoneda.setItems(nombres);
        } catch (Exception e) {
            mostrarError("Error al cargar monedas: " + e.getMessage());
        }
    }

    @FXML
    private void onConsultar() {
        try {
            String idPet = null;
            int idx = cmbMascota.getSelectionModel().getSelectedIndex();
            if (idx >= 0) idPet = datosMascotas.get(idx).get(0);

            RecompensasDAO.ResultadoConsulta res = RecompensasDAO.consultarRecompensas(idPet);
            tblRecompensas.setItems(res.filas);
            lblTotal.setText("Total: " + res.total);
        } catch (Exception e) {
            mostrarError("Error al consultar: " + e.getMessage());
        }
    }

    // Registrar bounty marca la mascota como perdida automáticamente
    @FXML
    private void onRegistrarBounty() {
        if (cmbMascota.getSelectionModel().getSelectedIndex() < 0) {
            mostrarError("Seleccione una mascota."); return;
        }
        if (txtMonto.getText().trim().isEmpty()) {
            mostrarError("Ingrese el monto de recompensa."); return;
        }
        if (cmbMoneda.getSelectionModel().getSelectedIndex() < 0) {
            mostrarError("Seleccione una moneda."); return;
        }

        String idPet    = datosMascotas.get(cmbMascota.getSelectionModel().getSelectedIndex()).get(0);
        String monto    = txtMonto.getText().trim();
        String idMoneda = datosMonedas.get(cmbMoneda.getSelectionModel().getSelectedIndex()).get(0);

        try {
            RecompensasDAO.marcarPerdida(idPet, monto, idMoneda,
                    java.time.LocalDate.now(), "SYSTEM");
            mostrarInfo("Mascota marcada como perdida con recompensa de " + monto + ".");
            txtMonto.clear();
            cmbMascota.setValue(null);
            cmbMoneda.setValue(null);
            onConsultar();
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        ((Stage) btnVolver.getScene().getWindow()).close();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}