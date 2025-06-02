package main.java.com.aixuniversity.maasaidictionary.parser.extractors;

import main.java.com.aixuniversity.maasaidictionary.config.AbbreviationConfig;
import main.java.com.aixuniversity.maasaidictionary.config.IPAConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe qui extrait les syllabes et leurs patterns détaillés à partir d'une chaîne IPA.
 * <p>
 * Ce découpeur utilise le fichier IPA.properties pour définir les symboles IPA et leurs catégories
 * phonétiques (possiblement multiples), par exemple "plosive,alveolar" ou "vowel,central,rounded".
 * Chaque token est ainsi transformé en pattern détaillé en mappant chacune des catégories à une abréviation.
 */
public class SyllableExtractor {

    // Liste des digrammes IPA définis dans IPA.properties (en minuscules pour la comparaison).
    private static final List<String> IPA_DIGRAMS = Arrays.asList("ch", "sh", "ny");

    /**
     * Extrait les syllabes et leurs patterns détaillés d'une chaîne IPA.
     *
     * @param ipaWord La chaîne IPA à traiter.
     * @return Une liste d'objets Syllable contenant le texte de la syllabe et son pattern.
     */
    public static List<Syllable> extractSyllablesAndPatterns(String ipaWord) {
        // Récupérer la liste des voyelles à partir des symboles configurés en IPA.properties.
        IPAConfig.getAllVowels();
        // Tokenisation du mot IPA en tenant compte des digrammes et des diacritiques.
        List<String> tokens = tokenizeIPAWord(ipaWord);

        List<Syllable> syllables = new ArrayList<>();
        List<String> currentSyllableTokens = new ArrayList<>();

        int pos = 0;
        while (pos < tokens.size()) {
            String token = tokens.get(pos);
            // Si on n'a pas d'onset en cours et que le token est une voyelle, on en fait
            // une syllabe autonome : V . (pour éviter VCV), puis on continue.
            if (currentSyllableTokens.isEmpty() && isVowelToken(token)) {
                String pat = computeDetailedPattern(List.of(token));
                syllables.add(new Syllable(token, pat));
                pos++;
                continue;
            }
            //cas normal
            if (isConsonantToken(token)) {
                currentSyllableTokens.add(token);
                pos++;
            } else if (isVowelToken(token)) {
                currentSyllableTokens.add(token);
                pos++;

                // Rassembler les tokens suivants qui sont des consonnes pour constituer un cluster candidat.
                List<String> candidateCoda = new ArrayList<>();
                while (pos < tokens.size() && isConsonantToken(tokens.get(pos))) {
                    candidateCoda.add(tokens.get(pos));
                    pos++;
                }

                // Déterminer, via une règle d'onset maximal, combien de tokens du cluster vont à la coda.
                int splitPoint = determineSplit(candidateCoda);
                for (int i = 0; i < splitPoint; i++) {
                    currentSyllableTokens.add(candidateCoda.get(i));
                }
                String syllableText = joinTokens(currentSyllableTokens);
                // Calcul du pattern détaillé basé sur les multiples catégories.
                String pattern = computeDetailedPattern(currentSyllableTokens);
                syllables.add(new Syllable(syllableText, pattern));

                // Les tokens restants du cluster forment l'onset de la syllabe suivante.
                currentSyllableTokens = new ArrayList<>();
                for (int i = splitPoint; i < candidateCoda.size(); i++) {
                    currentSyllableTokens.add(candidateCoda.get(i));
                }
            } else {
                pos++;
            }
        }
        if (!currentSyllableTokens.isEmpty()) {
            String syllableText = joinTokens(currentSyllableTokens);
            String pattern = computeDetailedPattern(currentSyllableTokens);
            syllables.add(new Syllable(syllableText, pattern));
        }
        // 4) Post‑processing : si la 1ère syllabe est V + C…, on la scinde en [V] + [C…]
        if (!syllables.isEmpty()) {
            Syllable first = syllables.getFirst();
            List<String> toks = first.getTokens();  // grâce à votre getter getTokens()
            if (toks.size() >= 2
                    && isVowelToken(toks.get(0))
                    && isConsonantToken(toks.get(1))) {
                // 1) nouvelle syl V
                String syl1 = toks.getFirst();
                String pat1 = computeDetailedPattern(List.of(syl1));
                // 2) reste → CV…
                List<String> rest = toks.subList(1, toks.size());
                String syl2 = joinTokens(rest);
                String pat2 = computeDetailedPattern(rest);
                // on remplace / insère
                syllables.set(0, new Syllable(syl1, pat1));
                syllables.add(1, new Syllable(syl2, pat2));
            }
        }

        return syllables;
    }

    /**
     * Tokenise la chaîne IPA en tenant compte des digrammes et des diacritiques.
     * On tente d'abord d'identifier un digramme (ex. "ch", "sh", "ny"), sinon on traite caractère par caractère.
     *
     * @param ipaWord La chaîne IPA à tokeniser.
     * @return Une liste de tokens représentant les unités phonémiques.
     */
    static List<String> tokenizeIPAWord(String ipaWord) {
        List<String> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < ipaWord.length()) {
            boolean foundDigram = false;
            if (pos < ipaWord.length() - 1) {
                String twoChar = ipaWord.substring(pos, pos + 2).toLowerCase();
                if (IPA_DIGRAMS.contains(twoChar)) {
                    tokens.add(ipaWord.substring(pos, pos + 2));
                    pos += 2;
                    foundDigram = true;
                }
            }
            if (foundDigram) continue;

            char ch = ipaWord.charAt(pos);
            if (isDiacritic(ch)) {
                if (!tokens.isEmpty()) {
                    String prev = tokens.removeLast();
                    tokens.add(prev + ch);
                } else {
                    tokens.add(String.valueOf(ch));
                }
            } else {
                tokens.add(String.valueOf(ch));
            }
            pos++;
        }
        return tokens;
    }

    /**
     * Concatène une liste de tokens en une seule chaîne.
     */
    private static String joinTokens(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            sb.append(token);
        }
        return sb.toString();
    }

    /**
     * Calcule un pattern détaillé à partir d'une liste de tokens.
     * Pour chaque token, récupère la chaîne de catégories depuis IPAConfig, la découpe par virgule,
     * mappe chaque catégorie à une abréviation, puis joint ces abréviations par un slash.
     * Le pattern de la syllabe est ensuite la concaténation des patterns de chaque token, séparés par des tirets.
     *
     * @param tokens La liste de tokens constituant la syllabe.
     * @return Le pattern détaillé sous forme de chaîne.
     */
    private static String computeDetailedPattern(List<String> tokens) {
        StringBuilder pattern = new StringBuilder();
        boolean firstToken = true;
        for (String token : tokens) {
            String baseToken = token.replaceAll("\\p{M}", "").toLowerCase();
            String categoryString = IPAConfig.getCategory(IPAConfig.getLetterFromIPA(baseToken));
            String tokenPattern;
            if (categoryString != null && !categoryString.isEmpty()) {
                String[] categories = categoryString.split(",");
                StringBuilder catAbbrev = new StringBuilder();
                boolean firstCat = true;
                for (String cat : categories) {
                    String trimmedCat = cat.trim();
                    String abbr = mapCategoryToAbbreviation(trimmedCat);
                    if (!firstCat) {
                        catAbbrev.append("/");
                    } else {
                        firstCat = false;
                    }
                    catAbbrev.append(abbr);
                }
                tokenPattern = catAbbrev.toString();
            } else {
                tokenPattern = "X";
            }
            if (!firstToken) {
                pattern.append("-");
            } else {
                firstToken = false;
            }
            pattern.append(tokenPattern);
        }
        return pattern.toString();
    }

    /**
     * Mappe une catégorie phonétique à une abréviation.
     * La méthode prend en compte plusieurs catégories possibles telles que "vowel", "plosive", "nasal",
     * "fricative", "affricate", "approximant", "trill", "flap", "glottal", "liquid", "alveolar",
     * "labiodental", "labial", "velar", "palatal", "uvular", "pharyngeal", "central", "rounded", etc.
     *
     * @param category La catégorie à mapper.
     * @return L'abréviation correspondante.
     */
    private static String mapCategoryToAbbreviation(String category) {
        String abbreviation = AbbreviationConfig.get(category.toLowerCase().trim());
        return abbreviation != null ? abbreviation : "0";
    }

    /**
     * Détermine le nombre de tokens à affecter à la coda à partir d'un cluster de tokens consonantiques.
     * On teste divers splits afin de laisser un onset légal (0 à 2 tokens) pour la syllabe suivante.
     *
     * @param clusterTokens La liste des tokens du cluster.
     * @return Le nombre de tokens à affecter à la coda.
     */
    private static int determineSplit(List<String> clusterTokens) {
        for (int split = 0; split <= clusterTokens.size(); split++) {
            List<String> onsetCandidate = clusterTokens.subList(split, clusterTokens.size());
            if (isLegalOnsetTokens(onsetCandidate)) {
                return split;
            }
        }
        return 0;
    }

    /**
     * Vérifie si une liste de tokens constitue un onset légal.
     * Ici, un onset est défini comme contenant au maximum 2 tokens.
     *
     * @param onsetTokens La liste candidate.
     * @return true si légal, false sinon.
     */
    private static boolean isLegalOnsetTokens(List<String> onsetTokens) {
        return onsetTokens.size() <= 2;
    }

    /**
     * Vérifie si un token représente une voyelle en se basant sur sa catégorie dans IPA.properties.
     *
     * @param token Le token à tester.
     * @return true si le token est catégorisé comme "vowel", false sinon.
     */
    private static boolean isVowelToken(String token) {
        String baseToken = token.replaceAll("\\p{M}", "").toLowerCase();
        String category = IPAConfig.getCategory(IPAConfig.getLetterFromIPA(baseToken));
        return category != null && category.toLowerCase().contains("vowel");
    }

    /**
     * Vérifie si un token est une consonne, c'est-à-dire s'il n'est pas catégorisé comme "vowel".
     *
     * @param token Le token à tester.
     * @return true si considéré comme consonantique, false sinon.
     */
    private static boolean isConsonantToken(String token) {
        String baseToken = token.replaceAll("\\p{M}", "").toLowerCase();
        String category = IPAConfig.getCategory(IPAConfig.getLetterFromIPA(baseToken));
        return category == null || !category.toLowerCase().contains("vowel");
    }

    /**
     * Vérifie si un caractère est un diacritique.
     * On prend Mn, Mc et Me.
     */
    private static boolean isDiacritic(char ch) {
        int type = Character.getType(ch);
        return type == Character.NON_SPACING_MARK    // Mn
                || type == Character.COMBINING_SPACING_MARK  // Mc
                || type == Character.ENCLOSING_MARK;    // Me
    }
}
