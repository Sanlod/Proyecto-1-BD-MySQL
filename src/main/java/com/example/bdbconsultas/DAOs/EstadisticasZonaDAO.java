package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class EstadisticasZonaDAO {

    public ObservableList<ObservableList<String>> getStatsByLocation(
            String level, int idState) throws SQLException, ClassNotFoundException {

        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();
        String sql = "{ call SP_STATS_PETS_BY_LOCATION(?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, level);
            cs.setInt(2, idState);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("zona"));   // [0]
                    row.add(rs.getString("estado")); // [1]
                    row.add(rs.getString("total"));  // [2]
                    results.add(row);
                }
            }
        }
        return results;
    }

}
