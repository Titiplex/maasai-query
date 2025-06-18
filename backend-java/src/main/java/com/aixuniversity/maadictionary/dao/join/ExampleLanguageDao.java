package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Language;

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
