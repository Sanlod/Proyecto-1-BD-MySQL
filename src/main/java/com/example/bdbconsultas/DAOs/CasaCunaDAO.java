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

    public static ObservableList<ObservableList<String>> listadosCatalogo(String nomSP)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL " + nomSP + "() }")) {

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

    public static ObservableList<ObservableList<String>> getTiposMascotas()
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

    public int registrarCasaCuna(int idPersona, int requiereComida, String tamanio, int idDistrito)
            throws SQLException, ClassNotFoundException {
        String sql = "CALL SP_REGISTRAR_CASACUNA(?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, requiereComida);
            cs.setInt(2, idDistrito);
            cs.setInt(3, idPersona);
            cs.setString(4, tamanio);
            cs.registerOutParameter(5, java.sql.Types.INTEGER);

            cs.execute();
            int idGenerado = cs.getInt(5);
            return idGenerado;

        } catch (SQLException e) {
            // Imprime el error COMPLETO de Oracle
            e.printStackTrace();
            return -1;
        }
    }

    public void insertarRelacionPetType(int idCasa, int idPetType) throws SQLException, ClassNotFoundException {
        String sql = "CALL SP_Insertar_Tipos_Crib(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idCasa);
            cs.setInt(2, idPetType);
            cs.execute();
        }
    }

    public void insertarRelacionEnergy(int idCasa, int idEnergyLevel) throws SQLException, ClassNotFoundException {
        String sql = "CALL SP_Insertar_Niveles_Crib(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idCasa);
            cs.setInt(2, idEnergyLevel);
            cs.execute();
        }
    }

    public static ResultadoCribHouse listarCribHouses() throws SQLException, ClassNotFoundException {
        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();

        String sql = "CALL SP_LISTAR_CRIBHOUSE()";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            try (ResultSet rs = cs.executeQuery()) {
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