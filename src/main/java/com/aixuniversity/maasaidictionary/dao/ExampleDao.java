package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.Example;

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
