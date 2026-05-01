package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AssociationDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.PersonaDAO;
import com.example.bdbconsultas.DAOs.RecompensasDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
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

public class RecompensaAdminController implements Initializable {

    @FXML
    private TableView<ObservableList<String>> tblMascotasPerdidas;
    @FXML
    private ComboBox<String> cmbAsociacion;
    @FXML
    private ComboBox<String> cmbRescatistas;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblRescatista;
    @FXML
    private Button btnPagarRescatista;
    @FXML
    private Button btnDonarAsociacion;
    @FXML
    private Button btnVolver;

    private ObservableList<ObservableList<String>> datosAsociaciones;
    private ObservableList<ObservableList<String>> datosRescatistas;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarMascotasPerdidas();
        cargarAsociaciones();
        cargarRescatistas();
        btnPagarRescatista.setDisable(true);
        btnDonarAsociacion.setDisable(true);

        tblMascotasPerdidas.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    boolean haySeleccion = sel != null;
                    btnPagarRescatista.setDisable(!haySeleccion);
                    btnDonarAsociacion.setDisable(!haySeleccion);
                    if (haySeleccion) {
                        // Mostrar rescatista asignado a la mascota hallada del match
                        lblRescatista.setText("Rescatista: " +
                                (sel.size() > 5 ? sel.get(5) : "Sin rescatista"));
                    }
                });
    }

    private void configurarTabla() {
        String[] headers = {"ID", "Nombre", "Tipo", "Raza", "Fecha Pérdida", "Bounty", "Moneda"};
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(idx)));
            tblMascotasPerdidas.getColumns().add(col);
        }
    }

    private void cargarMascotasPerdidas() {
        try {
            ObservableList<ObservableList<String>> datos = MascotasDAO.getMascotasPerdidas();
            tblMascotasPerdidas.setItems(datos);
            lblTotal.setText("Total: " + datos.size());
        } catch (Exception e) {
            mostrarError("Error al cargar: " + e.getMessage());
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

    @FXML
    private void onPagarRescatista() {
        ObservableList<String> sel = tblMascotasPerdidas.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Seleccione una mascota."); return; }
        if (cmbRescatistas.getSelectionModel().getSelectedIndex() < 0) {
            mostrarError("Seleccione un rescatista."); return;
        }

        String idPet      = sel.get(0);
        String idRescuer  = datosRescatistas.get(
                cmbRescatistas.getSelectionModel().getSelectedIndex()).get(0);

        try {
            RecompensasDAO.pagarRescatista(idRescuer, idPet, "SYSTEM");
            RecompensasDAO.marcarHallada(idPet, "SYSTEM");
            mostrarInfo("Bounty pagado y mascota marcada como hallada.");
            cargarMascotasPerdidas();
            cmbRescatistas.setValue(null);
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

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
            RecompensasDAO.ResultadoConsulta bounties = RecompensasDAO.consultarRecompensas(idPet);
            if (bounties.filas.isEmpty()) {
                mostrarError("No hay bounty activo para esta mascota."); return;
            }
            String idBounty = bounties.filas.get(0).get(0);
            RecompensasDAO.donarRecompensa(idBounty, idAsociacion, "SYSTEM");
            RecompensasDAO.marcarHallada(idPet, "SYSTEM");
            mostrarInfo("Bounty donado y mascota marcada como hallada.");
            cargarMascotasPerdidas();
            cmbAsociacion.setValue(null);
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }
    public void switchVolver(ActionEvent event) throws IOException {
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

    private void cargarRescatistas() {
        try {
            datosRescatistas = PersonaDAO.getRescatistas();
            ObservableList<String> nombres = FXCollections.observableArrayList();
            for (ObservableList<String> fila : datosRescatistas)
                nombres.add(fila.get(1));
            cmbRescatistas.setItems(nombres);
        } catch (Exception e) {
            mostrarError("Error al cargar rescatistas: " + e.getMessage());
        }
    }
}