package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.PersonaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PersonaController implements Initializable {

    @FXML private TableView<ObservableList<String>> tblDatos;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colNombreCompleto;
    @FXML private TableColumn<ObservableList<String>, String> colListaNegra;
    @FXML private TableColumn<ObservableList<String>, String> colNotas;

    @FXML private TextField txtId;
    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private TextField txtCalificacion;
    @FXML private TextArea txtNotas;
    @FXML private CheckBox chkListaNegra;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnReportarListaNegra;
    @FXML private Button btnVolver;

    private final PersonaDAO dao = new PersonaDAO();
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatos();
        limpiarFormulario();
        deshabilitarBotonesAccion();
        btnGuardar.setDisable(true);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(0)));
        colNombreCompleto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get(1)));


        tblDatos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarSeleccionEnFormulario(newVal);
                habilitarBotonesAccion();
            }
        });
    }

    private void cargarDatos() {
        try {
            ObservableList<ObservableList<String>> datos = PersonaDAO.getPersonasCRUD();
            tblDatos.setItems(datos);
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarSeleccionEnFormulario(ObservableList<String> fila) {
        String idPersona = fila.get(0);
        txtId.setText(idPersona);
        txtNotas.setText(fila.get(3) != null ? fila.get(3) : "");
        chkListaNegra.setSelected("Sí".equals(fila.get(2)));

        try {
            String calificacion = PersonaDAO.obtenerCalificacionPersona(idPersona);
            txtCalificacion.setText(calificacion != null ? calificacion + " estrellas" : "Sin calificación");
        } catch (Exception e) {
            txtCalificacion.setText("Error al cargar calificación");
            e.printStackTrace();
        }
    }

    @FXML
    private void onNuevo() {
        modoEdicion = false;
        limpiarFormulario();
        txtPrimerNombre.requestFocus();
        btnGuardar.setDisable(false);
        deshabilitarBotonesAccion();
    }

    @FXML
    private void onEditar() {
        if (tblDatos.getSelectionModel().getSelectedItem() == null) {
            mostrarError("Seleccione una persona para editar");
            return;
        }
        modoEdicion = true;
        btnGuardar.setDisable(false);
    }

    @FXML
    private void onGuardar() {
        String primerNombre = txtPrimerNombre.getText().trim();
        String primerApellido = txtPrimerApellido.getText().trim();

        if (primerNombre.isEmpty() || primerApellido.isEmpty()) {
            mostrarError("Primer nombre y primer apellido son obligatorios");
            return;
        }
/// Cambié el parámetro a Integer para que pueda ser null y no se tenga que actualizar siempre
        try {
            if (modoEdicion) {
                PersonaDAO.actualizarPersona(
                        Integer.valueOf(txtId.getText()),
                        primerNombre,
                        txtSegundoNombre.getText().trim(),
                        primerApellido,
                        txtSegundoApellido.getText().trim(),
                        txtNotas.getText().trim(),
                        null,
                        null,
                        null
                );
                mostrarInfo("Persona actualizada correctamente");
            } else {
                PersonaDAO.registrarPersona(
                        primerNombre,
                        txtSegundoNombre.getText().trim(),
                        primerApellido,
                        txtSegundoApellido.getText().trim()
                );
                mostrarInfo("Persona registrada correctamente");
            }
            cargarDatos();
            limpiarFormulario();
            btnGuardar.setDisable(true);
            deshabilitarBotonesAccion();
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onEliminar() {
        ObservableList<String> seleccion = tblDatos.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una persona para eliminar");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de eliminar esta persona?");

        Optional<ButtonType> resultado = confirm.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                PersonaDAO.eliminarPersona(Integer.parseInt(seleccion.get(0)));
                cargarDatos();
                limpiarFormulario();
                btnGuardar.setDisable(true);
                deshabilitarBotonesAccion();
                mostrarInfo("Persona eliminada correctamente");
            } catch (Exception e) {
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onReportarListaNegra() {
        ObservableList<String> seleccion = tblDatos.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarError("Seleccione una persona para reportar");
            return;
        }

        String idPersona = seleccion.get(0);
        String nombrePersona = seleccion.get(1);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar reporte");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Reportar a " + nombrePersona + " en lista negra?");

        Optional<ButtonType> resultado = confirm.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                PersonaDAO.reportarListaNegra(idPersona, "SYSTEM");
                cargarDatos();
                mostrarInfo("Persona reportada en lista negra");
            } catch (Exception e) {
                mostrarError("Error al reportar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        btnGuardar.setDisable(true);
        deshabilitarBotonesAccion();
        tblDatos.getSelectionModel().clearSelection();
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtPrimerNombre.clear();
        txtSegundoNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        txtNotas.clear();
        chkListaNegra.setSelected(false);
        modoEdicion = false;
    }

    private void habilitarBotonesAccion() {
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
        btnReportarListaNegra.setDisable(false);
    }

    private void deshabilitarBotonesAccion() {
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        btnReportarListaNegra.setDisable(true);
    }

    @FXML
    private void onVolver() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
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
}