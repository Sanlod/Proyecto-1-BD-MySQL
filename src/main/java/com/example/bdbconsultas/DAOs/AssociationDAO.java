package com.example.bdbconsultas.DAOs;
import java.sql.*;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.*;

public class AssociationDAO {
    public static ObservableList<String> getAssociations(){
        ObservableList<String> associations = FXCollections.observableArrayList();
        try{
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Pet";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                associations.add(
                        rs.getInt("ID"),
                        rs.getString("NAME")
                );
            }
            conn.close();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return associations;
    }
    public static ObservableList<ObservableList<String>> listadosCatalogo(String nomSP)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL " + nomSP + " (?) }")) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                int numCols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= numCols; i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

}
