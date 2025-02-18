package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.Objects;

/**
 * Représente un sens ou une définition d'un mot,
 * éventuellement dans une certaine langue.
 */
public class Meaning extends AbstractModel {
    private String definition;
    private String language;

    public Meaning() {
        this.definition = "";
        this.language = "";
    }

    public Meaning(String definition) {
        this.definition = definition;
        // English as default language
        this.language = "en";
    }

    public Meaning(String definition, String language) {
        this.definition = definition;
        this.language = language;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return definition + "\t(" + language + ")\n";
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

