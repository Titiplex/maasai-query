package com.aixuniversity.maadictionary.service.conversion;

import com.aixuniversity.maadictionary.app.ImportStatus;
import com.aixuniversity.maadictionary.dao.normal.GraphemeMapDao;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.model.Phoneme;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public final class OrthographyRegistry {

    private static final Pattern SEP = Pattern.compile("\\s*,\\s*");

    /**
     * orthName -> grapheme -> mapping
     */
    private static final Map<String, Map<String, GraphemeMapping>> MAP = new HashMap<>();

    static {
        try {
            // 1) Toujours disponibles (aucune BD)
            loadProperties();
            loadPayneOrthography();

            // 2) Best-effort: surcharge depuis la BD si dispo.
            try { loadFromDatabase(); }
            catch (SQLException e) {
                System.err.println("[OrthographyRegistry] DB load skipped: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void loadProperties() throws IOException {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();
        // toutes les ressources *.properties sous /orthography
        Resource[] files = resolver.getResources("classpath*:orthography/*.properties");

        for (Resource res : files) {
            String filename = Objects.requireNonNull(res.getFilename());
            try (InputStream in = res.getInputStream()) {
                loadFile(filename, in);
            }
        }
    }

    private static boolean loadFromDatabase() throws SQLException {
        GraphemeMapDao dao = new GraphemeMapDao();
        if (dao.isEmpty()) return false;

        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT orthography, grapheme, ipa_options, likelihood FROM GraphemeMap");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String orth = rs.getString(1).toLowerCase(Locale.ROOT);
                String g = rs.getString(2);

                String[] ipa = rs.getString(3)  // ["o","ɔ"]
                        .replace("[", "").replace("]", "")
                        .replace("\"", "").split("\\s*,\\s*");

                String[] pStr = rs.getString(4)  // [0.7,0.3]
                        .replace("[", "").replace("]", "")
                        .split("\\s*,\\s*");
                float[] prob = new float[pStr.length];
                for (int i = 0; i < pStr.length; i++) {
                    prob[i] = Float.parseFloat(pStr[i]);
                }

                MAP.computeIfAbsent(orth, _ -> new HashMap<>())
                        .put(g, new GraphemeMapping(ipa, prob));
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadPayneOrthography() {
        Map<String, GraphemeMapping> map = new HashMap<>();
        Phoneme.getPhonemeList().forEach((_, ph) -> map.put(ph.getCode(), new GraphemeMapping(new String[]{ph.getIpa()}, new float[]{1f})));
        MAP.put("payne", map);
    }

    private static void loadFile(String filename, InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        String orthName = filename.substring(0, filename.indexOf('.'));
        Map<String, GraphemeMapping> map = new HashMap<>();
        props.forEach((k, v) -> map.put(k.toString(), parse(v.toString())));
        MAP.put(orthName, map);
    }

    private static GraphemeMapping parse(String v) {
        String[] parts = SEP.split(v.trim());
        String[] ipa = new String[parts.length];
        float[] prob = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String[] p = parts[i].split(":");
            ipa[i] = p[0];
            prob[i] = (p.length == 2) ? Float.parseFloat(p[1]) : 1f;
        }
        return new GraphemeMapping(ipa, prob);
    }

    /**
     * Renvoie la table grapheme→mapping pour une orthographie ("payne", "official", …)
     */
    public static Map<String, GraphemeMapping> table(String orth) {
        Map<String, GraphemeMapping> t = MAP.get(orth.toLowerCase(Locale.ROOT));
        if (t == null) throw new IllegalArgumentException("Unknown orthography '" + orth + "'");
        return t;
    }

    /**
     * orthographies disponibles, ex. ["payne","official"]
     */
    public static Set<String> available() {
        return MAP.keySet();
    }
}
