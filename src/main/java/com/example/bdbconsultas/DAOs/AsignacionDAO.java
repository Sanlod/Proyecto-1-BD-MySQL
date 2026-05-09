package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class AsignacionDAO {

    private static AsignacionDAO instance;
    public static AsignacionDAO getInstance() {
        if (instance == null) instance = new AsignacionDAO();
        return instance;
    }

    //Consultar mascotas sin cribHouse
    public ObservableList<ObservableList<String>> getMascotasSinCasa()
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_MASCOTAS_SIN_CASA(?) }")) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

    //Consultar mascotas con cribHouse
    public ObservableList<ObservableList<String>> getMascotasConCasa()
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_MASCOTAS_CON_CASA(?) }")) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

    //Consultar las cribHouses que acepten la mascota seleccionada
    public ObservableList<ObservableList<String>> getCasasCompatibles(int idPet)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_CASAS_COMPATIBLES(?, ?) }")) {
            cs.setInt(1, idPet);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Object val = rs.getObject(i);
                        fila.add(val != null ? val.toString() : "");
                    }
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

    //Asignar mascota a cribHouse
    public void asignarMascotaCasa(int idPet, int idCribHouse)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_ASIGNAR_MASCOTA_CASA(?, ?) }")) {
            cs.setInt(1, idPet);
            cs.setInt(2, idCribHouse);
            cs.execute();
        }
    }

    //Quitar asignación
    public void quitarAsignacion(int idPet) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_NULL_PET_CRIB(?) }")) {
            cs.setInt(1, idPet);
            cs.execute();
        }
    }
}