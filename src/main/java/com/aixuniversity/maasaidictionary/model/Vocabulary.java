package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vocabulary {
    private Word maaWord;
    private List<Meaning> meanings;
    private List<Example> examples;

    public Vocabulary() {
        // Par défaut, on initialise la liste
        this.meanings = new ArrayList<>();
        this.maaWord = new Word();
        this.examples = new ArrayList<>();
    }

    public Vocabulary(String word) {
        this.maaWord = new Word(word);
        this.meanings = new ArrayList<>();
        this.examples = new ArrayList<>();
    }

    public Vocabulary(String word, List<PartOfSpeech> partsOfSpeech) {
        this.maaWord = new Word(word, partsOfSpeech);
        this.meanings = new ArrayList<>();
        this.examples = new ArrayList<>();
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

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Vocabulary {" +
                "\n\tmaaWord\t\t=\t" + this.maaWord.toString() +
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
        return Objects.equals(maaWord, that.maaWord) && Objects.equals(meanings, that.meanings) && Objects.equals(examples, that.examples);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maaWord, meanings, examples);
    }
}

