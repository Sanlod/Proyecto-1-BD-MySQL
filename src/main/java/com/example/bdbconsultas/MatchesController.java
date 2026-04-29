package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.MatchesDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MatchesController implements Initializable {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private ComboBox<String> cmbRaza;
    @FXML private ComboBox<String> cmbColor;
    @FXML private ComboBox<String> cmbProvincia;
    @FXML private ComboBox<String> cmbCanton;
    @FXML private ComboBox<String> cmbDistrito;
    @FXML private ComboBox<String> cmbRescatista;
    @FXML private ComboBox<String> cmbAsociacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtChip;
    @FXML private ComboBox<String> cmbMatchState;
    @FXML private DatePicker dtDesde;
    @FXML private DatePicker dtHasta;
    @FXML private ComboBox<String> cmbMascotaPerdida;

    @FXML private TableView<ObservableList<String>> tblMatches;
    @FXML private Label lblTotal;

    @FXML private ComboBox<String> cmbNuevoEstado;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolver;

    private ObservableList<ObservableList<String>> datosTiposActuales;
    private ObservableList<ObservableList<String>> datosRazasActuales;
    private ObservableList<ObservableList<String>> datosColoresActuales;
    private ObservableList<ObservableList<String>> datosProvinciasActuales;
    private ObservableList<ObservableList<String>> datosCantonesActuales;
    private ObservableList<ObservableList<String>> datosDistritosActuales;
    private ObservableList<ObservableList<String>> datosRescatistasActuales;
    private ObservableList<ObservableList<String>> datosAsociacionesActuales;
    private ObservableList<ObservableList<String>> datosMascotasPerdidas;
    private ObservableList<ObservableList<String>> datosEstadosMatch;

    private final MatchesDAO dao = new MatchesDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombos();
        cargarEstadosMatch();
        tblMatches.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnCambiarEstado.setDisable(newVal == null);
        });
        btnCambiarEstado.setDisable(true);
    }

    private void cargarCombos() {
        try {
            datosTiposActuales = MascotasDAO.getTiposMascotas();
            cmbTipo.setItems(datosTiposActuales.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosColoresActuales = MascotasDAO.getColores();
            cmbColor.setItems(datosColoresActuales.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosProvinciasActuales = MascotasDAO.getProvincias();
            cmbProvincia.setItems(datosProvinciasActuales.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosRescatistasActuales = MascotasDAO.getRescatistas();
            cmbRescatista.setItems(datosRescatistasActuales.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosAsociacionesActuales = MascotasDAO.getAsociaciones();
            cmbAsociacion.setItems(datosAsociacionesActuales.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosMascotasPerdidas = MatchesDAO.getMascostasPerdidas();
            cmbMascotaPerdida.setItems(datosMascotasPerdidas.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            configurarEventos();
        } catch (Exception e) {
            mostrarError("Error al cargar catálogos: " + e.getMessage());
        }
    }

    private void cargarEstadosMatch() {
        try {
            datosEstadosMatch = MatchesDAO.getEstadosMatch();
            cmbMatchState.setItems(datosEstadosMatch.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            cmbNuevoEstado.setItems(datosEstadosMatch.stream().map(r -> r.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } catch (Exception e) {
            mostrarError("Error al cargar estados de match: " + e.getMessage());
        }
    }

    private void configurarEventos() {
        cmbTipo.setOnAction(e -> {
            try {
                String idTipo = obtenerIdSeleccionado(cmbTipo, datosTiposActuales);
                datosRazasActuales = MascotasDAO.getRazasPorTipo(idTipo);
                cmbRaza.setItems(datosRazasActuales.stream().map(r -> r.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (Exception ex) {
                mostrarError("Error al cargar razas: " + ex.getMessage());
            }
        });

        cmbProvincia.setOnAction(e -> {
            try {
                String idProvincia = obtenerIdSeleccionado(cmbProvincia, datosProvinciasActuales);
                datosCantonesActuales = MascotasDAO.getCantonesPorProvincia(idProvincia);
                cmbCanton.setItems(datosCantonesActuales.stream().map(r -> r.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                cmbDistrito.setItems(null);
            } catch (Exception ex) {
                mostrarError("Error al cargar cantones: " + ex.getMessage());
            }
        });

        cmbCanton.setOnAction(e -> {
            try {
                String idCanton = obtenerIdSeleccionado(cmbCanton, datosCantonesActuales);
                datosDistritosActuales = MascotasDAO.getDistritosPorCanton(idCanton);
                cmbDistrito.setItems(datosDistritosActuales.stream().map(r -> r.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (Exception ex) {
                mostrarError("Error al cargar distritos: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void onBuscar() {
        try {
            String idMascotaPerdida = obtenerIdSeleccionado(cmbMascotaPerdida, datosMascotasPerdidas);
            String idTipo = obtenerIdSeleccionado(cmbTipo, datosTiposActuales);
            String idRaza = obtenerIdSeleccionado(cmbRaza, datosRazasActuales);
            String idColor = obtenerIdSeleccionado(cmbColor, datosColoresActuales);
            String idEstado = obtenerIdSeleccionado(cmbMatchState, datosEstadosMatch);
            String idProvincia = obtenerIdSeleccionado(cmbProvincia, datosProvinciasActuales);
            String idCanton = obtenerIdSeleccionado(cmbCanton, datosCantonesActuales);
            String idDistrito = obtenerIdSeleccionado(cmbDistrito, datosDistritosActuales);
            String idAsociacion = obtenerIdSeleccionado(cmbAsociacion, datosAsociacionesActuales);

            MatchesDAO.ResultadoConsulta resultado = MatchesDAO.consultarMatches(
                    idMascotaPerdida, idTipo, idRaza,
                    txtNombre.getText().trim(),
                    txtChip.getText().trim(),
                    idColor, idEstado, idProvincia,
                    idCanton, idDistrito, idAsociacion);

            configurarColumnasDinamicas(resultado.columnas);
            tblMatches.setItems(resultado.filas);
            lblTotal.setText("Total: " + resultado.total);

        } catch (Exception e) {
            mostrarError("Error al buscar: " + e.getMessage());
        }
    }

    private void configurarColumnasDinamicas(java.util.List<String> columnas) {
        tblMatches.getColumns().clear();
        for (int i = 0; i < columnas.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnas.get(i));
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get(idx)));
            tblMatches.getColumns().add(col);
        }
    }

    @FXML
    private void onCambiarEstado() {
        ObservableList<String> seleccion = tblMatches.getSelectionModel().getSelectedItem();
        if (seleccion == null) { mostrarError("Seleccione un match."); return; }
        if (cmbNuevoEstado.getValue() == null) { mostrarError("Seleccione un estado."); return; }

        String idEstado = obtenerIdSeleccionado(cmbNuevoEstado, datosEstadosMatch);
        if (idEstado == null) { mostrarError("Estado no encontrado."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Cambiar estado del match?");
        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            try {
                MatchesDAO.cambiarEstadoMatch(seleccion.get(0), idEstado, "SYSTEM");
                mostrarInfo("Estado actualizado.");
                onBuscar();
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onLimpiar() {
        cmbTipo.setValue(null);
        cmbRaza.setValue(null);
        cmbColor.setValue(null);
        cmbProvincia.setValue(null);
        cmbCanton.setValue(null);
        cmbDistrito.setValue(null);
        cmbRescatista.setValue(null);
        cmbAsociacion.setValue(null);
        cmbMatchState.setValue(null);
        txtNombre.clear();
        txtChip.clear();
        dtDesde.setValue(null);
        dtHasta.setValue(null);
        tblMatches.setItems(null);
        lblTotal.setText("Total: 0 resultados");
    }

    private String obtenerIdSeleccionado(ComboBox<String> cmb, ObservableList<ObservableList<String>> datos) {
        if (cmb.getValue() == null || datos == null) return null;
        String nombre = cmb.getValue();
        for (ObservableList<String> fila : datos) {
            if (fila.get(1).equals(nombre)) return fila.get(0);
        }
        return null;
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    @FXML
    private void onVolver() {
        ((Stage) btnVolver.getScene().getWindow()).close();
    }
}