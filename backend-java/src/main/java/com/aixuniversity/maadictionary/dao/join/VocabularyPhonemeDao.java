package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.model.Phoneme;

public class VocabularyPhonemeDao extends MultiInterfaceDao<Phoneme> {
    @Override
    protected String getLinkTableKey() {
        return "voPh";
    }

    @Override
    protected Class<Phoneme> returnEntityClass() {
        return Phoneme.class;
    }
}
