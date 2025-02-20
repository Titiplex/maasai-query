package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.Objects;

/**
 * Représente un sens ou une définition d'un mot,
 * éventuellement dans une certaine langue.
 */
public class Meaning extends AbstractModel {
    private String definition;
    private Language language;

    public Meaning() {
        this.definition = "";
        this.language = Language.getLanguage("en");
    }

    public Meaning(String definition) {
        this.definition = definition;
        // English as default language
        this.language = Language.getLanguage("en");
    }

    public Meaning(String definition, String languageCode) {
        this.definition = definition;
        this.language = Language.getLanguage(languageCode);
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return definition + "\t(" + language.getCode() + ")\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meaning meaning = (Meaning) o;
        return Objects.equals(definition, meaning.definition) &&
                Objects.equals(language, meaning.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition, language);
    }
}

