package com.example.bdbconsultas.DAOs;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.*;
import javafx.scene.control.Alert;

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
                    "CALL SP_CONSULTAR_DONACIONES(?,?,?,?,?)")) {

                // Parámetros IN — si viene vacío manda NULL

                if (fechaDesde != null)
                    cs.setTimestamp(1, Timestamp.valueOf(fechaDesde.atStartOfDay()));
                else
                    cs.setNull(1, Types.TIMESTAMP);

                if (fechaHasta != null)
                    cs.setTimestamp(2, Timestamp.valueOf(fechaHasta.atTime(23, 59, 59)));
                else
                    cs.setNull(2, Types.TIMESTAMP);

                cs.setString(3, (idPersona == null || idPersona.trim().isEmpty()) ? null : idPersona);
                cs.setString(4, (idAsociacion == null || idAsociacion.trim().isEmpty()) ? null : idAsociacion);
                // Parámetros OUT
                cs.registerOutParameter(5, Types.INTEGER);



                try (ResultSet rs = cs.executeQuery()) {
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
                total = cs.getInt(5);
            }
        }
        return new ResultadoConsulta(columnas, filas, total);
    }

    public void registrarDonacion(int monto, int porcentaje, int idPersona, int idAsociacion, int idCurrency, String nomPersona)
            throws SQLException, ClassNotFoundException {
        try (Connection con = DBConnection.getConnection();
        CallableStatement cs = con.prepareCall("CALL SP_DONAR(?,?,?,?,?,?) ")){
            cs.setInt(1, monto);
            cs.setInt(2, porcentaje);
            cs.setInt(3, idPersona);
            cs.setInt(4, idAsociacion);
            cs.setInt(5, idCurrency);
            cs.setString(6, nomPersona);
            cs.execute();

        }
    }
}