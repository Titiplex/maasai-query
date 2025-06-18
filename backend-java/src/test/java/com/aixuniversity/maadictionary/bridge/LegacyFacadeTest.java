package com.aixuniversity.maadictionary.bridge;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LegacyFacadeTest {

    @Test
    void one_returns_word_from_dao() throws SQLException {
        // given
        Vocabulary word = new Vocabulary("enkai");
        VocabularyDao mockDao = mock(VocabularyDao.class);
        when(mockDao.searchById(mockDao.searchIdOfUniqueElement("enkai", "entry"))).thenReturn(word);

        LegacyFacade facade = new LegacyFacade(mockDao);

        // when
        Vocabulary result = facade.one("enkai");

        // then
        assertThat(result).isSameAs(word);
        verify(mockDao).searchById(mockDao.searchIdOfUniqueElement("enkai", "entry"));
    }

    @Test
    void all_delegates_to_dao() throws SQLException {
        VocabularyDao mockDao = mock(VocabularyDao.class);
        when(mockDao.getAll()).thenReturn(List.of());

        LegacyFacade facade = new LegacyFacade(mockDao);

        assertThat(facade.all()).isEmpty();
        verify(mockDao).getAll();
    }
}
