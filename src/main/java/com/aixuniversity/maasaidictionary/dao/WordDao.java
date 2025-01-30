package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.model.Word;

import java.util.List;

public class WordDao implements DaoInterface<Word> {
    @Override
    public void save() {

    }

    @Override
    public <U> List<Word> findAll(U item) {
        return List.of();
    }

    @Override
    public Word findById(int id) {
        return null;
    }
}
