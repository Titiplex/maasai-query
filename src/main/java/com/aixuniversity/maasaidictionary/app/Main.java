package main.java.com.aixuniversity.maasaidictionary.app;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.parser.HtmlParser;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String baseUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";

        List<Vocabulary> vocabularyList = HtmlParser.parseAll(baseUrl);

        System.out.println("vocabularyList");

        PrintWriter writer = new PrintWriter("list.txt", StandardCharsets.UTF_8);

        int a = 0;
        for (Vocabulary vocabulary : vocabularyList) {
            String string = vocabulary.toString();
            //System.out.println(string);
            writer.println(string);
            a++;
        }
        System.out.println("nb : " + a);
        writer.close();
    }
}
