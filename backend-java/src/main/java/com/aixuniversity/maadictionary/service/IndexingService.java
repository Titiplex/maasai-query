package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.config.ImportStatus;
import com.aixuniversity.maadictionary.dao.join.PhonemeCategoryDao;
import com.aixuniversity.maadictionary.dao.join.VocabularyPhonemeCategoryDao;
import com.aixuniversity.maadictionary.dao.join.VocabularyPhonemeDao;
import com.aixuniversity.maadictionary.dao.normal.CategoryDao;
import com.aixuniversity.maadictionary.dao.normal.PhonemeDao;
import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Category;
import com.aixuniversity.maadictionary.model.Phoneme;
import com.aixuniversity.maadictionary.model.Syllable;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.parser.extractors.SyllableExtractor;
import com.aixuniversity.maadictionary.service.search.SyllablePattern;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public final class IndexingService {
    public static void reindex() throws SQLException {
        VocabularyDao vDao = new VocabularyDao();
        PhonemeDao pDao = new PhonemeDao();
        CategoryDao cDao = new CategoryDao();
        VocabularyPhonemeDao vpDao = new VocabularyPhonemeDao();
        VocabularyPhonemeCategoryDao vpcDao = new VocabularyPhonemeCategoryDao();
        PhonemeCategoryDao pcDao = new PhonemeCategoryDao();

        List<Integer> idListToIndex = ImportStatus.unindexedVocabularyIds();
        int total = idListToIndex.size();
        int done = 0;

        // for (Vocabulary v : vDao.getAll()) {
        for (int vid : idListToIndex) {
            done++;
            ImportStatus.ProgressBar.print(done, total);
            Vocabulary v = vDao.searchById(vid);
            int existing = vpDao.getLinkedIds(vid).size();
            if (existing == SyllableExtractor.tokenizeIPAWord(v.getIpa()).size()) {
                continue;
            }
            // System.out.println("Indexing " + v.getEntry());

            int pos = 0;
            for (Syllable s : v.getSyllables()) {
                List<String> toks = s.getTokens();
                List<List<String>> pat = SyllablePattern.parseUniqueSyllable(s.getPattern());
                // System.out.println(pat);

                int indexSyll = v.getSyllables().indexOf(s);
                if (toks.size() != pat.size()) {
                    System.err.printf(
                            "⚠  Pattern/token size mismatch for vocabulary id %d, syllable %d: %d tokens vs %d pattern parts%n",
                            v.getId(), indexSyll, toks.size(), pat.size());
                }

                for (int i = 0; i < toks.size(); i++) {
                    String tok = toks.get(i);
                    Phoneme ph = Phoneme.getOrCreateSQL(tok, pDao);
                    // System.out.println(ph);
                    ph.addFreq();

                    int vpId = (int) vpDao.insertLink(v.getId(), ph.getId(), pos++, i, indexSyll);

                    // Use empty list when pattern is missing to avoid IndexOutOfBoundsException
                    List<String> catAbbrs = (i < pat.size()) ? pat.get(i) : Collections.emptyList();
                    for (String abbr : catAbbrs) {
                        Category cat = Category.getOrCreate(abbr, cDao);
                        // System.out.println(cat);
                        cat.addFreq();
                        pcDao.insertLink(ph.getId(), cat.getId());
                        vpcDao.insertLink(vpId, cat.getId(), i, indexSyll);
                    }
                }
            }
            ImportStatus.markIndexed(vid);
        }
    }

    public static void main(String[] args) throws SQLException {
        reindex();
    }
}