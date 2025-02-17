package main.java.com.aixuniversity.maasaidictionary.config;

import java.io.IOException;
import java.io.InputStream;
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

    // Méthode d'accès
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    // Surcharge si vous voulez des valeurs par défaut
    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }
}
