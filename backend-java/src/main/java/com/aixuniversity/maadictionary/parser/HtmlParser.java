package com.aixuniversity.maadictionary.parser;

import com.aixuniversity.maadictionary.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe HtmlParser permettant de récupérer le vocabulaire
 * en parcourant récursivement tous les liens HTM/HTML
 * à partir d'une URL de base (ex :<a href="https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/"></a>).
 */
public abstract class HtmlParser {

    /**
     * Explore toutes les pages liées (en .htm / .html) à partir de l'URL de base,
     * et récupère l'ensemble du vocabulaire.
     *
     * @param startUrl L'URL de base, par ex. "<a href="https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/">...</a>"
     * @return Une liste de Vocabulary (objet métier) contenant l'ensemble du lexique trouvé
     */
    public static List<Vocabulary> parseAll(String startUrl) {
        // Ensemble pour éviter de revisiter plusieurs fois la même page
        Set<String> visited = new HashSet<>();
        // File d’attente (BFS)
        Queue<String> toVisit = new LinkedList<>();
        toVisit.add(startUrl);

        // Liste finale de tout le vocabulaire collecté
        List<Vocabulary> allVocabulary = new ArrayList<>();

        while (!toVisit.isEmpty()) {
            String currentUrl = toVisit.poll();
            System.out.println("Current Url : " + currentUrl);
            // Si déjà visité, on saute
            if (visited.contains(currentUrl)) {
                continue;
            }
            visited.add(currentUrl);

            try {
                // Récupération du HTML
                Document doc = Jsoup.connect(currentUrl)
                        // Par exemple 60 secondes (60000 ms)
                        .timeout(60000)
                        // Pour autoriser une taille illimitée de la réponse (par défaut ~2Mo)
                        .maxBodySize(0)
                        .get();

                // 1) Parser la page pour extraire du vocabulaire
                // (Si la page courante en contient)
                // On peut adapter la condition : si c’est un .htm ou .html, on tente de parser
                if (currentUrl.endsWith(".htm") || currentUrl.endsWith(".html")) {
                    allVocabulary.addAll(parseVocab(doc));
                }

                // 2) Récupérer tous les liens <a href="...">
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String absUrl = link.absUrl("href");  // Transforme l’URL en absolu
                    // Filtrer : uniquement le même répertoire/domaine + .htm / .html
                    // Éviter la page main.htm, etc.
                    if (absUrl.startsWith(startUrl)
                            && (absUrl.endsWith(".htm") || absUrl.endsWith(".html"))
                            && !absUrl.contains("main.htm")
                            && !absUrl.contains("01.htm")) {
                        toVisit.add(absUrl);
                    }
                }

            } catch (Exception e) {
                // Logger l'erreur, puis continuer l'exploration
                System.err.println("Impossible de charger " + currentUrl + " : " + e.getMessage());
            }
        }

        return allVocabulary;
    }

    /**
     * Parse le contenu d'un document HTML (Document Jsoup) pour en extraire les entrées lexicales.
     * Ici, on part du principe que chaque entrée est dans un <p> ou similaire.
     *
     * @param doc Le Document (déjà téléchargé par Jsoup)
     * @return Liste de Vocabulary correspondant au contenu de la page
     */
    private static List<Vocabulary> parseVocab(Document doc) {
        List<Vocabulary> pageVocabulary = new ArrayList<>();

        // Exemple : on sélectionne tous les paragraphes
        // À vous d’adapter selon la structure HTML réelle
        Elements entries = doc.select(".lpLexEntryPara, .lpLexEntryPara2"); // :has(.lpLexEntryPara)
        // Liste pour stocker les groupes (un mot par groupe)
        List<List<Element>> groupedEntries = new ArrayList<>();
        List<Element> currentGroup = new ArrayList<>();

        // on constitue les groupes
        for (Element entry : entries) {
            if (entry.hasClass("lpLexEntryPara")) {
                // Si on trouve un bloc principal, on démarre un nouveau groupe
                if (!currentGroup.isEmpty()) {
                    groupedEntries.add(currentGroup);
                }
                currentGroup = new ArrayList<>();
            }
            currentGroup.add(entry);
        }
        // Ne pas oublier d'ajouter le dernier groupe
        if (!currentGroup.isEmpty()) {
            groupedEntries.add(currentGroup);
        }

        // TODO : review
        for (List<Element> group : groupedEntries) {
            Element baseEntry = group.getFirst(); // lpLexEntryPara est toujours le premier

            Element name = baseEntry.select(".lpLexEntryName").first();
            Element partOfSpeech = baseEntry.select(".lpPartOfSpeech").first();

            Vocabulary vocabulary = new Vocabulary(name != null ? name.text() : "");

            Element idx = baseEntry.select("sub > span.lpHomonymIndex").first();
            if (idx != null) {
                String rawIdx = idx.text().trim();
                try {
                    vocabulary.setHomonymIndex(Integer.parseInt(rawIdx));
                } catch (NumberFormatException ignored) { }
            }

            if (partOfSpeech != null) {
                for (String pos : partOfSpeech.text().split(" ")) {
                    vocabulary.addPartOfSpeech(new PartOfSpeech(pos));
                }
            }

            // On traite tous les enfants de tous les éléments du groupe
            for (Element entry : group) {
                Elements children = entry.children();

                for (int i = 0; i < children.size(); i++) {
                    Element child = children.get(i);

                    if (child.hasClass("lpMiniHeading")) {
                        // Vérifie si l'élément suivant existe dans l'ordre DOM local
                        if (i + 1 < children.size()) {
                            Element next = children.get(i + 1);
                            if (next.hasClass("lpParadigm")) {
                                Vocabulary linkedVocabulary = extractLinkedVocabulary(child, next);
                                vocabulary.addLinkedVocabulary(linkedVocabulary);
                                linkedVocabulary.addLinkedVocabulary(vocabulary);
                                pageVocabulary.add(linkedVocabulary);
                            }
                        } else {
                            vocabulary.addDialect(extractDialects(child));
                        }
                    } else if (child.hasClass("lpExample")) {
                        if (i + 1 < children.size()) {
                            Element next = children.get(i + 1);
                            if (next.hasClass("lpGlossEnglish")) {
                                vocabulary.addExample(new Example(child.text(), next.text()));
                            }
                        } else vocabulary.addExample(new Example(child.text()));
                    } else if (child.hasClass("lpPartOfSpeech") || child.hasClass("lpSenseNumber")) {
                        if (i + 1 < children.size()) {
                            Element next = children.get(i + 1);
                            if (next.hasClass("lpGlossEnglish")) {
                                vocabulary.addMeaning(new Meaning(next.text()));
                            }
                        }
                    }
                }
            }
            pageVocabulary.add(vocabulary);
        }

        return pageVocabulary;
    }

    private static Dialect extractDialects(Element dialectElement) {
        Dialect dialect = new Dialect();
        if (dialectElement != null) {
            String fullText = dialectElement.text();

            // Regex pour extraire le contenu entre crochets
            Pattern pattern = Pattern.compile("\\[(.*?)]");
            Matcher matcher = pattern.matcher(fullText);

            if (matcher.find()) {
                String insideBrackets = matcher.group(1); // contenu entre []
                // Si plusieurs dialectes séparés par des virgules
                // TODO liste dialects
//                for (String d : insideBrackets.split(",")) {
//                    dialect.setDialect(d.trim());
//                }
                dialect.setDialectName(insideBrackets);
            }
        }
        return dialect;
    }

    private static Vocabulary extractLinkedVocabulary(Element heading, Element paradigm) {
        Vocabulary linkedVocabulary = new Vocabulary();

        linkedVocabulary.addDialect(extractDialects(heading));
        for(String element : heading.text().split(" ")) {
            if(!element.isEmpty() && !(element.contains("[") || element.contains("]"))) {
                linkedVocabulary.addPartOfSpeech(new PartOfSpeech(element.replaceAll("\\W+", "")));
            }
        }

        linkedVocabulary.setEntry(paradigm.text());

        return linkedVocabulary;
    }
}
