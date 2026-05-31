package com.example.bdbconsultas.DAOs;
import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class EstadisticasDonacionDAO {

    public ObservableList<ObservableList<String>> getStatsByEntityAndDate(
            LocalDate startDate, LocalDate endDate) throws SQLException {

        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();

        results.addAll(fetchCursor("{ call SP_STATS_DONABYASSO(?, ?) }", startDate, endDate));
        results.addAll(fetchCursor("{ call SP_STATS_BOUNTIES_BY_RESCUER(?, ?) }", startDate, endDate));

        return results;
    }

    private ObservableList<ObservableList<String>> fetchCursor(
            String sql, LocalDate startDate, LocalDate endDate) throws SQLException {

        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setDate(1, Date.valueOf(startDate));
            cs.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("entity_name")); // [0]
                    row.add(rs.getString("entity_type")); // [1]
                    row.add(rs.getString("total"));       // [2]
                    results.add(row);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    /** Fechas por defecto: 1-ene del año actual → hoy */
    public ObservableList<ObservableList<String>> getStatsByEntityAndDate() throws SQLException {
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate end   = LocalDate.now();
        return getStatsByEntityAndDate(start, end);
    }
}