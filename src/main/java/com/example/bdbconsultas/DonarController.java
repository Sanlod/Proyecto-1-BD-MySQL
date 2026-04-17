package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.AssociationDAO;
import com.example.bdbconsultas.DAOs.DonacionesDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;

public class DonarController {

    //Tomar entradas y registrar donaciones

    @FXML private ComboBox<ObservableList<String>> aso1;
    @FXML private ComboBox<ObservableList<String>> aso2;
    @FXML private ComboBox<ObservableList<String>> aso3;
    @FXML private Spinner monto;
    @FXML private ComboBox<ObservableList<String>> divisa;

    private DonacionesDAO dao = new DonacionesDAO();

    @FXML
    public void initialize() throws Exception {
        ObservableList<ObservableList<String>> catalogos = AssociationDAO.getAsociaciones();
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

        catalogos.clear();

        catalogos = MascotasDAO.getMonedas();
        divisa.setItems(catalogos);
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
        if(aso1.getValue() != null) porcentaje += 1;
        if(aso2.getValue() != null) porcentaje += 1;
        if(aso3.getValue() != null) porcentaje += 1;

        porcentaje = porcentaje * 100 / 3;

        int idCurrency = Integer.parseInt(divisa.getValue().get(0));

        int idPersona = 1;

        boolean ninguna = true;

        if(aso1.getValue() != null) {
            ninguna = false;
            dao.registrarDonacion(nuMonto,porcentaje,idPersona,Integer.parseInt(aso1.getValue().get(0)),idCurrency);
        }
        if(aso2.getValue() != null) {
            ninguna = false;
            dao.registrarDonacion(nuMonto,porcentaje,idPersona,Integer.parseInt(aso2.getValue().get(0)),idCurrency);
        }
        if(aso3.getValue() != null) {
            ninguna = false;
            dao.registrarDonacion(nuMonto,porcentaje,idPersona,Integer.parseInt(aso3.getValue().get(0)),idCurrency);
        }
        if(ninguna){
            mostrarAlerta("Por favor seleccione una asociacion");
        }

    }
    public void mostrarAlerta(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Problema con entradas de usuario");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }



}
