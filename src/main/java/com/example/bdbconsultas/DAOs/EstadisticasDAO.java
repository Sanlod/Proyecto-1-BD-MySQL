package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class EstadisticasDAO {

    public ObservableList<ObservableList<String>> getStatsByTypeAndState(
            LocalDate startDate, LocalDate endDate) throws SQLException {

        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();
        String sql = "{ call SP_STATS_PETBYTYPESTATE(?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setDate(1, Date.valueOf(startDate));
            cs.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("pet_type_name")); // [0]
                    row.add(rs.getString("state_name"));    // [1]
                    row.add(rs.getString("total"));         // [2]
                    results.add(row);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return results;
    }


}