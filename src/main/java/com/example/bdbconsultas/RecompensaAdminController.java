package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AssociationDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.PersonaDAO;
import com.example.bdbconsultas.DAOs.RecompensasDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RecompensaAdminController implements Initializable {

    @FXML private TableView<ObservableList<String>> tblMascotasPerdidas;
    @FXML private ComboBox<String> cmbPersonaEncontro;
    @FXML private ComboBox<String> cmbAsociacion;
    @FXML private Label lblTotal;
    @FXML private Button btnAsignarPersona;
    @FXML private Button btnDonarAsociacion;
    @FXML private Button btnVolver;

    private ObservableList<ObservableList<String>> datosPersonas;
    private ObservableList<ObservableList<String>> datosAsociaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMascotasPerdidas();
        cargarPersonas();
        cargarAsociaciones();
        btnAsignarPersona.setDisable(true);
        btnDonarAsociacion.setDisable(true);

        tblMascotasPerdidas.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    btnAsignarPersona.setDisable(sel == null);
                    btnDonarAsociacion.setDisable(sel == null);
                });
    }

    private void cargarMascotasPerdidas() {
        try {
            ObservableList<ObservableList<String>> datos =
                    MascotasDAO.getMascotasPerdidas();
            tblMascotasPerdidas.getColumns().clear();
            if (!datos.isEmpty()) {
                String[] headers = {"ID", "Nombre", "Tipo", "Raza", "Fecha Pérdida"};
                for (int i = 0; i < headers.length && i < datos.get(0).size(); i++) {
                    final int idx = i;
                    TableColumn<ObservableList<String>, String> col =
                            new TableColumn<>(headers[i]);
                    col.setCellValueFactory(d ->
                            new SimpleStringProperty(d.getValue().get(idx)));
                    tblMascotasPerdidas.getColumns().add(col);
                }
            }
            tblMascotasPerdidas.setItems(datos);
            lblTotal.setText("Total: " + datos.size());
        } catch (Exception e) {
            mostrarError("Error al cargar: " + e.getMessage());
        }
    }

    private void cargarPersonas() {
        try {
            datosPersonas = PersonaDAO.getPersonas();
            ObservableList<String> nombres = FXCollections.observableArrayList();
            for (ObservableList<String> fila : datosPersonas)
                nombres.add(fila.get(1));
            cmbPersonaEncontro.setItems(nombres);
        } catch (Exception e) {
            mostrarError("Error al cargar personas: " + e.getMessage());
        }
    }

    private void cargarAsociaciones() {
        try {
            datosAsociaciones = AssociationDAO.getAsociaciones();
            ObservableList<String> nombres = FXCollections.observableArrayList();
            for (ObservableList<String> fila : datosAsociaciones)
                nombres.add(fila.get(1));
            cmbAsociacion.setItems(nombres);
        } catch (Exception e) {
            mostrarError("Error al cargar asociaciones: " + e.getMessage());
        }
    }

    // Marcar hallada y asignar bounty a persona
    @FXML
    private void onAsignarPersona() {
        ObservableList<String> sel = tblMascotasPerdidas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Seleccione una mascota."); return; }
        if (cmbPersonaEncontro.getSelectionModel().getSelectedIndex() < 0) {
            mostrarError("Seleccione la persona que encontró la mascota."); return;
        }

        String idPet     = sel.get(0);
        String idPersona = datosPersonas.get(
                cmbPersonaEncontro.getSelectionModel().getSelectedIndex()).get(0);

        try {
            boolean ok = RecompensasDAO.marcarHallada(idPet, idPersona, "SYSTEM");
            if (ok) {
                mostrarInfo("Mascota hallada. Bounty asignado a la persona.");
                cargarMascotasPerdidas();
                cmbPersonaEncontro.setValue(null);
            } else {
                mostrarError("No se pudo procesar.");
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    // Marcar hallada y donar bounty a asociación
    @FXML
    private void onDonarAsociacion() {
        ObservableList<String> sel = tblMascotasPerdidas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Seleccione una mascota."); return; }
        if (cmbAsociacion.getSelectionModel().getSelectedIndex() < 0) {
            mostrarError("Seleccione una asociación."); return;
        }

        String idPet        = sel.get(0);
        String idAsociacion = datosAsociaciones.get(
                cmbAsociacion.getSelectionModel().getSelectedIndex()).get(0);

        try {
            // Marcar hallada sin asignar persona
            RecompensasDAO.marcarHallada(idPet, null, "SYSTEM");

            // Obtener bounty y donar
            RecompensasDAO.ResultadoConsulta bounties =
                    RecompensasDAO.consultarRecompensas(idPet);
            if (!bounties.filas.isEmpty()) {
                String idBounty = bounties.filas.get(0).get(0);
                RecompensasDAO.donarRecompensa(idBounty, idAsociacion, "SYSTEM");
                mostrarInfo("Mascota hallada. Bounty donado a la asociación.");
            } else {
                mostrarInfo("Mascota hallada. No había bounty activo.");
            }
            cargarMascotasPerdidas();
            cmbAsociacion.setValue(null);
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
        a.setHeaderText(null); a.setContentText(msg); a.show();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.show();
    }
}