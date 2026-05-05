package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CasaCunaDAO {

    private static CasaCunaDAO instance;

    public static class ResultadoCribHouse {
        public final List<String> columnas;
        public final ObservableList<ObservableList<String>> filas;

        public ResultadoCribHouse(List<String> columnas, ObservableList<ObservableList<String>> filas) {
            this.columnas = columnas;
            this.filas = filas;
        }
    }

    public static CasaCunaDAO getInstance() {
        if (instance == null) {
            instance = new CasaCunaDAO();
        }
        return instance;
    }

    private ObservableList<ObservableList<String>> listadosCatalogo(String nomSP) throws SQLException, ClassNotFoundException {
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

    public ObservableList<ObservableList<String>> getTiposMascotas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_TIPOS");
    }

    public ObservableList<ObservableList<String>> getDistritos()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_DISTRITOS");
    }

    public ObservableList<ObservableList<String>> getNivelesEnergia()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_NIV_ENERGIA");
    }

    public int CasaCuna(int idPersona, int requiereComida, String tipos, String tamanios, int idDistrito, String nivelesEnergia) {
        String sql = "{ CALL SP_REGISTRAR_CASACUNA(?, ?, ?, ?, ?, ?, ?) }";
        int idGenerado = -1;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, requiereComida);
            cs.setString(2, tipos);
            cs.setString(3, tamanios);
            cs.setInt(4, idDistrito);
            cs.setInt(5, idPersona);
            cs.setString(6, nivelesEnergia);

            cs.registerOutParameter(7, java.sql.Types.NUMERIC);

            cs.execute();
            idGenerado = cs.getInt(7);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return idGenerado;
    }

    public static ResultadoCribHouse listarCribHouses() throws SQLException, ClassNotFoundException {
        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();

        String sql = "{ CALL SP_LISTAR_CRIBHOUSE(?) }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                ResultSetMetaData meta = rs.getMetaData();
                int numCols = meta.getColumnCount();

                // Sacar los nombres de las columnas
                for (int i = 1; i <= numCols; i++) {
                    columnas.add(meta.getColumnLabel(i));
                }

                // Se recorren las filas
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
        return new ResultadoCribHouse(columnas, filas);
    }
}