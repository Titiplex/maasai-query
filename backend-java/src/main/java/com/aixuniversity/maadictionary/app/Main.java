package com.aixuniversity.maadictionary.app;

import com.aixuniversity.maadictionary.dao.join.VocabularyPhonemeCategoryDao;
import com.aixuniversity.maadictionary.service.ImportService;
import com.aixuniversity.maadictionary.service.IndexingService;
import com.aixuniversity.maadictionary.service.SearchService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
//        String baseUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";

//        List<Vocabulary> vocabularyList = HtmlParser.parseAll(baseUrl);
//
//        System.out.println("vocabularyList");
//
//        PrintWriter writer = new PrintWriter("list.txt", StandardCharsets.UTF_8);
//
//        int a = 0;
//        for (Vocabulary vocabulary : vocabularyList) {
//            String string = vocabulary.toString();
//            //System.out.println(string);
//            writer.println(string);
//            a++;
//        }
//        System.out.println("nb : " + a);
//        writer.close();

//        System.out.println("Processing...");
//        System.out.println("Result : " + process());

        SearchService searchService = new SearchService();
        System.out.println("Search:");
        System.out.println(searchService.search("aeyu"));
    }

    public static void test() throws SQLException {
        VocabularyPhonemeCategoryDao dao = new VocabularyPhonemeCategoryDao();
        dao.insertLink(1, 2, 3, 4);
    }

    public static boolean process() {
        try {
            ImportService.main(new String[]{""});
            System.out.println("Import done");
            IndexingService.main(new String[]{""});
            System.out.println("Indexing done");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }
}
