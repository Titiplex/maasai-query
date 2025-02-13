package main.java.com.aixuniversity.maasaidictionary.app;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.parser.HtmlParser;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String baseUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";
        List<Vocabulary> vocabularyList = HtmlParser.parseAll(baseUrl);
        System.out.println("vocabularyList");

        int a = 0;
        for (Vocabulary vocabulary : vocabularyList) {
            System.out.println(vocabulary.toString());
            a++;
        }
        System.out.println("nb : " + a);
    }
}
