package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Category;

public class VocabularyPhonemeCategoryDao extends MultiInterfaceDao<Category> {
    @Override
    protected String getLinkTableKey() {
        return "vpc";
    }

    @Override
    protected Class<Category> returnEntityClass() {
        return Category.class;
    }
}
