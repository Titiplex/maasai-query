package main.java.com.aixuniversity.maasaidictionary.dao.normal;

import main.java.com.aixuniversity.maasaidictionary.model.Language;

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
