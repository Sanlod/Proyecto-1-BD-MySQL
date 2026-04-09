package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotasDAO {

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

    public static ObservableList<ObservableList<String>> getTiposMascotas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_TIPOS");
    }
    public static ObservableList<ObservableList<String>> getColores()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_COLORES");
    }

    public static ObservableList<ObservableList<String>> getEstados()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_ESTADOS");
    }

    public static ObservableList<ObservableList<String>> getSeveridades()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_SEVERIDADES");
    }

    public static ObservableList<ObservableList<String>> getNivEnergia()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_NIV_ENERGIA");
    }

    public static ObservableList<ObservableList<String>> getDifEntrenamiento()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_DIF_ENTRENAMIENTO");
    }

    public static ObservableList<ObservableList<String>> getRescatistas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_RESCATISTAS");
    }

    public static ObservableList<ObservableList<String>> getDistritos()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_DISTRITOS");
    }

    public static ObservableList<ObservableList<String>> getVeterinarios()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_VETERINARIOS");
    }

    public static ObservableList<ObservableList<String>> getCasasCuna()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_CASASCUNA");
    }
    public static ObservableList<ObservableList<String>> getAsociaciones()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_ASOCIACIONES");
    }

    public static ObservableList<ObservableList<String>> getRazas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_RAZAS");
    }

    public static ObservableList<ObservableList<String>> geRazasPorTipo(String idTipo)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_RAZAS(?,?) }")) {
            cs.setString(1, idTipo);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
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

    public static ObservableList<ObservableList<String>> getMonedas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MONEDAS");
    }

    public static ObservableList<ObservableList<String>> getProvincias()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PROVINCIAS");
    }
    public static ObservableList<ObservableList<String>> getCantonesPorProvincia(String idProvincia)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_CANTONES(?,?) }")) {
            cs.setString(1, idProvincia);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
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

    public static ObservableList<ObservableList<String>> getDistritosCanton(String idCanton)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_DISTRITOS(?,?) }")) {
            cs.setString(1, idCanton);
            cs.registerOutParameter(2, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
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


    public MascotasDAO.ResultadoConsulta consultarMascotas(
            String idTipoMascota,
            String idRaza,
            String nombre,
            String idRescatista,
            String idEstado,
            String idProvincia,
            String idCanton,
            String idDistrito,
            String idAsociacion) throws SQLException, ClassNotFoundException {


        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall(
                    "{ CALL SP_CONSULTAR_MASCOTAS(?,?,?,?,?,?,?,?,?,?,?) }")) {

                // Parámetros IN — si viene vacío manda NULL

                cs.setString(1, idTipoMascota.isEmpty()  ? null : idTipoMascota);
                cs.setString(2, idRaza.isEmpty()  ? null : idRaza);
                cs.setString(3, nombre.isEmpty()  ? null : nombre);
                cs.setString(4, idRescatista.isEmpty()  ? null : idRescatista);
                cs.setString(5, idEstado.isEmpty()  ? null : idEstado);
                cs.setString(6, idProvincia.isEmpty()  ? null : idProvincia);
                cs.setString(7, idCanton.isEmpty()  ? null : idCanton);
                cs.setString(8, idDistrito.isEmpty()  ? null : idDistrito);
                cs.setString(9, idAsociacion.isEmpty() ? null : idAsociacion);

                // Parámetros OUT
                cs.registerOutParameter(10, Types.REF_CURSOR);
                cs.registerOutParameter(11, Types.NUMERIC);

                cs.execute();

                total = cs.getInt(11);

                try (ResultSet rs = (ResultSet) cs.getObject(10)) {
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
        return new MascotasDAO.ResultadoConsulta(columnas, filas, total);
    }

    public void registrarMascota(
            String nombre,
            String idTipo,
            String idColor,
            String idRaza,
            String chip,
            String idEstado,
            String idSeveridad,
            String idNivelEnergia,
            String idDistrito,
            String montoRecompensa,
            String idMoneda,
            java.time.LocalDate fecha,
            String descripcion,
            String telefono,
            String email,
            byte[] imagenAntes,
            byte[] imagenDespues) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_REGISTRAR_MASCOTA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }")) {

            cs.setString(1, nombre);
            cs.setString(2, idTipo.isEmpty()          ? null : idTipo);
            cs.setString(3, idColor.isEmpty()         ? null : idColor);
            cs.setString(4, idRaza.isEmpty()          ? null : idRaza);
            cs.setString(5, chip.isEmpty()            ? null : chip);
            cs.setString(6, idEstado.isEmpty()        ? null : idEstado);
            cs.setString(7, idSeveridad.isEmpty()     ? null : idSeveridad);
            cs.setString(8, idNivelEnergia.isEmpty()  ? null : idNivelEnergia);
            cs.setString(9, idDistrito.isEmpty()      ? null : idDistrito);
            cs.setString(10, montoRecompensa.isEmpty() ? null : montoRecompensa);
            cs.setString(11, idMoneda.isEmpty()        ? null : idMoneda);
            cs.setDate(12, fecha != null ? java.sql.Date.valueOf(fecha) : null);
            cs.setString(13, descripcion.isEmpty()    ? null : descripcion);
            cs.setString(14, telefono.isEmpty()       ? null : telefono);
            cs.setString(15, email.isEmpty()          ? null : email);

            if (imagenAntes != null)
                cs.setBytes(16, imagenAntes);
            else
                cs.setNull(16, Types.BLOB);

            if (imagenDespues != null)
                cs.setBytes(17, imagenDespues);
            else
                cs.setNull(17, Types.BLOB);

            cs.execute();
        }
    }
}
