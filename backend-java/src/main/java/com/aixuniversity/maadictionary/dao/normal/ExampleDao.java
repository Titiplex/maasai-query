package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Example;

public class ExampleDao extends AbstractDao<Example> {
    @Override
    protected String getEntityKey() {
        return "ex";
    }

    @Override
    protected Class<Example> getEntityClass() {
        return Example.class;
    }
}
