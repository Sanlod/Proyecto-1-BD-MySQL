package com.example.bdbconsultas;
import javafx.collections.*;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.bdbconsultas.DAOs.DonacionesDAO;

public class DonacionesController {

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private ComboBox<ObservableList<String>> cbDonador;
    @FXML private ComboBox<ObservableList<String>> cbAsociacion;
    @FXML private Label lblResultados;
    @FXML private TableView<ObservableList<String>> tablaDonaciones;

    private final DonacionesDAO dao = new DonacionesDAO();

    @FXML
    public void initialize() {
        dpDesde.setValue(LocalDate.now().withDayOfYear(1));
        dpHasta.setValue(LocalDate.now());

        cargarPersonas();
        cargarAsociaciones();

    }


    private void cargarPersonas() {
        try {
            ObservableList<ObservableList<String>> items =
                    FXCollections.observableArrayList();

            // Opción "Todos" con ID vacío
            items.add(FXCollections.observableArrayList("", "Todos"));
            items.addAll(DonacionesDAO.getPersonas());

            cbDonador.setItems(items);
            cbDonador.getSelectionModel().selectFirst();

            // Mostrar solo la columna de nombre (índice 1)
            cbDonador.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }
                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarAsociaciones() {
        try {
            ObservableList<ObservableList<String>> items =
                     FXCollections.observableArrayList();

            // Opción "Todos" con ID vacío
            items.add(FXCollections.observableArrayList("", "Todos"));
            items.addAll(DonacionesDAO.getAsociaciones());

            cbAsociacion.setItems(items);
            cbAsociacion.getSelectionModel().selectFirst();

            // Mostrar solo la columna de nombre (índice 1)
            cbAsociacion.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }
                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }




    @FXML
    public void onBuscar() {
        try {LocalDate desde = dpDesde.getValue();
            LocalDate hasta = dpHasta.getValue();

            // índice 0 = id; si es la opción "Todos" el id es ""
            String idPersona    = cbDonador.getValue().get(0);
            String idAsociacion = cbAsociacion.getValue().get(0);

            DonacionesDAO.ResultadoConsulta resultado =
                    dao.consultarDonaciones(desde, hasta, idPersona, idAsociacion);

            // Reconstruir columnas
            tablaDonaciones.getColumns().clear();
            tablaDonaciones.getItems().clear();

            for (int i = 0; i < resultado.columnas.size(); i++) {
                final int colIndex = i;
                TableColumn<ObservableList<String>, String> col =
                        new TableColumn<>(resultado.columnas.get(i));
                col.setCellValueFactory(data ->
                        new javafx.beans.property.SimpleStringProperty(
                                data.getValue().get(colIndex)));
                col.setPrefWidth(150);
                tablaDonaciones.getColumns().add(col);
            }

            tablaDonaciones.setItems(resultado.filas);
            lblResultados.setText(Integer.toString(resultado.total));

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Error al consultar: " + e.getMessage()).showAndWait();
        }
    }

}