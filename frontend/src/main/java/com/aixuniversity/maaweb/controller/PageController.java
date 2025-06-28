package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maadictionary.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

@Controller                       // <-- pas RestController
@RequiredArgsConstructor
public class PageController {

    private final SearchService searchService;

    /** Formulaire vide */
    @GetMapping("/")
    public String home() { return "index"; }

    /** Traitement du formulaire */
    @PostMapping("/search")
    public String handle(@RequestParam String q, Model model) throws SQLException {
        var results = searchService.search(q);
        model.addAttribute("q", q);
        model.addAttribute("results", results);
        return "index";            // ré-affiche la même page avec les données
    }
}
