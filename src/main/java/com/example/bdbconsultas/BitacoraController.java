package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.BitacoraDAO;
import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BitacoraController implements Initializable {
    public ComboBox<String> usuario;
    public ComboBox<String> tablacbx;
    public DatePicker fecha;
    public TableView<ObservableList<String>> tabla;


    public void buscar() throws SQLException, ClassNotFoundException {
        String usuarioEntrada;
        if(usuario.getSelectionModel().getSelectedItem() == null){
            usuarioEntrada = null;
        }else{usuarioEntrada = usuario.getSelectionModel().getSelectedItem().toString();}

        String tablaEntrada;
        if(tablacbx.getSelectionModel().getSelectedItem() == null){tablaEntrada = null;}
        else{tablaEntrada = tablacbx.getSelectionModel().getSelectedItem().toString();}

        LocalDate fechaEntrada;
        if(fecha == null){fechaEntrada = null;}
        else{fechaEntrada = fecha.valueProperty().get();}

        BitacoraDAO.ResultadoConsulta bitacora =BitacoraDAO.consultarBitacora(usuarioEntrada,tablaEntrada,fechaEntrada);

        tabla.getColumns().clear();
        for (int i = 0; i < bitacora.columnas.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(bitacora.columnas.get(idx));
            col.setCellValueFactory(c ->
                    new javafx.beans.property.SimpleStringProperty(c.getValue().get(idx)));
            tabla.getColumns().add(col);
        }
        tabla.setItems(bitacora.filas);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<ObservableList<String>> usuarios = BitacoraDAO.getUsuarios();
            ObservableList<String> names = FXCollections.observableArrayList();
            usuarios.forEach(r -> names.add(r.get(0)));
            usuario.setItems(names);

            ObservableList<String> tablasBitacora = FXCollections.observableArrayList(
                        "AdoptionRequest",
                        "AssociationXDonation",
                        "Person",
                        "Bounty",
                        "CribHouse",
                        "Donation",
                        "Match",
                        "Pet",
                        "PetMedication");

            tablacbx.setItems(tablasBitacora);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/bdbconsultas/Admin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
