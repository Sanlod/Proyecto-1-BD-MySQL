package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CatalogoDAO {

    public static void editarCatalogo(String tabla, int id, String nuevoValor)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_EDITAR_CATALOGO(?,?,?)")) {
            cs.setString(1, tabla);
            cs.setInt(2, id);
            cs.setString(3, nuevoValor);
            cs.execute();
        }
    }

    public static void agregarCatalogo(String tabla, String valor)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_CATALOGO(?,?)")) {
            cs.setString(1, tabla);
            cs.setString(2, valor);
            cs.execute();
        }
    }

    public static boolean existeRegistro(String tabla, String valor)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_EXISTE_CATALOGO(?,?,?)")) {
            cs.setString(1, tabla);
            cs.setString(2, valor);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return cs.getInt(3) > 0;
        }
    }

    public static void agregarRaza(String nombre, int idTipo)
            throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Breed (name, idPetType, createdBy, createdAt, modifiedBy, modifiedAt)" +
                " VALUES (?, ?, USER(), CURRENT_TIMESTAMP, USER(), CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idTipo);
            ps.executeUpdate();
        }
    }

    public static boolean existeRaza(String nombre, int idTipo)
            throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM Breed WHERE UPPER(name) = UPPER(?) AND idPetType = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idTipo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static void agregarEnfermedad(String nombre, String descripcion)
            throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Disease (name, description, createdBy, createdAt, modifiedBy, modifiedAt)" +
                " VALUES (?, ?, USER(), CURRENT_TIMESTAMP, USER(), CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.executeUpdate();
        }
    }

    public static void editarEnfermedad(int id, String nombre, String descripcion)
            throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Disease SET name = ?, description = ?," +
                " modifiedBy = USER(), modifiedAt = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public static void agregarCanton(String nombre, int idProvincia)
            throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Canton (name, idProvince, createdBy, createdAt, modifiedBy, modifiedAt)" +
                " VALUES (?, ?, USER(), CURRENT_TIMESTAMP, USER(), CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idProvincia);
            ps.executeUpdate();
        }
    }

    public static void agregarDistrito(String nombre, int idCanton)
            throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO District (name, idCanton, createdBy, createdAt, modifiedBy, modifiedAt)" +
                " VALUES (?, ?, USER(), CURRENT_TIMESTAMP, USER(), CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idCanton);
            ps.executeUpdate();
        }
    }

    public static void agregarPregunta(String texto, String tipo, String createdBy)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_PREGUNTA(?,?,?)")) {
            cs.setString(1, texto);
            cs.setString(2, tipo);
            cs.setString(3, createdBy);
            cs.execute();
        }
    }

    public static void editarPregunta(int id, String texto, String tipo, String modifiedBy)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_EDITAR_PREGUNTA(?,?,?,?)")) {
            cs.setInt(1, id);
            cs.setString(2, texto);
            cs.setString(3, tipo);
            cs.setString(4, modifiedBy);
            cs.execute();
        }
    }

    public static void eliminarCatalogo(String tabla, int id)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_CATALOGO(?,?)")) {
            cs.setString(1, tabla);
            cs.setInt(2, id);
            cs.execute();
        }
    }
}