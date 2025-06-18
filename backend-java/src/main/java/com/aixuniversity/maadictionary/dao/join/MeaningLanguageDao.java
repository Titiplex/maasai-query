package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Language;

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
