// service/search/SearchService.java
package com.aixuniversity.maadictionary.service;

import com.google.common.reflect.ClassPath;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.Searcher;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orchestrateur : on enregistre dynamiquement des moteurs de recherche.
 */
public final class SearchService {
    private static final Map<String, Searcher<?>> registry = new HashMap<>();

    public static List<Vocabulary> search(String raw) throws SQLException {
        // TODO searching mixt instead of pure dichotomy
        String type;
        String q = raw;
        int sep = raw.indexOf(':');
        if (sep > 0 && sep < 6) {
            type = raw.substring(0, sep).toLowerCase();
            q = raw.substring(sep + 1);
        } else if (raw.contains("|") || raw.contains("-")) type = "syll";
            // else if (raw.matches(".*[\\u0300-\\u0302\\u02E5-\\u02E9].*")) type = "tone";
        else type = "ipa";
        return query(type, q);
    }

    public static void register() {
        try {
            findImplementations().forEach(classInfo -> {
                try {
                    Class<?> clazz = classInfo.load();
                    Searcher<?> s = (Searcher<?>) clazz.getDeclaredConstructor().newInstance();
                    registry.put(s.getClass().getSimpleName().toLowerCase(), s);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<ClassPath.ClassInfo> findImplementations() throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(classInfo -> {
                    Class<?> clazz = classInfo.load();
                    return Searcher.class.isAssignableFrom(clazz)
                            && !Modifier.isInterface(clazz.getModifiers());
                })
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private static <Q> List<Vocabulary> query(String name, Q q) throws SQLException {
        Searcher<Q> s = (Searcher<Q>) registry.get(name);
        if (s == null) throw new IllegalArgumentException("No searcher: " + name);
        return s.search(q);
    }
}