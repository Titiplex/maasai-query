package com.aixuniversity.maadictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.Category;

public class VocabularyPhonemeCategoryDao extends AbstractLinkTableDao<Category> {
    @Override
    protected String getLinkTableKey() {
        return "vpc";
    }

    @Override
    protected Class<Category> returnEntityClass() {
        return Category.class;
    }
}
