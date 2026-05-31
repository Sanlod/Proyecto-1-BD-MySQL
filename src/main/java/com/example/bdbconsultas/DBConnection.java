package com.example.bdbconsultas;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Proyecto?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                "PR",
                "Flavio5107");
    }
}
