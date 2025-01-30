package main.java.com.aixuniversity.maasaidictionary.parser;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

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
                    List<Vocabulary> vocabPage = parsePage(doc);
                    allVocabulary.addAll(vocabPage);
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
    private static List<Vocabulary> parsePage(Document doc) {
        List<Vocabulary> pageVocabulary = new ArrayList<>();

        // Exemple : on sélectionne tous les paragraphes
        // À vous d’adapter selon la structure HTML réelle
        Elements entries = doc.select(".lpLexEntryPara"); // :has(.lpLexEntryPara)

        for (Element entry : entries) {
            Element name = entry.selectFirst(".lpLexEntryName");
            Element partOfSpeech = entry.selectFirst(".lpPartOfSpeech");

            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setMaaWord(new Word(name != null ? name.text() : "", partOfSpeech != null ? partOfSpeech.text() : ""));
            pageVocabulary.add(vocabulary);
        }

        return pageVocabulary;
    }
}
