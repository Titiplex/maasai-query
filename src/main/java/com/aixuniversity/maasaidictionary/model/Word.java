package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente un mot (par exemple un mot en Maa).
 */
public class Word extends AbstractModel {
    private String entryName;
    private List<PartOfSpeech> partsOfSpeech;

    public Word() {
        this.entryName = "";
        this.partsOfSpeech = new ArrayList<PartOfSpeech>();
    }

    public Word(String text) {
        this.entryName = text;
        this.partsOfSpeech = new ArrayList<PartOfSpeech>();
    }

    public Word(String text, List<PartOfSpeech> partsOfSpeech) {
        this.entryName = text;
        this.partsOfSpeech = partsOfSpeech;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public List<PartOfSpeech> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartOfSpeech(List<PartOfSpeech> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public void addPartOfSpeech(String partOfSpeech) {
        if (this.partsOfSpeech == null) {
            this.partsOfSpeech = new ArrayList<>();
        }
        PartOfSpeech p = new PartOfSpeech(partOfSpeech);
        if (partsOfSpeech.contains(p)) return;
        this.partsOfSpeech.add(p);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(this.entryName + " \n\tParts of speech: ");
        for (PartOfSpeech p : this.partsOfSpeech) {
            string.append("\n\t\t");
            string.append(p.toString());
        }
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(this.entryName, word.entryName) &&
                Objects.equals(this.partsOfSpeech, word.partsOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.entryName, this.partsOfSpeech);
    }
}

