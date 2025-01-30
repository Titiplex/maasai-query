package main.java.com.aixuniversity.maasaidictionary.dao;

import java.util.List;

interface DatabaseHelper {

    static void save() {

    }

    static <T, U> List<T> findAll(U item) {
        return List.of();
    }

    static <T> T findById(int id) {
        return null;
    }
}
