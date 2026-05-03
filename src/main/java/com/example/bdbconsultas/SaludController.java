package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class SaludController  implements Initializable {

    public TextField nombre;
    public TextField chip;
    public ComboBox<ObservableList<String>> buscMedicina;
    public ComboBox<ObservableList<String>> buscTratamiento;
    public ComboBox<ObservableList<String>> enfermedad;
    public TableView<ObservableList<String>> tablaMascotas;
    public ComboBox<ObservableList<String>> seleccTratamiento;
    public ComboBox<ObservableList<String>> seleccMedicina;
    public TextField dosis;
    public DatePicker startDate;
    public DatePicker endDate;
    public Label lblTotal;
    private MascotasDAO mascotasDAO = MascotasDAO.getMascotasDAO();

    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<ObservableList<String>> enfermedades = MascotasDAO.getEnfermedades();
            enfermedad.setItems(enfermedades);
            enfermedad.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            ObservableList<ObservableList<String>> tratamientos = MascotasDAO.getTratamientos();
            buscTratamiento.setItems(tratamientos);
            buscTratamiento.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            seleccTratamiento.setItems(tratamientos);
            seleccTratamiento.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            ObservableList<ObservableList<String>> medicinas = MascotasDAO.getMedicamentos();
            buscMedicina.setItems(medicinas);
            buscMedicina.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            seleccMedicina.setItems(medicinas);
            seleccMedicina.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

        }catch (Exception e){
            mostrarAlerta("Error inesperado al abrir la ventana, por favor vuelva e intente más tarde");
        }
    }

//crear y cambiar por buscarMascotaSalud que reciba y busque por nombre, chip, enfermedad, medicamento y tratamiento
    public void buscar() throws SQLException, ClassNotFoundException {
        String nombreStr;
        String chipStr;
        Integer medicina;
        Integer tratamiento;
        Integer idEnfermedad;
        LocalDate fechaInicio;
        LocalDate fechaFin;
        if(!nombre.getText().isEmpty()){
            nombreStr = nombre.getText();
        }else{nombreStr = null;}

        if(!chip.getText().isEmpty()){
            chipStr = chip.getText();
        }else{chipStr = null;}

        if(buscMedicina.getSelectionModel().getSelectedItem() != null){
            medicina = Integer.valueOf(buscMedicina.getValue().getFirst());
        }else{medicina = null;}

        if(buscTratamiento.getSelectionModel().getSelectedItem() != null){
            tratamiento = Integer.valueOf(buscTratamiento.getValue().getFirst());
        }else{tratamiento = null;}

        if(enfermedad.getSelectionModel().getSelectedItem() != null){
            idEnfermedad = Integer.valueOf(enfermedad.getValue().getFirst());
        }else{idEnfermedad = null;}

        if(startDate.getValue() != null){
            fechaInicio = startDate.getValue();
        }else{ fechaInicio = null;}

        if(endDate.getValue() != null){
            fechaFin = endDate.getValue();
        }else{ fechaFin = null;}

        MascotasDAO.ResultadoConsulta mascotas = mascotasDAO.consultarSalud(nombreStr,chipStr,medicina,tratamiento,idEnfermedad,fechaInicio,fechaFin);

        tablaMascotas.getColumns().clear();
        for (int i = 0; i < mascotas.columnas.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(mascotas.columnas.get(idx));
            col.setCellValueFactory(c ->
                    new javafx.beans.property.SimpleStringProperty(c.getValue().get(idx)));
            tablaMascotas.getColumns().add(col);
        }
        tablaMascotas.setItems(mascotas.filas);

        lblTotal.setText(String.valueOf(mascotas.total));
    }

    public void asignarMedicina() throws SQLException, ClassNotFoundException {
        if(tablaMascotas.getSelectionModel().getSelectedItem() == null){
            mostrarAlerta("Por favor seleccione una mascota");
            return;
        }
        if(seleccMedicina.getValue().toString().isEmpty()){
            mostrarAlerta("Por favor seleccione una medicina");
        }
        MascotasDAO.registrarMedicamentoMascota(tablaMascotas.getSelectionModel().getSelectedItem().getFirst(),seleccMedicina.getSelectionModel().getSelectedItem().getFirst(),
                dosis.getText(), LocalDate.now(),LogInController.loggedUser);
        buscar();
    }

    public void asignarTratamiento() throws SQLException, ClassNotFoundException {
        if(tablaMascotas.getSelectionModel().getSelectedItem() == null){
            mostrarAlerta("Por favor seleccione una mascota");
            return;
        }
        if(seleccTratamiento.getValue().toString().isEmpty()){
            mostrarAlerta("Por favor seleccione un tratamiento");
        }
        MascotasDAO.registrarTratamientoMascota(tablaMascotas.getSelectionModel().getSelectedItem().getFirst(),seleccTratamiento.getSelectionModel().getSelectedItem().getFirst()
                ,LocalDate.now(),LogInController.loggedUser);
        buscar();
    }


    public void mostrarAlerta(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Problema con entradas de usuario");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
