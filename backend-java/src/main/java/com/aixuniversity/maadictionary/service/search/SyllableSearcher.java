package main.java.com.aixuniversity.maasaidictionary.service.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import main.java.com.aixuniversity.maasaidictionary.config.AbbreviationConfig;
import main.java.com.aixuniversity.maasaidictionary.dao.index.CategoryIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.CategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.*;

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

    /**
     * Searcher classique en mode purement syllabique.
     */
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

    /**
     * Vérifie la correspondance exacte entre une structure syllabique et une forme.
     */
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

    /**
     * Vérifie structurellement un mot donné selon un motif mixte (IPA + caté).
     */
    public boolean validateHybrid(String query, String raw) {
        String[] ipa = query.replaceAll("#", "")
                .replaceAll("\\.", " ")
                .split(" ");
        String[] syll = raw.split("\\|", -1);
        List<String> flatCatList = Arrays.stream(syll)
                .flatMap(s -> Arrays.stream(s.split("-")))
                .toList();
        if (ipa.length != flatCatList.size()) return false;

        for (int i = 0; i < ipa.length; i++) {
            String q = ipa[i];
            String form = flatCatList.get(i);
            Set<String> have = Set.of(form.split("/"));
            if (AbbreviationConfig.get(q) != null) {
                // c’est une catégorie, on vérifie que le set contienne la bonne abréviation
                if (!have.contains(q)) return false;
            } else {
                // sinon on vérifie que c’est bien le phonème exact
                if (!have.contains("!" + q)) return false; // convention: !k = phonème k
            }
        }
        return true;
    }
}
