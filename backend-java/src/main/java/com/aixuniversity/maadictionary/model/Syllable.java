package com.aixuniversity.maadictionary.model;

import java.util.List;
import com.aixuniversity.maadictionary.parser.extractors.SyllableExtractor;

public class Syllable {
    private String text;
    private String pattern;

    public Syllable(String text, String pattern) {
        this.text = text;
        this.pattern = pattern;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPattern() {
        return pattern;
    }

    @SuppressWarnings("unused")
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return "Syllable: " + text + " | SyllablePattern: " + pattern;
    }

    /**
     * Décompose cette syllabe en tokens phonémiques,
     * en réutilisant le tokenizeIPAWord de SyllableExtractor.
     */
    public List<String> getTokens() {
        // Tokenise le texte de la syllabe
        return SyllableExtractor.tokenizeIPAWord(this.text);
    }
}
