package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.dao.index.CategoryFlatIndex;
import com.aixuniversity.maadictionary.dao.index.CategoryPosIndex;
import com.aixuniversity.maadictionary.dao.index.PhonemeFlatIndex;
import com.aixuniversity.maadictionary.dao.index.PhonemePosIndex;
import com.aixuniversity.maadictionary.dao.normal.CategoryDao;
import com.aixuniversity.maadictionary.dao.normal.PhonemeDao;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.tokens.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class HybridPattern {
    public final List<Token> tokens;
    private final CategoryDao cDao = new CategoryDao();
    private final PhonemeDao pDao = new PhonemeDao();

    private HybridPattern(List<Token> t) {
        tokens = t;
    }  // immuable

    // ------------- PARSE -------------
    public static HybridPattern parse(String raw) throws SQLException {
        raw = raw.replace('.', '-');        // unifie les séparateurs syllabiques
        List<Token> list = new ArrayList<>();
        byte syl = 0;
        for (String syllSeg : raw.split("-")) {
            boolean explicitPos = syllSeg.contains("\\|");
            String[] items = explicitPos ? syllSeg.split("\\|") : new String[]{syllSeg};
            byte pos = 0;
            for (String item : items) {
                if (item.isBlank()) continue;
                list.add(tokenFromString(item.trim(), syl, explicitPos ? pos : null));
                if (explicitPos) pos++;
            }
            // si le segment était vide (cas "|" double) on incrémente quand même la syll index
            syl++;
        }
        return new HybridPattern(list);
    }

    private static Token tokenFromString(String s, byte syl, Byte pos) throws SQLException {
        if (s.equals("?")) return new TokAny();
        if (s.startsWith("[") && s.endsWith("]")) {
            String inside = s.substring(1, s.length() - 1);
            List<Token> opts = new ArrayList<>();
            for (String opt : inside.split("[ ,]")) {
                if (opt.isBlank()) continue;
                opts.add(tokenFromString(opt.trim(), syl, pos));
            }
            return new TokChoice(opts, syl, pos);
        }
        boolean isCat = Character.isUpperCase(s.charAt(0));
        if (isCat) {
            Integer cid = new CategoryDao().searchIdOfUniqueElement(s, "abbr");
            if (cid == null) {                  // ← unknown phoneme
                return new TokImpossible();
            }
            if (pos != null) return new TokCatPos(cid, syl, pos);
            else return new TokCatFlat(cid);
        } else {
            Integer pid = new PhonemeDao().searchIdOfUniqueElement(s, "ipa");
            if (pid == null) {
                return new TokImpossible();
            }
            if (pos != null) return new TokPhonPos(pid, syl);
            else return new TokPhonFlat(pid);
        }
    }

    // ------------- PIVOT -------------
    Token pickPivot(CategoryPosIndex cp, PhonemePosIndex pp, CategoryFlatIndex cf, PhonemeFlatIndex pf) {
        int best = Integer.MAX_VALUE;
        Token bestTok = null;
        for (Token t : tokens) {
            int f = frequencyForTok(t, cp, pp, cf, pf);
            if (f > 0 && f < best && !(t instanceof TokAny)) { // on ignore joker comme pivot
                best = f;
                bestTok = t;
            }
        }
        return bestTok != null ? bestTok : new TokAny(); // fallback
    }

    private static int frequencyForTok(Token t, CategoryPosIndex cp, PhonemePosIndex pp,
                                       CategoryFlatIndex cf, PhonemeFlatIndex pf) {
        return switch (t) {
            case TokCatPos p -> cp.frequency(new CategoryPosIndex.Key(p.cat(), p.syl(), p.pos()));
            case TokPhonPos p -> pp.unionFreqFor(p.phon());
            case TokCatFlat f -> cf.frequency(f.cat());
            case TokPhonFlat f -> pf.frequency(f.phon());
            case TokChoice ch -> ch.options().stream()
                    .mapToInt(tok -> frequencyForTok(tok, cp, pp, cf, pf))
                    .min().orElse(Integer.MAX_VALUE);
            default /* TokAny */ -> Integer.MAX_VALUE;
        };
    }

    IntArrayList idsForPivot(Token pivot, CategoryPosIndex cp, PhonemePosIndex pp,
                             CategoryFlatIndex cf, PhonemeFlatIndex pf) {
        return switch (pivot) {
            case TokCatPos p -> cp.idsFor(new CategoryPosIndex.Key(p.cat(), p.syl(), p.pos()));
            case TokPhonPos p -> pp.unionIdsFor(p.phon());
            case TokCatFlat f -> cf.idsFor(f.cat());
            case TokPhonFlat f -> pf.idsFor(f.phon());
            case TokChoice ch -> idsForPivot(ch.options().getFirst(), cp, pp, cf, pf); // 1er choix (arbitraire) ⚠️
            default /* Any */ -> pf.allIds();
        };
    }

    // ------------- MATCHES -------------
    boolean matches(Vocabulary v) throws SQLException {
        String[] syls = v.getSyll_pattern().split("-");
        for (Token t : tokens) if (!tokenOk(t, syls)) return false;
        return true;
    }

    private boolean tokenOk(Token t, String[] syls) throws SQLException {
        return switch (t) {
            case TokAny _ -> true;
            case TokChoice c -> c.options().stream().anyMatch(opt -> {
                try {
                    return tokenOk(opt, syls);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            case TokCatPos p -> {
                if (p.syl() >= syls.length) yield false;
                String[] ph = syls[p.syl()].split("\\|");
                if (p.pos() >= ph.length) yield false;
                yield Set.of(ph[p.pos()].split("/")).contains(cDao.searchById(p.cat()).getAbbr());
            }
            case TokPhonPos p -> {
                if (p.syl() >= syls.length) yield false;
                yield Arrays.stream(syls[p.syl()].split("\\|"))
                        .anyMatch(s -> {
                            try {
                                return s.startsWith("!" + pDao.searchById(p.phon()));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
            case TokCatFlat f -> {
                String ab = cDao.searchById(f.cat()).getAbbr();
                boolean ok = false;
                for (String s : syls)
                    for (String ph : s.split("\\|"))
                        if (Set.of(ph.split("/")).contains(ab)) {
                            ok = true;
                            break;
                        }
                yield ok;
            }
            case TokPhonFlat f -> {
                String sym = "!" + pDao.searchById(f.phon());
                boolean ok = false;
                for (String s : syls)
                    for (String ph : s.split("\\|"))
                        if (ph.startsWith(sym)) {
                            ok = true;
                            break;
                        }
                yield ok;
            }
            case TokImpossible ignored -> false;
        };
    }

    /**
     * expose la liste immuable de tokens – utile pour ApproximateSearcher
     */
    public List<Token> tokens() {
        return java.util.List.copyOf(tokens);
    }

    /**
     * util statique : teste si un token t est présent dans la séquence syll[]
     */
    public static boolean tokenOkStatic(Token t, String[] syll) {
        try {
            return new HybridPattern(java.util.List.of(t)).tokenOk(t, syll);
        } catch (Exception e) {
            return false;
        }
    }
}