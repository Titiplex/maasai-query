package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Dialect;

public class DialectDao extends AbstractDao<Dialect>{
    @Override
    public Class<Dialect> getEntityClass() {
        return Dialect.class;
    }

    @Override
    protected String getEntityKey() {
        return "dialect";
    }
}
