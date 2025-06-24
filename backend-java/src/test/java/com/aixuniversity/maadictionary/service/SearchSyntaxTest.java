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
            "u,           3000",
            "i,           1500",
            "[u i].VO,      30",
            "PL|VO,         20",
            "?.PL,          20",
            "#VO,           10",
            "VO#,           10",
            "#VO.PL#,        0"
    })
    void basicQueriesGiveResults(String query, int expectedMin) throws Exception {
        List<Vocabulary> out = searcher.search(query);
        assertTrue(out.size() >= expectedMin,
                () -> "« " + query + " » ne renvoie que " + out.size() + " résultats");
    }

    @ParameterizedTest(name = "wildcard {index} ⇒ \"{0}\" doit retourner au moins {1} résultat(s)")
    @CsvSource({
            "VO+,                  50",
            "ɑ*i,                50",
            "[u i]?PL,            30"
    })
    void repeatsWork(String q, int min) throws Exception {
        List<Vocabulary> out = searcher.search(q);
        assertTrue(out.size() >= min, () -> "« " + q + " » ne renvoie que " + out.size() + " résultats");
    }

    @Test
    void anchorLengthIsExact() throws Exception {
        List<Vocabulary> out = searcher.search("#u.i#");
        assertTrue(out.stream().allMatch(v ->
                        v.getSyll_pattern().split("-").length == 2),
                "Tous les résultats doivent avoir exactement 2 syllabes");
    }

}
