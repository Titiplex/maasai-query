package com.aixuniversity.maadictionary.service.conversion;

import java.util.*;

/**
 * Conversion IPA -> orthographe (payne, official, …)
 * Symétrique de Orthography2Ipa :
 * - on construit un index inverse {ipa -> [graphèmes, proba]}
 * - on découpe la chaîne IPA par “plus-long-d'abord”
 * - on combine en gardant les meilleurs chemins (<= 32)
 */
public final class IPA2Orthography {
    // TODO RECORD ORTHOGRAPHY VARIANTS IN DB, and deal with reconstruction if orthographic norms change

    private static final String UNKNOWN_GRAPHEME = "!";  // ou "X"
    private static final float UNKNOWN_PROB = 1e-3f; // > 1e-5 pour passer le filtre

    /**
     * Un résultat : une forme (graphies concaténées) + sa proba
     */
    public record Path(String form, float prob) {
    }

    private IPA2Orthography() {
    }

    /**
     * Convenience si tu veux un défaut (ex: payne)
     */
    public static List<Path> convert(String ipaText) {
        return convert("payne", ipaText);
    }

    /**
     * @param orthography "payne", "official", …
     * @param ipaText     "/naːt/" ou "naːt" ; points de syllabe facultatifs
     */
    public static List<Path> convert(String orthography, String ipaText) {
        String s = ipaText.trim()
                .replace("/", "")
                .replace(".", "")
                .toLowerCase(Locale.ROOT);

        // 1) index inverse ipa -> (graphème, p)
        Inverse inv = buildInverse(orthography);

        // 2) tokenisation IPA par "plus-long-d'abord"
        List<Segment> segs = tokenize(inv, s);

        // 3) combinaison des segments en chemins
        List<Path> paths = combine(segs);

        // 4) tri décroissant par proba
        paths = new ArrayList<>(paths);
        paths.sort(Comparator.comparingDouble((Path p) -> p.prob).reversed());
        return paths;
    }

    /* ------------------------------------------------------------------ */

    private static final class Option {
        final String grapheme;
        final float p; // probabilité déjà normalisée par ipa (somme==1 pour une même clé ipa)

        Option(String g, float p) {
            this.grapheme = g;
            this.p = p;
        }
    }

    /**
     * petit conteneur pour l’index inverse + longueur max d’un lexème IPA
     */
    private static final class Inverse {
        final Map<String, List<Option>> byIpa = new HashMap<>();
        int maxIpaLen = 1;
    }

    /**
     * tableau d'options pour une tranche IPA (comme Orthography2Ipa.Segment)
     */
    private record Segment(String[] graphemes, float[] p) {
    }

    /**
     * Construit l’index inverse à partir du registre en RAM
     */
    private static Inverse buildInverse(String orth) {
        Map<String, GraphemeMapping> table = OrthographyRegistry.table(orth);
        // ipa -> (grapheme, P(ipa|grapheme))
        Map<String, List<Option>> tmp = new HashMap<>();

        for (var e : table.entrySet()) {
            String g = e.getKey();
            GraphemeMapping gm = e.getValue();
            String[] ipa = gm.ipa;
            float[] pr = gm.prob;

            for (int i = 0; i < ipa.length; i++) {
                String ip = ipa[i];
                float p = (pr != null && pr.length == ipa.length) ? pr[i] : 1f / ipa.length;
                tmp.computeIfAbsent(ip, _ -> new ArrayList<>()).add(new Option(g, p));
            }
        }

        // Normalise pour approx. P(grapheme | ipa) (somme == 1 par clé ipa)
        Inverse inv = new Inverse();
        tmp.forEach((ip, list) -> {
            float sum = 0f;
            for (Option o : list) sum += o.p;
            float norm = (sum > 0f) ? sum : 1f;
            List<Option> normed = new ArrayList<>(list.size());
            for (Option o : list) normed.add(new Option(o.grapheme, o.p / norm));
            inv.byIpa.put(ip, normed);
            inv.maxIpaLen = Math.max(inv.maxIpaLen, ip.length());
        });
        return inv;
    }

    /**
     * Découpe s en segments IPA en cherchant la plus longue clé présente dans l’index inverse
     */
    private static List<Segment> tokenize(Inverse inv, String s) {
        List<Segment> segs = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            Segment seg = null;
            int bestLen = 0;
            int max = Math.min(inv.maxIpaLen, s.length() - i);

            for (int len = max; len >= 1; len--) {
                String sub = s.substring(i, i + len);
                List<Option> opts = inv.byIpa.get(sub);
                if (opts != null) {
                    String[] g = new String[opts.size()];
                    float[] p = new float[opts.size()];
                    for (int k = 0; k < opts.size(); k++) {
                        g[k] = opts.get(k).grapheme;
                        p[k] = opts.get(k).p;
                    }
                    seg = new Segment(g, p);
                    bestLen = len;
                    break;
                }
            }

            if (seg == null) {
                // 1 caractère inconnu → fallback
                seg = new Segment(new String[]{UNKNOWN_GRAPHEME}, new float[]{UNKNOWN_PROB});
                bestLen = 1;
                System.err.println("Warning: unknown IPA '" + s.charAt(i) + "'");
            }

            segs.add(seg);
            i += bestLen;
        }
        return segs;
    }

    /**
     * Combine les segments en chemins (top-32, seuil 1e-5)
     */
    private static List<Path> combine(List<Segment> segs) {
        List<Path> out = new ArrayList<>();
        out.add(new Path("", 1f));

        for (Segment seg : segs) {
            List<Path> nxt = new ArrayList<>();
            for (Path pref : out) {
                for (int j = 0; j < seg.graphemes.length; j++) {
                    nxt.add(new Path(pref.form + seg.graphemes[j], pref.prob * seg.p[j]));
                }
            }
            // garde les meilleurs
            nxt.sort(Comparator.comparingDouble((Path p) -> p.prob).reversed());
            out = new ArrayList<>(32);
            for (Path p : nxt) {
                // seuil 1e-5 : UNKNOWN_PROB (=1e-3) passe
                if (p.prob <= 1e-5f) break;
                out.add(p);
                if (out.size() >= 32) break;
            }
            // si tout a été coupé (ex cas extrême), garde au moins le meilleur
            if (out.isEmpty() && !nxt.isEmpty()) out.add(nxt.getFirst());
        }
        return out;
    }
}
