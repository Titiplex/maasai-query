// maasai-web/src/main/java/com/aixuniversity/maadictionary/web/SearchController.java
package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maadictionary.service.SearchService;
import com.aixuniversity.maaweb.dto.ScoredResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * GET /api/search?q=kasa
     */
    @GetMapping
    public ResponseEntity<List<ScoredResultDto>> search(@RequestParam("q") String q)
            throws SQLException {
        var list = searchService.search(q)
                .stream()
                .map(ScoredResultDto::from)
                .toList();
        return ResponseEntity.ok(list);
    }
}
