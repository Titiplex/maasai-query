package main.java.com.aixuniversity.maasaidictionary.dao.normal;

import main.java.com.aixuniversity.maasaidictionary.model.Category;

public class CategoryDao extends AbstractDao<Category> {
    @Override
    protected Class<Category> getEntityClass() {
        return Category.class;
    }

    @Override
    protected String getEntityKey() {
        return "cat";
    }
}
