package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Vocabulary;

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
