package com.aixuniversity.maadictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.Language;

public class ExampleLanguageDao extends AbstractLinkTableDao<Language> {
    @Override
    protected String getLinkTableKey() {
        return "exLang";
    }

    @Override
    protected Class<Language> returnEntityClass() {
        return Language.class;
    }
}
