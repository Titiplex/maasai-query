package main.java.com.aixuniversity.maasaidictionary.service;

import main.java.com.aixuniversity.maasaidictionary.config.AbbreviationConfig;
import main.java.com.aixuniversity.maasaidictionary.config.IPAConfig;
import main.java.com.aixuniversity.maasaidictionary.dao.join.PhonemeCategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.join.VocabularyPhonemeCategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.join.VocabularyPhonemeDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.CategoryDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.PhonemeDao;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Category;
import main.java.com.aixuniversity.maasaidictionary.model.Phoneme;
import main.java.com.aixuniversity.maasaidictionary.model.Syllable;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;
import main.java.com.aixuniversity.maasaidictionary.service.search.PatternSpec;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public abstract class IndexingService {
    public static void index() throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        List<Vocabulary> vocabularyList = new VocabularyDao().getAll();
        PhonemeDao phonemeDao = new PhonemeDao();
        CategoryDao categoryDao = new CategoryDao();
        PhonemeCategoryDao phonemeCategoryDao = new PhonemeCategoryDao();
        VocabularyPhonemeDao vocabularyPhonemeDao = new VocabularyPhonemeDao();
        VocabularyPhonemeCategoryDao vocabularyPhonemeCategoryDao = new VocabularyPhonemeCategoryDao();

        for (Vocabulary v : vocabularyList) {

            int phonemePosition = 0;

            for (Syllable syllable : v.getSyllablesList()) {

                List<String> sTokens = syllable.getTokens();

                List<List<String>> sylPattern = PatternSpec.parseUniqueSyllable(syllable.getPattern());

                for (int i = 0; i < sylPattern.size(); i++) {

                    String token = sTokens.get(i);
                    Phoneme phon = new Phoneme(
                            IPAConfig.getLetterFromIPA(token), token
                    );

                    int vpId;
                    try {
                        if (Phoneme.addPhoneme(phon)) phon.setId(phonemeDao.insert(phon));
                        else phon = Phoneme.getPhoneme(token);
                        assert phon != null;
                        vpId = (int) vocabularyPhonemeDao.insertLink(v.getId(), phon.getId(), phonemePosition++);
                    } catch (SQLException|NullPointerException e) {
                        throw new RuntimeException(e);
                    }

                    List<String> tokenAbbrList = sylPattern.get(i);
                    for (String label : tokenAbbrList) {
                        Category cat = getOrCreateCategory(label);

                        try {
                            cat.setId(categoryDao.insert(cat));
                            phonemeCategoryDao.insertLink(phon.getId(), cat.getId());
                            vocabularyPhonemeCategoryDao.insertLink(vpId, cat.getId());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private static Category getOrCreateCategory(String label) {
        Category existingCategory = Category.getCategory(label);
        if (existingCategory != null) {
            return existingCategory;
        }
        String categoryName = AbbreviationConfig.getFromAbbreviation(label);
        return new Category(categoryName, label);
    }
}