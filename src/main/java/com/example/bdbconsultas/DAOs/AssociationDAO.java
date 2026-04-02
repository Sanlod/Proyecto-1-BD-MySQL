package com.example.bdbconsultas.DAOs;
import java.sql.*;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.*;

public class AssociationDAO {
    public static ObservableList<String> getAssociations(){
        ObservableList<String> associations = FXCollections.observableArrayList();
        try{
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM association";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                associations.add(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE")
                );
            }
            conn.close();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return associations;
    }

}
