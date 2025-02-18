package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vocabulary extends AbstractModel {
    private String entry;
    private List<PartOfSpeech> partsOfSpeech;
    private List<Meaning> meanings;
    private List<Example> examples;

    public Vocabulary() {
        // Par défaut, on initialise la liste
        this.entry = "";
        this.meanings = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.partsOfSpeech = new ArrayList<>();
    }

    public Vocabulary(String word) {
        this.entry = word;
        this.meanings = new ArrayList<>();
        this.examples = new ArrayList<>();
    }

    public Vocabulary(String word, List<PartOfSpeech> partsOfSpeech, List<Meaning> meanings, List<Example> examples) {
        entry = word;
        this.partsOfSpeech = partsOfSpeech;
        this.meanings = meanings;
        this.examples = examples;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public List<PartOfSpeech> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(List<PartOfSpeech> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
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

    public void addExample(Example example) {
        if (this.examples == null) {
            this.examples = new ArrayList<>();
        }
        this.examples.add(example);
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
        StringBuilder string = new StringBuilder("Vocabulary {" +
                "\n\tmaaWord\t\t=\t" + this.entry +
                "\n\tmeanings\t=\t");
        for (Meaning meaning : this.meanings) {
            string.append(meaning.toString());
        }
        string.append("\n\texamples\t=\t");
        for (Example example : this.examples) {
            string.append(example.toString());
        }
        string.append("\n}");
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vocabulary that = (Vocabulary) o;
        return Objects.equals(entry, that.entry) && Objects.equals(partsOfSpeech, that.partsOfSpeech) && Objects.equals(meanings, that.meanings) && Objects.equals(examples, that.examples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, partsOfSpeech, meanings, examples);
    }
}

