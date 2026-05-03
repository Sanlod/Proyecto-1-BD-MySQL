package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AssociationDAO;
import com.example.bdbconsultas.DAOs.DonacionesDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class DonarController {

    //Tomar entradas y registrar donaciones

    @FXML private ComboBox<ObservableList<String>> aso1;
    @FXML private ComboBox<ObservableList<String>> aso2;
    @FXML private ComboBox<ObservableList<String>> aso3;
    @FXML private Spinner monto;
    @FXML private ComboBox<ObservableList<String>> divisa;

    private DonacionesDAO dao = new DonacionesDAO();


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void initialize() throws Exception {
        monto.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,1));


        ObservableList<ObservableList<String>> catalogos = MascotasDAO.getAsociaciones();
        aso1.setItems(catalogos);
        aso1.setConverter(new StringConverter<ObservableList<String>>() {
            @Override
            public String toString(ObservableList<String> fila) {
                return fila != null ? fila.get(1) : "";
            }
            @Override
            public ObservableList<String> fromString(String s) {
                return null;
            }
        });
        aso2.setItems(catalogos);
        aso2.setConverter(new StringConverter<ObservableList<String>>() {
            @Override
            public String toString(ObservableList<String> fila) {
                return fila != null ? fila.get(1) : "";
            }
            @Override
            public ObservableList<String> fromString(String s) {
                return null;
            }
        });
        aso3.setItems(catalogos);
        aso3.setConverter(new StringConverter<ObservableList<String>>() {
            @Override
            public String toString(ObservableList<String> fila) {
                return fila != null ? fila.get(1) : "";
            }
            @Override
            public ObservableList<String> fromString(String s) {
                return null;
            }
        });

        ObservableList<ObservableList<String>> divisas = MascotasDAO.getMonedas();
        divisa.setItems(divisas);
        divisa.setConverter(new StringConverter<ObservableList<String>>() {
            @Override
            public String toString(ObservableList<String> fila) {
                return fila != null ? fila.get(1) : "";
            }
            @Override
            public ObservableList<String> fromString(String s) {
                return null;
            }
        });
    }

    public void donar(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(monto.getValue() == null){
            mostrarAlerta("Por favor ingrese un monto");
            return;
        }
        if(divisa.getValue() == null){
            mostrarAlerta("Por favor ingrese una divisa");
            return;
        }

        int nuMonto = (int) monto.getValue();
        int porcentaje = 0;
        int partes = 0;
        if(aso1.getValue() != null) partes += 1;
        if(aso2.getValue() != null) partes += 1;
        if(aso3.getValue() != null) partes += 1;

        if(partes == 0){
            mostrarAlerta(String.format("Por favor seleccione una asociación"));
            return;
        }

        porcentaje = 100 /partes ;

        int idCurrency = Integer.parseInt(divisa.getValue().get(0));

        String nombrePersona = LogInController.loggedUser;

        Integer idPersona = Integer.valueOf(LogInController.loggedUserId);

        if(aso1.getValue() != null) {
            dao.registrarDonacion(nuMonto/partes,porcentaje,idPersona,Integer.parseInt(aso1.getValue().get(0)),idCurrency,nombrePersona);
        }
        if(aso2.getValue() != null) {
            dao.registrarDonacion(nuMonto/partes,porcentaje,idPersona,Integer.parseInt(aso2.getValue().get(0)),idCurrency,nombrePersona);
        }
        if(aso3.getValue() != null) {
            dao.registrarDonacion(nuMonto/partes,porcentaje,idPersona,Integer.parseInt(aso3.getValue().get(0)),idCurrency,nombrePersona);
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Donación registrada correctamente, gracias por la colaboración");
        alert.showAndWait();
    }


    public void mostrarAlerta(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Problema con entradas de usuario");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }



}
