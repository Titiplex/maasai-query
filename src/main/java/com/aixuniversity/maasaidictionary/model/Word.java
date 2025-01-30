package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.Objects;

/**
 * Représente un mot (par exemple un mot en Maa).
 */
public class Word {
    private String entryName;         // Le mot lui-même (ex : "enkima")
    private String partOfSpeech; // Optionnel, catégorie grammaticale (ex : "n." pour nom)

    public Word() {
        this.entryName = "";
        this.partOfSpeech = "";
    }

    public Word(String text) {
        this.entryName = text;
        this.partOfSpeech = "";
    }

    public Word(String text, String partOfSpeech) {
        this.entryName = text;
        this.partOfSpeech = partOfSpeech;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    @Override
    public String toString() {
        return this.entryName + "(" + this.partOfSpeech + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(this.entryName, word.entryName) &&
                Objects.equals(this.partOfSpeech, word.partOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.entryName, this.partOfSpeech);
    }
}

