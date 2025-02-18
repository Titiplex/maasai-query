package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.config.SqlStringConfig;
import main.java.com.aixuniversity.maasaidictionary.model.Example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ExampleDao extends AbstractDao<Example> {
    @Override
    protected String getEntityKey() {
        return "ex";
    }

    @Override
    protected Class<Example> getEntityClass() {
        return Example.class;
    }

    @Override
    public Integer insert(Example item) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        String query = SqlStringConfig.getInsertionString(this.getEntityKey());
        PreparedStatement stmt = conn.prepareStatement(query);


    }

    @Override
    public void update(Example item) {

    }

    @Override
    public void delete(Example item) {

    }

    @Override
    public List<Example> getAll() {
        return List.of();
    }
}
