package com.aixuniversity.maadictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.Language;

public class MeaningLanguageDao extends AbstractLinkTableDao<Language> {
    @Override
    protected String getLinkTableKey() {
        return "meaningLang";
    }

    @Override
    protected Class<Language> returnEntityClass() {
        return Language.class;
    }
}
