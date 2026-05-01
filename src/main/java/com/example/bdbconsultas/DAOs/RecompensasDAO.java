package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecompensasDAO {
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


    public static RecompensasDAO.ResultadoConsulta consultarRecompensas(
            String idPet) throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_CONSULTAR_RECOMPENSAS(?,?,?) }")) {

            if (idPet != null && !idPet.isEmpty())
                cs.setString(1, idPet);
            else
                cs.setNull(1, Types.VARCHAR);

            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.registerOutParameter(3, Types.NUMERIC);

            cs.execute();

            total = cs.getInt(3);

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
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
        return new RecompensasDAO.ResultadoConsulta(columnas, filas, total);
    }

    public static void registrarRecompensa(
            String amount,
            String idPet,
            String idCurrency) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_RECOMPENSA(?, ?, ?) }")) {

            cs.setString(1, amount);
            cs.setString(2, idPet);
            cs.setString(3, idCurrency);

            cs.execute();

        }
    }
    public static void donarRecompensa(
            String idRecompensa,
            String idAsociacion,
            String modifiedBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_DONAR_RECOMPENSA(?,?,?) }")) {

            cs.setString(1, idRecompensa);
            cs.setString(2, idAsociacion);
            cs.setString(3, modifiedBy);

            cs.execute();
        }
    }

    public static void pagarRescatista(String idRescuer, String idPet, String modifiedBy)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_PAGAR_RECOMPENSA(?,?,?,?) }")) {
            cs.setString(1, idRescuer);
            cs.setString(2, idPet);
            cs.setString(3, modifiedBy);
            cs.registerOutParameter(4, Types.NUMERIC);
            cs.execute();
            if (cs.getInt(4) != 0)
                throw new SQLException("No se encontró bounty activo para esta mascota.");
        }
    }

    public static boolean marcarHallada(String idPet, String modifiedBy)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_MARCAR_HALLADA(?,?,?) }")) {
            cs.setString(1, idPet);
            cs.setString(2, modifiedBy);
            cs.registerOutParameter(3, Types.NUMERIC);
            cs.execute();
            return cs.getInt(3) > 0;
        }
    }

}
