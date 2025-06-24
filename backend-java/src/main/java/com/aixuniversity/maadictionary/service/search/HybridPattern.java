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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HybridPattern {
    public final List<Token> tokens;
    private final CategoryDao cDao = new CategoryDao();
    private final PhonemeDao pDao = new PhonemeDao();

    private final int segmentCount;

    private HybridPattern(List<Token> t, int segs) {
        tokens = t;
        segmentCount = segs;
    }

    public int segmentCount() {
        return segmentCount;
    }

    // ------------- PARSE -------------
    public static HybridPattern parse(String raw) throws SQLException {

        boolean anchoredStart = raw.startsWith("#");
        boolean anchoredEnd = raw.endsWith("#");
        if (anchoredStart) raw = raw.substring(1);
        if (anchoredEnd) raw = raw.substring(0, raw.length() - 1);

    /* on découpe UNIQUEMENT sur les points (.) expli­cites,
           pas sur les traits d’union (qui n’apparaissent plus côté user). */
        String[] segments = raw.split("\\.");

        List<Token> list = new ArrayList<>();
        byte syl = 0;
        for (String seg : segments) {

            boolean explicitPos = seg.contains("|");
            String[] items;
            if (explicitPos) {
                items = seg.split("\\|");
            } else {
                items = splitSmart(seg).toArray(String[]::new);
            }

            // ➜ si l’utilisateur n’a PAS mis de '.', on créera des Tok*Flat (pas de position).
            for (String item : items) {
                if (item.isBlank()) continue;
                Byte pos = explicitPos ? (byte) Arrays.asList(items).indexOf(item) : null;
                // si seg contient '.', item hérite de syl ; sinon syl = -1   (cf. tokenFromStringV2)
                list.add(tokenFromStringV2(item.trim(),
                        explicitPos ? syl : (byte) -1,
                        pos));
            }
            syl++;                         // passe à la syllabe suivante SEULEMENT quand il y a un '.'
        }

        if (anchoredStart) list.addFirst(new TokStart());
        if (anchoredEnd) list.add(new TokEnd());

        return new HybridPattern(list, segments.length);
    }

    /**
     * Construit un Token à partir d’une écriture utilisateur.
     * -  s  : lexème brut (sans espace)
     * - syl : index de syllabe, ou -1 si aucune syllabe imposée
     * - pos : position intra-syllabe (null si non imposée)
     */
    private static Token tokenFromStringV2(String s, byte syl, Byte pos) throws SQLException {

        /* ─── 1. Joker « ? » (exactement 1 occurrence) ─────────────────── */
        if (s.equals("?")) return new TokAny();

        /* ─── 2. Détection d’un quantificateur suffixe ─────────────────── */
        char last = s.charAt(s.length() - 1);
        boolean hasQuant = s.length() > 1 && (last == '+' || last == '*' || last == '?');

        String core = hasQuant ? s.substring(0, s.length() - 1) : s;   // sans le quantif.
        boolean noSyl = syl < 0;                                       // syllabe non fixée

        /* ─── 3. Construction du token « de base » (hors quantif.) ────── */
        Token base;

        // 3-a  Liste d’alternatives  [ … ]
        if (core.startsWith("[") && core.endsWith("]")) {
            List<Token> options = new ArrayList<>();
            for (String opt : core.substring(1, core.length() - 1).split("[ ,]"))
                if (!opt.isBlank())
                    options.add(tokenFromStringV2(opt.trim(), syl, pos));   // récursif
            base = new TokChoice(options, noSyl ? (byte) -1 : syl, pos);

        } else {                                   // 3-b  Catégorie ou Phonème
            boolean isCat = Character.isUpperCase(core.charAt(0));

            if (isCat) {                           // ---- Catégorie
                Integer cid = new CategoryDao().searchIdOfUniqueElement(core, "abbr");
                if (cid == null) return new TokImpossible();

                base = noSyl ? new TokCatFlat(cid)
                        : pos != null ? new TokCatPos(cid, syl, pos)
                        : new TokCatFlat(cid);     // pas de pos ⇒ flat même syllabe

            } else {                               // ---- Phonème
                Integer pid = new PhonemeDao().searchIdOfUniqueElement(core, "ipa");
                if (pid == null) return new TokImpossible();

                base = noSyl ? new TokPhonFlat(pid)
                        : pos != null ? new TokPhonPos(pid, syl)
                        : new TokPhonFlat(pid);
            }
        }

        /* ─── 4. Application éventuelle du quantificateur ─────────────── */
        if (!hasQuant) return base;                // simple

        int min, max;
        switch (last) {
            case '+' -> {
                min = 1;
                max = Integer.MAX_VALUE;
            }
            case '*' -> {
                min = 0;
                max = Integer.MAX_VALUE;
            }
            default -> {
                min = 0;
                max = 1;
            }     // suffixe '?'
        }
        return new TokRepeat(base, min, max);
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
                                return s.startsWith(ipaOf(p.phon()));
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
                String sym = ipaOf(f.phon());
                boolean ok = false;
                for (String s : syls)
                    for (String ph : s.split("\\|"))
                        if (ph.startsWith(sym)) {
                            ok = true;
                            break;
                        }
                yield ok;
            }
            case TokStart _ -> true;  // le filtrage séquentiel assure qu’il est 1ᵉʳ
            case TokEnd _ -> true;  // sera évalué en dernier → fin de motif
            case TokRepeat r -> {
                int found = countOccurrences(r.base(), syls);
                yield found >= r.min() && found <= r.max();
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
            return new HybridPattern(java.util.List.of(t), syll.length).tokenOk(t, syll);
        } catch (Exception e) {
            return false;
        }
    }

    private String ipaOf(int phonId) throws SQLException {
        return pDao.searchById(phonId).getIpa();
    }

    private int countOccurrences(Token base, String[] syllables) throws SQLException {
        int count = 0;
        for (String syl : syllables) {
            for (String phon : syl.split("\\|")) {
                if (tokenOk(base, new String[]{phon})) {
                    count++;
                }
            }
        }
        return count;
    }

    // liste [...] + quantificateur optionnel
    // suite de lettres (A-Z, a-z, IPA) + quantif. optionnel
    private static final Pattern TOKEN_RX =
            Pattern.compile("\\[[^]]+][+*?]?" + "|\\p{L}+[+*?]?" + "|.");


    private static List<String> splitSmart(String seg) {
        List<String> out = new ArrayList<>();
        Matcher m = TOKEN_RX.matcher(seg);
        while (m.find()) out.add(m.group());
        return out;
    }

}