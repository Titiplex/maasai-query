package main.java.com.aixuniversity.maasaidictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

public class VocabularyLinkedDao extends AbstractLinkTableDao<Vocabulary> {

    @Override
    protected String getLinkTableKey() {
        return "linkedWord";
    }

    @Override
    protected Class<Vocabulary> returnEntityClass() {
        return Vocabulary.class;
    }
}
