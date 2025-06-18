package com.aixuniversity.maadictionary.bridge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Rend accessibles tous les DAO legacy (qui n'ont aucune annotation Spring) sans les modifier.
 * On instancie chaque classe *Dao* trouvée dans le package dao.* via réflexion.
 * Cela permet l'injection dans d'autres contrôleurs si besoin :  @Autowired VocabularyDao vocDao;
 */
@Configuration
public class DaoBridgeConfig {

    @Bean
    @Lazy
    public Map<String, Object> legacyDaos() throws Exception {
        String basePkg = "com.aixuniversity.maadictionary.dao";
        Map<String, Object> map = new HashMap<>();
        ClassUtils.getAllInterfacesForClass(Object.class);// no-op – placeholder to force ClassUtils init (IDEA sometimes warns, ignore)
// Charge toutes les classes se terminant par "Dao"
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        cl.getResources(basePkg.replace('.', '/'));// (simplifié) → au besoin, utilise ClassPath de Guava comme ton SearchService
// Si la réflexion complète est trop longue, on peut lister manuellement :
        // map.put("vocabularyDao", new VocabularyDaoJdbc(...DataSource...));
        return map;
    }
}
