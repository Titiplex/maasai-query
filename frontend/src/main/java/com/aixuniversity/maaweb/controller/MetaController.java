package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maadictionary.service.ConversionService;
import com.aixuniversity.maadictionary.service.conversion.OrthographyRegistry;
import com.aixuniversity.maaweb.service.meta.CategoryService;
import com.aixuniversity.maaweb.service.meta.PhonemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MetaController {

    private final PhonemeService phonService;
    private final CategoryService catService;

    /* -------- 1) liste des orthographes dispo + "ipa" -------- */
    @GetMapping("/ortho")
    public Set<String> orthographies() {
        Set<String> set = new TreeSet<>(OrthographyRegistry.available()); // payne, official, …
        set.add("ipa");                                                   // orthographe virtuelle
        return set;
    }

    @GetMapping("/orth/convert")
    public String convert(@RequestParam int id, @RequestParam String orth, @RequestParam String ipa) {
        return ConversionService.getMainOrthography(id, ipa, orth);
    }

    @GetMapping("/orth/convert/txt")
    public String convertText(@RequestParam String orth, @RequestParam String text) {
        return ConversionService.toOrthographyKeepingPunct(text, orth);
    }

    /* -------- 2) symboles (graphèmes OU phonèmes) + catégories -------- */
    @GetMapping("/meta")
    public Map<String, List<String>> meta(@RequestParam(defaultValue = "payne") String orth) {
        List<String> cats = catService.listAbbr();          // ["V","C",…]

        List<String> symbols;
        if ("ipa".equalsIgnoreCase(orth)) symbols = new ArrayList<>(phonService.listIpa());
        else symbols = new ArrayList<>(OrthographyRegistry.table(orth).keySet());

        Collections.sort(symbols);
        return Map.of("symbols", symbols, "cats", cats);
    }
}
