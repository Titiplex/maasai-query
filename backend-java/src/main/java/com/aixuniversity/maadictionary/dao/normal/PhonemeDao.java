package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Phoneme;

public class PhonemeDao extends AbstractDao<Phoneme> {
    @Override
    public Class<Phoneme> getEntityClass() {
        return Phoneme.class;
    }

    @Override
    protected String getEntityKey() {
        return "phon";
    }
}
