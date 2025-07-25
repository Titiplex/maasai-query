package com.aixuniversity.maadictionary.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbbreviationConfig {
    private static final String RESOURCE = "abbreviation.properties";
    private static final Properties PROPS = new Properties();

    private static Map<String, String> ABBR_MAP;

    static {
        PropertiesLoader.load(RESOURCE, PROPS);
        buildAbbreviationMap();
    }

    public static Map<String, String> getAbbrMap() {
        return ABBR_MAP;
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

    /**
     * Même comportement que get(), mais retourne defaultValue si la clé n'existe pas.
     */
    public static String getOrDefault(String key, String defaultValue) {
        String val = get(key);
        return (val != null) ? val : defaultValue;
    }

    private static void buildAbbreviationMap() {
        Map<String, String> map = new HashMap<>();
        for (String key : PROPS.stringPropertyNames()) {

            String cat = PROPS.getProperty(key);
            if (cat == null) continue;
            String c = cat.trim().toLowerCase();
            if (!map.containsKey(c)) map.put(c, key);
        }

        if (ABBR_MAP == null || !ABBR_MAP.isEmpty()) {
            ABBR_MAP = map;
        } else {
            ABBR_MAP.putAll(map);
        }
    }

    public static String getFromAbbreviation(String abbr) {
        return ABBR_MAP.getOrDefault(abbr, null);
    }
}
