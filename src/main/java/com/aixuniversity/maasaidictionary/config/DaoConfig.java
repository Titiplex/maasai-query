package main.java.com.aixuniversity.maasaidictionary.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Classe utilitaire chargée de charger et d'exposer la configuration (dao-config.properties).
 */
public abstract class DaoConfig {
    private static final Properties PROPERTIES = new Properties();
    private static final String RESOURCE = "dao-config.properties";

    static {
        PropertiesLoader.load(RESOURCE, PROPERTIES);
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
