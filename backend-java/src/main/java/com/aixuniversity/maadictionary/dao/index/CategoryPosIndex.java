package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class CategoryPosIndex {
    public record Key(int catId, byte syl, byte pos) {
    }

    private final Object2ObjectOpenHashMap<Key, IntArrayList> posting = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Key> freq = new Object2IntOpenHashMap<>();

    public CategoryPosIndex() throws SQLException {
        String sql = """
                SELECT vpc.category_id, vp.syllableIndex, vp.posSyllable, vp.vocabularyId
                FROM VocabularyPhonemeCategory vpc
                JOIN VocabularyPhoneme vp ON vpc.vocab_phoneme_id = vp.id
                ORDER BY vpc.category_id, vp.syllableIndex, vp.posSyllable, vp.vocabularyId""";
        try (Connection c = DatabaseHelper.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            Key cur = null;
            IntArrayList list = null;
            while (rs.next()) {
                Key k = new Key(rs.getInt(1), rs.getByte(2), rs.getByte(3));
                if (!k.equals(cur)) {
                    list = new IntArrayList();
                    posting.put(k, list);
                    cur = k;
                }
                list.add(rs.getInt(4));
            }
        }
        posting.forEach((k, l) -> freq.put(k, l.size()));
    }

    public IntArrayList idsFor(Key k) {
        return posting.getOrDefault(k, new IntArrayList());
    }

    public int frequency(Key k) {
        return freq.getOrDefault(k, 0);
    }
}