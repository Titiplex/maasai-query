package main.java.com.aixuniversity.maasaidictionary.parser;

import main.java.com.aixuniversity.maasaidictionary.model.Meaning;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe HtmlParser permettant de récupérer le vocabulaire
 * sur la base du lexique Maa (pages uoregon.edu/maasai/...).
 */
public class HtmlParser {

    /**
     * Récupère l'ensemble du vocabulaire en parcourant la frame de gauche
     * et chaque page associée (A, B, C…).
     *
     * @param baseUrl   L'URL de base, par ex. "<a href="https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/">...</a>"
     * @param leftFrame Le nom du fichier HTML de la frame gauche, ex. "lexiconleft.htm"
     * @return Une liste de Vocabulary (objet métier) contenant l'ensemble du lexique.
     * @throws Exception En cas de problème réseau ou parsing.
     */
    public List<Vocabulary> parseAll(String baseUrl, String leftFrame) throws Exception {
        List<Vocabulary> allVocabulary = new ArrayList<>();

        // 1. Charger la page "lexiconleft.htm" (frame de gauche).
        String leftFrameUrl = baseUrl + leftFrame;
        Document leftDoc = Jsoup.connect(leftFrameUrl).get();

        // 2. Récupérer tous les liens (A, B, C…) dans la frame de gauche.
        //    Hypothèse : ils sont dans des <a href="A.htm">, <a href="B.htm">, etc.
        Elements links = leftDoc.select("a[href]");

        // 3. Parcourir chaque lien (ex : "A.htm", "B.htm", ...).
        for (Element link : links) {
            String relativeHref = link.attr("href");
            if (relativeHref.toLowerCase().endsWith(".htm")) {
                String pageUrl = baseUrl + relativeHref;
                System.out.println("Parsing page : " + pageUrl);

                List<Vocabulary> vocabFromPage = parsePage(pageUrl);
                allVocabulary.addAll(vocabFromPage);
            }
        }

        return allVocabulary;
    }

    /**
     * Parse une page du lexique (ex : A.htm) pour en extraire les entrées lexicales.
     *
     * @param pageUrl L'URL complète de la page (ex. "<a href="https://.../A.htm">...</a>").
     * @return Liste de Vocabulary correspondant au contenu de la page.
     * @throws Exception En cas de cas particulier
     */
    private List<Vocabulary> parsePage(String pageUrl) throws Exception {
        List<Vocabulary> pageVocabulary = new ArrayList<>();

        // Charger la page
        Document doc = Jsoup.connect(pageUrl).get();

        // Exemple fictif : Supposons que chaque mot se trouve dans
        // un <table> ou dans des <p> spécifiques.
        // Il faut adapter le sélecteur CSS en fonction de la structure réelle de la page.
        Elements entries = doc.select("p");
        // Ou doc.select("table tr") si c’est dans un tableau, etc.

        for (Element entry : entries) {
            // Ici, on doit déterminer la logique pour extraire  –
            //  Le mot (en Maa).
            //  La traduction (en anglais ou une autre langue)
            // - Éventuellement d'autres informations (phonétique, notes, etc.)

            // Exemples d’extraction :
            //   On récupère le texte brut du paragraphe
            //   On parse ou on coupe la chaîne selon un séparateur
            String rawText = entry.text().trim();
            if (rawText.isEmpty()) {
                continue;
            }

            // Supposons que la structure soit quelque chose du genre "Mot en Maa : Traduction"
            // → c’est souvent plus complexe, il faudra adapter.
            String[] parts = rawText.split(":");
            if (parts.length >= 2) {
                String maaWord = parts[0].trim();
                String meaning = parts[1].trim();

                // Construction des objets Model (exemple arbitraire)
                Word w = new Word(maaWord);
                Meaning m = new Meaning(meaning);

                // On assemble un Vocabulary (hypothèse : Vocabulary a une liste de sens, etc.)
                Vocabulary vocab = new Vocabulary();
                vocab.setMaaWord(w);
                vocab.addMeaning(m);

                pageVocabulary.add(vocab);
            } else {
                // Gérer les cas particuliers, ou logger un avertissement
                System.out.println("Format inattendu pour l'entrée : " + rawText);
            }
        }

        return pageVocabulary;
    }
}
