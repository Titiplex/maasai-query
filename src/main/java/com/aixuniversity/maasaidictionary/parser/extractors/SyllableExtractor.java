package main.java.com.aixuniversity.maasaidictionary.parser.extractors;

import main.java.com.aixuniversity.maasaidictionary.config.AbbreviationConfig;
import main.java.com.aixuniversity.maasaidictionary.config.IPAConfig;
import main.java.com.aixuniversity.maasaidictionary.model.Syllable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Syllabifier précis pour le maa (dialecte kisongo / arusa).
 * – syllabe = (C)(C)V(V)(G)(C)
 * – codas limitées, onsets validés phonotactiquement
 * – support des voyelles longues (ː ou double lettre)
 * – prise en compte des glides et des prénasalisées
 */
public abstract class SyllableExtractor {

    /* ----------  SEGMENTS  ---------- */
    // TODO reindexer les properties pour récupérer legal/pas
    private static final Set<String> DIGRAPHS = Set.of(
            "ch", "sh", "ny", "ng", "ŋg", "mb", "nd", "nj", "rr", "ww", "yy", "kʼ", "tʃʼ"
    );

    private static final Set<String> LEGAL_CODA = Set.of("r", "l", "m", "n", "ŋ", "s");

    private static final Set<List<String>> LEGAL_ONSETS = Set.of(
            List.of("mb"), List.of("nd"), List.of("nj"), List.of("ng"), List.of("ŋg"),
            List.of("l", "w"), List.of("l", "j")
    );

    /* ----------  PUBLIC API  ---------- */

    public static List<Syllable> extract(String ipaInput) {
        List<String> tokens = tokenize(ipaInput);
        return syllabify(tokens);
    }

    /**
     * Wrapper sécurisé pour la tokenisation IPA.
     * Retourne une liste non modifiable de tokens pour éviter qu'on casse la structure interne.
     */
    public static List<String> tokenizeIPAWord(String ipaWord) {
        List<String> raw = tokenize(ipaWord);
        return Collections.unmodifiableList(raw);
    }

    /* ----------  TOKENISATION  ---------- */

    private static List<String> tokenize(String ipa) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < ipa.length()) {

            // 1. digraph
            if (i + 1 < ipa.length()) {
                String bi = ipa.substring(i, i + 2);
                if (DIGRAPHS.contains(bi.toLowerCase())) {
                    out.add(bi);
                    i += 2;
                    continue;
                }
            }

            char ch = ipa.charAt(i);

            // 2. diacritique -> fusion avec token précédent
            if (isDiacritic(ch) || ch == 'ː') {
                if (!out.isEmpty()) {
                    out.set(out.size() - 1, out.getLast() + ch);
                } else {
                    out.add(String.valueOf(ch));
                }
                i++;
                continue;
            }

            // 3. voyelle doublée (longueur par redoublement)
            if (isVowelChar(ch) && i + 1 < ipa.length() && ipa.charAt(i + 1) == ch) {
                out.add("" + ch + ch);
                i += 2;
                continue;
            }

            // 4. simple caractère
            out.add(String.valueOf(ch));
            i++;
        }
        return out;
    }

    /* ----------  SYLLABIFICATION  ---------- */

    private static List<Syllable> syllabify(List<String> tokens) {
        List<Syllable> sylls = new ArrayList<>();
        List<String> onset = new ArrayList<>();
        List<String> nucleus = new ArrayList<>();
        List<String> coda = new ArrayList<>();

        int idx = 0;
        while (idx < tokens.size()) {
            String t = tokens.get(idx);

            if (isVowelToken(t)) {
                if (nucleus.isEmpty()) {        // première voyelle
                    nucleus.add(t);
                } else {                       // deuxième voyelle => test glide/dipht.
                    if (isGlide(t) && sameHarmonySet(nucleus.getLast(), t))
                        nucleus.add(t);        // noyau élargi
                    else {                     // nouvelle syllabe
                        addSyllable(sylls, onset, nucleus, coda);
                        onset.clear();
                        nucleus.clear();
                        coda.clear();
                        nucleus.add(t);
                    }
                }
            } else { // consonne
                if (nucleus.isEmpty()) onset.add(t);
                else coda.add(t);
            }
            idx++;
        }
        addSyllable(sylls, onset, nucleus, coda);
        return sylls;
    }

    private static void addSyllable(List<Syllable> list,
                                    List<String> onset, List<String> nucleus, List<String> coda) {
        if (nucleus.isEmpty()) return; // sécurité

        // règle NOCODA + max-onset
        if (coda.size() > 1 || (coda.size() == 1 && !LEGAL_CODA.contains(coda.getFirst()))) {
            onset.addAll(0, coda);
            coda.clear();
        }  // une seule consonne candidate, rien à faire


        List<String> sylTokens = new ArrayList<>();
        sylTokens.addAll(onset);
        sylTokens.addAll(nucleus);
        sylTokens.addAll(coda);

        list.add(new Syllable(join(sylTokens), computePattern(sylTokens)));
    }

    /* ----------  PATTERN  ---------- */

    private static String computePattern(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) sb.append("-");
            sb.append(tokenPattern(tokens.get(i)));
        }
        return sb.toString();
    }

    private static String tokenPattern(String tok) {
        String base = Normalizer.normalize(tok, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
        String cat = IPAConfig.getCategory(IPAConfig.getLetterFromIPA(base));
        if (cat == null) return "X";
        String[] cats = cat.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cats.length; i++) {
            if (i > 0) sb.append("/");
            sb.append(AbbreviationConfig.getOrDefault(cats[i].trim(), "0"));
        }
        if (tok.contains("ː") || (base.length() == 2 && base.charAt(0) == base.charAt(1)))
            sb.append("*"); // voyelle longue
        return sb.toString();
    }

    /* ----------  HELPERS  ---------- */

    private static boolean isGlide(String t) {
        return t.equalsIgnoreCase("w") || t.equalsIgnoreCase("j");
    }

    private static boolean sameHarmonySet(String vowel, String glide) {
        // simplification : renvoie true si glide = w après /o u ɔ ʊ/  ou j après /i ɪ e ɛ/.
        String v = vowel.toLowerCase();
        return ("wj".contains(glide.toLowerCase()) &&
                (("wu".contains(glide.toLowerCase()) && "oouɔʊ".contains(v)) ||
                        ("j".equalsIgnoreCase(glide) && "ieɪeɛ".contains(v))));
    }

    private static boolean isVowelChar(char ch) {
        return "aeiouɨɛɔʊɪ".indexOf(ch) >= 0;
    }

    private static boolean isVowelToken(String t) {
        String base = t.replaceAll("\\p{M}", "").toLowerCase();
        String cat = IPAConfig.getCategory(IPAConfig.getLetterFromIPA(base));
        return cat != null && cat.contains("vowel");
    }

    private static boolean isDiacritic(char c) {
        int type = Character.getType(c);
        return type == Character.NON_SPACING_MARK ||
                type == Character.COMBINING_SPACING_MARK ||
                type == Character.ENCLOSING_MARK;
    }

    private static String join(List<String> toks) {
        return String.join("", toks);
    }
}
