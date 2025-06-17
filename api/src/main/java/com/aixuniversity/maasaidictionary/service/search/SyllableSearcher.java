package main.java.com.aixuniversity.maasaidictionary.service.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import main.java.com.aixuniversity.maasaidictionary.dao.index.CategoryIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.CategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class SyllableSearcher implements Searcher<String> {
    private final CategoryIndex idx;
    private final CategoryDao catDao;
    private final VocabularyDao vocabDao;

    public SyllableSearcher(CategoryIndex idx, CategoryDao catDao, VocabularyDao vocabDao) {
        this.idx = idx;
        this.catDao = catDao;
        this.vocabDao = vocabDao;
    }

    public SyllableSearcher() throws SQLException {
        this.idx = new CategoryIndex();
        this.catDao = new CategoryDao();
        this.vocabDao = new VocabularyDao();
    }

    public List<Vocabulary> search(String pattern) throws SQLException {
        SyllablePattern spec = SyllablePattern.parse(pattern);
        Integer pivotId = Integer.valueOf(spec.labels().stream().min(Comparator.comparingInt(l -> {
            try {
                return catDao.searchIdOfUniqueElement(l, "abbr");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })).orElseThrow());
        IntArrayList cand = idx.idsFor(pivotId);
        List<Vocabulary> res = new ArrayList<>();
        for (int id : cand) {
            Vocabulary v = vocabDao.searchById(id);
            if (v != null && matches(spec, v.getSyllables())) res.add(v);
        }
        return res;
    }

    private static boolean matches(SyllablePattern spec, String raw) {
        String[] syll = raw.split("\\|", -1);
        if (syll.length != spec.syllables().size()) return false;
        for (int s = 0; s < syll.length; s++) {
            String[] phon = syll[s].split("-", -1);
            List<List<String>> want = spec.syllables().get(s);
            if (phon.length != want.size()) return false;
            for (int p = 0; p < phon.length; p++) {
                Set<String> have = Set.of(phon[p].split("/", -1));
                if (!have.containsAll(want.get(p))) return false;
            }
        }
        return true;
    }
}