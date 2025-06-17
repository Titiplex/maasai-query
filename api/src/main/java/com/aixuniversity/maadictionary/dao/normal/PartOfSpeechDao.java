package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.PartOfSpeech;

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
