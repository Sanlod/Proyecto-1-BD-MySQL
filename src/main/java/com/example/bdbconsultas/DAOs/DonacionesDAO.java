package com.example.bdbconsultas.DAOs;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.*;

public class DonacionesDAO {

    public static class ResultadoConsulta {
        public final List<String> columnas;
        public final ObservableList<ObservableList<String>> filas;
        public final int total;

        public ResultadoConsulta(List<String> columnas,
                                 ObservableList<ObservableList<String>> filas,
                                 int total) {
            this.columnas = columnas;
            this.filas    = filas;
            this.total    = total;
        }
    }


    public static ObservableList<ObservableList<String>> getAsociaciones() throws SQLException, ClassNotFoundException {

        ObservableList<ObservableList<String>> filas =
                FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL  SP_LISTAR_ASOCIACIONES(?) }")) {

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

    public static ObservableList<ObservableList<String>> getPersonas() throws SQLException, ClassNotFoundException {

        ObservableList<ObservableList<String>> filas =
                FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL  SP_LISTAR_PERSONAS(?) }")) {

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


    public ResultadoConsulta consultarDonaciones(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String idPersona,
            String idAsociacion) throws SQLException, ClassNotFoundException {



        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall(
                    "{ CALL SP_CONSULTAR_DONACIONES(?,?,?,?,?,?) }")) {

                // Parámetros IN — si viene vacío manda NULL

                if (fechaDesde != null)
                    cs.setTimestamp(1, Timestamp.valueOf(fechaDesde.atStartOfDay()));
                else
                    cs.setNull(1, Types.TIMESTAMP);

                if (fechaHasta != null)
                    cs.setTimestamp(2, Timestamp.valueOf(fechaHasta.atTime(23, 59, 59)));
                else
                    cs.setNull(2, Types.TIMESTAMP);

                cs.setString(3, idPersona.isEmpty()    ? null : idPersona);
                cs.setString(4, idAsociacion.isEmpty() ? null : idAsociacion);

                // Parámetros OUT
                cs.registerOutParameter(5, Types.REF_CURSOR);
                cs.registerOutParameter(6, Types.NUMERIC);

                cs.execute();

                total = cs.getInt(6);

                try (ResultSet rs = (ResultSet) cs.getObject(5)) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int numCols = meta.getColumnCount();

                    // Nombres de columnas
                    for (int i = 1; i <= numCols; i++) {
                        columnas.add(meta.getColumnLabel(i));
                    }

                    // Filas como strings
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
        }
        return new ResultadoConsulta(columnas, filas, total);
    }
}