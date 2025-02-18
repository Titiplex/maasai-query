package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.LinkedVocabulary;

public class LinkedVocabularyDao extends AbstractDao<LinkedVocabulary> {
    @Override
    protected Class<LinkedVocabulary> getEntityClass() {
        return LinkedVocabulary.class;
    }

    @Override
    protected String getEntityKey() {
        return "linkedWord";
    }
}
