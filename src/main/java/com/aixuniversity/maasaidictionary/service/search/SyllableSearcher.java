package main.java.com.aixuniversity.maasaidictionary.service.search;
import main.java.com.aixuniversity.maasaidictionary.dao.index.CategoryIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.index.IndexInterface;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.CategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.sql.SQLException; import java.util.*;

public final class SyllableSearcher {
    private final CategoryIndex idx; private final CategoryDao catDao; private final VocabularyDao vocabDao;
    public SyllableSearcher(CategoryIndex idx, CategoryDao catDao, VocabularyDao vocabDao) {
        this.idx = idx; this.catDao = catDao; this.vocabDao = vocabDao; }
    public List<Vocabulary> search(String pattern) throws SQLException {
        PatternSpec spec = PatternSpec.parse(pattern);
        Integer pivotId = Integer.valueOf(spec.labels().stream().min(Comparator.comparingInt(l -> {
            try {
                return catDao.searchIdOfUniqueElement(l, "abbr");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        })).orElseThrow());
        IntArrayList cand = idx.idsFor(IndexInterface.Token.of(pivotId));
        List<Vocabulary> res = new ArrayList<>();
        for (int id : cand) {
            Vocabulary v = vocabDao.searchById(id);
            if (v != null && matches(spec, v.getSyllables())) res.add(v);
        }
        return res;
    }
    private static boolean matches(PatternSpec spec, String raw) {
        String[] syll = raw.split("\\|", -1);
        if (syll.length != spec.syllables().size()) return false;
        for (int s=0;s<syll.length;s++) {
            String[] phon = syll[s].split("-", -1);
            List<List<String>> want = spec.syllables().get(s);
            if (phon.length!=want.size()) return false;
            for (int p=0;p<phon.length;p++) {
                Set<String> have = Set.of(phon[p].split("/", -1));
                if (!have.containsAll(want.get(p))) return false;
            }
        }
        return true;
    }
}