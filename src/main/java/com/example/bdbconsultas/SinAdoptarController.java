package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.MascotasDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class SinAdoptarController implements Initializable {
    public TableView<ObservableList<String>> mascotasTable;
    public Spinner<Integer> meses;
    public ComboBox<ObservableList<String>> tipo;
    public ComboBox<ObservableList<String>> raza;
    public ComboBox<ObservableList<String>> color;
    public Label total;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
            meses.setValueFactory(valueFactory);

            ObservableList<ObservableList<String>> tipoMascota = MascotasDAO.getTiposMascotas();
            tipo.setItems(tipoMascota);
            tipo.setConverter(new StringConverter<ObservableList<String>>() {
                @Override
                public String toString(ObservableList<String> fila) {
                    return fila != null ? fila.get(1) : "";
                }

                @Override
                public ObservableList<String> fromString(String s) {
                    return null;
                }
            });

            ObservableList<ObservableList<String>> colorMascota = MascotasDAO.getColores();
            color.setItems(colorMascota);
            color.setConverter(new StringConverter<ObservableList<String>>() {
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
        catch(Exception e){
            mostrarAlerta(e.getMessage());
        }
    }

    public void actualizarRazas() throws SQLException, ClassNotFoundException {
        if(tipo.getValue() != null) {
            ObservableList<ObservableList<String>> razaMascota = MascotasDAO.getRazasPorTipo(tipo.getValue().getFirst());
            raza.setItems(razaMascota);
            raza.setConverter(new StringConverter<ObservableList<String>>() {
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
    }

    public void buscar() throws SQLException, ClassNotFoundException {
        if(meses.getValue() == null){
            mostrarAlerta("Por favor determine los meses sin adoptar");
            return;
        }
        Integer mascotaTipo;
        Integer mascotaBreed;
        Integer mascotaColor;
        if(tipo.getValue() == null) {mascotaTipo = null;}else{mascotaTipo = Integer.valueOf(tipo.getValue().getFirst());}
        if(raza.getValue() == null) {mascotaBreed = null;}else{mascotaBreed = Integer.valueOf(raza.getValue().getFirst());}
        if(color.getValue() == null) {mascotaColor = null;}else{mascotaColor = Integer.valueOf(color.getValue().getFirst());}
        MascotasDAO.ResultadoConsulta mascotas = MascotasDAO.buscarMascotasSinAdoptar(meses.getValue(),mascotaTipo, mascotaBreed, mascotaColor);

        mascotasTable.getColumns().clear();
        for (int i = 0; i < mascotas.columnas.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(mascotas.columnas.get(idx));
            col.setCellValueFactory(c ->
                    new javafx.beans.property.SimpleStringProperty(c.getValue().get(idx)));
            mascotasTable.getColumns().add(col);
        }
        mascotasTable.setItems(mascotas.filas);

        total.setText(String.valueOf(mascotas.total));
    }


    public void mostrarAlerta(String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Problema con entradas de usuario");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void switchVolver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bdbconsultas/Usuario.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

}
