package com.aixuniversity.maadictionary.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class PosConfig {
    private static final String RESOURCE = "pos.properties";
    private static final Properties PROPS = new Properties();

    private static Map<String, String> POS_MAP;

    static {
        PropertiesLoader.load(RESOURCE, PROPS);
        buildPosMap();
    }

    public static Map<String, String> getPosMap() {
        return POS_MAP;
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

    private static void buildPosMap() {
        Map<String, String> map = new HashMap<>();
        for (String key : PROPS.stringPropertyNames()) {

            String cat = PROPS.getProperty(key);
            if (cat == null) continue;
            String c = cat.trim().toLowerCase();
            if (!map.containsKey(c)) map.put(c, key);
        }

        if (POS_MAP == null || !POS_MAP.isEmpty()) {
            POS_MAP = map;
        } else {
            POS_MAP.putAll(map);
        }
    }

    public static String getFromPos(String pos) {
        return POS_MAP.getOrDefault(pos, null);
    }
}
