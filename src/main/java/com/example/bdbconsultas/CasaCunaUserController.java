package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.CasaCunaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class CasaCunaUserController {

    @FXML private TableView<ObservableList<String>> tblCasaCuna;
    @FXML private TableColumn<ObservableList<String>, String> colNumero;
    @FXML private TableColumn<ObservableList<String>, String> colNombreDueño;
    @FXML private TableColumn<ObservableList<String>, String> colDistrito;
    @FXML private TableColumn<ObservableList<String>, String> colTipoMascota;
    @FXML private TableColumn<ObservableList<String>, String> colTamanoMascota;
    @FXML private TableColumn<ObservableList<String>, String> colNivelEnergia;
    @FXML private TableColumn<ObservableList<String>, String> colDonaciones;

    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private ComboBox<String> cmbFiltroTamano;
    @FXML private ComboBox<String> cmbFiltroEnergia;
    @FXML private ComboBox<String> cmbFiltroDistrito;

    private FilteredList<ObservableList<String>> datosFiltrados; //Lista para datos por filtración

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();

        configurarPromptFijo(cmbFiltroTipo, "Tipo Mascota");
        configurarPromptFijo(cmbFiltroTamano, "Tamaño");
        configurarPromptFijo(cmbFiltroEnergia, "Energía");
        configurarPromptFijo(cmbFiltroDistrito, "Distrito");

        cargarDatos();
    }

    private void configurarFiltros() {
        //Esto hace que se revisen constantemente los combobox y su valor, que actualiza automaticamente la tabla segun los filtros que se aplican en aplicarFIltro
        cmbFiltroTipo.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cmbFiltroTamano.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cmbFiltroEnergia.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cmbFiltroDistrito.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        datosFiltrados.setPredicate(fila -> {
            boolean matchTipo = (cmbFiltroTipo.getValue() == null) ||
                    fila.get(3).toLowerCase().contains(cmbFiltroTipo.getValue().toLowerCase());

            boolean matchDistrito = (cmbFiltroDistrito.getValue() == null) ||
                    fila.get(2).equals(cmbFiltroDistrito.getValue());

            boolean matchTamano = (cmbFiltroTamano.getValue() == null) ||
                    fila.get(4).contains(cmbFiltroTamano.getValue());

            boolean matchEnergia = (cmbFiltroEnergia.getValue() == null) ||
                    fila.get(5).toLowerCase().contains(cmbFiltroEnergia.getValue().toLowerCase());

            return matchTipo && matchDistrito && matchTamano && matchEnergia;
        });
    }

    private void llenarCombosDesdeDAO() throws SQLException, ClassNotFoundException {
        CasaCunaDAO dao = CasaCunaDAO.getInstance();
        // Limpiar para evitar duplicados si se recarga
        cmbFiltroTipo.getItems().clear();
        cmbFiltroDistrito.getItems().clear();
        cmbFiltroEnergia.getItems().clear();

        // Tipos
        for (ObservableList<String> fila : dao.getTiposMascotas()) {
            cmbFiltroTipo.getItems().add(fila.get(1)); // Índice 1 suele ser el nombre del catálogo
        }

        // Distritos
        for (ObservableList<String> fila : dao.getDistritos()) {
            cmbFiltroDistrito.getItems().add(fila.get(1));
        }

        // Niveles de Energía
        for (ObservableList<String> fila : dao.getNivelesEnergia()) {
            cmbFiltroEnergia.getItems().add(fila.get(1));
        }

        // Tamaños son fijos
        if (cmbFiltroTamano.getItems().isEmpty()) {
            cmbFiltroTamano.getItems().addAll("Pequeño", "Mediano", "Grande");
        }
    }

    private void configurarColumnas() {
        // Acomodadas segun el SP
        colNombreDueño.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colDistrito.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colTipoMascota.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colTamanoMascota.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colNivelEnergia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        colDonaciones.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        colNumero.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
    }

    private void cargarDatos() {
        try {
            CasaCunaDAO.ResultadoCribHouse resultado = CasaCunaDAO.listarCribHouses();
            datosFiltrados = new FilteredList<>(resultado.filas, p -> true);
            llenarCombosDesdeDAO();
            tblCasaCuna.setItems(datosFiltrados);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void limpiarFiltros() {
        //se limpian los filtros cambiando su valora nulo para no filtrar
        cmbFiltroTipo.setValue(null);
        cmbFiltroTamano.setValue(null);
        cmbFiltroEnergia.setValue(null);
        cmbFiltroDistrito.setValue(null);

        tblCasaCuna.requestFocus();
    }

    //Metodo para un prompt fijo en los bnotones
    private void configurarPromptFijo(ComboBox<String> combo, String texto) {
        combo.setPromptText(texto);
        combo.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(texto); // Si está vacío o nulo, fuerza el títul0
                } else {
                    setText(item);
                }
            }
        });
    }

    @FXML
    public void switchVolver(ActionEvent event) throws IOException {
        cambiarEscena("/com/example/bdbconsultas/Usuario.fxml" , event);
    }

    public void cambiarEscena(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}