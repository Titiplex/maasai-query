package com.aixuniversity.maaweb.service.meta;

import com.aixuniversity.maadictionary.dao.normal.CategoryDao;
import com.aixuniversity.maadictionary.model.Category;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryDao dao = new CategoryDao();

    public List<String> listAbbr() {
        try {
            return dao.getAll().stream().map(Category::getAbbr).toList();       // ["V", "C", "NC", …]
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load category list", e);
        }
    }
}
