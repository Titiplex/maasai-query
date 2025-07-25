package com.aixuniversity.maadictionary.app;

import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

/**
 * Centralised bookkeeping for:
 * • external import runs   (table ImportStatus)
 * • vocabulary index state (table VocabularyAudit)
 */
public final class ImportStatus {

    private static final Connection c = DatabaseHelper.getConnection();
    private static final String DEFAULT_SRC = "oregon-maa-dictionary";

    private ImportStatus() {/* util class */}

    /**
     * True when the remote payload hash differs from the last stored one.
     */
    public static boolean needsImport(String url) throws SQLException {
        String newHash;
        try {
            newHash = computeSha256(new ByteArrayInputStream(payload(url)));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        String oldHash = null;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT payload_hash FROM ImportStatus WHERE source = ?")) {
            ps.setString(1, DEFAULT_SRC);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) oldHash = rs.getString(1);
        }
        return !Objects.equals(oldHash, newHash);
    }

    /**
     * Records a successful import together with the payload hash.
     */
    public static void recordImport(String url) throws SQLException {
        String newHash;
        try {
            newHash = computeSha256(new ByteArrayInputStream(payload(url)));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement ps = c.prepareStatement(
                """
                        INSERT INTO ImportStatus(source, last_import_on, payload_hash)
                        VALUES(?, CURRENT_TIMESTAMP, ?)
                        ON DUPLICATE KEY UPDATE
                            last_import_on = CURRENT_TIMESTAMP,
                            payload_hash   = VALUES(payload_hash)
                        """)) {
            ps.setString(1, DEFAULT_SRC);
            ps.setString(2, newHash);
            ps.executeUpdate();
        }
    }

    /* ---------------------------------------------------- *
     *  2.  INDEX state bookkeeping                         *
     * ---------------------------------------------------- */

    /**
     * Vocabulary IDs that still need (re)-indexing:
     * • never indexed          (last_indexed IS NULL)
     * • modified after index   (last_indexed < last_modified)
     */
    public static List<Integer> unindexedVocabularyIds() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                """
                        SELECT v.id
                        FROM   Vocabulary v
                        LEFT   JOIN VocabularyAudit a ON a.vocabulary_id = v.id
                        WHERE  a.last_indexed IS NULL
                           OR  a.last_indexed < a.last_modified
                           OR  a.last_modified IS NULL          -- audit row missing
                        """)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getInt(1));
        }
        return ids;
    }

    /**
     * Marks one vocabulary entry as indexed “now”.
     * If no audit row exists yet, it will be created.
     */
    public static void markIndexed(int vocabId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                """
                        INSERT INTO VocabularyAudit(vocabulary_id, last_modified, last_indexed)
                        VALUES(?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                        ON DUPLICATE KEY UPDATE
                            last_indexed = CURRENT_TIMESTAMP
                        """)) {
            ps.setInt(1, vocabId);
            ps.executeUpdate();
        }
    }

    /**
     * Call this right after inserting / updating a Vocabulary row.
     */
    public static void markModified(int vocabId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO VocabularyAudit(vocabulary_id, last_modified)
                VALUES (?, CURRENT_TIMESTAMP)
                ON DUPLICATE KEY UPDATE last_modified = CURRENT_TIMESTAMP
                """)) {
            ps.setInt(1, vocabId);
            ps.executeUpdate();
        }
    }

    private static byte[] payload(String remoteUrl) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(remoteUrl)).GET().build();
            return client.send(request, HttpResponse.BodyHandlers.ofByteArray())
                    .body();
        }
    }

    /**
     * Reads the complete file located at {@code path} and returns its
     * SHA-256 as a lower-case 64-character hex string.
     */
    @SuppressWarnings("unused")
    private static String computeSha256(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return computeSha256(in);
        }
    }

    /**
     * Streams <b>any</b> input (HTTP body, ZIP entry, …) and returns the
     * SHA-256 hex.
     */
    private static String computeSha256(InputStream in) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buf = new byte[8 * 1024];
            for (int r; (r = in.read(buf)) != -1; ) {
                md.update(buf, 0, r);
            }
            return HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available; convert to unchecked just in case
            throw new IllegalStateException(e);
        }
    }

    /**
     * Very small console progress bar:
     * ProgressBar.print(ix, total);
     * The bar is redrawn on the same line (carriage-return, no newline).
     */
    public static final class ProgressBar {

        private static final int BAR_WIDTH = 50;     // characters

        private ProgressBar() { /* static helper */ }

        public static void print(int current, int total) {
            if (total <= 0) return;

            int filled = (int) (current * BAR_WIDTH / (double) total);
            String sb = "\r[" + "█".repeat(filled) +
                    " ".repeat(BAR_WIDTH - filled) +
                    "] " +
                    String.format("%d/%d", current, total);

            System.out.print(sb);

            if (current >= total) System.out.println();   // final newline
        }
    }

}