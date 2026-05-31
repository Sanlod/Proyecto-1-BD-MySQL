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

        // Primero obtener el hash guardado en la BD
        String hashGuardado = obtenerHashPorUsername(username);

        if (hashGuardado == null) {
            return new IntPair(-1, 0); // Usuario no existe
        }

        String[] partes = hashGuardado.split(":");
        String saltExtraido = partes[0];
        String hashOriginal = partes[1];

        String hashNuevo = Argon2Factory.create()
                .setIterations(3)
                .setMemory(16)
                .setParallelism(2)
                .hash(password.toCharArray(), saltExtraido);

        boolean coincide = hashNuevo.equals(hashOriginal);

        if (!coincide) {
            return new IntPair(-1, 0); // Contraseña incorrecta
        }

        // Si coincide, llamar al SP solo para obtener el tipo de usuario e id
        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("CALL SP_AUTENTICAR_USUARIO(?,?,?,?,?)")) {
                cs.setString(1, username);
                cs.setString(2, hashGuardado); // Mandamos el hash guardado para que el SP lo encuentre
                cs.registerOutParameter(3, Types.INTEGER);
                cs.registerOutParameter(4, Types.INTEGER);
                cs.registerOutParameter(5, Types.INTEGER);

                cs.execute();

                int status = cs.getInt(4);
                if (status == 1) {
                    return new IntPair(cs.getInt(3), cs.getInt(5));
                } else {
                    return new IntPair(-1, 0);
                }
            }
        }
    }

    // Registrar Usuario - Persona
    public int registrarUsuario(String firstName, String secondName, String firstSurname, String secondSurname, String username, String email, String password) throws SQLException, ClassNotFoundException {
        String passwordHashed = hashPassword(password);

        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("CALL SP_REGISTRAR_USUARIO_COMPLETO(?,?,?,?,?,?,?,?,?)")) {

                cs.setString(1, firstName);
                cs.setString(2, secondName);
                cs.setString(3, firstSurname);
                cs.setString(4, secondSurname);
                cs.setString(5, username);
                cs.setString(6, email);
                cs.setString(7, passwordHashed);
                cs.setString(8, username);
                cs.registerOutParameter(9, Types.INTEGER);

                cs.execute();
                return cs.getInt(9);
            }
        }
    }

    private String hashPassword(String password) {
        String salt = generateRandomSalt();
        String hash = Argon2Factory.create()
                .setIterations(3)
                .setMemory(16)
                .setParallelism(2)
                .hash(password.toCharArray(), salt);
        return salt + ":" + hash; // Guardamos salt:hash
    }

    private String generateRandomSalt() {
        byte[] saltBytes = new byte[16];          // Crea un arreglo de 16 bytes vacío
        new SecureRandom().nextBytes(saltBytes);  // Lo llena con bytes aleatorios criptográficamente seguros
        return Base64.getEncoder().encodeToString(saltBytes); // Lo convierte a texto legible
    }

    private String obtenerHashPorUsername(String username) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("CALL SP_Get_Pass_User(?,?)")) {
                cs.setString(1, username);
                cs.registerOutParameter(2, Types.VARCHAR);
                cs.execute();
                return cs.getString(2);
            }
        }
    }
}