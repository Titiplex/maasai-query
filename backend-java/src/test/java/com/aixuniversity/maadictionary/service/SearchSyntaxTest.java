package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.Searcher;
import com.aixuniversity.maadictionary.service.search.SimpleSequentialSearcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchSyntaxTest {

    private static Searcher<String> searcher;

    @BeforeAll
    static void initSearcher() throws SQLException {
        searcher = new SimpleSequentialSearcher();
    }

    @ParameterizedTest(name = "{index} ⇒ \"{0}\" doit retourner au moins {1} résultat(s)")
    @CsvSource({
            // requête , minRésultats
            "u,               1000",
            "u.i,             100",
            "ui,              100",
            "C|V,             50",
            "[u i].V,         10",
            "?.C,             10",
            "#u,              10",
            "u#,              10",
            "#u.i#,           5",
            "#C.V#,           5"
    })
    void basicQueriesGiveResults(String query, int expectedMin) throws Exception {
        List<Vocabulary> out = searcher.search(query);
        assertTrue(out.size() >= expectedMin,
                () -> "« " + query + " » ne renvoie que " + out.size() + " résultats");
    }

    @Test
    void anchorLengthIsExact() throws Exception {
        List<Vocabulary> out = searcher.search("#u.i#");
        assertTrue(out.stream().allMatch(v ->
                        v.getSyll_pattern().split("-").length == 2),
                "Tous les résultats doivent avoir exactement 2 syllabes");
    }
}
