package main.java.com.aixuniversity.maasaidictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Phoneme;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.List;

public final class IPAIndex implements IndexInterface<Phoneme> {
    private final Object2ObjectOpenHashMap<String, IntArrayList> map = new Object2ObjectOpenHashMap<>();
    private final Object2IntMap<String> freq = new Object2IntOpenHashMap<>();

    public IPAIndex(VocabularyDao vocabDao) throws SQLException {
        List<Vocabulary> all = vocabDao.getAll(); // méthode qui retourne id + ipa
        for (Vocabulary v : all) {
            int id = v.getId();
            for (String tok : v.getIpa().split("\\s+")) {
                map.computeIfAbsent(tok, k -> new IntArrayList()).add(id);
            }
        }
        map.forEach(this::updateFrequency);
    }

    @Override
    public IntArrayList idsFor(Phoneme phoneme) {
        return map.getOrDefault(phoneme.getIpa(), new IntArrayList());
    }

    @Override
    public int frequency(Phoneme phoneme) {
        return freq.getInt(phoneme.getIpa());
    }

    @Override
    public void updateFrequency(Object phoneme, IntArrayList list) {
        if (phoneme != null && list != null) {
            freq.put((String) phoneme, list.size());
        }
    }
}