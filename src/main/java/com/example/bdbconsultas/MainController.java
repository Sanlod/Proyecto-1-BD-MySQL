package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AssociationDAO;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.Types;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;


public class MainController extends Application {



    /// RECORDAR IGUAL Y PODEMOS REUNIRNOS CON EL ASISTENTE Y PREGUNTARLE SOBRE LA INTERFAZ
    /// SI HACEN FALTA COSAS O SI ES LO QUE LA PROFE ESPERA (porque la verdad no estoy muy seguro que lo que construí aquí está bien)
    /// Falta poder registrar personas (cassas cuna y esas cosas)   :(
    ///


    public TableView<ObservableList<String>> tabla;
    public TableColumn<ObservableList<String>, String> id;
    public TableColumn<ObservableList<String>, String> name;
    public TableColumn<ObservableList<String>, String> state;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 932, 608);
        stage.setTitle("Ventana");
        stage.setScene(scene);
        stage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}
