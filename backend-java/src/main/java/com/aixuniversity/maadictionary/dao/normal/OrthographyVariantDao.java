package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.model.OrthographyVariant;
import com.aixuniversity.maadictionary.model.Vocabulary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class OrthographyVariantDao extends AbstractDao<OrthographyVariant> {

    private static final String BASE_SELECT =
            "SELECT id,vocabularyId,form,script,is_primary,ambiguity_score,ipa_cache " +
                    "FROM OrthographyVariant ";

    public static OrthographyVariant findPrimary(int vocabId) throws SQLException {
        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    BASE_SELECT + "WHERE vocabularyId = ? AND is_primary = TRUE LIMIT 1");
            ps.setInt(1, vocabId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (SQLException e) {
            throw new SQLException("Error mapping OrthographyVariant", e);
        }
    }

    public static OrthographyVariant findByFormExact(String form) throws SQLException {
        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    BASE_SELECT + "WHERE LOWER(form) = LOWER(?) LIMIT 1");
            ps.setString(1, form);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } catch (SQLException e) {
            throw new SQLException("Error mapping OrthographyVariant", e);
        }
    }

    public static void updateIpaCache(int id, String json) throws SQLException {
        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE OrthographyVariant SET ipa_cache = ? WHERE id = ?");
            ps.setString(1, json);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error updating ipa_cache", e);
        }
    }

    /* ---------- mapping utilitaires ---------- */
    private static List<OrthographyVariant> mapAll(ResultSet rs) throws SQLException {
        List<OrthographyVariant> list = new ArrayList<>();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    /**
     * Récupère chaque variante orthographique disposant d’un ipa_cache (JSON valide)
     * et dont le lemme associé possède un champ IPA renseigné.
     * <p>
     * Utilisé par GraphemeProbTrainer pour estimer les probabilités.
     */
    public static List<OrthographyVariant> findAllWithIpaCache() throws SQLException {
        final String sql = """
                    SELECT ov.id,
                           ov.vocabularyId,
                           ov.form,
                           ov.script,
                           ov.is_primary,
                           ov.ambiguity_score,
                           ov.ipa_cache,
                           v.id      AS voc_id,
                           v.entry   AS voc_entry,
                           v.ipa     AS voc_ipa
                    FROM   OrthographyVariant ov
                    JOIN   Vocabulary v ON v.id = ov.vocabularyId
                    WHERE  v.ipa IS NOT NULL
                      AND  ov.ipa_cache IS NOT NULL
                      AND  JSON_VALID(ov.ipa_cache)
                """;

        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<OrthographyVariant> list = new ArrayList<>();

            while (rs.next()) {
                // ---------- OrthographyVariant ----------
                OrthographyVariant ov = map(rs);

                // ---------- Vocabulary (minimale) ----------
                Vocabulary v = new Vocabulary();
                v.setId(rs.getInt("voc_id"));
                v.setEntry(rs.getString("voc_entry"));
                v.setIpa(rs.getString("voc_ipa"));

                ov.setVocabulary(v);
                list.add(ov);
            }
            return list;
        } catch (SQLException e) {
            throw new SQLException("Error mapping OrthographyVariant", e);
        }
    }


    private static OrthographyVariant map(ResultSet rs) throws SQLException {
        // id et vocabulary_id ne sont pas utilisés plus loin ici
        OrthographyVariant ov = new OrthographyVariant();
        ov.setForm(rs.getString("form"));
        ov.setScript(rs.getString("script"));
        ov.setPrimary(rs.getBoolean("is_primary"));
        ov.setAmbiguityScore(rs.getFloat("ambiguity_score"));
        ov.setIpaCache(rs.getString("ipa_cache"));
        return ov;
    }

    @Override
    public Class<OrthographyVariant> getEntityClass() {
        return OrthographyVariant.class;
    }

    @Override
    protected String getEntityKey() {
        return "orthva";
    }
}
