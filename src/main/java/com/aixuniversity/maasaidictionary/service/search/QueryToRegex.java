// service/search/QueryToRegex.java
package main.java.com.aixuniversity.maasaidictionary.service.search;

import main.java.com.aixuniversity.maasaidictionary.config.AbbreviationConfig;
import main.java.com.aixuniversity.maasaidictionary.config.IPAConfig;

import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Convertit le mini‑DSL (# . ? [...] ) en regex Java
 */
final class QueryToRegex {
    private static String categoryToRegex(String cat) {
        return IPAConfig.IpaSymbolsForCategory(cat).stream()      // ex. ["p","t","k","t͡ʃ"]
                .map(Pattern::quote)
                .collect(Collectors.joining("|", "(?:", ")"));
    }

    /**
     * Transforme #aCuP# en regex IPA
     */
    public static String translate(String q) {
        StringBuilder out = new StringBuilder();
        boolean start = q.startsWith("#");
        boolean end = q.endsWith("#");
        if (start) q = q.substring(1);
        if (end) q = q.substring(0, q.length() - 1);

        for (int i = 0; i < q.length(); i++) {
            char ch = q.charAt(i);
            switch (ch) {
                case '?' -> out.append("[^ ]+");               // jokers : un token IPA
                case '.' -> out.append(" ");                   // frontière syllabe = espace
                case '[' -> {                                  // liste [C P]
                    int j = q.indexOf(']', i);
                    String inside = q.substring(i + 1, j);
                    String alt = inside.trim().replaceAll("\\s+", "|");
                    out.append("(?:").append(
                            alt.chars()
                                    .mapToObj(c -> categoryToRegex(String.valueOf((char) c)))
                                    .collect(Collectors.joining("|"))
                    ).append(")");
                    i = j;
                }
                default -> {
                    String cat = AbbreviationConfig.get(String.valueOf(ch));
                    if (cat != null) out.append(categoryToRegex(String.valueOf(ch)));
                    else out.append(Pattern.quote(String.valueOf(ch)));
                }
            }
        }
        if (start) out.insert(0, "^");
        if (end) out.append("$");
        return out.toString();
    }

    /**
     * choisit un pivot littéral (non joker) le plus rare
     */
    static String pickPivot(String q, Function<String, Integer> freq) {
        String best = null;
        int f = Integer.MAX_VALUE;
        for (String tok : q.split("\\s+"))
            if (tok.length() > 1 && tok.charAt(0) != '[' && tok.charAt(0) != '?' && tok.charAt(0) != '#') {
                int cf = freq.apply(tok);
                if (cf > 0 && cf < f) {
                    f = cf;
                    best = tok;
                }
            }
        return best;
    }
}