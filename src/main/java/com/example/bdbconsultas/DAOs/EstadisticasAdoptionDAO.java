package com.example.bdbconsultas.DAOs;
import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class EstadisticasAdoptionDAO {

    public ObservableList<ObservableList<String>> getAdoptionStats(
            LocalDate startDate, LocalDate endDate,
            Integer idPetType, Integer idBreed) throws SQLException {

        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();
        String sql = "{ call SP_STATS_ADOPTIONS(?, ?, ?, ?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setDate(1, Date.valueOf(startDate));
            cs.setDate(2, Date.valueOf(endDate));
            cs.setObject(3, idPetType, Types.INTEGER);
            cs.setObject(4, idBreed, Types.INTEGER);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString("adoption_status")); // [0]
                    row.add(rs.getString("total"));           // [1]
                    results.add(row);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    /** Fechas por defecto: 1-ene del año actual → hoy */
    public ObservableList<ObservableList<String>> getAdoptionStats(
            int idPetType, int idBreed) throws SQLException {
        LocalDate start = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate end   = LocalDate.now();
        return getAdoptionStats(start, end, idPetType, idBreed);
    }


    // Devuelve [id, name] por fila para poblar el ComboBox
    public ObservableList<ObservableList<String>> getPetTypes() throws SQLException, ClassNotFoundException {
        return fetchSimpleCursor("{ call SP_GET_PET_TYPES() }");
    }

    public ObservableList<ObservableList<String>> getBreeds() throws SQLException, ClassNotFoundException {
        return fetchSimpleCursor("{ call SP_GET_BREEDS() }");
    }

    private ObservableList<ObservableList<String>> fetchSimpleCursor(String sql) throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> results = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            try (ResultSet rs = cs.executeQuery()) {
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