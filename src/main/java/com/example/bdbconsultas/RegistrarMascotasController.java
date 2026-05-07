package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.RecompensasDAO;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RegistrarMascotasController implements Initializable {

    public DatePicker birthDatePicker;
    @FXML private AnchorPane pagina1;
    @FXML private AnchorPane pagina2;

    // pg1
    @FXML private TextField txtIdNombre1;
    @FXML private ComboBox<String> cmbIdTipo1;
    @FXML private ComboBox<String> cmbIdColor1;
    @FXML private ComboBox<String> cmbIdRaza1;
    @FXML private TextField txtChip1;
    @FXML private ComboBox<String> cmbIdEstado1;
    @FXML private ComboBox<String> cmbIdSeveridad1;
    @FXML private ComboBox<String> cmbIdNivEnergia1;
    @FXML private ComboBox<String> cmbIdProvincia1;
    @FXML private ComboBox<String> cmbIdCanton1;
    @FXML private ComboBox<String> cmbIdDistrito1;
    @FXML private TextField txtMonto1;
    @FXML private ComboBox<String> cmbIdMoneda1;
    @FXML private TextArea txtDescripcion1;
    @FXML private TextArea txtNotasAbandono;
    @FXML private Button btnFotoAntes1;
    @FXML private Button btnFotoDesp1;
    @FXML private ImageView imvFotoAntes;
    @FXML private ImageView imvFotoDespues;

    //pg2
    @FXML private ComboBox<String> cmbTamanio;
    @FXML private CheckBox chkRequiereEspacio;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbDificultadEntrenamiento;
    @FXML private DatePicker dpFechaperdida;
    @FXML private ComboBox<String> cmbVeterinario;
    @FXML private ComboBox<String> cmbRescatista;
    @FXML private ComboBox<String> cmbAsociacion;

    @FXML private Button btnRegistrar;

    private byte[] imagenAntesBytes;
    private byte[] imagenDespuesBytes;

    private final MascotasDAO mascotasDAO = new MascotasDAO();
    private final RecompensasDAO recompensasDAO = new RecompensasDAO();

    // Datos para combos
    private ObservableList<ObservableList<String>> datosTiposActuales;
    private ObservableList<ObservableList<String>> datosRazasActuales;
    private ObservableList<ObservableList<String>> datosColoresActuales;
    private ObservableList<ObservableList<String>> datosEstadosActuales;
    private ObservableList<ObservableList<String>> datosSeveridadesActuales;
    private ObservableList<ObservableList<String>> datosNivelesEnergiaActuales;
    private ObservableList<ObservableList<String>> datosProvinciasActuales;
    private ObservableList<ObservableList<String>> datosCantonesActuales;
    private ObservableList<ObservableList<String>> datosDistritosActuales;
    private ObservableList<ObservableList<String>> datosMonedasActuales;
    private ObservableList<ObservableList<String>> datosDificultadActuales;
    private ObservableList<ObservableList<String>> datosRescatistaActuales;
    private ObservableList<ObservableList<String>> datosVeterinariosActuales;
    private ObservableList<ObservableList<String>> datosCasasCunaActuales;
    private ObservableList<ObservableList<String>> datosAsociacionesActuales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombos();
        configurarEventos();
    }

    private void cargarCombos() {
        try {
            // Tipos
            datosTiposActuales = MascotasDAO.getTiposMascotas();
            cmbIdTipo1.setItems(datosTiposActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Colores
            datosColoresActuales = MascotasDAO.getColores();
            cmbIdColor1.setItems(datosColoresActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Estados
            datosEstadosActuales = MascotasDAO.getEstados();
            cmbIdEstado1.setItems(datosEstadosActuales.stream()
                    .filter(row -> !row.get(1).equalsIgnoreCase("ADOPTADO"))
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            // Severidades
            datosSeveridadesActuales = MascotasDAO.getSeveridades();
            cmbIdSeveridad1.setItems(datosSeveridadesActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Niveles Energia
            datosNivelesEnergiaActuales = MascotasDAO.getNivEnergia();
            cmbIdNivEnergia1.setItems(datosNivelesEnergiaActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Provincias
            datosProvinciasActuales = MascotasDAO.getProvincias();
            cmbIdProvincia1.setItems(datosProvinciasActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Monedas
            datosMonedasActuales = MascotasDAO.getMonedas();
            cmbIdMoneda1.setItems(datosMonedasActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Tamaño
            ObservableList<String> tamanios = FXCollections.observableArrayList("Pequeño", "Mediano", "Grande");
            cmbTamanio.setItems(tamanios);

            // Dificultad entrenamiento
            datosDificultadActuales = MascotasDAO.getDifEntrenamiento();
            cmbDificultadEntrenamiento.setItems(datosDificultadActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Rescatistas
            datosRescatistaActuales = MascotasDAO.getRescatistas();
            cmbRescatista.setItems(datosRescatistaActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Veterinarios
            datosVeterinariosActuales = MascotasDAO.getVeterinarios();
            cmbVeterinario.setItems(datosVeterinariosActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Asociaciones
            datosAsociacionesActuales = MascotasDAO.getAsociaciones();
            cmbAsociacion.setItems(datosAsociacionesActuales.stream()
                    .map(row -> row.get(1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } catch (Exception e) {
            mostrarError("Error al cargar catálogos: " + e.getMessage());
        }
    }

    private void configurarEventos() {
        cmbIdProvincia1.setOnAction(e -> {
            try {
                String idProvincia = obtenerIdSeleccionado(cmbIdProvincia1, datosProvinciasActuales);
                datosCantonesActuales = MascotasDAO.getCantonesPorProvincia(idProvincia);
                cmbIdCanton1.setItems(datosCantonesActuales.stream()
                        .map(row -> row.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                cmbIdDistrito1.setItems(null);
            } catch (Exception ex) {
                mostrarError("Error al cargar cantones: " + ex.getMessage());
            }
        });

        cmbIdCanton1.setOnAction(e -> {
            try {
                String idCanton = obtenerIdSeleccionado(cmbIdCanton1, datosCantonesActuales);
                datosDistritosActuales = MascotasDAO.getDistritosPorCanton(idCanton);
                cmbIdDistrito1.setItems(datosDistritosActuales.stream()
                        .map(row -> row.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (Exception ex) {
                mostrarError("Error al cargar distritos: " + ex.getMessage());
            }
        });

        cmbIdTipo1.setOnAction(e -> {
            try {
                String idTipo = obtenerIdSeleccionado(cmbIdTipo1, datosTiposActuales);
                datosRazasActuales = MascotasDAO.getRazasPorTipo(idTipo);
                cmbIdRaza1.setItems(datosRazasActuales.stream()
                        .map(row -> row.get(1))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (Exception ex) {
                mostrarError("Error al cargar razas: " + ex.getMessage());
            }
        });
        cmbIdEstado1.setOnAction(e -> {
            String estadoSeleccionado = cmbIdEstado1.getValue();
            boolean esPerdida = "PERDIDA".equalsIgnoreCase(estadoSeleccionado);
            txtMonto1.setDisable(!esPerdida);
            cmbIdMoneda1.setDisable(!esPerdida);
            if (!esPerdida) {
                txtMonto1.clear();
                cmbIdMoneda1.setValue(null);
            }

            boolean bloquearRescatista = "EN ADOPCION".equalsIgnoreCase(estadoSeleccionado)
                    || "PERDIDA".equalsIgnoreCase(estadoSeleccionado);
            cmbRescatista.setDisable(bloquearRescatista);
            if (bloquearRescatista) {
                cmbRescatista.setValue(null);
            }

        });

    }

    @FXML
    private void onExaminarAntes() {
        File file = abrirFileChooser();
        if (file != null) {
            try {
                imagenAntesBytes = Files.readAllBytes(file.toPath());
                imvFotoAntes.setImage(new Image(file.toURI().toString()));
            } catch (IOException e) {
                mostrarError("Error al leer la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onExaminarDespues() {
        File file = abrirFileChooser();
        if (file != null) {
            try {
                imagenDespuesBytes = Files.readAllBytes(file.toPath());
                imvFotoDespues.setImage(new Image(file.toURI().toString()));
            } catch (IOException e) {
                mostrarError("Error al leer la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onRegistrar() {
        if(birthDatePicker.getValue() == null){
            mostrarError("Seleccione una fecha de nacimiento");
            return;}
        try {
            if (!validarCampos()) return;

            String idRaza = obtenerIdSeleccionado(cmbIdRaza1, datosRazasActuales);
            String idColor = obtenerIdSeleccionado(cmbIdColor1, datosColoresActuales);
            String idEstado = obtenerIdSeleccionado(cmbIdEstado1, datosEstadosActuales);
            String idSeveridad = obtenerIdSeleccionado(cmbIdSeveridad1, datosSeveridadesActuales);
            String idNivelEnergia = obtenerIdSeleccionado(cmbIdNivEnergia1, datosNivelesEnergiaActuales);
            String idDistrito = obtenerIdSeleccionado(cmbIdDistrito1, datosDistritosActuales);
            String idVeterinario = obtenerIdSeleccionado(cmbVeterinario, datosVeterinariosActuales);
            String idAsociacion = obtenerIdSeleccionado(cmbAsociacion, datosAsociacionesActuales);
            String idDificultad = obtenerIdSeleccionado(cmbDificultadEntrenamiento, datosDificultadActuales);
            String idRescatista = obtenerIdSeleccionado(cmbRescatista, datosRescatistaActuales);

            LocalDate lossDate = dpFechaperdida.getValue();
            LocalDate foundDate = LocalDate.now();
            LocalDate birthDate = birthDatePicker.getValue();

            int idMascota = mascotasDAO.registrarMascota(
                    txtIdNombre1.getText(), idRaza, idColor, txtChip1.getText(),
                    idEstado, idSeveridad, idNivelEnergia, idDistrito,
                    cmbTamanio.getValue(), chkRequiereEspacio.isSelected() ? 1 : 0,
                    txtTelefono.getText(), txtCorreo.getText(),
                    txtDescripcion1.getText(), txtNotasAbandono.getText(),
                    idDificultad, lossDate, foundDate,
                    idVeterinario, null, idRescatista, idAsociacion,
                    imagenAntesBytes, imagenDespuesBytes, "SYSTEM",birthDate
            );

            if (txtMonto1.getText() != null && !txtMonto1.getText().isEmpty()) {
                String idMoneda = obtenerIdSeleccionado(cmbIdMoneda1, datosMonedasActuales);
                recompensasDAO.registrarRecompensa(txtMonto1.getText(), String.valueOf(idMascota), idMoneda);
            }

            mostrarInfo("Mascota registrada correctamente");
            limpiarFormulario();

        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerIdSeleccionado(ComboBox<String> cmb, ObservableList<ObservableList<String>> datos) {
        if (cmb.getValue() == null || datos == null) return null;
        String nombre = cmb.getValue();
        for (ObservableList<String> fila : datos) {
            if (fila.get(1).equals(nombre)) return fila.get(0);
        }
        return null;
    }

    private boolean validarCampos() {
        if (txtIdNombre1.getText().isEmpty()) { mostrarError("El nombre es obligatorio"); return false; }
        if (cmbIdTipo1.getValue() == null) { mostrarError("El tipo de mascota es obligatorio"); return false; }
        if (cmbIdEstado1.getValue() == null) { mostrarError("El estado es obligatorio"); return false; }
        String montoStr = (txtMonto1 != null && txtMonto1.getText() != null) ? txtMonto1.getText().trim() : "";
        String monedaSel = (cmbIdMoneda1 != null) ? cmbIdMoneda1.getValue() : null;
        String estadoSel = (cmbIdEstado1 != null) ? cmbIdEstado1.getValue() : null;
        String rescSel = (cmbRescatista != null) ? cmbRescatista.getValue() : null;

        if (txtChip1.getText() != null && !txtChip1.getText().isEmpty()) {
            try {
                Long.parseLong(txtChip1.getText());
            } catch (NumberFormatException e) {
                mostrarError("El chip debe contener solo números");
                return false;
            }
        }

        if (!montoStr.isEmpty() || monedaSel != null) {
            if (!"PERDIDA".equalsIgnoreCase(estadoSel)) {
                mostrarError("Solo se pueden asignar recompensas si el estado es 'PERDIDA'.");
                return false;
            }
        }
        if (!montoStr.isEmpty() && monedaSel == null) {
            mostrarError("Debe seleccionar una moneda para el monto.");
            return false;
        }
        if (montoStr.isEmpty() && monedaSel != null) {
            mostrarError("Debe ingresar un monto para la moneda seleccionada.");
            return false;
        }

        if (!montoStr.isEmpty()) {
            try {
                Long.parseLong(montoStr);
            } catch (NumberFormatException e) {
                mostrarError("El monto debe contener solo números.");
                return false;
            }
        }
        if ("HALLADA".equalsIgnoreCase(estadoSel) && rescSel == null) {
            mostrarError("Debe seleccionar un rescatista para una mascota hallada.");
            return false;
        }
        if ("EN ADOPCION".equalsIgnoreCase(estadoSel) || "PERDIDA".equalsIgnoreCase(estadoSel))
            { if (rescSel != null) {
                mostrarError("Una mascota en adopción o perdida no puede tener un rescatista asignado en este formulario.");
                return false;
            }
        }
        return true;
    }


    private File abrirFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        return chooser.showOpenDialog(null);
    }

    private void limpiarFormulario() {
        txtIdNombre1.clear();
        cmbIdTipo1.setValue(null);
        cmbIdColor1.setValue(null);
        cmbIdRaza1.setValue(null);
        txtChip1.clear();
        cmbIdEstado1.setValue(null);
        cmbIdSeveridad1.setValue(null);
        cmbIdNivEnergia1.setValue(null);
        cmbIdProvincia1.setValue(null);
        cmbIdCanton1.setValue(null);
        cmbIdDistrito1.setValue(null);
        txtMonto1.clear();
        cmbIdMoneda1.setValue(null);
        txtDescripcion1.clear();
        txtNotasAbandono.clear();
        cmbTamanio.setValue(null);
        chkRequiereEspacio.setSelected(false);
        txtTelefono.clear();
        txtCorreo.clear();
        cmbDificultadEntrenamiento.setValue(null);
        dpFechaperdida.setValue(null);
        cmbVeterinario.setValue(null);
        cmbRescatista.setValue(null);
        cmbAsociacion.setValue(null);
        imvFotoAntes.setImage(null);
        imvFotoDespues.setImage(null);
        imagenAntesBytes = null;
        imagenDespuesBytes = null;
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Admin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onIrPagina2() {
        pagina1.setVisible(false);
        pagina2.setVisible(true);
    }

    @FXML
    private void onIrPagina1() {
        pagina2.setVisible(false);
        pagina1.setVisible(true);
    }
}