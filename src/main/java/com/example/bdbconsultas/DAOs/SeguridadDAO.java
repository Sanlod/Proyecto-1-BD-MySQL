package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import java.sql.*;

public class SeguridadDAO {

    // Validar el login
    public int validarLogin(String username, String password) throws SQLException, ClassNotFoundException {

        //Hashear entrada
        String passwordHashed = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("{ CALL SP_AUTENTICAR_USUARIO(?,?,?,?) }")) {
                // Entradas
                cs.setString(1, username);
                cs.setString(2, passwordHashed);

                // Salidas
                cs.registerOutParameter(3, Types.NUMERIC); // p_userType
                cs.registerOutParameter(4, Types.NUMERIC); // p_status

                cs.execute();

                int status = cs.getInt(4);
                if (status == 1) {
                    return cs.getInt(3); // Retorna el id de tipo de usuario
                } else {
                    return -1; // Falló el login
                }
            }
        }
    }

    // Registrar Usuario - Persona
    public int registrarUsuario(String firstName, String secondName, String firstSurname, String secondSurname, String username, String email, String password) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection()) {
            // Se corrigió el paréntesis faltante aquí abajo:
            try (CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_USUARIO_COMPLETO(?,?,?,?,?,?,?,?,?) }")) {

                // Entradas Persona
                cs.setString(1, firstName);
                cs.setString(2, secondName);
                cs.setString(3, firstSurname);
                cs.setString(4, secondSurname);

                // Entradas Usuario
                cs.setString(5, username);
                cs.setString(6, email);
                cs.setString(7, password);

                // CreatedBy: Usamos el username del nuevo registro
                cs.setString(8, username);

                // Salida: p_result (0=OK, 1=Error, 2=Duplicado)
                cs.registerOutParameter(9, Types.NUMERIC);

                cs.execute();

                return cs.getInt(9);
            }
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            return null;
        }
    }
}