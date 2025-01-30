package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.Meaning;
import main.java.com.aixuniversity.maasaidictionary.model.Word;

import java.util.List;

public class MeaningDao implements DaoInterface<Meaning> {
    @Override
    public void save() {

    }

    @Override
    public <U> List<Meaning> findAll(U item) {
        return List.of();
    }

    @Override
    public Meaning findById(int id) {
        return null;
    }
}
