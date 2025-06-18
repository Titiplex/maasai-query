package com.aixuniversity.maadictionary.bridge;


import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import jakarta.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enregistre tous les objets « legacy » comme beans Spring
 * sans leur ajouter d’annotations.
 */
@Configuration
public class LegacyConfig {

    @Bean
    public VocabularyDao vocabularyDao(DataSource dataSource) {
        return new VocabularyDaoJdbc(dataSource);
    }

    @Bean
    public DictionaryParser dictionaryParser() {
        return new DictionaryParser();
    }
}
