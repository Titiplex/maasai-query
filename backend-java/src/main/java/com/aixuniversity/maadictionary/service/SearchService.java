package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.conversion.Orthography2Ipa;
import com.aixuniversity.maadictionary.service.search.Searcher;
import com.aixuniversity.maadictionary.service.search.SimpleSequentialSearcher;
import com.aixuniversity.maadictionary.service.tfidf.ApproximateSearcher;
import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public final class SearchService {
    private final Searcher<String> exact = new SimpleSequentialSearcher();
    private final ApproximateSearcher approx = new ApproximateSearcher();
    private static final String DEFAULT_ORTH = "payne";


    public SearchService() throws SQLException {
    }

    /**
     * @return liste de vocabulaires – exact ou top‑k approximatifs (k=25)
     */
    public List<ScoredResult> search(String raw) throws SQLException {
        System.out.println("[New query] Search: " + raw);
        String orth = DEFAULT_ORTH;
        String q = raw.trim();
        int sep = q.indexOf("::");
        if (sep > 0) {
            orth = q.substring(0, sep).toLowerCase(Locale.ROOT);
            q = q.substring(sep + 2);
        }
        System.out.println("[Current query] Search orthographie: " + orth);
        System.out.println("[Current query] Search query: " + q);

        List<Vocabulary> exactHits = new ArrayList<>();
        String rawQuery;
        if (orth.equalsIgnoreCase("ipa")) {
            System.out.println("[Current query] Search exact: " + q);
            // Requête IPA directe
            exactHits = exact.search(q);
            rawQuery = q;
        } else {
            // Conversion orthographie → IPA (probabiliste)
            List<Orthography2Ipa.Path> paths;
            try {
                System.out.println("[Current query] Converting orthography to IPA...");
                paths = Orthography2Ipa.convert(orth, q);
                System.out.println("[Current query] Converted to IPA: " + paths);
            } catch (IllegalArgumentException e) {
                // Caractère inconnu : on renvoie aucun résultat
                return List.of();
            }

            // Requête booléenne OR : (ipa1|ipa2|…)
            for (Orthography2Ipa.Path path : paths) {
                exactHits.addAll(exact.search(path.ipa()));
            }
            rawQuery = paths.getFirst().ipa();
        }

        if (!exactHits.isEmpty()) {
            System.out.println("[Current query] Exact hits :" + exactHits.size());
            return exactHits.stream().map(v -> new ScoredResult(v, 1.0)).toList();
        }
        System.out.println("[Current query] No exact hits.");
        System.out.println("[Current query] Searching with approximate searcher...");

        // fallback approx (toujours avec IPA la plus probable)
        return approx.searchAndRank(rawQuery, 25);
    }


    public static void main(String[] args) throws SQLException {
        try (Scanner scanner = new Scanner(System.in)) {
            SearchService searchService = new SearchService();   // reuse the service instance
            while (true) {
                System.out.print("Search (blank to quit): ");
                String query = scanner.nextLine().trim();
                if (query.isBlank()) {
                    System.out.println("Bye!");
                    break;
                }
                try {
                    List<ScoredResult> results = searchService.search(query);
                    if (results.isEmpty()) {
                        System.out.println("No results. Please check if all the characters in the query are known.");
                    } else {
                        System.out.println("Found " + results.size() + " result(s) :");
                        AtomicInteger i = new AtomicInteger(1);
                        results.forEach(r -> {
                            System.out.println(i + ". " + r);
                            i.getAndIncrement();
                        });
                    }
                } catch (SQLException e) {
                    System.err.println("Search failed: " + e.getMessage());
                }
            }
        }
    }
}