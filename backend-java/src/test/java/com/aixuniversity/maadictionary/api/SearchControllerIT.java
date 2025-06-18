package com.aixuniversity.maadictionary.api;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerIT {
    @Autowired
    MockMvc mvc;

    @MockBean
    SearchService dummy;   // pas appelé puisque SearchService est statique, mais oblige Spring à démarrer

    @Test
    void returns_json_array() throws Exception {
        List<Vocabulary> data = List.of(new Vocabulary());
        Mockito.mockStatic(SearchService.class).when(() -> SearchService.search("enkai")).thenReturn(data);

        mvc.perform(get("/api/v1/search?q=enkai"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}