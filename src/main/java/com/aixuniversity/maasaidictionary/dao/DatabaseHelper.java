package main.java.com.aixuniversity.maasaidictionary.dao;

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

    public static Connection getConnection() {
        // on ouvre trop de connections à chaque fois et on surcharge le serveur
        // solution : une connection que l'on ferme quand l'application se ferme
        if (conn == null) {
            try {
                Properties props = new Properties();
                props.setProperty("user", DatabaseHelper.USER);
                props.setProperty("password", DatabaseHelper.PASSWORD);
                props.setProperty("database", DatabaseHelper.DATABASE);

                conn = DriverManager.getConnection(URL, props);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("Database connection established.");
            }
        }

        return DatabaseHelper.conn;
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
