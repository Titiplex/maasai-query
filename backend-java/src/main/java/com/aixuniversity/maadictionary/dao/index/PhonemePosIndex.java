package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class PhonemePosIndex {
    public record Key(int phonId, byte syl) {
    }

    private final Object2ObjectOpenHashMap<Key, IntArrayList> posting = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Key> freq = new Object2IntOpenHashMap<>();

    public PhonemePosIndex() throws SQLException {
        String sql = "SELECT phonemeId, syllableIndex, vocabularyId FROM VocabularyPhoneme ORDER BY phonemeId, syllableIndex, vocabularyId";
        try (Connection c = DatabaseHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            Key cur = null;
            IntArrayList list = null;
            while (rs.next()) {
                Key k = new Key(rs.getInt(1), rs.getByte(2));
                if (!k.equals(cur)) {
                    list = new IntArrayList();
                    posting.put(k, list);
                    cur = k;
                }
                list.add(rs.getInt(3));
            }
            posting.forEach((k, l) -> freq.put(k, l.size()));
        }
    }

    public IntArrayList idsFor(Key k) {
        return posting.getOrDefault(k, new IntArrayList());
    }

    public int frequency(Key k) {
        return freq.getOrDefault(k, 0);
    }

    public IntArrayList unionIdsFor(int phonId) {
        IntArrayList all = new IntArrayList();
        posting.forEach((k, l) -> {
            if (k.phonId() == phonId) all.addAll(l);
        });
        return all;
    }

    public int unionFreqFor(int phonId) {
        int sum = 0;
        for (var e : posting.object2ObjectEntrySet()) if (e.getKey().phonId() == phonId) sum += e.getValue().size();
        return sum;
    }

}