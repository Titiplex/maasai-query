package com.aixuniversity.maadictionary.api;

import com.aixuniversity.maadictionary.bridge.LegacyFacade;
import com.aixuniversity.maadictionary.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vocabulary")
@RequiredArgsConstructor
public class VocabularyController {

    private final LegacyFacade legacy;

    @GetMapping("/{lemma}")
    public Vocabulary get(@PathVariable String lemma) throws SQLException {
        return legacy.one(lemma);
    }

    @GetMapping
    public List<Vocabulary> list() throws SQLException {
        return legacy.all();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Vocabulary v) throws SQLException {
        legacy.save(v);
    }
}
