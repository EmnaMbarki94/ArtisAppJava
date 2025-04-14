package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/artisdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DBConnection instance;
    private Connection cnx;

    private DBConnection() {
        createConnection();
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void createConnection() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Nouvelle connexion établie !");
        } catch (SQLException e) {
            System.out.println("p20");
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                createConnection();
            }
        } catch (SQLException e) {
            System.err.println("Erreur de vérification : " + e.getMessage());
        }
        return cnx;
    }
}