package main.java.com.aixuniversity.maasaidictionary.dao.normal;

import main.java.com.aixuniversity.maasaidictionary.model.Dialect;

public class DialectDao extends AbstractDao<Dialect>{
    @Override
    protected Class<Dialect> getEntityClass() {
        return Dialect.class;
    }

    @Override
    protected String getEntityKey() {
        return "dialect";
    }
}
