package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.Word;

public class WordDao extends AbstractDao<Word> {
    @Override
    protected Class<Word> getEntityClass() {
        return Word.class;
    }

    @Override
    protected String getEntityKey() {
        return "";
    }
}
