package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Vocabulary;

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
