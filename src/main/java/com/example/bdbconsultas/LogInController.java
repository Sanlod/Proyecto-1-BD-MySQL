package com.example.bdbconsultas;

import com.example.bdbconsultas.DAOs.SeguridadDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LogInController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnLogin;

    private final SeguridadDAO seguridadDAO = new SeguridadDAO();

    @FXML
    public void initialize() {
        // El boton responde al ENTER
        btnLogin.setDefaultButton(true);
    }

    @FXML
    public void onLogin() {
        String user = txtUsuario.getText();
        String password = txtContrasena.getText();

        // Error si se ingresan más los datos
        if (user.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor, introduce usuario y contraseña.");
            return;
        }

        try {
            int tipoUsuario = seguridadDAO.validarLogin(user, password);

            if (tipoUsuario == 1) { // Si idUserType == 1 entonces es admin
                cambiarEscena("Admin.fxml", "Panel de Administración");
            } else if (tipoUsuario == 2) { // Si idUserType == 2 entonces es usuario
                cambiarEscena("Usuario.fxml", "Panel de Usuario");
            } else {
                //Avisar si los datos están mal
                mostrarAlerta("Acceso Denegado", "Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error técnico", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Funcion para no repetir la carga de escenas
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
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            // Esto te dirá exactamente qué falló (ej. si hay un error de sintaxis dentro del FXML)
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