package main.java.com.aixuniversity.maasaidictionary.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DatabaseInterface<T> {
    Integer insert(T item) throws SQLException;

    Map<T, Integer> insertAll(Collection<T> collection) throws SQLException;

    void update(T item) throws SQLException;

    void delete(T item) throws SQLException;

    T searchById(int id) throws SQLException;

    List<T> getAll() throws SQLException;
}
