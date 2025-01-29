package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.Objects;

/**
 * Représente un mot (par exemple un mot en Maa).
 */
public class Word {
    private String text;         // Le mot lui-même (ex: "enkima")
    private String partOfSpeech; // Optionnel, catégorie grammaticale (ex: "n." pour nom)

    public Word() {
    }

    public Word(String text) {
        this.text = text;
    }

    public Word(String text, String partOfSpeech) {
        this.text = text;
        this.partOfSpeech = partOfSpeech;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    @Override
    public String toString() {
        return "Word{" +
                "text='" + text + '\'' +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(text, word.text) &&
                Objects.equals(partOfSpeech, word.partOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, partOfSpeech);
    }
}

