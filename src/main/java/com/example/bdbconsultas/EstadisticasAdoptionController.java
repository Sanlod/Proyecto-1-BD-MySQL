package com.example.bdbconsultas;
import com.example.bdbconsultas.DAOs.EstadisticasAdoptionDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.collections.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class EstadisticasAdoptionController {

    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    @FXML private ComboBox<ObservableList<String>> cbPetType;
    @FXML private ComboBox<ObservableList<String>> cbBreed;

    @FXML private TableView<ObservableList<String>>           tblStats;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;
    @FXML private TableColumn<ObservableList<String>, String> colTotal;
    @FXML private TableColumn<ObservableList<String>, String> colPercent;

    @FXML private PieChart pieChart;

    @FXML private Label lblSuccessTotal;
    @FXML private Label lblSuccessPct;
    @FXML private Label lblWaitingTotal;
    @FXML private Label lblWaitingPct;
    @FXML private Label lblGrandTotal;

    private final EstadisticasAdoptionDAO dao = new EstadisticasAdoptionDAO();

    // Guarda los ids correspondientes a cada opción del ComboBox
    private ObservableList<ObservableList<String>> petTypes;
    private ObservableList<ObservableList<String>> breeds;

    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }


    @FXML
    public void initialize() {
        dpStartDate.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        dpEndDate.setValue(LocalDate.now());
        setupTableColumns();
        loadComboBoxes();
    }

    private void setupTableColumns() {
        colStatus.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(0)));
        colTotal.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(1)));
        colPercent.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(2)));
    }

    private void loadComboBoxes() {
        try {
            petTypes = dao.getPetTypes();
            cbPetType.setItems(petTypes);
            cbPetType.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            breeds = dao.getBreeds();
            cbBreed.setItems(breeds);
            cbBreed.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            loadStats();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onFilter() {
        loadStats();
    }

    private void loadStats() {

        Integer idPetType;
        if(cbPetType.getSelectionModel().getSelectedItem() == null){idPetType = null;}else{
                idPetType = Integer.valueOf(cbPetType.getSelectionModel().getSelectedItem().getFirst());
        }
        Integer idBreed;
        if(cbBreed.getSelectionModel().getSelectedItem() == null){idBreed = null;}else{
            idBreed = Integer.parseInt(cbBreed.getSelectionModel().getSelectedItem().getFirst());
        }

        LocalDate start = dpStartDate.getValue();
        LocalDate end   = dpEndDate.getValue();

        try {
            ObservableList<ObservableList<String>> rows =
                    dao.getAdoptionStats(start, end, idPetType, idBreed);

            int grandTotal = rows.stream()
                    .mapToInt(r -> Integer.parseInt(r.get(1)))
                    .sum();

            lblGrandTotal.setText(String.valueOf(grandTotal));

            // Agregar porcentaje como [2]
            rows.forEach(r -> {
                if (r.size() == 2) {
                    int val = Integer.parseInt(r.get(1));
                    double pct = grandTotal == 0 ? 0 : (val * 100.0 / grandTotal);
                    r.add(String.format("%.1f%%", pct));
                }
            });

            // Llenar tarjetas
            rows.forEach(r -> {
                if (r.get(0).equals("Exitosa")) {
                    lblSuccessTotal.setText(r.get(1));
                    lblSuccessPct.setText(r.get(2) + " del total");
                } else if (r.get(0).equals("En espera")) {
                    lblWaitingTotal.setText(r.get(1));
                    lblWaitingPct.setText(r.get(2) + " del total");
                }
            });

            tblStats.setItems(rows);
            buildPieChart(rows, grandTotal);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buildPieChart(ObservableList<ObservableList<String>> rows, int grandTotal) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        rows.forEach(r -> {
            int val = Integer.parseInt(r.get(1));
            data.add(new PieChart.Data(r.get(0), val));
        });
        pieChart.setData(data);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
    }
}