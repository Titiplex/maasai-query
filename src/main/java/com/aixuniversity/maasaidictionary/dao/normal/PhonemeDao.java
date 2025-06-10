package main.java.com.aixuniversity.maasaidictionary.dao.normal;

import main.java.com.aixuniversity.maasaidictionary.model.Phoneme;

public class PhonemeDao extends AbstractDao<Phoneme> {
    @Override
    protected Class<Phoneme> getEntityClass() {
        return Phoneme.class;
    }

    @Override
    protected String getEntityKey() {
        return "phon";
    }
}
