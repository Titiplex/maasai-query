package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.sql.SQLException;

public final class PhonemeFlatIndex implements SearchFlatIndex<Integer> {
    private final Object2ObjectOpenHashMap<Integer, IntArrayList> post = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<Integer> freq = new Object2IntOpenHashMap<>();
    private int total = 0;

    public PhonemeFlatIndex() throws SQLException {
        var sql = "SELECT phonemeId, vocabularyId FROM VocabularyPhoneme ORDER BY phonemeId, vocabularyId";
        try (var ps = db.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            IntArrayList list = null;
            int cur = -1;
            while (rs.next()) {
                int pid = rs.getInt(1), vid = rs.getInt(2);
                if (pid != cur) {
                    list = new IntArrayList();
                    post.put(pid, list);
                    cur = pid;
                }
                assert list != null;
                list.add(vid);
                total++;
            }
        }
        post.forEach((k, l) -> freq.put(k, l.size()));
//        System.out.println("phonflatind : " + post + "\n" + freq + "\n" + total);
    }

    @Override
    public IntArrayList idsFor(Integer pid) {
        return post.getOrDefault(pid, new IntArrayList());
    }

    @Override
    public int frequency(Integer pid) {
        return freq.getOrDefault(pid, 1);
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

    public IntArrayList unionIdsFor(int pid) {
        return idsFor(pid);
    }

    public int unionFreqFor(int pid) {
        return frequency(pid);
    }
}