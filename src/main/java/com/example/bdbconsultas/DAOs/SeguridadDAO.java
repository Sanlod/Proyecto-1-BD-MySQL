package com.example.bdbconsultas.DAOs;

import at.gadermaier.argon2.Argon2Factory;
import com.example.bdbconsultas.DBConnection;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class SeguridadDAO {

    public record IntPair(int first, int second){}

    // Validar el login
    public IntPair validarLogin(String username, String password) throws SQLException, ClassNotFoundException {

        //Hashear entrada
        String passwordHashed = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("{ CALL SP_AUTENTICAR_USUARIO(?,?,?,?,?) }")) {
                // Entradas
                cs.setString(1, username);
                cs.setString(2, passwordHashed);

                // Salidas
                cs.registerOutParameter(3, Types.NUMERIC); // p_userType
                cs.registerOutParameter(4, Types.NUMERIC); // p_status
                cs.registerOutParameter(5, Types.NUMERIC);


                cs.execute();

                int status = cs.getInt(4);
                if (status == 1) {
                    return new IntPair (cs.getInt(3), cs.getInt(5)); // Retorna el id de tipo de usuario
                } else {
                    return new IntPair(-1,0); // Falló el login
                }
            }
        }
    }

    // Registrar Usuario - Persona
    public int registrarUsuario(String firstName, String secondName, String firstSurname, String secondSurname, String username, String email, String password) throws SQLException, ClassNotFoundException {
        String passwordHashed = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_USUARIO_COMPLETO(?,?,?,?,?,?,?,?,?) }")) {

                cs.setString(1, firstName);
                cs.setString(2, secondName);
                cs.setString(3, firstSurname);
                cs.setString(4, secondSurname);
                cs.setString(5, username);
                cs.setString(6, email);
                cs.setString(7, passwordHashed);
                cs.setString(8, username);
                cs.registerOutParameter(9, Types.NUMERIC);

                cs.execute();
                return cs.getInt(9);
            }
        }
    }

    private String hashPassword(String password) {
        String salt = generateRandomSalt(); // Crea un String de salt
        return Argon2Factory.create()
                .setIterations(3) //Cantidad de veces que se ejecuta
                .setMemory(16) //Uso de memoria en RAM 16 = 65MB aprox
                .setParallelism(2)  //Cantidad de hilos que usa simultaneamente
                .hash(password.toCharArray(), salt);
    }

    private String generateRandomSalt() {
        byte[] saltBytes = new byte[16];          // Crea un arreglo de 16 bytes vacío
        new SecureRandom().nextBytes(saltBytes);  // Lo llena con bytes aleatorios criptográficamente seguros
        return Base64.getEncoder().encodeToString(saltBytes); // Lo convierte a texto legible
    }
}