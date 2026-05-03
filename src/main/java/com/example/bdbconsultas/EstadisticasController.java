package com.example.bdbconsultas;
import com.example.bdbconsultas.DAOs.EstadisticasDAO;
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
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class EstadisticasController {

    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;

    @FXML private TableView<ObservableList<String>>           tblStats;
    @FXML private TableColumn<ObservableList<String>, String> colType;
    @FXML private TableColumn<ObservableList<String>, String> colState;
    @FXML private TableColumn<ObservableList<String>, String> colTotal;
    @FXML private TableColumn<ObservableList<String>, String> colPercent;

    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis            xAxis;
    @FXML private NumberAxis              yAxis;

    @FXML private Label lblGrandTotal;

    @FXML private Label lblLostTotal, lblLostPct;
    @FXML private Label lblFoundTotal, lblFoundPct;
    @FXML private Label lblAdoptedTotal, lblAdoptedPct;
    @FXML private Label lblInAdoptionTotal, lblInAdoptionPct;

    private final EstadisticasDAO dao = new EstadisticasDAO();

    @FXML
    public void initialize() {
        dpStartDate.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        dpEndDate.setValue(LocalDate.now());
        setupTableColumns();
        loadStats();
    }

    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void setupTableColumns() {
        colType.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(0)));
        colState.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(1)));
        colTotal.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(2)));
        colPercent.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().get(3))); // [3] se añade en loadStats
    }

    @FXML
    private void onFilter() {
        loadStats();
    }

    private void loadStats() {
        LocalDate start = dpStartDate.getValue();
        LocalDate end   = dpEndDate.getValue();

        try {
            ObservableList<ObservableList<String>> rows =
                    dao.getStatsByTypeAndState(start, end);

            // Total general
            int grandTotal = rows.stream()
                    .mapToInt(r -> Integer.parseInt(r.get(2)))
                    .sum();

            Map<String, Integer> totalsByState = new LinkedHashMap<>();
            rows.forEach(r -> totalsByState.merge(r.get(1), Integer.parseInt(r.get(2)), Integer::sum));

            int lost     = totalsByState.getOrDefault("PERDIDA", 0);
            int found    = totalsByState.getOrDefault("ENCONTRADA", 0);
            int adopted  = totalsByState.getOrDefault("ADOPTADA", 0);
            int inAdopt  = totalsByState.getOrDefault("EN ADOPCION", 0);

            lblLostTotal.setText(String.valueOf(lost));
            lblLostPct.setText(String.format("%.1f%% del total", grandTotal == 0 ? 0 : lost * 100.0 / grandTotal));

            lblFoundTotal.setText(String.valueOf(found));
            lblFoundPct.setText(String.format("%.1f%% del total", grandTotal == 0 ? 0 : found * 100.0 / grandTotal));

            lblAdoptedTotal.setText(String.valueOf(adopted));
            lblAdoptedPct.setText(String.format("%.1f%% del total", grandTotal == 0 ? 0 : adopted * 100.0 / grandTotal));

            lblInAdoptionTotal.setText(String.valueOf(inAdopt));
            lblInAdoptionPct.setText(String.format("%.1f%% del total", grandTotal == 0 ? 0 : inAdopt * 100.0 / grandTotal));

            lblGrandTotal.setText("Total general: " + grandTotal);

            // Agregar porcentaje como [3] a cada fila
            rows.forEach(r -> {
                int total = Integer.parseInt(r.get(2));
                double pct = grandTotal == 0 ? 0 : (total * 100.0 / grandTotal);
                r.add(String.format("%.1f%%", pct)); // [3]
            });

            tblStats.setItems(rows);
            buildBarChart(rows);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buildBarChart(ObservableList<ObservableList<String>> rows) {
        barChart.getData().clear();

        // Una serie por estado, eje X = tipo de mascota
        Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();

        for (ObservableList<String> row : rows) {
            String typeName  = row.get(0); // pet_type_name
            String stateName = row.get(1); // state_name
            int    total     = Integer.parseInt(row.get(2));

            seriesMap.computeIfAbsent(stateName, name -> {
                XYChart.Series<String, Number> s = new XYChart.Series<>();
                s.setName(name);
                return s;
            }).getData().add(new XYChart.Data<>(typeName, total));
        }

        barChart.getData().addAll(seriesMap.values());
    }
}