package main.java.com.aixuniversity.maasaidictionary.service.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import main.java.com.aixuniversity.maasaidictionary.dao.index.IndexInterface;
import main.java.com.aixuniversity.maasaidictionary.dao.index.IpaIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Phoneme;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class IpaSearcher {
    private final IpaIndex idx;
    private final VocabularyDao vocabDao;

    public IpaSearcher(IpaIndex idx, VocabularyDao vocabDao) {
        this.idx = idx;
        this.vocabDao = vocabDao;
    }

    public List<Vocabulary> search(List<String> tokens) throws SQLException {
        String pivot = tokens.stream().min(Comparator.comparingInt(token -> idx.frequency(
                IndexInterface.Token.of(token)
        ))).orElseThrow();
        IntArrayList cand = idx.idsFor(Objects.requireNonNull(Phoneme.getPhoneme(pivot)));
        List<Vocabulary> res = new ArrayList<>();
        for (int id : cand) {
            Vocabulary v = vocabDao.searchById(id);
            if (v != null && contains(tokens, v.getIpa())) res.add(v);
        }
        return res;
    }

    private static boolean contains(List<String> pat, String ipa) {
        String[] stream = ipa.split("\\s+");
        outer:
        for (int i = 0; i <= stream.length - pat.size(); i++) {
            for (int j = 0; j < pat.size(); j++) if (!pat.get(j).equals(stream[i + j])) continue outer;
            return true;
        }
        return false;
    }
}