package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.model.GraphemeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;

public final class GraphemeMapDao extends AbstractDao<GraphemeMap> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void upsert(String orth, String grapheme, String[] ipa, float[] prob) throws SQLException {
        if (ipa.length != prob.length || ipa.length == 0)
            throw new IllegalArgumentException("ipa / prob size mismatch");

        StringJoiner ipaJson = new StringJoiner("\",\"", "[\"", "\"]");
        StringJoiner probJson = new StringJoiner(",", "[", "]");
        for (int i = 0; i < ipa.length; i++) {
            ipaJson.add(ipa[i].replace("\"", "\\\""));
            probJson.add(Float.toString(prob[i]));
        }

        String sql = """
                    INSERT INTO GraphemeMap(orthography, grapheme, ipa_options, likelihood)
                    VALUES (?,?,?,?)
                    ON DUPLICATE KEY UPDATE
                           ipa_options = VALUES(ipa_options),
                           likelihood  = VALUES(likelihood)
                """;

        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, orth.toLowerCase());
            ps.setString(2, grapheme);
            ps.setString(3, ipaJson.toString());   // ex: ["a","ɛ"]
            ps.setString(4, probJson.toString());  // ex: [0.7,0.3]
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error mapping GraphemeMap", e);
        }
    }


    /**
     * Renvoie true si la table est vide (pour le boot).
     */
    public boolean isEmpty() throws SQLException {
        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT 1 FROM GraphemeMap LIMIT 1");
            return !ps.executeQuery().next();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public static GraphemeMap find(String grapheme) throws SQLException {
        String sql = "SELECT ipa_options,likelihood FROM GraphemeMap WHERE grapheme = ?";
        try {
            Connection c = DatabaseHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, grapheme);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            GraphemeMap gm = new GraphemeMap();
            gm.setGrapheme(grapheme);
            gm.setIpaOptions(MAPPER.readValue(rs.getString(1), new TypeReference<>() {
            }));
            gm.setLikelihood((float[]) rs.getArray(2).getArray());
            return gm;
        } catch (Exception e) {
            throw new SQLException("Error mapping GraphemeMap", e);
        }
    }

    @Override
    public Class<GraphemeMap> getEntityClass() {
        return GraphemeMap.class;
    }

    @Override
    protected String getEntityKey() {
        return "grmap";
    }
}
