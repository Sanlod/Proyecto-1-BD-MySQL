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
    public static ObservableList<ObservableList<String>> getEnfermedades()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_ENFERMEDADES");
    }

    public static ObservableList<ObservableList<String>> getTratamientos()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_TRATAMIENTOS");
    }

    public static ObservableList<ObservableList<String>> getMedicamentos()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MEDICAMENTOS");
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

    public static void registrarMascota(
            String nombre,
            String idBreed,
            String idColor,
            String chip,
            String idEstado,
            String idSeveridad,
            String idNivelEnergia,
            String idDistrito,
            String petSize,
            int requiresMuchSpace,
            String telefono,
            String email,
            String abandonSituationDescription,
            String descripcion,
            String trainingDifficulty,
            java.time.LocalDate lossDate,
            java.time.LocalDate foundDate,
            String idVeterinario,
            String idCasaCuna,
            String idRescatista,
            String idAsociacion,
            byte[] imagenAntes,
            byte[] imagenDespues,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_MASCOTA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }")) {

            cs.setString(1, nombre);
            cs.setString(2, idBreed.isEmpty()                      ? null : idBreed);
            cs.setString(3, idColor.isEmpty()                      ? null : idColor);
            cs.setString(4, chip.isEmpty()                         ? null : chip);
            cs.setString(5, idEstado.isEmpty()                     ? null : idEstado);
            cs.setString(6, idSeveridad.isEmpty()                  ? null : idSeveridad);
            cs.setString(7, idNivelEnergia.isEmpty()               ? null : idNivelEnergia);
            cs.setString(8, idDistrito.isEmpty()                   ? null : idDistrito);
            cs.setString(9, petSize.isEmpty()                      ? null : petSize);
            cs.setInt(10, requiresMuchSpace);
            cs.setString(11, telefono.isEmpty()                    ? null : telefono);
            cs.setString(12, email.isEmpty()                       ? null : email);
            cs.setString(13, abandonSituationDescription.isEmpty() ? null : abandonSituationDescription);
            cs.setString(14, descripcion.isEmpty()                 ? null : descripcion);
            cs.setString(15, trainingDifficulty.isEmpty()          ? null : trainingDifficulty);
            cs.setDate(16, lossDate  != null ? java.sql.Date.valueOf(lossDate)  : null);
            cs.setDate(17, foundDate != null ? java.sql.Date.valueOf(foundDate) : null);
            cs.setString(18, idVeterinario.isEmpty()               ? null : idVeterinario);
            cs.setString(19, idCasaCuna.isEmpty()                  ? null : idCasaCuna);
            cs.setString(20, idRescatista.isEmpty()                ? null : idRescatista);
            cs.setString(21, idAsociacion.isEmpty()                ? null : idAsociacion);

            if (imagenAntes != null) cs.setBytes(22, imagenAntes);
            else cs.setNull(22, Types.BLOB);

            if (imagenDespues != null) cs.setBytes(23, imagenDespues);
            else cs.setNull(23, Types.BLOB);

            cs.setString(24, createdBy);

            cs.execute();
        }
    }
    public static void registrarEnfermedadMascota(
            String idPet,
            String idDisease,
            java.time.LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_ENFERMEDAD_MASCOTA(?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idDisease);
            cs.setDate(3, startDate != null ? java.sql.Date.valueOf(startDate) : null);
            cs.setString(4, createdBy);

            cs.execute();
        }
    }

    public static void registrarTratamientoMascota(
            String idPet,
            String idTreatment,
            java.time.LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_TRATAMIENTO_MASCOTA(?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idTreatment);
            cs.setDate(3, startDate != null ? java.sql.Date.valueOf(startDate) : null);
            cs.setString(4, createdBy);

            cs.execute();
        }
    }

    public static void registrarMedicamentoMascota(
            String idPet,
            String idMedication,
            String dose,
            java.time.LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_MEDICAMENTO_MASCOTA(?,?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idMedication);
            cs.setString(3, dose.isEmpty() ? null : dose);
            cs.setDate(4, startDate != null ? java.sql.Date.valueOf(startDate) : null);
            cs.setString(5, createdBy);

            cs.execute();
        }
    }
}
