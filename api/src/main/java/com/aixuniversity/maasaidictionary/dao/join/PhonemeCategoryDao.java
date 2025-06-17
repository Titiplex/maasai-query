package main.java.com.aixuniversity.maasaidictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.Category;

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
