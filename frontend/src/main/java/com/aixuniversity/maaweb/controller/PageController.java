package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maaweb.dto.VocabularyDto;
import com.aixuniversity.maaweb.service.MetricsCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/entry")
public class PageController {

    @GetMapping("/{id}")
    public String show(@PathVariable int id, Model model) throws SQLException {
        model.addAttribute("entry", VocabularyDto.from(new VocabularyDao().searchById(id)));
        return "entry";
    }

    @GetMapping("/{id}/metrics")
    public String metrics(@PathVariable int id, Model model) throws SQLException {
        MetricsCatalog metricsCatalog = new MetricsCatalog(new VocabularyDao().searchById(id));
        model.addAttribute("metrics", Map.of(
                "entropy", metricsCatalog.entropy(),
                "vowelRatio", metricsCatalog.vowelRatio()
        ));
        return "metrics";
    }
}
