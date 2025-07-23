package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Meaning;

public class MeaningDao extends AbstractDao<Meaning> {
    @Override
    public Class<Meaning> getEntityClass() {
        return Meaning.class;
    }

    @Override
    protected String getEntityKey() {
        return "meaning";
    }
}
