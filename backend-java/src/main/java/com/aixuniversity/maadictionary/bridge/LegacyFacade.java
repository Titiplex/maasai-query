package com.aixuniversity.maadictionary.bridge;


import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Point d’entrée unique utilisé par les contrôleurs REST.
 */
@Service
@RequiredArgsConstructor
public class LegacyFacade {

    private final VocabularyDao vocabularyDao;
    private final SimpMessagingTemplate msg;

    public Vocabulary one(String lemma) throws SQLException {
        return vocabularyDao.searchById(
                vocabularyDao.searchIdOfUniqueElement(lemma, "entry")
        );
    }

    public List<Vocabulary> all() throws SQLException {
        return vocabularyDao.getAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void save(Vocabulary v) throws SQLException {
        vocabularyDao.insert(v);
        msg.convertAndSend("/topic/vocabulary", v);   // push vers clients
    }
}
