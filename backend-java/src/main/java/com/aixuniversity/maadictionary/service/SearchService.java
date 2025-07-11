package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.Searcher;
import com.aixuniversity.maadictionary.service.search.SimpleSequentialSearcher;
import com.aixuniversity.maadictionary.service.tfidf.ApproximateSearcher;
import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public final class SearchService {
    private final Searcher<String> exact = new SimpleSequentialSearcher();
    private final ApproximateSearcher approx = new ApproximateSearcher();

    public SearchService() throws SQLException {
    }

    /**
     * @return liste de vocabulaires – exact ou top‑k approximatifs (k=25)
     */
    public List<ScoredResult> search(String raw) throws SQLException {
        List<Vocabulary> exactHits = exact.search(raw);
        if (!exactHits.isEmpty())
            return exactHits.stream().map(v -> new ScoredResult(v, 1.0)).toList();
        return approx.searchAndRank(raw, 25);
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