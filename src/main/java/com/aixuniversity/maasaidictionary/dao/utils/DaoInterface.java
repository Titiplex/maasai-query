package main.java.com.aixuniversity.maasaidictionary.dao.utils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DaoInterface<T> {
    Integer insert(T item) throws SQLException;

    Map<T, Integer> insertAll(Collection<T> collection) throws SQLException;

    boolean update(T item) throws SQLException;

    boolean delete(T item) throws SQLException;

    T searchById(int id) throws SQLException;

    List<T> getAll() throws SQLException;

    List<T> getAllFromVocId(int vocId) throws SQLException;
}
