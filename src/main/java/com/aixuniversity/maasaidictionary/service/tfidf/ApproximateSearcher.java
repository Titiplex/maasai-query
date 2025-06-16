package main.java.com.aixuniversity.maasaidictionary.service.tfidf;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import main.java.com.aixuniversity.maasaidictionary.dao.index.CategoryFlatIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.index.PhonemeFlatIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.CategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.PhonemeDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.service.search.*;
import main.java.com.aixuniversity.maasaidictionary.service.search.tokens.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ApproximateSearcher {
    private final PhonemeFlatIndex phonFlat;
    private final CategoryFlatIndex catFlat;
    private final PhonemeDao pDao = new PhonemeDao();
    private final CategoryDao cDao = new CategoryDao();
    private final VocabularyDao vDao = new VocabularyDao();

    private final long totalTokens; // pré‑calcul : Σ freq(phon) + Σ freq(cat)

    public ApproximateSearcher() throws SQLException {
        phonFlat = new PhonemeFlatIndex();
        catFlat = new CategoryFlatIndex();
        totalTokens = phonFlat.totalFreq() + catFlat.totalFreq();
    }

    /**
     * renvoie les k meilleurs candidats
     */
    public List<ScoredResult> searchAndRank(String raw, int k) throws SQLException {
        HybridPattern pat = HybridPattern.parse(raw);
        // 1. récupérer l’union de tous les IDs via index plats
        Set<Integer> cand = new HashSet<>();
        for (var t : pat.tokens())
            switch (t) {
                case TokPhonFlat p -> cand.addAll(phonFlat.idsFor(p.phon()));
                case TokPhonPos p -> cand.addAll(phonFlat.idsFor(p.phon()));
                case TokCatFlat c -> cand.addAll(catFlat.idsFor(c.cat()));
                case TokCatPos c -> cand.addAll(catFlat.idsFor(c.cat()));
                case TokChoice ch -> ch.options().forEach(opt -> cand.addAll(idsForOpt(opt)));
                default /* TokAny */ -> cand.addAll(phonFlat.allIds());
            }

        // 2. score TF‑IDF très simplifié : Somme IDF des tokens présents
        List<ScoredResult> scored = new ArrayList<>();
        for (int id : cand) {
            Vocabulary v = vDao.searchById(id);
            if (v == null) continue;
            double score = tfIdfScore(pat, v);
            scored.add(new ScoredResult(v, score));
        }
        scored.sort((a, b) -> Double.compare(b.score(), a.score()));
        return scored.subList(0, Math.min(k, scored.size()));
    }

    private IntArrayList idsForOpt(Token t) {
        return switch (t) {
            case TokCatPos cp -> catFlat.idsFor(cp.cat());
            case TokCatFlat cf -> catFlat.idsFor(cf.cat());
            case TokPhonPos pp -> phonFlat.idsFor(pp.phon());
            case TokPhonFlat pf -> phonFlat.idsFor(pf.phon());
            default -> new IntArrayList();
        };
    }

    private double tfIdfScore(HybridPattern pat, Vocabulary v) {
        String[] syll = v.getSyllables().split("\\|");
        double score = 0.0;
        for (Token t : pat.tokens())
            switch (t) {
                case TokAny any -> {
                }
                case TokChoice ch -> {
                    if (ch.options().stream().anyMatch(opt -> present(opt, syll)))
                        score += maxIdf(ch.options());
                }
                default -> {
                    if (present(t, syll)) score += idf(t);
                }
            }
        return score;
    }

    private boolean present(Token t, String[] syll) {
        return HybridPattern.tokenOkStatic(t, syll); // util static exposé
    }

    private double maxIdf(List<Token> opts) {
        return opts.stream().mapToDouble(this::idf).max().orElse(0);
    }

    private double idf(Token t) {
        long f = switch (t) {
            case TokCatPos c -> catFlat.frequency(c.cat());
            case TokCatFlat c -> catFlat.frequency(c.cat());
            case TokPhonPos p -> phonFlat.frequency(p.phon());
            case TokPhonFlat p -> phonFlat.frequency(p.phon());
            default -> totalTokens;
        };
        return Math.log((double) totalTokens / Math.max(1, f));
    }
}