package main.java.com.aixuniversity.maasaidictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.dao.normal.AbstractDao;
import main.java.com.aixuniversity.maasaidictionary.model.Dialect;

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
