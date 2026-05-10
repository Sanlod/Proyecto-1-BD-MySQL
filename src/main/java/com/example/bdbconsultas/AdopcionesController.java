package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AdopcionesDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.PersonaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdopcionesController {

    @FXML private ImageView imgNuevaVida;
    @FXML private ImageView imgFoto;
    public StackPane stackPaneSolicitudes;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private ComboBox<ObservableList<String>> cbMascota;
    @FXML private ComboBox<ObservableList<String>> cbAdoptante;
    @FXML private Label lblResultados;
    @FXML private TableView<ObservableList<String>> tablaAdopciones;
    @FXML private TableView<ObservableList<String>> tablaSolicitudes;
    @FXML private Button btnAprobar;
    @FXML private Button btnRechazar;
    @FXML private Button btnVolver;
    @FXML private StackPane licitudes;
    @FXML private AnchorPane panelDetalleSolicitud;
    @FXML private Label lblIdSolicitud, lblMascota, lblAdoptante, lblEstado;
    @FXML private TextArea txtNotasSolicitud;
    @FXML private VBox vboxPreguntasRespuestas;
    @FXML private Button btnAprobarDetalle, btnRechazarDetalle;
    byte[] foto;
    byte[] fotoNew;

    private ObservableList<ObservableList<String>> datosEstados;


    @FXML
    public void initialize() {
        configurarEventos();
        dpDesde.setValue(LocalDate.now().withDayOfYear(1));
        dpHasta.setValue(LocalDate.now());
        btnAprobar.setVisible(true);
        btnRechazar.setVisible(true);
        cargarCombos();

        tablaSolicitudes.getSelectionModel().selectedItemProperty().addListener((obs, old, newSelection) -> {
            if (newSelection != null) {
                mostrarDetalleSolicitud(newSelection);
            }
        });

        tablaSolicitudes.setVisible(true);
        panelDetalleSolicitud.setVisible(false);
    }

    private void cargarCombos() {
        try {
            // Estados
            datosEstados = AdopcionesDAO.getEstadosSolicitud();

            // Mascotas
            ObservableList<ObservableList<String>> mascotas = FXCollections.observableArrayList();
            mascotas.add(FXCollections.observableArrayList("0", "Todos"));
            mascotas.addAll(AdopcionesDAO.getMascotas());
            cbMascota.setItems(mascotas);
            cbMascota.getSelectionModel().selectFirst();
            cbMascota.setConverter(converter());

            // Adoptantes
            ObservableList<ObservableList<String>> adoptantes = FXCollections.observableArrayList();
            adoptantes.add(FXCollections.observableArrayList("0", "Todos"));
            adoptantes.addAll(AdopcionesDAO.getAdoptantes());
            cbAdoptante.setItems(adoptantes);
            cbAdoptante.getSelectionModel().selectFirst();
            cbAdoptante.setConverter(converter());

            // Listener lista negra
            cbAdoptante.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
                if (nuevo != null && !nuevo.get(0).equals("0")) {
                    verificarListaNegra(nuevo.get(0));
                }
            });

        } catch (Exception e) {
            mostrarAlerta("Error al cargar filtros", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void verificarListaNegra(String idPersona) {
        try {
            String calificacion = PersonaDAO.obtenerCalificacionPersona(idPersona);
            ObservableList<ObservableList<String>> listaNegra = PersonaDAO.getPersonasListaNegra();
            boolean enListaNegra = listaNegra.stream()
                    .anyMatch(p -> p.get(0).equals(idPersona));

            if (enListaNegra) {
                mostrarAlerta("Lista Negra",
                        "Esta persona está en lista negra." +
                                (calificacion != null ? " Calificación: " + calificacion : ""),
                        Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void onBuscar() {
        try {
            String idM = cbMascota.getValue().get(0);
            String idA = cbAdoptante.getValue().get(0);

            AdopcionesDAO.ResultadoConsulta solicitudes = AdopcionesDAO.consultarSolicitudes(
                    dpDesde.getValue(), dpHasta.getValue(), idM, idA);

            configurarColumnas(solicitudes.columnas);
            tablaSolicitudes.setItems(solicitudes.filas);

            AdopcionesDAO.ResultadoConsulta adopciones = AdopcionesDAO.consultarAdopciones(
                    dpDesde.getValue(), dpHasta.getValue(), idM, idA);

            configurarColumnasAdop(adopciones.columnas);
            tablaAdopciones.setItems(adopciones.filas);

            lblResultados.setText("Total solicitudes: " + solicitudes.total +
                    " | Total adopciones: " + adopciones.total);

        } catch (Exception e) {
            mostrarAlerta("Error de consulta", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    public void onAprobar() {
        mostrarAlerta("Seleccionar foto", "Selecciona la foto del adoptante con su mascota",
                Alert.AlertType.INFORMATION);
        foto = seleccionarImagen();

        mostrarAlerta("Seleccionar foto", "Selecciona la foto de la nueva vida de la mascota",
                Alert.AlertType.INFORMATION);
        fotoNew = seleccionarImagen();

        gestionarSolicitud("APROBADA");
    }

    @FXML
    public void onRechazar() {
        gestionarSolicitud("RECHAZADA");
    }

    private void gestionarSolicitud(String nombreEstado) {
        ObservableList<String> seleccion = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Atención", "Seleccione una solicitud.", Alert.AlertType.WARNING);
            return;
        }
        try {
            String idEstado = datosEstados.stream()
                    .filter(e -> e.get(1).equalsIgnoreCase(nombreEstado))
                    .findFirst()
                    .map(e -> e.get(0))
                    .orElse(null);

            if (idEstado == null) {
                mostrarAlerta("Error", "Estado no encontrado.", Alert.AlertType.ERROR);
                return;
            }

            // idSolicitud=0, idPet=1 o viene del SP, idPerson=2
            // La tabla ya muestra estos datos desde SP_CONSULTAR_SOLICITUDES
            String idSolicitud = seleccion.get(0);
            String idPet       = seleccion.get(1); // ajustar índice según SP
            String idPerson    = seleccion.get(2); // ajustar índice según SP


            boolean ok = AdopcionesDAO.actualizarEstadoSolicitud(
                    idSolicitud, idPet, idPerson,
                    foto, "",   // foto, notas
                    idEstado, LogInController.loggedUser, fotoNew);

            if (ok) {
                mostrarAlerta("Éxito", "Estado actualizado.", Alert.AlertType.INFORMATION);
                onBuscar();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarColumnas(java.util.List<String> columnas) {
        tablaSolicitudes.getColumns().clear();
        for (int i = 0; i < columnas.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnas.get(i));
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get(idx)));
            tablaSolicitudes.getColumns().add(col);
        }
    }

    private void configurarColumnasAdop(java.util.List<String> columnas) {
        tablaAdopciones.getColumns().clear();
        for (int i = 0; i < columnas.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnas.get(i));
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get(idx)));
            tablaAdopciones.getColumns().add(col);
        }
    }

    private StringConverter<ObservableList<String>> converter() {
        return new StringConverter<>() {
            @Override public String toString(ObservableList<String> f) {
                return f != null ? f.get(1) : "";
            }
            @Override public ObservableList<String> fromString(String s) { return null; }
        };
    }

    private void mostrarDetalleSolicitud(ObservableList<String> fila) {
        // Ocultar la tabla y mostrar el panel de detalle
        tablaSolicitudes.setVisible(false);
        panelDetalleSolicitud.setVisible(true);

        // Llenar los datos básicos desde la fila de la tabla
        // Asume columnas: id, idPet, idPerson, nombreMascota, nombreAdoptante, fecha, estado
        lblIdSolicitud.setText(fila.get(0));
        lblMascota.setText(fila.get(3));
        lblAdoptante.setText(fila.get(4));
        lblEstado.setText(fila.get(6));

        // Cargar notas y preguntas/respuestas desde la BD
        cargarDetallesCompletos(fila.get(0));
    }

    private void cargarDetallesCompletos(String idSolicitud) {
        try {
            Map<String, Object> detalles = AdopcionesDAO.obtenerDetalleSolicitud(idSolicitud);

            List<Map<String, String>> preguntasRespuestas = (List<Map<String, String>>) detalles.get("preguntas");
            vboxPreguntasRespuestas.getChildren().clear();
            for (Map<String, String> pr : preguntasRespuestas) {
                Label lblPregunta = new Label("P: " + pr.get("question_text"));
                Label lblRespuesta = new Label("R: " + pr.get("answer_value"));
                vboxPreguntasRespuestas.getChildren().addAll(lblPregunta, lblRespuesta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el detalle: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onVolverALista() {
        // Regresar a la tabla
        panelDetalleSolicitud.setVisible(false);
        tablaSolicitudes.setVisible(true);
    }


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Admin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private byte[] seleccionarImagen() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
            );
            File archivo = fileChooser.showOpenDialog(null);
            if (archivo != null) {
                byte[] bytes = Files.readAllBytes(archivo.toPath());
                mostrarAlerta("Éxito", "Imagen cargada", Alert.AlertType.INFORMATION);
                return bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error cargando imagen, no se seleccionará ninguna", Alert.AlertType.ERROR);
        }
        return null;
    }

    public void verFotos() throws SQLException {
        try {
            System.out.println("El id del adoption es " + tablaAdopciones.getSelectionModel().getSelectedItem().get(0));
            byte[] imagental = AdopcionesDAO.obtenerImagenMascota(Integer.valueOf(tablaAdopciones.getSelectionModel().getSelectedItem().get(0)),true);

            if(imagental != null && imagental.length > 0) {
                imgFoto.setImage(new Image(new ByteArrayInputStream(imagental)));
            }

            imagental = AdopcionesDAO.obtenerImagenMascota(Integer.valueOf(tablaAdopciones.getSelectionModel().getSelectedItem().get(0)),false);

            if(imagental != null && imagental.length > 0) {
                imgNuevaVida.setImage(new Image(new ByteArrayInputStream(imagental)));
            }else{
                System.out.println("la imagen me llegó nul manin");}
        }catch(Exception e){
            mostrarAlerta("Error pepe", "Seleccione una adopción", Alert.AlertType.ERROR);
        }
    }

    private void configurarEventos() {
        btnAprobar.setOnAction(e -> {
            if (validarSolicitudParaGestion()) {
                onAprobar();
            }
        });

        btnRechazar.setOnAction(e -> {
            if (validarSolicitudParaGestion()) {
                onRechazar();
            }
        });
    }


    private boolean validarSolicitudParaGestion() {
        ObservableList<String> seleccion = tablaSolicitudes.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            mostrarAlerta("Atención", "Seleccione una solicitud de la tabla.", Alert.AlertType.WARNING);
            return false;
        }

        String estadoActual = seleccion.get(6);

        if ("APROBADA".equalsIgnoreCase(estadoActual)) {
            mostrarAlerta("Error", "Esta solicitud ya ha sido aprobada y no puede modificarse.", Alert.AlertType.ERROR);
            return false;
        }

        if ("RECHAZADA".equalsIgnoreCase(estadoActual)) {
            mostrarAlerta("Error", "Esta solicitud ya ha sido rechazada anteriormente.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }



}