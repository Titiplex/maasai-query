package main.java.com.aixuniversity.maasaidictionary.config;

import java.util.*;

/**
 * Utilitaire de configuration IPA.
 * <p>
 * - Lit IPA.properties en UTF‑8 (tous les symboles IPA sont supportés).
 * - expose get(key), getCategory(key), getAllVowels(), getAllConsonants(), reload().
 */
public abstract class IPAConfig {
    private static final String RESOURCE = "IPA.properties";
    private static final Properties PROPS = new Properties();
    private static Set<String> vowels, consonants;
    private static Map<String, String> IPA_MAP;

    static {
        PropertiesLoader.load(RESOURCE, PROPS);
        buildIPAMap();
    }

    /**
     * Recharge le fichier IPA.properties (utile pour un rechargement à chaud).
     */
    public static void reload() {
        PROPS.clear();
        vowels = consonants = null;
        PropertiesLoader.load(RESOURCE, PROPS);
    }

    /**
     * Valeur IPA pour le symbole (key) ou null si absent.
     */
    public static String get(String key) {
        String raw = PROPS.getProperty(key + ".value");
        if (raw == null) return null;
        // supprime tous les espaces (y compris tabulations, retours chariot…)
        return raw.replaceAll("\\s+", "");
    }


    /**
     * Catégories (virgule‑séparées) pour le symbole (key).
     */
    public static String getCategory(String key) {
        String raw = PROPS.getProperty(key + ".category");
        if (raw == null) return null;
        // supprime tous les espaces (y compris tabulations, retours chariot…)
        return raw.replaceAll("\\s+", "");
    }

    /**
     * Toutes les clés classées comme “vowel”.
     */
    public static Set<String> getAllVowels() {
        if (vowels == null) {
            Set<String> set = new HashSet<>();
            for (String name : PROPS.stringPropertyNames()) {
                if (name.endsWith(".category")) {
                    String sym = name.substring(0, name.length() - 9);
                    String cat = PROPS.getProperty(name);
                    if (cat != null && Arrays.stream(cat.split(","))
                            .anyMatch(s -> s.trim().equalsIgnoreCase("vowel"))) {
                        set.add(sym);
                    }
                }
            }
            vowels = Collections.unmodifiableSet(set);
        }
        return vowels;
    }

    /**
     * Toutes les clés définies moins celles qui sont des voyelles.
     */
    public static Set<String> getAllConsonants() {
        if (consonants == null) {
            Set<String> all = new HashSet<>();
            for (String name : PROPS.stringPropertyNames()) {
                if (name.endsWith(".value")) {
                    String sym = name.substring(0, name.length() - 6);
                    all.add(sym);
                }
            }
            all.removeAll(getAllVowels());
            consonants = Collections.unmodifiableSet(all);
        }
        return consonants;
    }

    private static void buildIPAMap() {
        Map<String, String> map = new HashMap<>();
        for (String name : PROPS.stringPropertyNames()) {
            if (name.endsWith(".value")) {
                String key = name.substring(0, name.length() - ".value".length());
                String cat = PROPS.getProperty(name);
                if (cat == null) continue;
                String c = cat.trim().toLowerCase();
                if (!map.containsKey(c)) map.put(c, key);
            }
        }

        if (IPA_MAP == null || !IPA_MAP.isEmpty()) {
            IPA_MAP = map;
        } else {
            IPA_MAP.putAll(map);
        }
    }

    public static String getLetterFromIPA(String key) {
        return IPA_MAP.getOrDefault(key, null);
    }
}
