package com.aixuniversity.maaweb.config;

import com.aixuniversity.maadictionary.dao.normal.CategoryDao;
import com.aixuniversity.maadictionary.dao.normal.PhonemeDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LegacyDaoConfig {

    @Bean
    public PhonemeDao phonemeDao() {
        return new PhonemeDao();
    }

    @Bean
    public CategoryDao categoryDao() {
        return new CategoryDao();
    }
}
