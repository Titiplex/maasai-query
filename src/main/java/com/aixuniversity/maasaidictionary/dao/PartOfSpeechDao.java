package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.PartOfSpeech;

public class PartOfSpeechDao extends AbstractDao<PartOfSpeech> {
    @Override
    protected Class<PartOfSpeech> getEntityClass() {
        return PartOfSpeech.class;
    }

    @Override
    protected String getEntityKey() {
        return "partOfSpeech";
    }
}
