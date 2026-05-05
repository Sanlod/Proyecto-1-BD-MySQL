package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AdopcionesDAO;
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
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;

public class AdopcionesController {

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

    private ObservableList<ObservableList<String>> datosEstados;


    @FXML
    public void initialize() {
        dpDesde.setValue(LocalDate.now().withDayOfYear(1));
        dpHasta.setValue(LocalDate.now());
        btnAprobar.setVisible(true);
        btnRechazar.setVisible(true);
        cargarCombos();
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
                    null, null, "",  // fotos y notas opcionales al aprobar
                    idEstado, "SYSTEM");

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
}