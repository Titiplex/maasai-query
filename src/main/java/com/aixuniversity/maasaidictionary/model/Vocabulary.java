package main.java.com.aixuniversity.maasaidictionary.model;

import main.java.com.aixuniversity.maasaidictionary.parser.extractors.IPAExtractor;
import main.java.com.aixuniversity.maasaidictionary.parser.extractors.Syllable;
import main.java.com.aixuniversity.maasaidictionary.parser.extractors.SyllableExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Vocabulary extends AbstractModel {
    private String entry;
    // TODO passer les listes en Set ?
    private List<PartOfSpeech> partsOfSpeech;
    private List<Meaning> meanings;
    private List<Example> examples;
    private List<Vocabulary> linkedVocabularies;
    private List<Dialect> dialects;
    private final String ipa;
    private final List<Syllable> syllables;
    private int homonymIndex = 1;
    private static final Pattern DIALECT_PATTERN = Pattern.compile("\\[(.+?)]");

    public Vocabulary() {
        this.entry = "";
        this.meanings = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.partsOfSpeech = new ArrayList<>();
        this.linkedVocabularies = new ArrayList<>();
        this.dialects = new ArrayList<>();
        this.ipa = "";
        this.syllables = new ArrayList<>();
    }

    public Vocabulary(String word) {
        this.meanings = new ArrayList<>();
        this.examples = new ArrayList<>();
        this.partsOfSpeech = new ArrayList<>();
        this.linkedVocabularies = new ArrayList<>();
        this.dialects = new ArrayList<>();

        this.entry = clean(word);

        this.ipa = IPAExtractor.parseIPA(this.entry);
        this.syllables = SyllableExtractor.extractSyllablesAndPatterns(this.ipa);
    }

    public Vocabulary(String word, List<PartOfSpeech> partsOfSpeech, List<Meaning> meanings, List<Example> examples, List<Dialect> dialects) {
        this.partsOfSpeech = partsOfSpeech;
        this.meanings = meanings;
        this.examples = examples;
        this.linkedVocabularies = new ArrayList<>();
        this.dialects = dialects;

        this.entry = clean(word);

        this.ipa = IPAExtractor.parseIPA(this.entry);
        this.syllables = SyllableExtractor.extractSyllablesAndPatterns(this.ipa);
    }

    public Vocabulary(String word, List<PartOfSpeech> partsOfSpeech, List<Meaning> meanings, List<Example> examples, List<Dialect> dialects, List<Syllable> syllables, String ipa) {
        this.partsOfSpeech = partsOfSpeech;
        this.meanings = meanings;
        this.examples = examples;
        this.linkedVocabularies = new ArrayList<>();
        this.dialects = dialects;

        this.entry = clean(word);

        this.ipa = ipa;
        this.syllables = syllables;
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

    public List<Vocabulary> getLinkedVocabularies() {
        return linkedVocabularies;
    }

    public int getHomonymIndex() {
        return homonymIndex;
    }

    public void setHomonymIndex(int homonymIndex) {
        this.homonymIndex = homonymIndex;
    }

    public void setLinkedVocabularies(List<Vocabulary> linkedVocabularies) {
        this.linkedVocabularies = linkedVocabularies;
    }

    public List<Dialect> getDialects() {
        return dialects;
    }

    public void setDialects(List<Dialect> dialects) {
        this.dialects = dialects;
    }

    public String getIpa() {
        return ipa;
    }

    public List<Syllable> getSyllablesList() {
        return syllables;
    }

    public String getSyllables() {
        return syllables.stream()
                .map(Syllable::getPattern)
                .collect(Collectors.joining("|"));
    }

    /**
     * Méthode utilitaire pour ajouter un sens à la liste.
     */
    public void addMeaning(Meaning meaning) {
        if (this.meanings == null) {
            this.meanings = new ArrayList<>();
        }
        if (this.meanings.contains(meaning)) return;
        this.meanings.add(meaning);
    }

    public void addExample(Example example) {
        if (this.examples == null) {
            this.examples = new ArrayList<>();
        }
        if (this.examples.contains(example)) return;
        this.examples.add(example);
    }

    public void addPartOfSpeech(PartOfSpeech partOfSpeech) {
        if (this.partsOfSpeech == null) {
            this.partsOfSpeech = new ArrayList<>();
        }
        if (partsOfSpeech.contains(partOfSpeech)) return;
        this.partsOfSpeech.add(partOfSpeech);
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
        StringBuilder string = new StringBuilder("Vocabulary {" +
                "\n\tmaaWord\t\t=\t" + this.entry + "\n\tdialects\t\t=\t" + this.dialects.toString() +
                "\n\tpos\t=\t");
        for (PartOfSpeech partOfSpeech : this.partsOfSpeech) {
            string.append(partOfSpeech == null ? "" : partOfSpeech.toString());
        }
        string.append("\n\tipa=\t").append(this.ipa);
        string.append("\n\tsyllables\t=\t").append(this.syllables.toString());
        string.append("\n\tmeanings\t=\t");
        for (Meaning meaning : this.meanings) {
            string.append(meaning == null ? "" : meaning.toString());
        }
        string.append("\n\texamples\t=\t");
        for (Example example : this.examples) {
            string.append(example == null ? "" : example.toString());
        }
        string.append("\n\tlinkedVocabularies\t=\t");
        for (Vocabulary vocabulary : this.linkedVocabularies) {
            string.append(vocabulary == null ? "" : vocabulary.getEntry());
        }
        string.append("\n}");
        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vocabulary that)) return false;
        return getHomonymIndex() == that.getHomonymIndex() && Objects.equals(getEntry(), that.getEntry()) && Objects.equals(getIpa(), that.getIpa());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEntry(), getIpa(), getHomonymIndex());
    }

    public void setAllIds() {
        // TODO erreur si id == null

        for (Example example : examples) {
            example.setVocabularyId(this.getId());
        }
        for (Meaning meaning : meanings) {
            meaning.setVocabularyId(this.getId());
        }
    }

    /**
     * Nettoie le nom cru d'une entrée :
     * 1) Extrait toutes les occurences de [....] comme codes de dialecte
     * 2) Supprime ces occurences du nom
     * 3) Retire les espaces superflus en début/fin
     *
     * @param rawName Nom d'entrée tel qu'on le récupère (p. ex. "foo [North] [South]")
     * @return un objet CleanedEntry contenant baseName et liste de dialectes
     */
    private String clean(String rawName) {
        Matcher m = DIALECT_PATTERN.matcher(rawName);

        // Collecte de tous les dialectes
        while (m.find()) {
            String dialect = m.group(1).trim();
            addDialect(new Dialect(dialect));
        }

        // On enlève toutes les balises [..] du nom
        //TODO split , par ex
        return rawName.replaceAll("\\s*\\[.+?]\\s*", "")
                .trim();
    }
}

