package main.java.com.aixuniversity.maasaidictionary.dao;

import java.util.Collection;
import java.util.List;

public interface DatabaseInterface<T> {
    void insert(T item);

    void insertAll(Collection<T> collection);

    void update(T item);

    void delete(T item);

    T searchById(int id);

    List<T> getALl();
}
