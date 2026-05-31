package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchesDAO {
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

    public static ObservableList<ObservableList<String>> getMascostasPerdidas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MASCOTASPERDIDAS");
    }
    public static ObservableList<ObservableList<String>> getMascotasHalladas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MASCOTASHALLADAS");
    }
    public static ObservableList<ObservableList<String>> getEstadosMatch()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_ESTADOSMATCH");
    }
    public static void cambiarEstadoMatch(
            String idMatch,
            String idEstado,
            String modifiedBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_CAMBIAR_ESTADOMATCH(?,?,?)")) {

            if (idMatch == null || idMatch.isEmpty()) cs.setNull(1, Types.INTEGER);
            else cs.setInt(1, Integer.parseInt(idMatch));

            if (idEstado == null || idEstado.isEmpty()) cs.setNull(2, Types.INTEGER);
            else cs.setInt(2, Integer.parseInt(idEstado));

            cs.setString(3, modifiedBy);

            cs.execute();
        }
    }

    public static ResultadoConsulta consultarMatches(
            String idMascotaPerdida,
            String idTipo,
            String idRaza,
            String nombre,
            String chip,
            String idColor,
            String idEstado,
            String idProvincia,
            String idCanton,
            String idDistrito,
            String idAsociacion,
            java.time.LocalDate desde,
            java.time.LocalDate hasta) throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "CALL SP_CONSULTAR_MATCHES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {

            cs.setString(1, idMascotaPerdida == null || idMascotaPerdida.isEmpty() ? null : idMascotaPerdida);
            cs.setString(2, idTipo == null || idTipo.isEmpty() ? null : idTipo);
            cs.setString(3, idRaza == null || idRaza.isEmpty() ? null : idRaza);
            cs.setString(4, nombre == null || nombre.isEmpty() ? null : nombre);
            cs.setString(5, chip == null || chip.isEmpty() ? null : chip);
            cs.setString(6, idColor == null || idColor.isEmpty() ? null : idColor);
            cs.setString(7, idEstado == null || idEstado.isEmpty() ? null : idEstado);
            cs.setString(8, idProvincia == null || idProvincia.isEmpty() ? null : idProvincia);
            cs.setString(9, idCanton == null || idCanton.isEmpty() ? null : idCanton);
            cs.setString(10, idDistrito == null || idDistrito.isEmpty() ? null : idDistrito);
            cs.setString(11, idAsociacion == null || idAsociacion.isEmpty() ? null : idAsociacion);

            if (desde != null) cs.setDate(12, java.sql.Date.valueOf(desde));
            else cs.setNull(12, Types.DATE);

            if (hasta != null) cs.setDate(13, java.sql.Date.valueOf(hasta));
            else cs.setNull(13, Types.DATE);

            cs.registerOutParameter(14, Types.INTEGER);

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
            total = cs.getInt(14);
        }
        return new ResultadoConsulta(columnas, filas, total);
    }

    public static void ejecutarMatch() throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_EJECUTAR_MATCH()")) {

            cs.execute();
        }
    }
}