package main.java.com.aixuniversity.maasaidictionary.service;

import main.java.com.aixuniversity.maasaidictionary.dao.DatabaseHelper;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.parser.HtmlParser;

import java.util.List;

/**
 * Orchestre la récupération des entrées du dictionnaire
 * et leur insertion dans la BDD
 */
public class ImportService {
    public ImportService() {
        try {
            String startUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";

            List<Vocabulary> vocabularyList = HtmlParser.parseAll(startUrl);

            for (Vocabulary vocabulary : vocabularyList) {
                // DatabaseHelper.save(vocabulary);
                System.out.println("Enregistrement pour " + vocabulary.getMaaWord().getEntryName());
            }

            System.out.println("Importation réussie pour : " + startUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
