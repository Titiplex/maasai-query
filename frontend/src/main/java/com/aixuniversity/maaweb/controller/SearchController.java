package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.service.SearchService;
import com.aixuniversity.maaweb.dto.PageDto;
import com.aixuniversity.maaweb.dto.ScoredResultDto;
import com.aixuniversity.maaweb.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 50 résultats max par page
     */
    @GetMapping("/search")
    public PageDto<ScoredResultDto> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) throws SQLException {
        var all = searchService.search(q);
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());

        var slice = all.subList(from, to).stream()
                .map(ScoredResultDto::from).toList();
        return new PageDto<>(all.size(), page, size, slice);
    }

    /**
     * Détail d’une entrée (clic dans la liste)
     */
    @GetMapping("/vocab/{id}")
    public VocabularyDto vocab(@PathVariable int id) throws SQLException {
        return VocabularyDto.from(new VocabularyDao().searchById(id));
    }
}
