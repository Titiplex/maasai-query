package main.java.com.aixuniversity.maasaidictionary.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Classe utilitaire chargée de charger et d'exposer la configuration (dao-config.properties).
 */
public abstract class DaoConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DaoConfig.class.getResourceAsStream("/src/main/resources/dao-config.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            } else {
                throw new RuntimeException("Fichier dao-config.properties introuvable dans le classpath !");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du fichier dao-config.properties", e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    // Surcharge si vous voulez des valeurs par défaut
    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Retourne le nom de la table associée à une entité (ex: "users" -> "USERS").
     */
    public static String getTableName(String key) {
        return PROPERTIES.getProperty(key + ".tableName");
    }

    /**
     * Retourne la liste des colonnes associées à une entité (ex: "users").
     */
    public static List<String> getColumns(String key) {
        // TODO changer pour retourner directement les noms
        String columns = PROPERTIES.getProperty(key + ".columns");
        if (columns == null) return Collections.emptyList();
        return Arrays.asList(columns.split(","));
    }

    /**
     * Retourne le nom réel d'une colonne en base de données.
     */
    public static String getColumnName(String key, String columnKey) {
        return PROPERTIES.getProperty(key + ".column." + columnKey);
    }

    public static String getColumnType(String key, String columnKey) {
        return PROPERTIES.getProperty(key + ".type." + columnKey);
    }
}
