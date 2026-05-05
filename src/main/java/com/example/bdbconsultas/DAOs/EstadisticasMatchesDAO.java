package com.example.bdbconsultas.DAOs;
import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class EstadisticasMatchesDAO {

    public ObservableList<ObservableList<String>> getMatchStats(
            LocalDate startDate, LocalDate endDate, int idPetType) throws SQLException {

        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();
        String sql = "{ call SP_STATS_MATCHES(?, ?, ?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            if (startDate == null) cs.setNull(1, Types.DATE);
            else cs.setDate(1, Date.valueOf(startDate));

            if (endDate == null) cs.setNull(2, Types.DATE);
            else cs.setDate(2, Date.valueOf(endDate));

            cs.setInt(3, idPetType);
            cs.registerOutParameter(4, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("match_status"));   // [0]
                    row.add(rs.getString("total"));          // [1]
                    row.add(rs.getString("avg_similarity")); // [2]
                    results.add(row);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    /** Fechas por defecto: 1-ene del año actual → hoy, todos los tipos */
    public ObservableList<ObservableList<String>> getMatchStats() throws SQLException {
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate end   = LocalDate.now();
        return getMatchStats(start, end, 0);
    }

    public ObservableList<ObservableList<String>> getPetTypes() throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();
        String sql = "{ call SP_GET_PET_TYPES(?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("id"));   // [0]
                    row.add(rs.getString("name")); // [1]
                    results.add(row);
                }
            }
        }
        return results;
    }
}