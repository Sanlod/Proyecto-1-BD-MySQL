package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BuscarMascotasController implements Initializable {


    @FXML private AnchorPane paginaBusqueda;
    @FXML private AnchorPane paginaDetalle;


    @FXML private TextField txtNombre;
    @FXML private TextField txtNumChip;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbRaza;
    @FXML private ComboBox<String> cmbRescatista;
    @FXML private ComboBox<String> cmbAsociacion;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private ComboBox<String> cmbColor;
    @FXML private ComboBox<String> cmbProvincia;
    @FXML private ComboBox<String> cmbCanton;
    @FXML private ComboBox<String> cmbDistrito;
    @FXML private DatePicker dtDesde;
    @FXML private DatePicker dtHasta;

    @FXML private TableView<ObservableList<String>> tblDatos;
    @FXML private TableColumn<ObservableList<String>, String> colNombre;
    @FXML private TableColumn<ObservableList<String>, String> colTipo;
    @FXML private TableColumn<ObservableList<String>, String> colRaza;
    @FXML private TableColumn<ObservableList<String>, String> colColor;
    @FXML private TableColumn<ObservableList<String>, String> colEstado;
    @FXML private TableColumn<ObservableList<String>, String> colUbicacion;
    @FXML private TableColumn<ObservableList<String>, String> colFecha;


    @FXML private ImageView imvMascota;
    @FXML private Label lblTotal;


    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnVolver;
    @FXML private Button btnVolverBusqueda;


    @FXML private Label lblDetalleNombre;
    @FXML private Label lblDetalleTipo;
    @FXML private Label lblDetalleRaza;
    @FXML private Label lblDetalleColor;
    @FXML private Label lblDetalleChip;
    @FXML private Label lblDetalleEstado;
    @FXML private Label lblDetalleSeveridad;
    @FXML private Label lblDetalleEnergia;
    @FXML private Label lblDetalleTamanio;
    @FXML private Label lblDetalleEspacio;
    @FXML private Label lblDetalleTelefono;
    @FXML private Label lblDetalleEmail;
    @FXML private Label lblDetalleUbicacion;
    @FXML private Label lblDetalleRescatista;
    @FXML private Label lblDetalleAsociacion;
    @FXML private Label lblDetalleVeterinario;
    @FXML private Label lblDetalleCasaCuna;
    @FXML private Label lblDetalleDificultad;
    @FXML private Label lblDetalleFechaPerdida;
    @FXML private Label lblDetalleFechaHallazgo;
    @FXML private Label lblDetalleDescAbandono;
    @FXML private Label lblDetalleNotas;

    @FXML private ImageView imvDetalleAntes;
    @FXML private ImageView imvDetalleDespues;


    private ObservableList<ObservableList<String>> datosProvinciasActuales;
    private ObservableList<ObservableList<String>> datosCantonesActuales;
    private ObservableList<ObservableList<String>> datosDistritosActuales;
    private ObservableList<ObservableList<String>> datosTiposActuales;
    private ObservableList<ObservableList<String>> datosRazasActuales;
    private ObservableList<ObservableList<String>> datosEstadosActuales;
    private ObservableList<ObservableList<String>> datosColoresActuales;
    private ObservableList<ObservableList<String>> datosAsociacionesActuales;
    private ObservableList<ObservableList<String>> datosRescatistasActuales;

    private final MascotasDAO mascotasDAO = new MascotasDAO();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombos();
        configurarTabla();
        configurarEventos();
        configurarSeleccionTabla();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(1)));
        colTipo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(2)));
        colRaza.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(3)));
        colColor.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(4)));
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(6)));
        colUbicacion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(8)));
        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(7)));
    }

    private void configurarSeleccionTabla() {
        tblDatos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String idMascota = newVal.get(0);
                cargarImagenMascota(idMascota);
            }
        });

        tblDatos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                onVerDetalle();
            }
        });
    }

    private void cargarImagenMascota(String idMascota) {
        try {
            byte[] imagen = mascotasDAO.obtenerImagenMascota(idMascota);
            if (imagen != null && imagen.length > 0) {
                imvMascota.setImage(new Image(new ByteArrayInputStream(imagen)));
            } else {
                imvMascota.setImage(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarCombos() {
        try {
            cmbEstado.setItems(datosEstadosActuales.stream()
                    .filter(row -> {
                        String nombreEstado = row.get(1); 
                        return !nombreEstado.equalsIgnoreCase("PROCESADA");
                    })
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            datosColoresActuales = MascotasDAO.getColores();
            cmbColor.setItems(datosColoresActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosAsociacionesActuales = MascotasDAO.getAsociaciones();
            cmbAsociacion.setItems(datosAsociacionesActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosRescatistasActuales = MascotasDAO.getRescatistas();
            cmbRescatista.setItems(datosRescatistasActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosTiposActuales = MascotasDAO.getTiposMascotas();
            cmbTipo.setItems(datosTiposActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            datosProvinciasActuales = MascotasDAO.getProvincias();
            cmbProvincia.setItems(datosProvinciasActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        } catch (Exception e) {
            mostrarError("Error al cargar catálogos: " + e.getMessage());
        }
    }

    private void configurarEventos() {
        cmbTipo.setOnAction(e -> {
            try {
                String idTipo = obtenerIdSeleccionado(cmbTipo, datosTiposActuales);
                datosRazasActuales = MascotasDAO.getRazasPorTipo(idTipo);
                cmbRaza.setItems(datosRazasActuales.stream()
                        .map(row -> row.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (Exception ex) {
                mostrarError("Error al cargar razas: " + ex.getMessage());
            }
        });

        cmbProvincia.setOnAction(e -> {
            try {
                String idProvincia = obtenerIdSeleccionado(cmbProvincia, datosProvinciasActuales);
                datosCantonesActuales = MascotasDAO.getCantonesPorProvincia(idProvincia);
                cmbCanton.setItems(datosCantonesActuales.stream()
                        .map(row -> row.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                cmbDistrito.setItems(null);
                datosDistritosActuales = null;
            } catch (Exception ex) {
                mostrarError("Error al cargar cantones: " + ex.getMessage());
            }
        });

        cmbCanton.setOnAction(e -> {
            try {
                String idCanton = obtenerIdSeleccionado(cmbCanton, datosCantonesActuales);
                datosDistritosActuales = MascotasDAO.getDistritosPorCanton(idCanton);
                cmbDistrito.setItems(datosDistritosActuales.stream()
                        .map(row -> row.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (Exception ex) {
                mostrarError("Error al cargar distritos: " + ex.getMessage());
            }
        });
    }

    @FXML
    private void onBuscar() {
        try {
            String idTipo = obtenerIdSeleccionado(cmbTipo, datosTiposActuales);
            String idRaza = obtenerIdSeleccionado(cmbRaza, datosRazasActuales);
            String idEstado = obtenerIdSeleccionado(cmbEstado, datosEstadosActuales);
            String idColor = obtenerIdSeleccionado(cmbColor, datosColoresActuales);
            String idProvincia = obtenerIdSeleccionado(cmbProvincia, datosProvinciasActuales);
            String idCanton = obtenerIdSeleccionado(cmbCanton, datosCantonesActuales);
            String idDistrito = obtenerIdSeleccionado(cmbDistrito, datosDistritosActuales);
            String idAsociacion = obtenerIdSeleccionado(cmbAsociacion, datosAsociacionesActuales);
            String idRescatista = obtenerIdSeleccionado(cmbRescatista, datosRescatistasActuales);

            LocalDate fechaDesde = dtDesde.getValue();
            LocalDate fechaHasta = dtHasta.getValue();

            MascotasDAO.ResultadoConsulta resultado = mascotasDAO.consultarMascotas(
                    idTipo, idRaza, txtNombre.getText(), txtNumChip.getText(),
                    idRescatista, idEstado, idColor, idProvincia, idCanton, idDistrito,
                    idAsociacion, fechaDesde, fechaHasta
            );

            tblDatos.setItems(resultado.filas);
            lblTotal.setText("Total: " + resultado.total + " resultados");

            if (resultado.filas.isEmpty()) {
                imvMascota.setImage(null);
            }

        } catch (Exception e) {
            mostrarError("Error al buscar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onLimpiar() {
        txtNombre.clear();
        txtNumChip.clear();

        cmbTipo.setValue(null);
        cmbRaza.setValue(null);
        cmbEstado.setValue(null);
        cmbColor.setValue(null);
        cmbProvincia.setValue(null);
        cmbCanton.setValue(null);
        cmbDistrito.setValue(null);
        cmbAsociacion.setValue(null);
        cmbRescatista.setValue(null);

        dtDesde.setValue(null);
        dtHasta.setValue(null);

        if (tblDatos.getItems() != null) {
            tblDatos.getItems().clear();
        }

        imvMascota.setImage(null);
        lblTotal.setText("Total: 0 resultados");
    }
    @FXML
    private void onVerDetalle() {
        ObservableList<String> seleccion = tblDatos.getSelectionModel().getSelectedItem();
        if (seleccion == null || seleccion.isEmpty()) {
            mostrarError("Seleccione una mascota para ver el detalle");
            return;
        }


        String idMascota = seleccion.get(0);
        cargarDatosDetalle(idMascota);

        paginaBusqueda.setVisible(false);
        paginaDetalle.setVisible(true);
    }

    @FXML
    private void onVolverBusqueda() {
        paginaDetalle.setVisible(false);
        paginaBusqueda.setVisible(true);
    }

    private void cargarDatosDetalle(String idMascota) {
        try {
            Map<String, Object> d = mascotasDAO.obtenerMascotaPorId(idMascota);

            if (d != null) {
                lblDetalleNombre.setText(obtenerString(d, "nombre"));
                lblDetalleTipo.setText(obtenerString(d, "tipo"));
                lblDetalleRaza.setText(obtenerString(d, "raza"));
                lblDetalleColor.setText(obtenerString(d, "color", "N/A"));
                lblDetalleChip.setText(obtenerString(d, "chip", "No tiene"));
                lblDetalleEstado.setText(obtenerString(d, "estado"));
                lblDetalleSeveridad.setText(obtenerString(d, "severidad", "N/A"));
                lblDetalleEnergia.setText(obtenerString(d, "nivelEnergia", "N/A"));
                lblDetalleTamanio.setText(obtenerString(d, "tamanio", "N/A"));

                Object requiereEspacio = d.get("requiereEspacio");
                if (requiereEspacio != null && (int) requiereEspacio == 1) {
                    lblDetalleEspacio.setText("Sí");
                } else {
                    lblDetalleEspacio.setText("No");
                }

                lblDetalleTelefono.setText(obtenerString(d, "telefono", "N/A"));
                lblDetalleEmail.setText(obtenerString(d, "email", "N/A"));
                lblDetalleUbicacion.setText(obtenerString(d, "ubicacion", "N/A"));
                lblDetalleRescatista.setText(obtenerString(d, "rescatista", "N/A"));
                lblDetalleAsociacion.setText(obtenerString(d, "asociacion", "N/A"));
                lblDetalleVeterinario.setText(obtenerString(d, "veterinario", "N/A"));
                lblDetalleCasaCuna.setText(obtenerString(d, "casaCuna", "N/A"));
                lblDetalleDificultad.setText(obtenerString(d, "dificultad", "N/A"));
                lblDetalleFechaPerdida.setText(obtenerString(d, "fechaPerdida", "N/A"));
                lblDetalleFechaHallazgo.setText(obtenerString(d, "fechaHallazgo", "N/A"));
                lblDetalleDescAbandono.setText(obtenerString(d, "descripcionAbandono", "N/A"));
                lblDetalleNotas.setText(obtenerString(d, "notas", "N/A"));

                byte[] imgAntes = (byte[]) d.get("imagenAntes");
                if (imgAntes != null && imgAntes.length > 0) {
                    imvDetalleAntes.setImage(new Image(new ByteArrayInputStream(imgAntes)));
                } else {
                    imvDetalleAntes.setImage(null);
                }

                byte[] imgDespues = (byte[]) d.get("imagenDespues");
                if (imgDespues != null && imgDespues.length > 0) {
                    imvDetalleDespues.setImage(new Image(new ByteArrayInputStream(imgDespues)));
                } else {
                    imvDetalleDespues.setImage(null);
                }
            }
        } catch (Exception e) {
            mostrarError("Error al cargar detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerIdSeleccionado(ComboBox<String> cmb, ObservableList<ObservableList<String>> datos) {
        if (cmb.getValue() == null || datos == null) return null;

        String nombre = cmb.getValue();
        for (ObservableList<String> fila : datos) {
            if (fila.get(1).equals(nombre)) {
                return fila.get(0);
            }
        }
        return null;
    }

    private String obtenerString(Map<String, Object> mapa, String clave) {
        Object valor = mapa.get(clave);
        return valor != null ? valor.toString() : "";
    }

    private String obtenerString(Map<String, Object> mapa, String clave, String valorPorDefecto) {
        Object valor = mapa.get(clave);
        return valor != null ? valor.toString() : valorPorDefecto;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}