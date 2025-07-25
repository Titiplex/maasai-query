package com.aixuniversity.maadictionary.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class LanguageConfig {
    private static final String RESOURCE = "language.properties";
    private static final Properties PROPS = new Properties();

    private static Map<String, String> LANG_MAP;

    static {
        PropertiesLoader.load(RESOURCE, PROPS);
        buildLangMap();
    }

    public static Map<String, String> getLangMap() {
        return LANG_MAP;
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

            String lang = PROPS.getProperty(key);
            if (lang == null) continue;
            String c = lang.trim().toLowerCase();
            if (!map.containsKey(c)) map.put(c, key);
        }

        if (LANG_MAP == null || !LANG_MAP.isEmpty()) {
            LANG_MAP = map;
        } else {
            LANG_MAP.putAll(map);
        }
    }

    public static String getFromLang(String lang) {
        return LANG_MAP.getOrDefault(lang, null);
    }
}
