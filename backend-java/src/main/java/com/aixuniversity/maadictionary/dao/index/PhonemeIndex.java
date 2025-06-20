// dao/index/PhonemeIndex.java
package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.List;

public final class PhonemeIndex implements SearchIndex<String> {
    private final Object2ObjectOpenHashMap<String, IntArrayList> posting = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<String> freq = new Object2IntOpenHashMap<>();

    public PhonemeIndex(VocabularyDao vocabDao) throws SQLException {
        List<Vocabulary> all = vocabDao.getAll();
        for (Vocabulary v : all) {
            int id = v.getId();
            for (String tok : v.getIpa().trim().split("\\s+")) {
                posting.computeIfAbsent(tok,k -> new IntArrayList()).add(id);
            }
        }
        posting.forEach((k, l) -> freq.put(k, l.size()));
    }

    public IntArrayList idsFor(String token) {
        return posting.getOrDefault(token, new IntArrayList());
    }

    public Object2ObjectOpenHashMap<String, IntArrayList> getPosting() {
        return posting;
    }

    public int frequency(String token) {
        return freq.getOrDefault(token, 0);
    }
}