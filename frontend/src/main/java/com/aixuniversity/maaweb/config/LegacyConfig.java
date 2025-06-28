// maasai-web/src/main/java/com/aixuniversity/maadictionary/web/LegacyConfig.java
package com.aixuniversity.maaweb.config;

import com.aixuniversity.maadictionary.service.SearchService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class LegacyConfig {

    @Bean
    public SearchService searchService() throws SQLException {
        return new SearchService();
    }
}
