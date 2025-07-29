package com.aixuniversity.maadictionary.dao.utils;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.flywaydb.core.Flyway;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public abstract class DatabaseHelper {

    private static Connection conn;
    private static DB embeddedDB;         // référence pour le shutdown hook

    // Variables d'env
    private static final String URL = System.getenv("URL");
    private static final String USER = System.getenv("USER");
    private static final String PASSWORD = System.getenv("PASSWORD");
    private static final String DATABASE = System.getenv("DATABASE");   // ex. maa

    private static volatile boolean migrated = false;   // ⇦ garantit 1 seule migration

    /* … (variables d'env inchangées) … */

    public static synchronized Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                String url = Optional.ofNullable(URL).orElseGet(DatabaseHelper::buildUrlFromMariaDB4j);
                conn = DriverManager.getConnection(url, buildProps());
                System.out.println("Database connection established.");

                runFlywayMigrationOnce(conn);           // ✨ ICI
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to obtain DB connection", e);
        }
    }

    /* ─────────────────────────────── */
    private static void runFlywayMigrationOnce(Connection c) throws SQLException {
        if (migrated) return;          // déjà fait ?
        synchronized (DatabaseHelper.class) {
            if (migrated) return;
            Flyway.configure()
                    .dataSource(c.getMetaData().getURL(), USER == null ? "root" : USER, PASSWORD)
                    .cleanDisabled(true)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load()
                    .migrate();
            migrated = true;
        }
    }

    /* ======== Construction de l’URL JDBC ======== */
    private static String buildUrlFromMariaDB4j() {
        // 1) Est-ce que Spring a déjà publié DB_PORT ?
        String portProp = System.getProperty("PORT");

        // 2) Sinon on démarre un serveur embarqué « on demand » (tests / CLI)
        if (portProp == null) {
            try {
                File file = new File("db/data");
                System.out.println("ATTENTION !!! Using embedded MariaDB4j at " + file.getAbsolutePath());
                DBConfigurationBuilder cfg = DBConfigurationBuilder.newBuilder()
                        .setPort(0)   // port libre
                        .setDataDir(file);
                embeddedDB = DB.newEmbeddedDB(cfg.build());
                embeddedDB.start();
                portProp = String.valueOf(cfg.getPort());
                System.setProperty("PORT", portProp);
            } catch (ManagedProcessException ex) {
                throw new RuntimeException("Could not start embedded MariaDB4j", ex);
            }
        }

        String db = Optional.ofNullable(DATABASE).orElse("maa");
        return "jdbc:mariadb://localhost:" + portProp + "/" + db +
                "?createDatabaseIfNotExist=true" +
                "&useUnicode=true&characterEncoding=UTF-8";
    }

    /* ======== Propriétés JDBC ======== */
    private static Properties buildProps() {
        Properties p = new Properties();
        p.setProperty("user", Optional.ofNullable(USER).orElse("root"));
        p.setProperty("password", Optional.ofNullable(PASSWORD).orElse(""));
        return p;
    }

    /* ======== Fermeture ======== */
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
            if (embeddedDB != null) embeddedDB.stop();
        } catch (Exception ignored) {
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseHelper::closeConnection));
    }
}
