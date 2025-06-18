package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Dialect;

public class VocabularyDialectDao extends AbstractLinkTableDao<Dialect> {
    @Override
    protected Class<Dialect> returnEntityClass() {
        return Dialect.class;
    }

    @Override
    protected String getLinkTableKey() {
        return "voD";
    }
}
