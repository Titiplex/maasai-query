package com.aixuniversity.maadictionary.config;

import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EmbeddedMariaDbConfig {

    @Value("${DATABASE:maa}")
    private String dbName;

    @Value("${USER:root}")
    private String user;

    @Value("${PASSWORD:}")
    private String pwd;

    /* 1️⃣  Lance le serveur MariaDB4j et le stoppe à la fin */
    @Bean(destroyMethod = "stop")
    public MariaDB4jSpringService mariaDB4j() {
        return new MariaDB4jSpringService();      // lit application.yml (mariaDB4j.*)
    }

    /* 2️⃣  Crée le DataSource que Spring/Flyway utiliseront */
    @Bean
    public DataSource dataSource(MariaDB4jSpringService db) {
        int port = db.getConfiguration().getPort();
        System.setProperty("PORT", String.valueOf(port));  // pour DatabaseHelper

        String url = "jdbc:mariadb://localhost:" + port + "/" + dbName +
                "?createDatabaseIfNotExist=true" +        // <-- clé !
                "&useUnicode=true&characterEncoding=UTF-8";

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pwd);
        return ds;
    }
}
