package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.SeguridadDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import java.util.Base64;

import java.io.IOException;

public class RegistrarController {
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtSecondName;
    @FXML
    private TextField txtFirstSurname;
    @FXML
    private TextField txtSecondSurname;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnVolver;

    private final SeguridadDAO seguridadDAO = new SeguridadDAO();

    @FXML
    public void onRegistrar() {
        btnRegistrar.setDisable(true);

        String nombre = txtFirstName.getText();
        String segundoNombre = txtSecondName.getText();
        String apellido = txtFirstSurname.getText();
        String segundoApellido = txtSecondSurname.getText();
        String email = txtEmail.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        //Verificar campos no vacios
        if (nombre.isEmpty() || apellido.isEmpty() || segundoApellido.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Por favor, completa todos los campos obligatorios.");
            btnRegistrar.setDisable(false);
            return;
        }

        //Verificar formato de correo
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,3}$")) {
            mostrarAlerta("Email Inválido", "El formato del correo electrónico no es correcto.");
            btnRegistrar.setDisable(false);
            return;
        }

        //Verificar contraseña
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            mostrarAlerta("Contraseña requerida", "Debes completar ambos campos de contraseña.");
            btnRegistrar.setDisable(false);
            return;
        }

        //(?=.*[a-zA-Z]) para al menos una letra
        //(?=.*\d) para al menos un numero
        //(?=.*[@$!%*?&]) para al menos un símbolo
        //[A-Za-z\d@$!%*?&] Caracteres válidos
        //{8,} mínimo de caracteres y $ fin de cadena
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";;

        if (!password.matches(passwordRegex)) {
            mostrarAlerta("Contraseña no válida",
                    "La contraseña debe tener:\n" +
                            "- Al menos 8 caracteres\n" +
                            "- Al menos una mayúscula\n" +
                            "- Al menos una minúscula\n" +
                            "- Al menos un número\n" +
                            "- Al menos un símbolo (@$!%*?&)");
            btnRegistrar.setDisable(false);
            return;
        }

        //Verificar igualdad
        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Error de coincidencia", "Las contraseñas no coinciden. Inténtalo de nuevo.");
            btnRegistrar.setDisable(false);
            return;
        }

        //Conectar al DAO
        try {
            int resultado = seguridadDAO.registrarUsuario(nombre, segundoNombre, apellido, segundoApellido, username, email, password);

            switch (resultado) {
                //Casos segun p_result
                case 0:
                    mostrarAlerta("Éxito", "Usuario registrado correctamente.");
                    cambiarEscena("LogIn.fxml", "Iniciar Sesión");
                    break;
                case 2:
                    mostrarAlerta("Duplicado", "El nombre de usuario o el email ya están en uso.");
                    btnRegistrar.setDisable(false);
                    break;
                default:
                    mostrarAlerta("Error", "No se pudo realizar el registro en la base de datos.");
                    btnRegistrar.setDisable(false);
                    break;
            }
        } catch (Exception e) {
            mostrarAlerta("Error Crítico", "Error de conexión: " + e.getMessage());
            btnRegistrar.setDisable(false);
        }
    }

    @FXML
    public void onVolverClick() {
        cambiarEscena("LogIn.fxml", "Iniciar Sesión");
    }

    private void cambiarEscena(String fxml, String titulo) {
        try {
            // Construcción de la ruta completa
            String rutaCompleta = "/com/example/bdbconsultas/" + fxml;

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaCompleta));

            // Verificación de seguridad: si no encuentra el archivo, tira error
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("No se encontró el archivo FXML en: " + rutaCompleta);
            }

            Scene scene = new Scene(fxmlLoader.load()); // Asi usa el tamaño del fxml
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error de Interfaz", "Error al cargar " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}