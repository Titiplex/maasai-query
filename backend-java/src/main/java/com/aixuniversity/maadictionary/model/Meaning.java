package com.aixuniversity.maadictionary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente un sens ou une définition d'un mot,
 * éventuellement dans une certaine langue.
 */
public class Meaning extends AbstractModel {
    private String definition;
    private Language language;
    private int vocabularyId;

    private List<Dialect> dialects;

    public Meaning() {
        this.definition = "";
        this.language = Language.getLanguage("en")!=null ? Language.getLanguage("en") : new Language("en", "English");
        this.dialects = new ArrayList<>();
    }

    public Meaning(String definition) {
        this.definition = definition;
        // English as default language
        this.language = Language.getLanguage("en")!=null ? Language.getLanguage("en") : new Language("en", "English");
        this.dialects = new ArrayList<>();
    }

    public Meaning(String definition, String languageCode) {
        this.definition = definition;
        this.language = Language.getLanguage(languageCode)!=null ? Language.getLanguage(languageCode) : new Language("en", "English");
        this.dialects = new ArrayList<>();
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(int vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Dialect> getDialects() {
        return dialects;
    }

    public void setDialects(List<Dialect> dialects) {
        this.dialects = dialects;
    }

    public void addDialect(Dialect dialect) {
        if (this.dialects == null) {
            this.dialects = new ArrayList<>();
        }
        if (this.dialects.contains(dialect)) return;
        this.dialects.add(dialect);
    }

    @Override
    public String toString() {
        return definition + /*this.language == null ? "" : "\t(" + language.getCode() + ")" +*/ "\n" + dialects.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Meaning meaning = (Meaning) o;
        return getVocabularyId() == meaning.getVocabularyId() && Objects.equals(getDefinition(), meaning.getDefinition()) && Objects.equals(getLanguage(), meaning.getLanguage()) && Objects.equals(dialects, meaning.dialects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDefinition(), getLanguage(), getVocabularyId(), dialects);
    }
}

