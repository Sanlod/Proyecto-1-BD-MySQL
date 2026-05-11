package com.example.bdbconsultas;
import com.example.bdbconsultas.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.*;

public class TablaController {

    public TableView<ObservableList<String>> tabla;
    public TableColumn<ObservableList<String>, String> id;
    public TableColumn<ObservableList<String>, String> name;
    public TableColumn<ObservableList<String>, String> state;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {

        id.setCellValueFactory(lista ->
                new SimpleStringProperty(lista.getValue().get(0)));

        name.setCellValueFactory(lista ->
                new SimpleStringProperty(lista.getValue().get(1)));

        state.setCellValueFactory(lista ->
                new SimpleStringProperty(lista.getValue().get(2)));


        Connection conn = DBConnection.getConnection();
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        CallableStatement cs = conn.prepareCall("{call listar_pets(?)}");
        cs.registerOutParameter(1, Types.REF_CURSOR);

        cs.execute();

        ResultSet rs = (ResultSet) cs.getObject(1);
        if (!rs.isBeforeFirst()) {
        }



        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();

            row.add(rs.getString("id"));
            row.add(rs.getString("name"));
            row.add(rs.getString("state"));

            data.add(row);
        }
        tabla.setItems(data);
    }
}
