package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.Optional;

public final class CategoryFlatIndex implements SearchFlatIndex<Integer> {
    private final it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<Integer, IntArrayList> post = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>();
    private final it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<Integer> freq = new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<>();
    private int total = 0;

    public CategoryFlatIndex() throws SQLException {
        var sql = """
                SELECT vpc.category_id, vp.vocabularyId FROM VocabularyPhonemeCategory vpc
                JOIN VocabularyPhoneme vp ON vpc.vocab_phoneme_id = vp.id
                ORDER BY category_id, vocabularyId
                """;
        try (var c = DatabaseHelper.getConnection();
             var ps = c.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            IntArrayList list = null;
            int cur = -1;
            while (rs.next()) {
                int cid = rs.getInt(1), vid = rs.getInt(2);
                if (cid != cur) {
                    list = new IntArrayList();
                    post.put(cid, list);
                    cur = cid;
                }
                assert list != null;
                list.add(vid);
                total++;
            }
        }
        post.forEach((k, l) -> freq.put(k, l.size()));
    }

    @Override
    public IntArrayList idsFor(Integer cid) {
        return post.getOrDefault(cid, new IntArrayList());
    }

    @Override
    public int frequency(Integer cid) {
        return freq.getOrDefault(Optional.of(cid), 0);
    }

    @Override
    public int totalFreq() {
        return total;
    }

    @Override
    public IntArrayList allIds() {
        IntArrayList all = new IntArrayList();
        post.values().forEach(all::addAll);
        return all;
    }
}