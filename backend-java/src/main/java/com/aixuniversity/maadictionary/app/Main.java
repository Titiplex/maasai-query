package com.aixuniversity.maadictionary.app;

import com.aixuniversity.maadictionary.config.ImportStatus;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.parser.HtmlParser;
import com.aixuniversity.maadictionary.service.ImportService;
import com.aixuniversity.maadictionary.service.IndexingService;
import com.aixuniversity.maadictionary.service.SearchService;
import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String baseUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";

        System.out.println("Processing...");
        System.out.println("Result : " + (process(baseUrl) ? "OK" : "KO"));


        try (Scanner scanner = new Scanner(System.in)) {
            SearchService searchService = new SearchService();   // reuse the service instance
            while (true) {
                System.out.print("Search (blank to quit): ");
                String query = scanner.nextLine().trim();        // real user input
                if (query.isBlank()) {
                    System.out.println("Bye!");
                    break;                                       // explicit exit condition
                }
                try {
                    List<ScoredResult> results = searchService.search(query);
                    if (results.isEmpty()) {
                        System.out.println("No results. Please check if all the characters in the query are known.");
                    } else {
                        results.forEach(System.out::println);
                    }
                } catch (SQLException e) {
                    System.err.println("Search failed: " + e.getMessage());
                }
            }
        }
    }

    public static boolean process(String url) {
        try {
            if (ImportStatus.needsImport(url)) {
                System.out.println("\n\tImporting " + url);
                ImportService.main(new String[]{""});
                ImportStatus.recordImport(url);
            }
            System.out.println("\n\tReindexing");
            IndexingService.main(new String[]{});
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
