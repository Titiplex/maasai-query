package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Language extends AbstractModel{

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

    public static void addLanguage(Language language) {
        if (!languages.containsKey(language.getCode())) {
            languages.put(language.getCode(), language);
        }
    }

    public static void removeLanguage(String code) {
        languages.remove(code);
    }

    public static Language getLanguage(String code) {
        return languages.get(code);
        // TODO exceptions perso
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(code, language.code) && Objects.equals(name, language.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }

    @Override
    public String toString() {
        return "Language {" +
                "\n\tcode='" + code + '\'' +
                "\n\tname='" + name + '\'' +
                '}';
    }
}
