package main.java.com.aixuniversity.maasaidictionary.dao.normal;

import main.java.com.aixuniversity.maasaidictionary.model.Meaning;

public class MeaningDao extends AbstractDao<Meaning> {
    @Override
    protected Class<Meaning> getEntityClass() {
        return Meaning.class;
    }

    @Override
    protected String getEntityKey() {
        return "meaning";
    }
}
