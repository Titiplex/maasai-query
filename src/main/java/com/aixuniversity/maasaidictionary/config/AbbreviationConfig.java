package main.java.com.aixuniversity.maasaidictionary.config;

import java.util.Properties;

public abstract class AbbreviationConfig {
    private static final String RESOURCE = "abbreviation.properties";
    private static final Properties PROPS = new Properties();

    static {
        PropertiesLoader.load(RESOURCE, PROPS);
    }

    /**
     * Recharge le fichier IPA.properties (utile pour un rechargement à chaud).
     */
    public static void reload() {
        PROPS.clear();
        PropertiesLoader.load(RESOURCE, PROPS);
    }

    public static String get(String key) {
        String raw = PROPS.getProperty(key);
        if (raw == null) return null;
        // supprime tous les espaces (y compris tabulations, retours chariot…)
        return raw.replaceAll("\\s+", "");
    }
}
