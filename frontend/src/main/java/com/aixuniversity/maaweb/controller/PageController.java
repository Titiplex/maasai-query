package com.aixuniversity.maaweb.controller;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maaweb.dto.VocabularyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/entry")
public class PageController {


    @GetMapping("/{id}")
    public String show(@PathVariable int id, Model model) throws SQLException {
        model.addAttribute("entry", VocabularyDto.from(new VocabularyDao().searchById(id)));
        return "entry";
    }
}
