package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.model.Language;

public class LanguageDao extends AbstractDao<Language> {
    @Override
    protected Class<Language> getEntityClass() {
        return Language.class;
    }

    @Override
    protected String getEntityKey() {
        return "lang";
    }
}
