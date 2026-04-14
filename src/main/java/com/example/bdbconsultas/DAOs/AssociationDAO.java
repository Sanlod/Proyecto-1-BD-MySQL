package com.example.bdbconsultas.DAOs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.*;

public class AssociationDAO {
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

    public static ObservableList<ObservableList<String>> getAsociaciones()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_ASOCIACIONES");
    }

    public static void registrarAsociacion(
            String nombre) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_ASOCIACION(?) }")) {

            cs.setString(1, nombre);


            cs.execute();

        }
    }
    public static AssociationDAO.ResultadoConsulta consultarAsociaciones(
            String nombre) throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_CONSULTAR_ASOCIACIONES(?,?,?) }")) {

            cs.setString(1, nombre.isEmpty()          ? null : nombre);


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
        return new AssociationDAO.ResultadoConsulta(columnas, filas, total);
    }
}
