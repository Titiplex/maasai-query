// service/search/Searcher.java
package main.java.com.aixuniversity.maasaidictionary.service.search;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.List;

public interface Searcher<Q> {
    List<Vocabulary> search(Q query) throws SQLException;
}