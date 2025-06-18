package com.aixuniversity.maadictionary.api;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class SearchController {

    /**
     * Endpoint unifié : /api/v1/search?q=<expression>
     * Exemple :  q=ipa:ŋai   ou   q=enkai
     */
    @GetMapping("/search")
    public List<Vocabulary> search(@RequestParam("q") String raw) throws SQLException {
        log.info("Search request: {}", raw);
        return SearchService.search(raw);
    }
}
