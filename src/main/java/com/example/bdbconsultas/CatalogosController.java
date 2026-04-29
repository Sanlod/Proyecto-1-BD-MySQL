package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AdopcionesDAO;
import com.example.bdbconsultas.DAOs.AssociationDAO;
import com.example.bdbconsultas.DAOs.CatalogoDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CatalogosController implements Initializable {

    @FXML private ComboBox<String> cmbEntidad;
    @FXML private TableView<ObservableList<String>> tblDatos;
    @FXML private TableColumn<ObservableList<String>, String> colC1;
    @FXML private TableColumn<ObservableList<String>, String> colC2;
    @FXML private Spinner<Integer> spnIdEditar;
    @FXML private TextField txtNuevoValor;
    @FXML private TextField txtValorAgregar;

    @FXML private ComboBox<String> cmbFiltroEspecial;
    @FXML private TextField txtDescripcion;

    @FXML private Button btnEditar;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    private boolean esAdmin = false;

    private ObservableList<ObservableList<String>> datosFiltroEspecial;

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
        configurarAcceso();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSpinner();
        cargarEntidades();
        configurarTabla();
        cmbFiltroEspecial.setVisible(false);
        txtDescripcion.setVisible(false);
    }

    private void configurarAcceso() {
        btnEditar.setDisable(!esAdmin);
        btnAgregar.setDisable(!esAdmin);
        spnIdEditar.setDisable(!esAdmin);
        txtNuevoValor.setDisable(!esAdmin);
        txtValorAgregar.setDisable(!esAdmin);
        cmbFiltroEspecial.setDisable(!esAdmin);
        txtDescripcion.setDisable(!esAdmin);
    }

    private void configurarSpinner() {
        spnIdEditar.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));
        spnIdEditar.setEditable(true);
    }

    private void cargarEntidades() {
        cmbEntidad.setItems(FXCollections.observableArrayList(
                "Asociación", "Color", "Raza", "Tipo Mascota", "Estado",
                "Severidad", "Nivel Energía", "Dificultad Entrenamiento",
                "Moneda", "Enfermedad", "Tratamiento", "Medicamento",
                "Provincia", "Cantón", "Distrito", "Pregunta"
        ));
    }

    private void configurarTabla() {
        colC1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(0)));
        colC2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(1)));
        tblDatos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                spnIdEditar.getValueFactory().setValue(
                        Integer.parseInt(sel.get(0)));
                txtNuevoValor.setText(sel.get(1));
            }
        });
    }

    @FXML
    private void onEntidadSeleccionada() {
        String entidad = cmbEntidad.getValue();
        if (entidad == null) return;

        cmbFiltroEspecial.setVisible(false);
        txtDescripcion.setVisible(false);
        datosFiltroEspecial = null;

        try {
            switch (entidad) {
                case "Raza":
                    cmbFiltroEspecial.setVisible(true);
                    cmbFiltroEspecial.setPromptText("Tipo de mascota");
                    datosFiltroEspecial = MascotasDAO.getTiposMascotas();
                    cmbFiltroEspecial.setOnAction(e -> {
                        int idx = cmbFiltroEspecial.getSelectionModel().getSelectedIndex();
                        if (idx >= 0) {
                            try {
                                String idTipo = datosFiltroEspecial.get(idx).get(0);
                                tblDatos.setItems(MascotasDAO.getRazasPorTipo(idTipo));
                            } catch (Exception ex) {
                                mostrarError("Error al filtrar razas: " + ex.getMessage());
                            }
                        } else {
                            try {
                                tblDatos.setItems(MascotasDAO.getRazasTodas());
                            } catch (Exception ex) {
                                mostrarError(ex.getMessage());
                            }
                        }
                    });
                    break;
                case "Enfermedad":
                    txtDescripcion.setVisible(true);
                    txtDescripcion.setPromptText("Descripción");
                    break;
                case "Cantón":
                    cmbFiltroEspecial.setVisible(true);
                    cmbFiltroEspecial.setPromptText("Provincia");
                    datosFiltroEspecial = MascotasDAO.getProvincias();
                    break;
                case "Distrito":
                    cmbFiltroEspecial.setVisible(true);
                    cmbFiltroEspecial.setPromptText("Cantón");
                    datosFiltroEspecial = MascotasDAO.getCantonesPorProvincia("");
                    break;
                case "Pregunta":
                    txtDescripcion.setVisible(true);
                    txtDescripcion.setPromptText("Tipo de respuesta (TEXT/YESNO/NUMBER)");
                    break;
            }

            if (datosFiltroEspecial != null) {
                ObservableList<String> nombres = FXCollections.observableArrayList();
                for (ObservableList<String> fila : datosFiltroEspecial) {
                    nombres.add(fila.get(1));
                }
                cmbFiltroEspecial.setItems(nombres);
            }

        } catch (Exception e) {
            mostrarError("Error al cargar filtros: " + e.getMessage());
        }

        cargarDatos(entidad);
    }

    @FXML
    private void onEliminar() {
        if (!esAdmin) { mostrarError("Sin permisos de administrador."); return; }

        String entidad = cmbEntidad.getValue();
        if (!validarCampo(entidad, "Entidad")) return;

        try {
            CatalogoDAO.eliminarCatalogo(obtenerTabla(entidad), spnIdEditar.getValue());
            cargarDatos(entidad);
            limpiarCamposEdicion();
            mostrarInfo("Registro eliminado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al eliminar: " + e.getMessage());
        }
    }

    private void cargarDatos(String entidad) {
        try {
            ObservableList<ObservableList<String>> datos = null;
            switch (entidad) {
                case "Asociación":              datos = AssociationDAO.getAsociaciones(); break;
                case "Color":                   datos = MascotasDAO.getColores(); break;
                case "Raza":                    datos = MascotasDAO.getRazasTodas(); break;
                case "Tipo Mascota":            datos = MascotasDAO.getTiposMascotas(); break;
                case "Estado":                  datos = MascotasDAO.getEstados(); break;
                case "Severidad":               datos = MascotasDAO.getSeveridades(); break;
                case "Nivel Energía":           datos = MascotasDAO.getNivEnergia(); break;
                case "Dificultad Entrenamiento":datos = MascotasDAO.getDifEntrenamiento(); break;
                case "Moneda":                  datos = MascotasDAO.getMonedas(); break;
                case "Enfermedad":              datos = MascotasDAO.getEnfermedades(); break;
                case "Tratamiento":             datos = MascotasDAO.getTratamientos(); break;
                case "Medicamento":             datos = MascotasDAO.getMedicamentos(); break;
                case "Provincia":               datos = MascotasDAO.getProvincias(); break;
                case "Cantón":                  datos = MascotasDAO.getCantonesPorProvincia(""); break;
                case "Distrito":                datos = MascotasDAO.getDistritos(); break;
                case "Pregunta":                datos = AdopcionesDAO.getPreguntas(); break;
            }
            if (datos != null) tblDatos.setItems(datos);
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    @FXML
    private void onEditar() {
        if (!esAdmin) { mostrarError("Sin permisos de administrador."); return; }

        String entidad = cmbEntidad.getValue();
        String nuevoValor = txtNuevoValor.getText().trim();
        Integer id = spnIdEditar.getValue();

        if (!validarCampo(entidad, "Entidad")) return;
        if (!validarCampo(nuevoValor, "Nuevo valor")) return;

        try {
            if (entidad.equals("Enfermedad")) {
                String descripcion = txtDescripcion.getText().trim();
                if (!validarCampo(descripcion, "Descripción")) return;
                CatalogoDAO.editarEnfermedad(id, nuevoValor, descripcion);
            } else if (entidad.equals("Pregunta")) {
                String tipo = txtDescripcion.getText().trim();
                if (!validarCampo(tipo, "Tipo de respuesta")) return;
                CatalogoDAO.editarPregunta(id, nuevoValor, tipo, "SYSTEM");
            } else {
                CatalogoDAO.editarCatalogo(obtenerTabla(entidad), id, nuevoValor);
            }
            cargarDatos(entidad);
            limpiarCamposEdicion();
            mostrarInfo("Registro actualizado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al editar: " + e.getMessage());
        }
    }

    @FXML
    private void onAgregar() {
        if (!esAdmin) { mostrarError("Sin permisos de administrador."); return; }

        String entidad = cmbEntidad.getValue();
        String valor = txtValorAgregar.getText().trim();

        if (!validarCampo(entidad, "Entidad")) return;
        if (!validarCampo(valor, "Valor a agregar")) return;

        try {
            switch (entidad) {
                case "Raza": {
                    int idxTipo = cmbFiltroEspecial.getSelectionModel().getSelectedIndex();
                    if (idxTipo < 0) { mostrarError("Seleccione el tipo de mascota."); return; }
                    int idTipo = Integer.parseInt(datosFiltroEspecial.get(idxTipo).get(0));
                    if (CatalogoDAO.existeRaza(valor, idTipo)) { mostrarError("Raza ya existe."); return; }
                    CatalogoDAO.agregarRaza(valor, idTipo);
                    break;
                }
                case "Enfermedad": {
                    String desc = txtDescripcion.getText().trim();
                    if (!validarCampo(desc, "Descripción")) return;
                    if (CatalogoDAO.existeRegistro("Disease", valor)) { mostrarError("Enfermedad ya existe."); return; }
                    CatalogoDAO.agregarEnfermedad(valor, desc);
                    break;
                }
                case "Cantón": {
                    int idxProv = cmbFiltroEspecial.getSelectionModel().getSelectedIndex();
                    if (idxProv < 0) { mostrarError("Seleccione la provincia."); return; }
                    int idProv = Integer.parseInt(datosFiltroEspecial.get(idxProv).get(0));
                    CatalogoDAO.agregarCanton(valor, idProv);
                    break;
                }
                case "Distrito": {
                    int idxCan = cmbFiltroEspecial.getSelectionModel().getSelectedIndex();
                    if (idxCan < 0) { mostrarError("Seleccione el cantón."); return; }
                    int idCan = Integer.parseInt(datosFiltroEspecial.get(idxCan).get(0));
                    CatalogoDAO.agregarDistrito(valor, idCan);
                    break;
                }
                case "Pregunta": {
                    if (CatalogoDAO.existeRegistro("Question", valor)) {
                        mostrarError("Pregunta ya existe."); return;
                    }
                    String tipo = txtDescripcion.getText().trim();
                    if (!validarCampo(tipo, "Tipo de respuesta")) return;
                    CatalogoDAO.agregarPregunta(valor, tipo, "SYSTEM");
                    break;
                }
                default: {
                    String tabla = obtenerTabla(entidad);
                    if (CatalogoDAO.existeRegistro(tabla, valor)) { mostrarError("Ya existe ese registro."); return; }
                    CatalogoDAO.agregarCatalogo(tabla, valor);
                }
            }
            cargarDatos(entidad);
            txtValorAgregar.clear();
            mostrarInfo("Registro agregado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al agregar: " + e.getMessage());
        }
    }

    private String obtenerTabla(String entidad) {
        switch (entidad) {
            case "Asociación":               return "Association";
            case "Color":                    return "Colour";
            case "Raza":                     return "Breed";
            case "Tipo Mascota":             return "PetType";
            case "Estado":                   return "State";
            case "Severidad":                return "Severity";
            case "Nivel Energía":            return "EnergyLevel";
            case "Dificultad Entrenamiento": return "TrainingDifficulty";
            case "Moneda":                   return "Currency";
            case "Enfermedad":               return "Disease";
            case "Tratamiento":              return "Treatment";
            case "Medicamento":              return "Medication";
            case "Provincia":                return "Province";
            case "Cantón":                   return "Canton";
            case "Distrito":                 return "District";
            case "Pregunta":                 return "Question";
            default:                         return null;
        }
    }

    private boolean validarCampo(String valor, String nombre) {
        if (valor == null || valor.trim().isEmpty()) {
            mostrarError("El campo '" + nombre + "' no puede estar vacío.");
            return false;
        }
        return true;
    }

    private void limpiarCamposEdicion() {
        spnIdEditar.getValueFactory().setValue(1);
        txtNuevoValor.clear();
        txtDescripcion.clear();
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

    @FXML
    private void onVolver() {
        ((Stage) btnVolver.getScene().getWindow()).close();
    }
}