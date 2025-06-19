// dao/index/CategoryIndex.java
package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.*;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class CategoryIndex implements SearchIndex<Integer> {
    private final Int2ObjectMap<IntArrayList> posting = new Int2ObjectOpenHashMap<>();
    private final Int2IntMap freq = new Int2IntOpenHashMap();

    public CategoryIndex() throws SQLException {
        try (Connection c = DatabaseHelper.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                     SELECT categoryId, vocabularyId
                     FROM VocabularyPhonemeCategory
                     ORDER BY categoryId, vocabularyId""")) {
            ResultSet rs = ps.executeQuery();
            int cur = -1;
            IntArrayList lst = null;
            while (rs.next()) {
                int cat = rs.getInt(1);
                int vid = rs.getInt(2);
                if (cat != cur) {
                    lst = new IntArrayList();
                    posting.put(cat, lst);
                    cur = cat;
                }
                assert lst != null;
                lst.add(vid);
            }
        }
        posting.forEach((k, l) -> freq.put(k.intValue(), l.size()));
    }

    public IntArrayList idsFor(Integer cat) {
        return posting.getOrDefault(cat.intValue(), new IntArrayList());
    }

    public int frequency(Integer cat) {
        return freq.getOrDefault(cat.intValue(), 0);
    }
}