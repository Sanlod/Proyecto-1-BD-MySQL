package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static MascotasDAO mascotasDAO = new MascotasDAO();

    public static MascotasDAO getMascotasDAO() {
        if(mascotasDAO == null){
            return mascotasDAO =  new MascotasDAO();
        }
        else return mascotasDAO;
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

    public static ObservableList<ObservableList<String>> getMonedas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MONEDAS");
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

    public static ObservableList<ObservableList<String>> getProvincias()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_PROVINCIAS");
    }

    public static ObservableList<ObservableList<String>> getDistritos()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_DISTRITOS");
    }

    public static ObservableList<ObservableList<String>> getRazas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_RAZAS");
    }

    public static ObservableList<ObservableList<String>> getRazasPorTipo(String idTipo)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_RAZAS(?,?) }")) {
            cs.setString(1, idTipo == null || idTipo.isEmpty() ? null : idTipo);
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

    public static ObservableList<ObservableList<String>> getCantonesPorProvincia(String idProvincia)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_CANTONES(?,?) }")) {
            cs.setString(1, idProvincia == null || idProvincia.isEmpty() ? null : idProvincia);
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

    public static ObservableList<ObservableList<String>> getDistritosPorCanton(String idCanton)
            throws SQLException, ClassNotFoundException {
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_LISTAR_DISTRITOS_CANTON(?,?) }")) {
            cs.setString(1, idCanton == null || idCanton.isEmpty() ? null : idCanton);
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


    public ResultadoConsulta consultarSalud(
            String nombre,
            String chip,
            Integer idMedicina,
            Integer idTreatment,
            Integer idDisease,
            LocalDate fechaInicio,
            LocalDate fechaFin
            ) throws SQLException, ClassNotFoundException {
        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_CONSULTAR_SALUD(?,?,?,?,?,?,?,?,?) }")) {

            cs.setObject(1, nombre, Types.VARCHAR);
            cs.setObject(2, chip, Types.VARCHAR);
            cs.setObject(3, idMedicina, Types.INTEGER);
            cs.setObject(4, idTreatment, Types.INTEGER);
            cs.setObject(5, idDisease, Types.INTEGER);

            if (fechaInicio == null) {
                cs.setNull(6, Types.DATE);
            } else {
                cs.setDate(6, Date.valueOf(fechaInicio));
            }

            if (fechaFin == null) {
                cs.setNull(7, Types.DATE);
            } else {
                cs.setDate(7, Date.valueOf(fechaFin));
            }

            cs.registerOutParameter(8, Types.REF_CURSOR);
            cs.registerOutParameter(9, Types.NUMERIC);

            cs.execute();

            total = cs.getInt(9);

            try (ResultSet rs = (ResultSet) cs.getObject(8)) {
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
        return new ResultadoConsulta(columnas, filas, total);
    }




    public ResultadoConsulta consultarMascotas(
            String idTipo,
            String idRaza,
            String nombre,
            String chip,
            String idRescatista,
            String idEstado,
            String idColor,
            String idProvincia,
            String idCanton,
            String idDistrito,
            String idAsociacion,
            LocalDate fechaDesde,
            LocalDate fechaHasta) throws SQLException, ClassNotFoundException {

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_CONSULTAR_MASCOTAS(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }")) {

            cs.setString(1, idTipo == null || idTipo.isEmpty() ? null : idTipo);
            cs.setString(2, idRaza == null || idRaza.isEmpty() ? null : idRaza);
            cs.setString(3, nombre == null || nombre.isEmpty() ? null : nombre);
            cs.setString(4, chip == null || chip.isEmpty() ? null : chip);
            cs.setString(5, idRescatista == null || idRescatista.isEmpty() ? null : idRescatista);
            cs.setString(6, idEstado == null || idEstado.isEmpty() ? null : idEstado);
            cs.setString(7, idColor == null || idColor.isEmpty() ? null : idColor);
            cs.setString(8, idProvincia == null || idProvincia.isEmpty() ? null : idProvincia);
            cs.setString(9, idCanton == null || idCanton.isEmpty() ? null : idCanton);
            cs.setString(10, idDistrito == null || idDistrito.isEmpty() ? null : idDistrito);
            cs.setString(11, idAsociacion == null || idAsociacion.isEmpty() ? null : idAsociacion);

            if (fechaDesde == null) {
                cs.setNull(12, Types.DATE);
            } else {
                cs.setDate(12, Date.valueOf(fechaDesde));
            }

            if (fechaHasta == null) {
                cs.setNull(13, Types.DATE);
            } else {
                cs.setDate(13, Date.valueOf(fechaHasta));
            }

            cs.registerOutParameter(14, Types.REF_CURSOR);
            cs.registerOutParameter(15, Types.NUMERIC);

            cs.execute();

            total = cs.getInt(15);

            try (ResultSet rs = (ResultSet) cs.getObject(14)) {
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
        return new ResultadoConsulta(columnas, filas, total);
    }

    public int registrarMascota(
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
            String descriptionNotes,
            String idTrainingDifficulty,
            LocalDate lossDate,
            LocalDate foundDate,
            String idVeterinarian,
            String idCribHouse,
            String idRescuer,
            String idAssociation,
            byte[] beforePicture,
            byte[] afterPicture,
            String createdBy,
            LocalDate birthDate) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_MASCOTA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }")) {

            cs.setString(1, nombre);
            cs.setString(2, idBreed == null || idBreed.isEmpty() ? null : idBreed);
            cs.setString(3, chip == null || chip.isEmpty() ? null : chip);
            cs.setString(4, idColor == null || idColor.isEmpty() ? null : idColor);
            cs.setString(5, idEstado == null || idEstado.isEmpty() ? null : idEstado);
            cs.setString(6, idSeveridad == null || idSeveridad.isEmpty() ? null : idSeveridad);
            cs.setString(7, idNivelEnergia == null || idNivelEnergia.isEmpty() ? null : idNivelEnergia);
            cs.setString(8, idDistrito == null || idDistrito.isEmpty() ? null : idDistrito);
            cs.setString(9, petSize == null || petSize.isEmpty() ? null : petSize);
            cs.setInt(10, requiresMuchSpace);
            cs.setString(11, telefono == null || telefono.isEmpty() ? null : telefono);
            cs.setString(12, email == null || email.isEmpty() ? null : email);
            cs.setString(13, abandonSituationDescription == null || abandonSituationDescription.isEmpty() ? null : abandonSituationDescription);
            cs.setString(14, descriptionNotes == null || descriptionNotes.isEmpty() ? null : descriptionNotes);
            cs.setString(15, idTrainingDifficulty == null || idTrainingDifficulty.isEmpty() ? null : idTrainingDifficulty);

            if (lossDate == null) {
                cs.setNull(16, Types.DATE);
            } else {
                cs.setDate(16, Date.valueOf(lossDate));
            }

            if (foundDate == null) {
                cs.setNull(17, Types.DATE);
            } else {
                cs.setDate(17, Date.valueOf(foundDate));
            }

            cs.setString(18, idVeterinarian == null || idVeterinarian.isEmpty() ? null : idVeterinarian);
            cs.setString(19, idCribHouse == null || idCribHouse.isEmpty() ? null : idCribHouse);
            cs.setString(20, idRescuer == null || idRescuer.isEmpty() ? null : idRescuer);
            cs.setString(21, idAssociation == null || idAssociation.isEmpty() ? null : idAssociation);

            if (beforePicture != null) {
                cs.setBytes(22, beforePicture);
            } else {
                cs.setNull(22, Types.BLOB);
            }

            if (afterPicture != null) {
                cs.setBytes(23, afterPicture);
            } else {
                cs.setNull(23, Types.BLOB);
            }

            cs.setString(24, createdBy);

            cs.setDate(25, birthDate == null ? null : Date.valueOf(birthDate));

            cs.registerOutParameter(26, Types.NUMERIC);

            cs.execute();

            return cs.getInt(26);
        }
    }

    public static void registrarEstadoMascota(
            String idPet,
            String idState,
            LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL REGISTRARESTADOMASCOTA(?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idState);
            cs.setDate(3, startDate != null ? Date.valueOf(startDate) : null);
            cs.setString(4, createdBy);

            cs.execute();
        }
    }

    public static void registrarEnfermedadMascota(
            String idPet,
            String idDisease,
            LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_ENF_MASCOTA(?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idDisease);
            cs.setDate(3, startDate != null ? Date.valueOf(startDate) : null);
            cs.setString(4, createdBy);

            cs.execute();
        }
    }

    public static void registrarTratamientoMascota(
            String idPet,
            String idTreatment,
            LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_TRAT_MASCOTA(?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idTreatment);
            cs.setDate(3, startDate != null ? Date.valueOf(startDate) : null);
            cs.setString(4, createdBy);

            cs.execute();
        }
    }

    public static void registrarMedicamentoMascota(
            String idPet,
            String idMedication,
            String dose,
            LocalDate startDate,
            String createdBy) throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_REGISTRAR_MED_MASCOTA(?,?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idMedication);
            cs.setString(3, dose == null || dose.isEmpty() ? null : dose);
            cs.setDate(4, startDate != null ? Date.valueOf(startDate) : null);
            cs.setString(5, createdBy);

            cs.execute();
        }
    }

    public byte[] obtenerImagenMascota(String idMascota) throws SQLException, ClassNotFoundException {
        String sql = "SELECT beforePicture FROM Pet WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(idMascota));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Blob blob = rs.getBlob("beforePicture");
                if (blob != null) {
                    return blob.getBytes(1, (int) blob.length());
                }
            }
        }
        return null;
    }

    public Map<String, Object> obtenerMascotaPorId(String idMascota) throws SQLException, ClassNotFoundException {
        String sql = "{call SP_GET_DETALLES_PET(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, Integer.parseInt(idMascota));
            cs.registerOutParameter(2, Types.REF_CURSOR);

            cs.execute();




            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                if (rs.next()) {
                    Map<String, Object> datos = new HashMap<>();

                    datos.put("id", rs.getString("id"));
                    datos.put("nombre", rs.getString("name"));
                    datos.put("tipo", rs.getString("tipo"));
                    datos.put("raza", rs.getString("raza"));
                    datos.put("color", rs.getString("color"));

                    datos.put("chip", rs.getString("chip"));

                    datos.put("estado", rs.getString("estado"));
                    datos.put("severidad", rs.getString("severidad"));
                    datos.put("nivelEnergia", rs.getString("nivel_energia"));
                    datos.put("tamanio", rs.getString("tamanio"));
                    datos.put("requiereEspacio", rs.getInt("requiere_espacio"));
                    datos.put("telefono", rs.getString("telephone"));
                    datos.put("email", rs.getString("email"));
                    datos.put("ubicacion", rs.getString("ubicacion"));
                    datos.put("rescatista", rs.getString("rescatista"));
                    datos.put("asociacion", rs.getString("asociacion"));
                    datos.put("veterinario", rs.getString("veterinario"));
                    datos.put("casaCuna", rs.getString("casa_cuna"));
                    datos.put("dificultad", rs.getString("dificultad"));
                    datos.put("fechaPerdida", rs.getString("fecha_perdida"));
                    datos.put("fechaHallazgo", rs.getString("fecha_hallada"));
                    datos.put("descripcionAbandono", rs.getString("abandonSituationDescription"));
                    datos.put("notas", rs.getString("descriptionNotes"));

                    procesarBlob(rs, datos, "beforePicture", "imagenAntes");
                    procesarBlob(rs, datos, "afterPicture", "imagenDespues");

                    return datos;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void procesarBlob(ResultSet rs, Map<String, Object> datos, String colName, String key) throws SQLException {
        Blob blob = rs.getBlob(colName);
        if (blob != null) {
            datos.put(key, blob.getBytes(1, (int) blob.length()));
        } else {
            datos.put(key, null);
        }
    }



    public static ResultadoConsulta buscarMascotasSinAdoptarEstadistica(){

        List<String> columnas = new ArrayList<>();
        ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
        int total = 0;
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{ CALL SP_STATS_SIN_ADOPTAR_EDAD(?,?) }")) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.registerOutParameter(2, Types.NUMERIC);

            cs.execute();

            total = cs.getInt(2);

            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                ResultSetMetaData meta = rs.getMetaData();
                int numCols = meta.getColumnCount();

                for (int i = 1; i <= numCols; i++) {
                    columnas.add(meta.getColumnLabel(i));
                }

                while (rs.next()) {
                    ObservableList<String> fila = FXCollections.observableArrayList();
                    for (int i = 1; i <= numCols; i++) {
                        Object val = rs.getObject(i);
                        if (val instanceof Blob blob) {
                            fila.add("Imagen");
                        }else {
                            fila.add(val != null ? val.toString() : "");
                        }
                    }
                    filas.add(fila);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new ResultadoConsulta(columnas, filas, total);
    }

    public static ResultadoConsulta buscarMascotasSinAdoptar(
        Integer meses,
        Integer idTipo,
        Integer idRaza,
        Integer idColor,
        Integer edad
        ) throws SQLException, ClassNotFoundException {

            List<String> columnas = new ArrayList<>();
            ObservableList<ObservableList<String>> filas = FXCollections.observableArrayList();
            int total = 0;

            try (Connection conn = DBConnection.getConnection();
                 CallableStatement cs = conn.prepareCall(
                         "{ CALL SP_CONSULTAR_SIN_ADOPTAR(?,?,?,?,?,?,?) }")) {

                cs.setObject(1, meses, Types.INTEGER);
                cs.setObject(2, idTipo, Types.INTEGER);
                cs.setObject(3, idRaza, Types.INTEGER);
                cs.setObject(4, idColor, Types.INTEGER);

                cs.registerOutParameter(5, Types.REF_CURSOR);
                cs.registerOutParameter(6, Types.NUMERIC);
                cs.setObject(7, edad, Types.INTEGER);

                cs.execute();

                total = cs.getInt(6);

                try (ResultSet rs = (ResultSet) cs.getObject(5)) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int numCols = meta.getColumnCount();

                    for (int i = 1; i <= numCols; i++) {
                        columnas.add(meta.getColumnLabel(i));
                    }

                    while (rs.next()) {
                        ObservableList<String> fila = FXCollections.observableArrayList();
                        for (int i = 1; i <= numCols; i++) {
                            Object val = rs.getObject(i);
                            if (val instanceof Blob blob) {
                                fila.add("Imagen");
                            }else {
                                fila.add(val != null ? val.toString() : "");
                            }
                        }
                        filas.add(fila);
                    }
                }
            }
            return new ResultadoConsulta(columnas, filas, total);
    }


    public static ObservableList<ObservableList<String>> getMascotas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MASCOTAS");
    }
    public static ObservableList<ObservableList<String>> getRazasTodas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_TODAS_RAZAS");
    }

    public static ObservableList<ObservableList<String>> getMascotasPerdidas()
            throws SQLException, ClassNotFoundException {
        return listadosCatalogo("SP_LISTAR_MASCOTASPERDIDAS");
    }

    public static int marcarHallada(String idPet, String idFoundPet, String modifiedBy)
            throws SQLException, ClassNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{ CALL SP_MARCAR_HALLADA(?,?,?,?) }")) {

            cs.setString(1, idPet);
            cs.setString(2, idFoundPet);
            cs.setString(3, modifiedBy);
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();

            return cs.getInt(4);
        }
    }
}