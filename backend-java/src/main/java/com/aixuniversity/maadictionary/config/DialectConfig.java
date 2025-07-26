package com.aixuniversity.maadictionary.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class DialectConfig {
    private static final String RESOURCE = "dialect.properties";
    private static final Properties PROPS = new Properties();

    private static Map<String, String> Dialect_MAP;

    static {
        PropertiesLoader.load(RESOURCE, PROPS);
        buildLangMap();
    }

    public static Map<String, String> getDialectMap() {
        return Dialect_MAP;
    }

    /**
     * Recharge le fichier pos.properties (utile pour un rechargement à chaud).
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

    /**
     * Même comportement que get(), mais retourne defaultValue si la clé n'existe pas.
     */
    public static String getOrDefault(String key, String defaultValue) {
        String val = get(key);
        return (val != null) ? val : defaultValue;
    }

    private static void buildLangMap() {
        Map<String, String> map = new HashMap<>();
        for (String key : PROPS.stringPropertyNames()) {

            String dialect = PROPS.getProperty(key);
            if (dialect == null) continue;
            String c = dialect.trim().toLowerCase();
            if (!map.containsKey(c)) map.put(c, key);
        }

        if (Dialect_MAP == null || !Dialect_MAP.isEmpty()) {
            Dialect_MAP = map;
        } else {
            Dialect_MAP.putAll(map);
        }
    }

    public static String getFromDialect(String dialect) {
        return Dialect_MAP.getOrDefault(dialect, null);
    }
}
