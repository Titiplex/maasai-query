package main.java.com.aixuniversity.maasaidictionary.service;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.service.search.FlexibleSearcher;
import main.java.com.aixuniversity.maasaidictionary.service.tfidf.ApproximateSearcher;
import main.java.com.aixuniversity.maasaidictionary.service.tfidf.ScoredResult;

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
}