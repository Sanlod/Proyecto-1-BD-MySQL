package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdopcionesDAO {


    public ResultadoConsulta consultarSolicitudes(String idM, String idA) {
        return null;
    }

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


    public static ObservableList<ObservableList<String>> getMascotas()
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_MASCOTAS(?) }")) {
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

    public static ObservableList<ObservableList<String>> getAdoptantes()
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_ADOPTANTES(?) }")) {
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

    public static ResultadoConsulta consultarSolicitudes(
            LocalDate desde, LocalDate hasta,
            String idMascota, String idAdoptante) throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_CONSULTAR_SOLICITUDES(?,?,?,?,?,?) }")) {

            cs.setObject(1, desde != null ? Date.valueOf(desde) : null);
            cs.setObject(2, hasta != null ? Date.valueOf(hasta) : null);
            cs.setString(3, (idMascota == null || idMascota.equals("0")) ? null : idMascota);
            cs.setString(4, (idAdoptante == null || idAdoptante.equals("0")) ? null : idAdoptante);

            cs.registerOutParameter(5, Types.REF_CURSOR);
            cs.registerOutParameter(6, Types.NUMERIC);

            cs.execute();
            total = cs.getInt(6);


            try (ResultSet rs = (ResultSet) cs.getObject(5)) {
                if (rs == null) {
                    System.out.println("ERROR: El cursor es NULL");
                    return new ResultadoConsulta(columnas, filas, total);
                }

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
        return new ResultadoConsulta(columnas, filas, total);
    }

    public static ResultadoConsulta consultarAdopciones(
            LocalDate desde, LocalDate hasta,
            String idMascota, String idAdoptante) throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_CONSULTAR_ADOPS(?,?,?,?,?,?) }")) {

            cs.setObject(1, desde != null ? Date.valueOf(desde) : null);
            cs.setObject(2, hasta != null ? Date.valueOf(hasta) : null);
            cs.setString(3, (idMascota == null || idMascota.equals("0")) ? null : idMascota);
            cs.setString(4, (idAdoptante == null || idAdoptante.equals("0")) ? null : idAdoptante);

            cs.registerOutParameter(5, Types.REF_CURSOR);
            cs.registerOutParameter(6, Types.NUMERIC);

            cs.execute();
            total = cs.getInt(6);

            try (ResultSet rs = (ResultSet) cs.getObject(5)) {
                ResultSetMetaData meta = rs.getMetaData();
                int numCols = meta.getColumnCount();

                for (int i = 1; i <= numCols; i++) columnas.add(meta.getColumnLabel(i));

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
        return new ResultadoConsulta(columnas, filas, total);
    }


    public static boolean actualizarEstadoSolicitud(
            String idSolicitud, String idPet, String idPerson,
            byte[] foto, String notas,
            String nuevoEstado, String usuario) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_GESTIONAR_SOLICITUD(?,?,?,?,?,?,?,?) }")) {
            cs.setInt(1, Integer.parseInt(idSolicitud));
            cs.setInt(2, Integer.parseInt(idPet));
            cs.setInt(3, Integer.parseInt(idPerson));
            if (foto != null) cs.setBytes(4, foto);
            else cs.setNull(4, Types.BLOB);
            cs.setString(5, notas != null ? notas : "");
            cs.setInt(6, Integer.parseInt(nuevoEstado));
            cs.setString(7, usuario);
            cs.registerOutParameter(8, Types.NUMERIC);
            cs.execute();
            return cs.getInt(8) == 0;
        }
    }

    public static int registrarRequest(
            String idMascota, String idPerson, String createdBy)
            throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_REQUEST(?,?,?,?,?) }")) {
            cs.setString(1, idMascota);
            cs.setString(2, idPerson);
            cs.setString(3, createdBy);
            cs.registerOutParameter(4, Types.NUMERIC);
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.execute();
            return cs.getInt(5) == 0 ? cs.getInt(4) : -1;
        }
    }

    public static int registrarAdopcion(
            String idMascota, String idAdoptante,
            String notas, byte[] foto, byte[] fotoNueva,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_ADOPCION(?,?,?,?,?,?,?,?) }")) {

            cs.setString(1, idMascota);
            cs.setString(2, idAdoptante);
            cs.setString(3, notas.isEmpty() ? null : notas);

            if (foto != null) cs.setBytes(4, foto);
            else cs.setNull(4, Types.BLOB);

            if (fotoNueva != null) cs.setBytes(5, fotoNueva);
            else cs.setNull(5, Types.BLOB);

            cs.setString(6, createdBy);
            cs.registerOutParameter(7, Types.NUMERIC);
            cs.registerOutParameter(8, Types.NUMERIC);
            cs.execute();

            return cs.getInt(7) == 0 ? cs.getInt(8) : -1;
        }
    }

    public static ObservableList<ObservableList<String>> getPreguntas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PREGUNTAS");
    }

    public static void registrarRespuesta(
            String idPregunta, String idRequest,
            String valor, String createdBy)
            throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_RESPUESTA(?,?,?,?) }")) {
            cs.setString(1, idPregunta);
            cs.setString(2, idRequest);
            cs.setString(3, valor);
            cs.setString(4, createdBy);
            cs.execute();
        }
    }

    public static ObservableList<ObservableList<String>> getEstadosSolicitud()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_ESTADOS_SOLICITUD");
    }

    public static ResultadoConsulta seguimientoMascota(String idPet)
            throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_SEGUIMIENTO_MASCOTA(?,?) }")) {

            cs.setString(1, idPet);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                ResultSetMetaData meta = rs.getMetaData();
                int numCols = meta.getColumnCount();
                for (int i = 1; i <= numCols; i++) columnas.add(meta.getColumnLabel(i));
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= numCols; i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                    total++;
                }
            }
        }
        return new ResultadoConsulta(columnas, filas, total);
    }

    public static Map<String, Object> obtenerDetalleSolicitud(String idSolicitud) throws SQLException, ClassNotFoundException {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, String>> preguntasRespuestas = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_OBTENER_DETALLE_SOLICITUD(?,?,?) }")) {

            cs.setInt(1, Integer.parseInt(idSolicitud));
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.registerOutParameter(3, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    resultado.put("id", rs.getString("id"));
                    resultado.put("idPet", rs.getString("idPet"));
                    resultado.put("idPerson", rs.getString("idPerson"));
                    resultado.put("pet_name", rs.getString("pet_name"));
                    resultado.put("adoptant_name", rs.getString("adoptant_name"));
                    resultado.put("status_name", rs.getString("status_name"));
                    resultado.put("createdAt", rs.getTimestamp("createdAt"));
                    resultado.put("createdBy", rs.getString("createdBy"));
                }
            }

            try (ResultSet rsPreg = (ResultSet) cs.getObject(3)) {
                while (rsPreg.next()) {
                    Map<String, String> pr = new HashMap<>();
                    pr.put("question_id", rsPreg.getString("question_id"));
                    pr.put("question_text", rsPreg.getString("question_text"));
                    pr.put("answer_value", rsPreg.getString("answer_value"));
                    preguntasRespuestas.add(pr);
                }
            }
            resultado.put("preguntas", preguntasRespuestas);
        }
        return resultado;
    }
}