package com.aixuniversity.maadictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.model.PartOfSpeech;

public class PosLinkedDao extends AbstractLinkTableDao<PartOfSpeech> {

    @Override
    protected String getLinkTableKey() {
        return "vocPOS";
    }

    @Override
    protected Class<PartOfSpeech> returnEntityClass() {
        return PartOfSpeech.class;
    }
}
