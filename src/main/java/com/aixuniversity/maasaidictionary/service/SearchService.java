// Revised version of SearchService.java with unified hybrid handling
package main.java.com.aixuniversity.maasaidictionary.service;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.service.search.IpaSearcher;
import main.java.com.aixuniversity.maasaidictionary.service.search.SyllableSearcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified search service that dispatches to IPA or Syllable searchers,
 * and performs hybrid analysis by intersecting both and validating structure.
 */
public final class SearchService {
    private static final IpaSearcher ipaSearcher;
    private static final SyllableSearcher syllableSearcher;

    static {
        try {
            ipaSearcher = new IpaSearcher();
            syllableSearcher = new SyllableSearcher();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize searchers", e);
        }
    }

    /**
     * Main entry point for queries.
     * Automatically detects hybrid, IPA, or syllabic search.
     */
    public static List<Vocabulary> search(String raw) throws SQLException {
        String query = raw;
        if (raw.contains(":")) {
            int sep = raw.indexOf(":");
            query = raw.substring(sep + 1);
        }

        boolean hasCategory = query.chars()
                .anyMatch(c -> Character.isUpperCase(c) && c != 'I'); // crude heuristic for category
        boolean hasLiteral = query.chars()
                .anyMatch(c -> Character.isLowerCase(c) || "ɲŋʃʧʤɾʔ".indexOf(c) >= 0);

        if (hasCategory && hasLiteral) {
            return hybridSearch(query);
        } else if (query.contains("|") || query.contains("-")) {
            return syllableSearcher.search(query);
        } else {
            return ipaSearcher.search(query);
        }
    }

    /**
     * Hybrid search runs IPA search for performance, then structural validation.
     */
    private static List<Vocabulary> hybridSearch(String query) throws SQLException {
        List<Vocabulary> ipaMatches = ipaSearcher.search(query);
        List<Vocabulary> filtered = new ArrayList<>();

        for (Vocabulary v : ipaMatches) {
            if (syllableSearcher.validateHybrid(query, v.getSyllables())) {
                filtered.add(v);
            }
        }
        return filtered;
    }
}