// service/IndexingService.java   (transactionnel & incrémental)
package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.dao.join.PhonemeCategoryDao;
import com.aixuniversity.maadictionary.dao.join.VocabularyPhonemeCategoryDao;
import com.aixuniversity.maadictionary.dao.join.VocabularyPhonemeDao;
import com.aixuniversity.maadictionary.dao.normal.CategoryDao;
import com.aixuniversity.maadictionary.dao.normal.PhonemeDao;
import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.model.Category;
import com.aixuniversity.maadictionary.model.Phoneme;
import com.aixuniversity.maadictionary.model.Syllable;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.SyllablePattern;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class IndexingService {
    public static void reindex() throws SQLException {
        VocabularyDao vDao = new VocabularyDao();
        PhonemeDao pDao = new PhonemeDao();
        CategoryDao cDao = new CategoryDao();
        VocabularyPhonemeDao vpDao = new VocabularyPhonemeDao();
        VocabularyPhonemeCategoryDao vpcDao = new VocabularyPhonemeCategoryDao();
        PhonemeCategoryDao pcDao = new PhonemeCategoryDao();

        for (Vocabulary v : vDao.getAll()) {
            if (!vpDao.getLinkedIds(v.getId()).isEmpty()) continue; // déjà indexé ⇒ skip
            try (Connection con = DatabaseHelper.getConnection()) {
                con.setAutoCommit(false);
                int pos = 0;
                for (Syllable s : v.getSyllablesList()) {
                    List<String> toks = s.getTokens();
                    List<List<String>> pat = SyllablePattern.parseUniqueSyllable(s.getPattern());
                    for (int i = 0; i < toks.size(); i++) {
                        String tok = toks.get(i);
                        Phoneme ph = Phoneme.getOrCreateSQL(tok, pDao); // helper static que tu avais
                        ph.addFreq();
                        int vpId = (int) vpDao.insertLink(v.getId(), ph.getId(), pos++);
                        for (String abbr : pat.get(i)) {
                            Category cat = Category.getOrCreate(abbr, cDao);
                            cat.addFreq();
                            pcDao.insertLink(ph.getId(), cat.getId());
                            vpcDao.insertLink(vpId, cat.getId());
                        }
                    }
                }
                con.commit();
            }
        }
    }
}