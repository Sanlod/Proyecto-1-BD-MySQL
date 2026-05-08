package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {
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

        public static ObservableList<ObservableList<String>> getUsuarios()
                throws SQLException, ClassNotFoundException {
            return listadosCatalogo("SP_LISTAR_USUARIOS");
        }

        public static ResultadoConsulta consultarBitacora(
                String usuario,
                String tabla,
                LocalDate fecha) throws SQLException, ClassNotFoundException {

            List<String> columnas = new ArrayList<>();
            ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
            int total = 0;

            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs = conn.prepareCall("{ CALL SP_CONSULTAR_BITACORA(?,?,?,?,?) }")) {

                cs.setObject(1, usuario, Types.VARCHAR);
                cs.setObject(2, tabla, Types.VARCHAR);

                if (fecha != null)
                    cs.setTimestamp(3, Timestamp.valueOf(fecha + " 00:00:00"));
                else
                    cs.setNull(3, Types.TIMESTAMP);

                cs.registerOutParameter(4, Types.REF_CURSOR);
                cs.registerOutParameter(5, Types.NUMERIC);

                cs.execute();

                total = cs.getInt(5);

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
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
            return new BitacoraDAO.ResultadoConsulta(columnas, filas, total);
        }
    }

