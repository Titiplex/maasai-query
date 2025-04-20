package main.java.com.aixuniversity.maasaidictionary.service;

import main.java.com.aixuniversity.maasaidictionary.dao.join.*;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.*;
import main.java.com.aixuniversity.maasaidictionary.model.*;
import main.java.com.aixuniversity.maasaidictionary.parser.HtmlParser;

import java.util.List;
import java.util.Map;

/**
 * Orchestre la récupération des entrées du dictionnaire
 * et leur insertion dans la BDD
 */
public abstract class ImportService {
    public static boolean importVocabulary(List<Vocabulary> vocabularyList) {
        try {
            Map<Language, Integer> languageIntegerMap = new LanguageDao().insertAll(Language.getLanguages().values());
            Map<PartOfSpeech, Integer> posIntegerMap = new PartOfSpeechDao().insertAll(PartOfSpeech.getPartOfSpeechList().values());
            Map<Dialect, Integer> dialectIntegerMap = new DialectDao().insertAll(Dialect.getDialects().values());
            Map<Vocabulary, Integer> vocabularyIntegerMap = new VocabularyDao().insertAll(vocabularyList);

            MeaningDao meaningDao = new MeaningDao();
            ExampleDao exampleDao = new ExampleDao();
            MeaningLanguageDao meaningLanguageDao = new MeaningLanguageDao();
            MeaningDialectDao meaningDialectDao = new MeaningDialectDao();
            ExampleLanguageDao exampleLanguageDao = new ExampleLanguageDao();
            ExampleDialectDao exampleDialectDao = new ExampleDialectDao();
            PosLinkedDao posLinkedDao = new PosLinkedDao();
            VocabularyLinkedDao vocLinkedDao = new VocabularyLinkedDao();
            VocabularyDialectDao vocabularyDialectDao = new VocabularyDialectDao();

            for (Vocabulary vocabulary : vocabularyList) {
                vocabulary.setId(vocabularyIntegerMap.get(vocabulary));
                vocabulary.setAllIds();

                List<Meaning> meanings = vocabulary.getMeanings();
                Map<Meaning, Integer> meaningsIntegerMap = meaningDao.insertAll(meanings);
                for (Meaning meaning : meanings) {
                    int meaningId = meaningsIntegerMap.get(meaning);
                    meaningLanguageDao.insertLink(meaningId, languageIntegerMap.get(meaning.getLanguage()));
                    for (Dialect dialect : meaning.getDialects()) {
                        meaningDialectDao.insertLink(meaningId, dialectIntegerMap.get(dialect));
                    }
                }

                List<Example> examples = vocabulary.getExamples();
                Map<Example, Integer> exampleIntegerMap = exampleDao.insertAll(examples);
                for (Example example : examples) {
                    int exampleId = exampleIntegerMap.get(example);
                    exampleLanguageDao.insertLink(exampleId, languageIntegerMap.get(example.getGlossLanguage()));
                    exampleDialectDao.insertLink(exampleId, dialectIntegerMap.get(example.getDialect()));
                }

                for (PartOfSpeech pos : vocabulary.getPartsOfSpeech()) {
                    posLinkedDao.insertLink(vocabulary.getId(), posIntegerMap.get(pos));
                }

                for (Vocabulary vocLinked : vocabulary.getLinkedVocabularies()) {
                    vocLinkedDao.insertLink(vocabulary.getId(), vocabularyIntegerMap.get(vocLinked));
                }

                List<Dialect> dialects = vocabulary.getDialects();
                for (Dialect dialect : dialects) {
                    vocabularyDialectDao.insertLink(vocabulary.getId(), dialectIntegerMap.get(dialect));
                }

                System.out.println("Enregistrement pour : " + vocabulary.getEntry());
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main (String[] args) {
        String baseUrl = "https://pages.uoregon.edu/maasai/Maa%20Lexicon/lexicon/";
        System.out.println(ImportService.importVocabulary(HtmlParser.parseAll(baseUrl)));
    }
}
