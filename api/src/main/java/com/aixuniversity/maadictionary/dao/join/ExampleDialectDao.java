package com.aixuniversity.maadictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.Dialect;

public class ExampleDialectDao extends AbstractLinkTableDao<Dialect>{
    @Override
    protected String getLinkTableKey() {
        return "exD";
    }

    @Override
    protected Class<Dialect> returnEntityClass() {
        return Dialect.class;
    }
}
