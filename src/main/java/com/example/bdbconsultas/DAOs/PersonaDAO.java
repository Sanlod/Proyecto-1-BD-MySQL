package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public static class ResultadoConsulta {
        public final List<String> columnas;
        public final ObservableList<ObservableList<String>> filas;
        public final int total;

        public ResultadoConsulta(List<String> columnas,
                                 ObservableList<ObservableList<String>> filas,
                                 int total) {
            this.columnas = columnas;
            this.filas = filas;
            this.total = total;
        }
    }

    public static ObservableList<ObservableList<String>> listadosCatalogo(String nomSP)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL " + nomSP + "()")) {

            try (ResultSet rs = cs.executeQuery()) {
                int numCols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= numCols; i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

    //para combos(solo ID y nombre)
    public static ObservableList<ObservableList<String>> getPersonas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PERSONAS");
    }

    //para CRUD(ID, nombre completo, lista negra, notas)
    public static ObservableList<ObservableList<String>> getPersonasCRUD()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PERSONAS_CRUD");
    }

    public static ObservableList<ObservableList<String>> getPersonasListaNegra()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PERSONASLISTANEGRA");
    }

    public static ObservableList<ObservableList<String>> getNotasPersona()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_NOTASPERSONA");
    }

    public static ObservableList<ObservableList<String>> getRescatistas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_RESCATISTAS");
    }

    public static void reportarListaNegra(
            String idPersona,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_REPORTAR_LISTANEGRA(?,?)")) {
            cs.setString(1, idPersona);
            cs.setString(2, createdBy);
            cs.execute();
        }
    }

    public static String makeRescatista(String idPersona, String createdBy)
            throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_MAKE_RESCATISTA(?,?,?)")) {

            cs.setString(1, idPersona);
            cs.setString(2, createdBy);

            cs.registerOutParameter(3, java.sql.Types.VARCHAR);

            cs.execute();

            return cs.getString(3);
        }
    }

    public static PersonaDAO.ResultadoConsulta consultarPersonas(
            String nombre,
            String primerApellido,
            String segundoApellido,
            String enListaNegra) throws SQLException, ClassNotFoundException {
        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_CONSULTAR_PERSONAS(?,?,?,?,?)")) {
            cs.setString(1, nombre == null || nombre.isEmpty() ? null : nombre);
            cs.setString(2, primerApellido == null || primerApellido.isEmpty() ? null : primerApellido);
            cs.setString(3, segundoApellido == null || segundoApellido.isEmpty() ? null : segundoApellido);
            cs.setString(4, enListaNegra == null || enListaNegra.isEmpty() ? null : enListaNegra);

            cs.registerOutParameter(5, Types.INTEGER);

            try (ResultSet rs = cs.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int numCols = meta.getColumnCount();
                for (int i = 1; i <= numCols; i++) {
                    columnas.add(meta.getColumnLabel(i));
                }
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= numCols; i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                }
            }
            total = cs.getInt(5);
        }
        return new PersonaDAO.ResultadoConsulta(columnas, filas, total);
    }

    public static void registrarCalificacionAdoptante(
            String idPersona,
            String calificacion,
            String notas,
            String modifiedBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_REGISTRAR_CALIFICACION(?,?,?,?)")) {
            cs.setString(1, idPersona);
            cs.setString(2, calificacion);
            cs.setString(3, notas == null || notas.isEmpty() ? null : notas);
            cs.setString(4, modifiedBy);
            cs.execute();
        }
    }

    public static void registrarPersona(
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_REGISTRAR_PERSONA(?,?,?,?)")) {
            cs.setString(1, primerNombre);
            cs.setString(2, segundoNombre == null || segundoNombre.isEmpty() ? null : segundoNombre);
            cs.setString(3, primerApellido);
            cs.setString(4, segundoApellido == null || segundoApellido.isEmpty() ? null : segundoApellido);
            cs.execute();
        }
    }

    /// Cambié el id a Integer para que
    public static void actualizarPersona(
            Integer id,
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            String notas,
            Integer rating,
            String modifiedBy,
            Integer idBlackList
    ) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_ACTUALIZAR_PERSONA(?,?,?,?,?,?,?,?,?)")) {

            cs.setObject(1, id, Types.INTEGER);
            cs.setObject(2, primerNombre, Types.VARCHAR);
            cs.setObject(3, segundoNombre == null || segundoNombre.isEmpty() ? null : segundoNombre, Types.VARCHAR);
            cs.setObject(4, primerApellido, Types.VARCHAR);
            cs.setObject(5, segundoApellido == null || segundoApellido.isEmpty() ? null : segundoApellido, Types.VARCHAR);
            cs.setObject(6, notas == null || notas.isEmpty() ? null : notas, Types.VARCHAR);
            cs.setObject(7, rating, Types.INTEGER);
            cs.setObject(8, modifiedBy, Types.VARCHAR);
            cs.setObject(9, idBlackList, Types.INTEGER);
            cs.execute();
        }
    }

    public static void eliminarPersona(int id) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_PERSONA(?)")) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    public static String obtenerCalificacionPersona(String idPersona)
            throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_OBTENER_CALIF_PERSONA(?)")) {

            cs.setInt(1, Integer.parseInt(idPersona));

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    int rating = rs.getInt("rating");
                    return rating > 0 ? String.valueOf(rating) : null;
                }
            }
        }
        return null;
    }

    public static void hacerAdmin(String idPersona) throws SQLException, ClassNotFoundException {
        // LLamar al prcedimiento que convierte en admin
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_MAKE_ADM(?)")) {

            cs.setInt(1, Integer.parseInt(idPersona.trim()));
            cs.execute();
        } catch (SQLException e) {
            throw e;
        }
    }
}