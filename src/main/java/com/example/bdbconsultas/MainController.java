package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AssociationDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MainController extends Application {



    /// RECORDAR IGUAL Y PODEMOS REUNIRNOS CON EL ASISTENTE Y PREGUNTARLE SOBRE LA INTERFAZ
    /// SI HACEN FALTA COSAS O SI ES LO QUE LA PROFE ESPERA (porque la verdad no estoy muy seguro que lo que construí aquí está bien)
    /// FALTA HACER LA PARTE DEL LOGIN Y TAL VEZ QUE EXISTA UN USUARIO ADMIN QUE PUEDA ACCEDER A CIERTA INFO IDK LO QUE DÉ TIEMPOAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA


    public TableView tableView;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("Tabla.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 932, 608);
        stage.setTitle("Ojalamemuerahoyoigualynoxdxdxd!");
        stage.setScene(scene);
        stage.show();



        TableColumn<String, String> idCol = new TableColumn<>("ID");
        //idCol.setCellValueFactory(data ->
             //   new javafx.beans.property.SimpleStringProperty(Integer.toString(data.getValue().getId())));

        TableColumn<String, String> nomCol = new TableColumn<>("NOME");
        //idCol.setCellValueFactory(data ->
              //  new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        tableView.getColumns().addAll(idCol, nomCol);
        tableView.setItems(AssociationDAO.getAssociations());

    }
    public static void main(String[] args) {
        launch();
    }
}
