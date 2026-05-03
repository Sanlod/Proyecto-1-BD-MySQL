package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import com.example.bdbconsultas.DAOs.PersonaDAO;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReportesController implements Initializable {
    public ComboBox<ObservableList<String>> enfermedadCbx;
    public TextArea notas;
    public Spinner<Integer> calificacion;
    public TableView<ObservableList<String>> tablaMascota;
    public TableView<ObservableList<String>> tablaPersona;
    public MascotasDAO mascotasDAO = new MascotasDAO();
    public Label lblTotalMascotas;
    public Label lblTotalPersonas;


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            System.out.println("Se abrió la ventana de reportes");
            MascotasDAO.ResultadoConsulta mascotas = mascotasDAO.consultarMascotas(null, null, null, null, null, null,
                    null, null, null, null, null, null, null);
            PersonaDAO.ResultadoConsulta personas = PersonaDAO.consultarPersonas(null, null, null, null);
            ObservableList<ObservableList<String>> enfermedades = MascotasDAO.getEnfermedades();

            tablaMascota.getColumns().clear();
            for (int i = 0; i < mascotas.columnas.size(); i++) {
                final int idx = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(mascotas.columnas.get(idx));
                col.setCellValueFactory(c ->
                       new javafx.beans.property.SimpleStringProperty(c.getValue().get(idx)));
                tablaMascota.getColumns().add(col);
            }
            tablaMascota.setItems(mascotas.filas);
            lblTotalMascotas.setText(String.valueOf(mascotas.total ));

            tablaPersona.getColumns().clear();
            for (int i = 0; i < personas.columnas.size(); i++) {
                final int idx = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(personas.columnas.get(idx));
                col.setCellValueFactory(c ->
                        new javafx.beans.property.SimpleStringProperty(c.getValue().get(idx)));
                tablaPersona.getColumns().add(col);
            }
            tablaPersona.setItems(personas.filas);
            lblTotalPersonas.setText(String.valueOf(personas.total));

            enfermedadCbx.setItems(enfermedades);
            enfermedadCbx.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });


            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
            calificacion.setValueFactory(valueFactory);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void reportarPerdido() throws SQLException, ClassNotFoundException {
        try {
            if (tablaMascota.getSelectionModel().getSelectedItem() == null) {
                mostrarAlerta("Por favor seleccione una mascota");
                return;
            }
            MascotasDAO.registrarEstadoMascota(tablaMascota.getSelectionModel().getSelectedItem().getFirst(), "1", LocalDate.now(), LogInController.loggedUser);
            actualizarMascotas();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Mascota reportada como perdida satisfactoriamente");
            alert.showAndWait();
        }catch (SQLException e){
            mostrarAlerta(e.getMessage());
        }
    }


    public void reportarEnfermo() throws SQLException, ClassNotFoundException {
        try {
            if (tablaMascota.getSelectionModel().getSelectedItem() == null || enfermedadCbx.getSelectionModel().getSelectedItem() == null) {
                mostrarAlerta("Por favor seleccione una mascota y una enfermedad");
                return;
            }
            MascotasDAO.registrarEnfermedadMascota(tablaMascota.getSelectionModel().getSelectedItem().getFirst(), enfermedadCbx.getSelectionModel().getSelectedItem().getFirst(), LocalDate.now(), LogInController.loggedUser);
            actualizarMascotas();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Mascota reportada como enferma satisfactoriamente");
            alert.showAndWait();
        }catch (SQLException e){
            mostrarAlerta(e.getMessage());
        }
    }

    public void reportarPersona() throws SQLException, ClassNotFoundException {
        if(tablaPersona.getSelectionModel().getSelectedItem()==null || calificacion.getValue()==null){
            mostrarAlerta("Por favor seleccione un persona y una calificacion");
            return;
        }
        PersonaDAO.actualizarPersona(Integer.valueOf(tablaPersona.getSelectionModel().getSelectedItem().get(0)),null,null,null,null,notas.getText(), (Integer) calificacion.getValue(),LogInController.loggedUser,null);
        actualizarPersonas();
    }

    public void denunciarPersona() throws SQLException, ClassNotFoundException {
        if(tablaPersona.getSelectionModel().getSelectedItem()==null || calificacion.getValue()==null){
            mostrarAlerta("Por favor seleccione un persona y una calificacion");
            return;
        }
        PersonaDAO.actualizarPersona(Integer.valueOf(tablaPersona.getSelectionModel().getSelectedItem().get(0)),null,null,null,null,notas.getText(), (Integer) calificacion.getValue(),LogInController.loggedUser,(Integer) 1);
        actualizarPersonas();
    }

    public void mostrarAlerta(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Problema con entradas de usuario");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void actualizarMascotas() throws SQLException, ClassNotFoundException {
        MascotasDAO.ResultadoConsulta mascotas = mascotasDAO.consultarMascotas(null, null, null, null, null, null,
                null, null, null, null, null, null, null);
        tablaMascota.setItems(mascotas.filas);
        lblTotalMascotas.setText(String.valueOf(mascotas.total ));
    }

    private void actualizarPersonas() throws SQLException, ClassNotFoundException {
        PersonaDAO.ResultadoConsulta personas = PersonaDAO.consultarPersonas(null, null, null, null);
        tablaPersona.setItems(personas.filas);
        lblTotalPersonas.setText(String.valueOf(personas.total));
    }
}
