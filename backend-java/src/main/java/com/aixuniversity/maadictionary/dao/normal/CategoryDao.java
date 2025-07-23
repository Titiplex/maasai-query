package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Category;

public class CategoryDao extends AbstractDao<Category> {
    @Override
    public Class<Category> getEntityClass() {
        return Category.class;
    }

    @Override
    protected String getEntityKey() {
        return "cat";
    }
}
