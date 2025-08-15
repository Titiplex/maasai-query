package com.aixuniversity.maadictionary.service.conversion;

import java.util.*;

public final class Orthography2Ipa {

    public record Path(String ipa, float prob) {
    }

    private Orthography2Ipa() {
    }

    /* ---------- API publique ---------- */

    /**
     * Appel court : orth==payne par défaut.
     */
    public static List<Path> convert(String text) {
        return convert("payne", text);
    }

    /**
     * Conversion en utilisant l’orthographie indiquée.
     */
    public static List<Path> convert(String orthography, String text) {
        List<Segment> segs = tokenize(orthography, text.toLowerCase(Locale.ROOT).trim());
        List<Path> paths = combine(segs);
        paths = new ArrayList<>(paths);
        paths.sort(Comparator.comparingDouble(p -> -p.prob));
        return paths;
    }

    /* ---------- internes ---------- */

    private record Segment(String[] ipa, float[] p) {
    }

    private static List<Segment> tokenize(String orth, String s) {
        Map<String, GraphemeMapping> table = OrthographyRegistry.table(orth);
        List<Segment> segs = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            GraphemeMapping gm = null;
            int best = 0;
            int max = Math.min(3, s.length() - i);
            for (int len = max; len >= 1; len--) {
                gm = table.get(s.substring(i, i + len));
                if (gm != null) {
                    best = len;
                    break;
                }
            }
            if (gm == null)
                throw new IllegalArgumentException("Unknown grapheme '" + s.charAt(i) + "' in orthography " + orth);
            segs.add(new Segment(gm.ipa, gm.prob));
            i += best;
        }
        return segs;
    }

    private static List<Path> combine(List<Segment> segs) {
        List<Path> out = new ArrayList<>();
        out.add(new Path("", 1f));

        for (Segment seg : segs) {
            List<Path> nxt = new ArrayList<>();
            for (Path pref : out) {
                for (int j = 0; j < seg.ipa.length; j++) {
                    nxt.add(new Path(pref.ipa + seg.ipa[j], pref.prob * seg.p[j]));
                }
            }
            // Conserve max 32 chemins + proba>1e-5
            out = nxt.stream()
                    .filter(p -> p.prob > 1e-5f)
                    .sorted(Comparator.comparingDouble(p -> -p.prob))
                    .limit(32)
                    .toList();
        }
        return out;
    }
}
