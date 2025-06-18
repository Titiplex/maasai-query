package com.aixuniversity.maadictionary.dao.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class DatabaseHelper {

    public static final String URL = System.getenv("URL");
    public static final String USER = System.getenv("USER");
    public static final String PASSWORD = System.getenv("PASSWORD");
    public static final String DATABASE = System.getenv("DATABASE");
    public static Connection conn = null;

    public static synchronized Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Properties props = new Properties();
                props.setProperty("user",     USER);
                props.setProperty("password", PASSWORD);
                // généralement la database est déjà dans l’URL ; sinon :
                props.setProperty("database", DATABASE);

                conn = DriverManager.getConnection(URL, props);
                System.out.println("Database connection established.");
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to obtain DB connection", e);
        }
    }

    /**
     * Closes the connection with postgresql database.
     */
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Ferme la connexion automatiquement lorsque l'application se termine.

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseHelper::closeConnection));
    }
}
