package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.app.ImportStatus;
import com.aixuniversity.maadictionary.config.DialectConfig;
import com.aixuniversity.maadictionary.config.LanguageConfig;
import com.aixuniversity.maadictionary.config.PosConfig;
import com.aixuniversity.maadictionary.dao.join.*;
import com.aixuniversity.maadictionary.dao.normal.*;
import com.aixuniversity.maadictionary.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestre la récupération des entrées du dictionnaire
 * et leur insertion dans la BDD
 */
public abstract class ImportService {
    public static boolean importVocabulary(List<Vocabulary> vocabularyList) {
        System.out.println("--- Importing vocabulary ---");
        System.out.println("Total vocabulary count: " + vocabularyList.size());
        System.out.println("Cleaning vocabulary list...");
        vocabularyList.removeIf(v -> v.getEntry() == null || v.getEntry().isEmpty());
        System.out.println("Vocabulary list cleaned, count: " + vocabularyList.size());
        try {
            System.out.println("Inserting language data...");
            System.out.println("Inserting language properties...");
            LanguageDao languageDao = new LanguageDao();
            Map<Language, Integer> languageIntegerMap = new HashMap<>();
            for (Map.Entry<String, String> lang : LanguageConfig.getLangMap().entrySet()) {
                Language language = new Language(lang.getKey(), lang.getValue());
                languageIntegerMap.put(language, languageDao.insert(language));
            }
            System.out.println("Imported languages");

            System.out.println("Inserting POS data...");
            System.out.println("Inserting POS properties...");
            PartOfSpeechDao posDao = new PartOfSpeechDao();
            Map<PartOfSpeech, Integer> posIntegerMap = new HashMap<>();
            for (Map.Entry<String, String> pos : PosConfig.getPosMap().entrySet()) {
                // TODO les abbréviations de pos -> noms complets
                PartOfSpeech partOfSpeech = new PartOfSpeech(pos.getValue());
                posIntegerMap.put(partOfSpeech, posDao.insert(partOfSpeech));
            }
            System.out.println("Inserted POS properties.");
            System.out.println("Inserting residual Pos from HTML...");
            Collection<PartOfSpeech> posList = PartOfSpeech.getPartOfSpeechList().values();
            posList.removeIf(
                    p ->
                            PartOfSpeech.getPartOfSpeech(p.getPos()) != null
                                    || p.getPos().isEmpty()
                                    || p.getPos() == null
            );
            posIntegerMap.putAll(posDao.insertAll(posList));
            System.out.println("Imported POS.");

            System.out.println("Inserting dialect data...");
            System.out.println("Inserting dialect properties...");
            DialectDao dialectDao = new DialectDao();
            Map<Dialect, Integer> dialectIntegerMap = new HashMap<>();
            for (Map.Entry<String, String> dialect : DialectConfig.getDialectMap().entrySet()) {
                Dialect dial = new Dialect(dialect.getKey());
                dialectIntegerMap.put(dial, dialectDao.insert(dial));
            }
            System.out.println("Imported dialects");

            Map<Vocabulary, Integer> vocabularyIntegerMap = new VocabularyDao().insertAll(vocabularyList);
            System.out.println("Imported entries");

            MeaningDao meaningDao = new MeaningDao();
            ExampleDao exampleDao = new ExampleDao();
            MeaningLanguageDao meaningLanguageDao = new MeaningLanguageDao();
            MeaningDialectDao meaningDialectDao = new MeaningDialectDao();
            ExampleLanguageDao exampleLanguageDao = new ExampleLanguageDao();
            ExampleDialectDao exampleDialectDao = new ExampleDialectDao();
            PosLinkedDao posLinkedDao = new PosLinkedDao();
            VocabularyLinkedDao vocLinkedDao = new VocabularyLinkedDao();
            VocabularyDialectDao vocabularyDialectDao = new VocabularyDialectDao();

            int total = vocabularyList.size();
            int done = 0;

            System.out.println("Inserting vocabulary data...");
            for (Vocabulary vocabulary : vocabularyList) {
                vocabulary.setId(vocabularyIntegerMap.get(vocabulary));
                vocabulary.setAllIds();

                ImportStatus.markModified(vocabulary.getId());
                done++;
                ImportStatus.ProgressBar.print(done, total);

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

                // TODO linked ids sometimes not inserted prior in vocab
                for (Vocabulary vocLinked : vocabulary.getLinkedVocabularies()) {
                    Integer linkedId = vocabularyIntegerMap.get(vocLinked);

                    // If the linked vocabulary has not been inserted yet, skip it and warn the user
                    if (linkedId == null) {
                        System.err.printf(
                                "Warning: linked vocabulary '%s' referenced from '%s' is missing from the import list and will be ignored.%n",
                                vocLinked.getEntry(), vocabulary.getEntry()
                        );
                        continue;          // Prevents NullPointerException
                    }
                    vocLinkedDao.insertLink(vocabulary.getId(), linkedId);
                }

                List<Dialect> dialects = vocabulary.getDialects();
                for (Dialect dialect : dialects) {
                    vocabularyDialectDao.insertLink(vocabulary.getId(), dialectIntegerMap.get(dialect) != null ? dialectIntegerMap.get(dialect) : 1);
                }

                // System.out.println("Enregistrement pour : " + vocabulary.getEntry());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'importation : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}