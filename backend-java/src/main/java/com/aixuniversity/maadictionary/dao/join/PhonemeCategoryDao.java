package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Category;

public class PhonemeCategoryDao extends AbstractLinkTableDao<Category>{
    @Override
    protected String getLinkTableKey() {
        return "phoncat";
    }

    @Override
    protected Class<Category> returnEntityClass() {
        return Category.class;
    }
}
