// service/search/IpaSearcher.java
package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.dao.index.PhonemeIndex;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class IpaSearcher implements Searcher<String> {
    private final PhonemeIndex idx;
    private final VocabularyDao dao;

    public IpaSearcher(PhonemeIndex index, VocabularyDao vocabularyDao) {
        idx = index;
        dao = vocabularyDao;
    }

    public IpaSearcher() throws SQLException {
        this.dao = new VocabularyDao();
        this.idx = new PhonemeIndex(this.dao);
    }

    /**
     * DSL rapide :
     * # = frontière mot ; . = frontière syllabe ; ? = joker ; [a b] = set.
     */
    @Override
    public List<Vocabulary> search(String query) throws SQLException {
        Pattern p = Pattern.compile(QueryToRegex.translate(query));
        // pivot = premier token littéral le plus rare
        String pivot = QueryToRegex.pickPivot(query, idx::frequency);
        if (pivot == null) pivot = ""; // cas full‑joker → pas de pivot
        IntArrayList cand = pivot.isEmpty() ? collectAll() : idx.idsFor(pivot);
        List<Vocabulary> out = new ArrayList<>();
        for (int id : cand) {
            Vocabulary v = dao.searchById(id);
            if (v != null && p.matcher(v.getIpa()).find()) out.add(v);
        }
        return out;
    }

    private IntArrayList collectAll() {
        IntArrayList all = new IntArrayList();
        idx.getPosting().values().forEach(all::addAll);  // expose un getter dans PhonemeIndex
        return all;
    }

}