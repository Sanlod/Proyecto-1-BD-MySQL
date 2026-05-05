package com.example.bdbconsultas;
import com.example.bdbconsultas.DAOs.EstadisticasMatchesDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.collections.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EstadisticasMatchesController implements Initializable {

    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private ComboBox<String> cbPetType;

    @FXML private TableView<ObservableList<String>>           tblStats;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;
    @FXML private TableColumn<ObservableList<String>, String> colTotal;
    @FXML private TableColumn<ObservableList<String>, String> colAvgSimilarity;
    @FXML private TableColumn<ObservableList<String>, String> colPercent;

    @FXML private PieChart pieChart;

    @FXML private Label lblTotalRecords;
    @FXML private Label lblOverallAvg;

    private final EstadisticasMatchesDAO dao = new EstadisticasMatchesDAO();
    private ObservableList<ObservableList<String>> petTypes;


    private void setupTableColumns() {
        colStatus.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(0)));
        colTotal.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(1)));
        colAvgSimilarity.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(2) + "%"));
        colPercent.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(3)));
    }

    private void loadPetTypes() {
        try {
            petTypes = dao.getPetTypes();
            ObservableList<String> names = FXCollections.observableArrayList();
            names.add("Todos");
            petTypes.forEach(r -> names.add(r.get(1)));
            cbPetType.setItems(names);
            cbPetType.getSelectionModel().selectFirst();
            loadStats();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onFilter() {
        loadStats();
    }

    private void loadStats() {
        LocalDate start = dpStartDate.getValue();
        LocalDate end   = dpEndDate.getValue();

        int typeIndex = cbPetType.getSelectionModel().getSelectedIndex();
        int idPetType = typeIndex <= 0
                ? 0
                : Integer.parseInt(petTypes.get(typeIndex - 1).get(0));

        try {
            ObservableList<ObservableList<String>> rows =
                    dao.getMatchStats(start, end, idPetType);

            int grandTotal = rows.stream()
                    .mapToInt(r -> Integer.parseInt(r.get(1)))
                    .sum();

            double overallAvg = rows.stream()
                    .mapToDouble(r -> Double.parseDouble(r.get(2)))
                    .average()
                    .orElse(0);

            lblTotalRecords.setText(String.valueOf(grandTotal));
            lblOverallAvg.setText(String.format("%.1f%%", overallAvg));

            // Agregar porcentaje como [3]
            rows.forEach(r -> {
                if (r.size() == 3) {
                    int val = Integer.parseInt(r.get(1));
                    double pct = grandTotal == 0 ? 0 : (val * 100.0 / grandTotal);
                    r.add(String.format("%.1f%%", pct));
                }
            });

            tblStats.setItems(rows);
            buildPieChart(rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buildPieChart(ObservableList<ObservableList<String>> rows) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        rows.forEach(r -> data.add(
                new PieChart.Data(r.get(0) + " (" + r.get(3) + ")", Integer.parseInt(r.get(1)))
        ));
        pieChart.setData(data);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dpStartDate.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        dpEndDate.setValue(LocalDate.now());
        setupTableColumns();
        loadPetTypes();
    }

    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}