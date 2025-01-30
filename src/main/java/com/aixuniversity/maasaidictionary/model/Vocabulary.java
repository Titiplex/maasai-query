package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Regroupe un mot Maa (Word) et la liste de ses sens (Meanings).
 */
public class Vocabulary {
    private Word maaWord;
    private List<Meaning> meanings;

    public Vocabulary() {
        // Par défaut, on initialise la liste
        this.meanings = new ArrayList<>();
        this.maaWord = new Word();
    }

    public Word getMaaWord() {
        return maaWord;
    }

    public void setMaaWord(Word maaWord) {
        this.maaWord = maaWord;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    /**
     * Méthode utilitaire pour ajouter un sens à la liste.
     */
    public void addMeaning(Meaning meaning) {
        if (this.meanings == null) {
            this.meanings = new ArrayList<>();
        }
        this.meanings.add(meaning);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Vocabulary {" +
                "\n\tmaaWord\t=\t" + this.maaWord.toString() +
                "\n\tmeanings\t=\t");
        for (Meaning meaning : this.meanings) {
            string.append(meaning.toString());
        }
        string.append("\n}");
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vocabulary that)) return false;
        return Objects.equals(this.maaWord, that.maaWord) &&
                Objects.equals(this.meanings, that.meanings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.maaWord, this.meanings);
    }
}

