// service/search/Searcher.java
package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.List;

public interface Searcher<Q> {
    List<Vocabulary> search(Q query) throws SQLException;
}