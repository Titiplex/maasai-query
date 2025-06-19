package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.FlexibleSearcher;
import com.aixuniversity.maadictionary.service.tfidf.ApproximateSearcher;
import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

import java.sql.SQLException;
import java.util.List;

public final class SearchService {
    private final FlexibleSearcher exact = new FlexibleSearcher();
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
        SearchService searchService = new SearchService();

        System.out.println("Search:");
        System.out.println(searchService.search("aeyu"));
    }
}