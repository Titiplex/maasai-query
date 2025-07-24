package com.aixuniversity.maaweb.service;

import com.aixuniversity.maadictionary.dao.normal.CategoryDao;
import com.aixuniversity.maadictionary.dao.normal.PhonemeDao;
import com.aixuniversity.maadictionary.model.Category;
import com.aixuniversity.maadictionary.model.Phoneme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SqlCatalog implements PhonemeCatalog {

    private final PhonemeDao phonemeDao;
    private final CategoryDao categoryDao;

    @Override
    public List<String> allPhonemes() {
        try {
            return phonemeDao.getAll().stream().map(Phoneme::getIpa).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> allCategories() {
        try {
            return categoryDao.getAll().stream().map(Category::getAbbr).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
