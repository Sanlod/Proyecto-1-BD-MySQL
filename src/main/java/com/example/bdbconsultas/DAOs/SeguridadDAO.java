package com.example.bdbconsultas.DAOs;

import com.example.bdbconsultas.DBConnection;
import java.sql.*;

public class SeguridadDAO {
    public int validarLogin(String username, String password) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBConnection.getConnection()) {
            try (CallableStatement cs = conn.prepareCall("{ CALL SP_AUTENTICAR_USUARIO(?,?,?,?) }")) {
                //Entradas
                cs.setString(1, username);
                cs.setString(2, password);

                //Salidas
                cs.registerOutParameter(3, Types.NUMERIC); // p_userType
                cs.registerOutParameter(4, Types.NUMERIC); // p_status

                cs.execute();

                int status = cs.getInt(4);
                if (status == 1) {
                    return cs.getInt(3); // Retorna el id de tipo de usuario
                } else {
                    return -1; // Falló el login
                }
            }
        }
    }
}