package br.com.gestordeprazo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:mariadb://localhost:3306/gestordeprazo";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MariaDB não encontrado.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}