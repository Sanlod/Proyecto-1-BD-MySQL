package com.example.bdbconsultas;
import com.example.bdbconsultas.DAOs.EstadisticasZonaDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
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

import java.io.IOException;
import java.sql.SQLException;

public class EstadisticasZonaController {

    @FXML private ComboBox<String> cbLevel;
    @FXML private ComboBox<String> cbState;

    @FXML private TableView<ObservableList<String>>           tblStats;
    @FXML private TableColumn<ObservableList<String>, String> colZona;
    @FXML private TableColumn<ObservableList<String>, String> colState;
    @FXML private TableColumn<ObservableList<String>, String> colTotal;
    @FXML private TableColumn<ObservableList<String>, String> colPercent;

    @FXML private BarChart<String, Number> barChart;

    @FXML private Label lblGrandTotal;

    private final EstadisticasZonaDAO dao = new EstadisticasZonaDAO();
    private ObservableList<ObservableList<String>> states;

    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupLevelComboBox();
        loadStates();
    }

    private void setupTableColumns() {
        colZona.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(0)));
        colState.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(1)));
        colTotal.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(2)));
        colPercent.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(3)));
    }

    private void setupLevelComboBox() {
        cbLevel.setItems(FXCollections.observableArrayList(
                "Provincia", "Cantón", "Distrito"
        ));
        cbLevel.getSelectionModel().selectFirst();
    }

    private void loadStates() {
        try {
            states = MascotasDAO.getEstados();
            ObservableList<String> names = FXCollections.observableArrayList();
            names.add("Todos");
            states.forEach(r -> names.add(r.get(1)));
            cbState.setItems(names);
            cbState.getSelectionModel().selectFirst();
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
        // Nivel de agrupación
        String level;
        switch (cbLevel.getSelectionModel().getSelectedIndex()) {
            case 1  -> level = "CANTON";
            case 2  -> level = "DISTRITO";
            default -> level = "PROVINCIA";
        }

        // Estado
        int stateIndex = cbState.getSelectionModel().getSelectedIndex();
        int idState = stateIndex <= 0
                ? 0
                : Integer.parseInt(states.get(stateIndex - 1).get(0));

        try {
            ObservableList<ObservableList<String>> rows =
                    dao.getStatsByLocation(level, idState);

            int grandTotal = rows.stream()
                    .mapToInt(r -> Integer.parseInt(r.get(2)))
                    .sum();

            lblGrandTotal.setText(String.valueOf(grandTotal));

            rows.forEach(r -> {
                if (r.size() == 3) {
                    int val = Integer.parseInt(r.get(2));
                    double pct = grandTotal == 0 ? 0 : (val * 100.0 / grandTotal);
                    r.add(String.format("%.1f%%", pct));
                }
            });

            tblStats.setItems(rows);
            buildBarChart(rows);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void buildBarChart(ObservableList<ObservableList<String>> rows) {
        barChart.getData().clear();

        java.util.Map<String, XYChart.Series<String, Number>> seriesMap =
                new java.util.LinkedHashMap<>();

        for (ObservableList<String> row : rows) {
            String zona  = row.get(0);
            String state = row.get(1);
            int    total = Integer.parseInt(row.get(2));

            seriesMap.computeIfAbsent(state, name -> {
                XYChart.Series<String, Number> s = new XYChart.Series<>();
                s.setName(name);
                return s;
            }).getData().add(new XYChart.Data<>(zona, total));
        }

        barChart.getData().addAll(seriesMap.values());
    }
}
