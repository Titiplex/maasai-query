package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maaweb.service.PhonemeCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MetaController {

    private final PhonemeCatalog catalog;

    @GetMapping("/meta")
    public Map<String, Object> meta() {
        return Map.of(
                "phonemes", catalog.allPhonemes(),
                "cats", catalog.allCategories(),
                "mods", List.of("", "?", "+", "*")
        );
    }
}
