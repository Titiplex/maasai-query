package com.aixuniversity.maadictionary.model;

import com.aixuniversity.maadictionary.exc.LanguageNullCodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Language extends AbstractModel {

    private final static Map<String, Language> languages = new HashMap<>();
    private String code;
    private String name;

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
        addLanguage(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Map<String, Language> getLanguages() {
        return languages;
    }

    public static void addLanguage(Language language) {
        if (!languages.containsKey(language.getCode())) {
            languages.put(language.getCode(), language);
        }
    }

    public static void removeLanguage(String code) throws LanguageNullCodeException {
        if (!languages.containsKey(code)) throw new LanguageNullCodeException(code);
        languages.remove(code);
    }

    public static Language getLanguage(String code) {
        if (!languages.containsKey(code)) return null;
        return languages.get(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language that)) return false;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCode());
    }

    @Override
    public String toString() {
        return "Language {" +
                "\n\tcode='" + code + '\'' +
                "\n\tname='" + name + '\'' +
                '}';
    }
}
