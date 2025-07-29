package com.aixuniversity.maadictionary.app;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.parser.HtmlParser;
import com.aixuniversity.maadictionary.service.ImportService;
import com.aixuniversity.maadictionary.service.IndexingService;
import com.aixuniversity.maadictionary.service.SearchService;
import com.aixuniversity.maadictionary.service.search.Searcher;
import com.aixuniversity.maadictionary.service.search.SimpleSequentialSearcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String baseUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";

        System.out.println("Processing...");
        if (process(baseUrl)) {
            System.out.println("Processing finished.");
        }

//        SearchService.main(new String[]{""});
//        Searcher<String> s = new SimpleSequentialSearcher();
//        for (String q : List.of("u", "VO#", "VO+")) {
//            System.out.println(q + " → " + s.search(q).size() + " résultats");
//        }
//        System.out.println(s.search("VO#"));
//        System.out.println(new VocabularyDao().getAll());
    }

    public static boolean process(String url) {
        try {
            System.out.println("Starting parsing...");
            List<Vocabulary> vocab = HtmlParser.parseAll(url);
            System.out.println("Parsing Finished. Do you want to import the vocabulary ? (y/n) ");
            String answer = System.console().readLine();
            if (answer.equalsIgnoreCase("y") && ImportStatus.needsImport(url)) {
                System.out.println("\n\tImporting " + url);
                if (ImportService.importVocabulary(vocab)) {
                    System.out.println("Imported successfully !");
                    ImportStatus.recordImport(url);
                } else {
                    System.out.println("Import failed !");
                }
            } else if (answer.equalsIgnoreCase("n") && ImportStatus.needsImport(url)) {
                System.out.println("Skipping import of " + url);
            } else {
                System.err.println("Invalid answer or couldn't determine if import is needed. Please answer y/n and try again.");
                return false;
            }
            System.out.println("Do you want to reindex the vocabulary ? (y/n) ");
            answer = System.console().readLine();
            if (answer.equalsIgnoreCase("y")) {
                IndexingService.reindex();
            } else if (answer.equalsIgnoreCase("n")) {
                System.out.println("Skipping reindexing of " + url);
            } else {
                System.err.println("Invalid answer or couldn't determine if reindex is needed. Please answer y/n and try again.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error while importing " + url);
            return false;
        }
        return true;
    }

    public static void printVocabList(String baseUrl) {
        List<Vocabulary> vocabularyList = HtmlParser.parseAll(baseUrl);

        System.out.println("vocabularyList");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("list.txt", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error while opening file");
        }

        int a = 0;
        for (Vocabulary vocabulary : vocabularyList) {
            String string = vocabulary.toString();
            //System.out.println(string);
            assert writer != null;
            writer.println(string);
            a++;
        }
        System.out.println("nb : " + a);
        assert writer != null;
        writer.close();
    }
}
