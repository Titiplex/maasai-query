package com.aixuniversity.maadictionary.bridge;

import com.aixuniversity.maadictionary.service.SearchService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/** Initialise la registry Searcher<> au démarrage de Spring. */
@Component
public class SearchStartup {
    @PostConstruct
    public void init() {
        SearchService.register();
    }
}