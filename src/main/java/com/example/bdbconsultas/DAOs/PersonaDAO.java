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
             CallableStatement cs = conn.prepareCall("{ CALL " + nomSP + " (?) }")) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
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

    public static ObservableList<ObservableList<String>> getPersonasListaNegra()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PERSONASLISTANEGRA");
    }

    public static ObservableList<ObservableList<String>> getNotasPersona()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_NOTASPERSONA");
    }
    public static ObservableList<ObservableList<String>> getPersonas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PERSONAS");
    }

    public static void reportarListaNegra(
            String idPersona,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_REPORTAR_LISTANEGRA(?,?) }")) {

            cs.setString(1, idPersona);
            cs.setString(2, createdBy);

            cs.execute();
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
             CallableStatement cs = conn.prepareCall("{ CALL SP_CONSULTAR_PERSONAS(?,?,?,?,?) }")) {

            cs.setString(1, nombre.isEmpty()          ? null : nombre);
            cs.setString(2, primerApellido.isEmpty()  ? null : primerApellido);
            cs.setString(3, segundoApellido.isEmpty() ? null : segundoApellido);
            cs.setString(4, enListaNegra.isEmpty()    ? null : enListaNegra);

            cs.registerOutParameter(5, Types.REF_CURSOR);
            cs.registerOutParameter(6, Types.NUMERIC);

            cs.execute();

            total = cs.getInt(6);

            try (ResultSet rs = (ResultSet) cs.getObject(5)) {
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
        }
        return new PersonaDAO.ResultadoConsulta(columnas, filas, total);
    }

    public static void registrarCalificacionAdoptante(
            String idPersona,
            String calificacion,
            String notas,
            String modifiedBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_CALIFICACION(?,?,?,?) }")) {

            cs.setString(1, idPersona);
            cs.setString(2, calificacion);
            cs.setString(3, notas.isEmpty() ? null : notas);
            cs.setString(4, modifiedBy);

            cs.execute();
        }
    }


    public static void registrarPersona(
            String primerNombre,
            String segundoNombre,
            String primerApellido,
            String segundoApellido,
            String correo,
            String telefono) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_PERSONA(?,?,?,?,?,?) }")) {

            cs.setString(1, primerNombre);
            cs.setString(2, segundoNombre.isEmpty()          ? null : segundoNombre);
            cs.setString(3, primerApellido);
            cs.setString(4, segundoApellido);
            cs.setString(5, correo);
            cs.setString(6, telefono);

            cs.execute();

        }
    }


}
