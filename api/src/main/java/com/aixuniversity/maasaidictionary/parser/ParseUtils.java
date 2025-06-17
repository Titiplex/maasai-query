package main.java.com.aixuniversity.maasaidictionary.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseUtils {
    /**
     * Parse les minis headings pour une liste
     *
     * @param doc Le document à Parser
     * @return Un set contenant les miniheadings
     */
    private static Set<String> parseMiniHeadings(Document doc) {
        Set<String> miniHeadings = new HashSet<>();

        Elements miniHeadingElements = doc.select(".lpMiniHeading");

        for (Element headingElement : miniHeadingElements) {
            miniHeadings.addAll(List.of(headingElement.text().split("\\W+")));
        }

        return miniHeadings;
    }

    /**
     * Parses a page to get its headings in the form of strings.
     *
     * @param doc The Document you want to get the Headings from.
     * @return The list of headings in form of a string.
     */
    private static Set<String> parseLpHeadings(Document doc) {
        Set<String> lpClasses = new HashSet<>();
        Elements headingElements = doc.getAllElements();

        for (Element headingElement : headingElements) {
            // Récupérer la liste des classes de l'élément
            for (String className : headingElement.classNames()) {
                // Vérifier si la classe commence par "lp"
                if (className.startsWith("lp")) {
                    lpClasses.add(className);
                }
            }
        }

        return lpClasses;
    }

    private static Set<String> parsePOS(Document doc) {
        Set<String> pos = new HashSet<>();

        Elements miniHeadingElements = doc.select(".lpPartOfSpeech");

        for (Element headingElement : miniHeadingElements) {
            pos.addAll(List.of(headingElement.text().split("\\W+")));
        }

        return pos;
    }
}
