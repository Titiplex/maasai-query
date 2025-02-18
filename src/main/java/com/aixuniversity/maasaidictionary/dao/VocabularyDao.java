package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

public class VocabularyDao extends AbstractDao<Vocabulary> {
    @Override
    protected Class<Vocabulary> getEntityClass() {
        return Vocabulary.class;
    }

    @Override
    protected String getEntityKey() {
        return "vocabulary";
    }
}
