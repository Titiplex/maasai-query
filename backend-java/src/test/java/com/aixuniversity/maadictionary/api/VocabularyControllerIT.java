package com.aixuniversity.maadictionary.api;

import com.aixuniversity.maadictionary.bridge.LegacyFacade;
import com.aixuniversity.maadictionary.model.Vocabulary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VocabularyControllerIT {

    @Autowired
    MockMvc mvc;

    @MockBean                   // Le vrai DAO reste intact ; on isole le test
    LegacyFacade legacy;

    @Test
    @DisplayName("GET /api/v1/vocabulary/enkai renvoie 200 et le mot")
    void get_word_returns_json() throws Exception {
        Vocabulary word = new Vocabulary("enkai");
        when(legacy.one("enkai")).thenReturn(word);

        mvc.perform(get("/api/v1/vocabulary/enkai"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.entry").value("enkai"))
                .andExpect(jsonPath("$.meaning").value("Dieu"));

        verify(legacy).one("enkai");
    }
}
