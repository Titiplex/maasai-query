package com.aixuniversity.maadictionary.model;

import com.aixuniversity.maadictionary.dao.join.PosLinkedDao;
import com.aixuniversity.maadictionary.dao.join.VocabularyDialectDao;
import com.aixuniversity.maadictionary.dao.join.VocabularyLinkedDao;
import com.aixuniversity.maadictionary.dao.normal.ExampleDao;
import com.aixuniversity.maadictionary.dao.normal.MeaningDao;
import com.aixuniversity.maadictionary.parser.extractors.IPAExtractor;
import com.aixuniversity.maadictionary.parser.extractors.SyllableExtractor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Vocabulary extends AbstractModel {
    private static final Pattern DIALECT_PATTERN = Pattern.compile("\\[(.+?)]");
    private String ipa;
    private List<Syllable> syllables;

    private String syll_pattern;

    private int syll_count = 0;

    private String entry;
    // TODO passer les listes en Set ?
    private List<PartOfSpeech> partsOfSpeech;
    private List<Meaning> meanings;
    private List<Example> examples;
    private List<Vocabulary> linkedVocabularies;
    private List<Dialect> dialects;
    private int homonymIndex = 1;

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
        this.syllables = SyllableExtractor.extract(this.ipa);
        this.syll_pattern = patternize();
        this.syll_count = this.syllables.size();
    }

    public Vocabulary(String word, List<PartOfSpeech> partsOfSpeech, List<Meaning> meanings, List<Example> examples, List<Dialect> dialects) {
        this.partsOfSpeech = partsOfSpeech;
        this.meanings = meanings;
        this.examples = examples;
        this.linkedVocabularies = new ArrayList<>();
        this.dialects = dialects;

        this.entry = clean(word);

        this.ipa = IPAExtractor.parseIPA(this.entry);
        this.syllables = SyllableExtractor.extract(this.ipa);
        this.syll_pattern = patternize();
        this.syll_count = this.syllables.size();
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
        this.syll_pattern = patternize();
        this.syll_count = this.syllables.size();
    }

    public String getEntry() {
        if (this.entry == null || this.entry.isEmpty()) return null;
        return this.entry;
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

    public void setLinkedVocabularies(List<Vocabulary> linkedVocabularies) {
        this.linkedVocabularies = linkedVocabularies;
    }

    public int getHomonymIndex() {
        return homonymIndex;
    }

    public void setHomonymIndex(int homonymIndex) {
        this.homonymIndex = homonymIndex;
    }

    public List<Dialect> getDialects() {
        return dialects;
    }

    public void setDialects(List<Dialect> dialects) {
        this.dialects = dialects;
    }

    public String getIpa() {
        return this.ipa.isEmpty() ? this.ipa = IPAExtractor.parseIPA(getEntry()) : this.ipa;
    }

    public List<Syllable> getSyllables() {
        return syllables.isEmpty() ? syllables = SyllableExtractor.extract(this.getIpa()) : syllables;
    }

    public String getSyll_pattern() {
        return syll_pattern;
    }

    private String patternize() {
        return syllables.stream()
                .map(Syllable::getPattern)
                .collect(Collectors.joining("-"));
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

    public void addDialect(Collection<Dialect> dialects) {
        if (this.dialects == null) {
            this.dialects = new ArrayList<>();
        }
        for (Dialect dialect : dialects) {
            if (this.dialects.contains(dialect)) continue;
            this.dialects.add(dialect);
        }
    }

    public void addLinkedVocabulary(Vocabulary vocabulary) {
        if (this.linkedVocabularies == null) {
            this.linkedVocabularies = new ArrayList<>();
        }
        if (this.linkedVocabularies.contains(vocabulary)) return;
        this.linkedVocabularies.add(vocabulary);
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

    public void setSyll_pattern(String syll_pattern) {
        this.syll_pattern = syll_pattern;
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
        if (this.getId() == null) throw new RuntimeException("Cannot set null id to objects");

        for (Example example : examples) {
            example.setVocabularyId(this.getId());
        }
        for (Meaning meaning : meanings) {
            meaning.setVocabularyId(this.getId());
        }
    }

    public boolean fill() {
        Integer id = this.getId();
        if (id == null) return false;
        try {
            if (this.partsOfSpeech.isEmpty()) this.partsOfSpeech = new PosLinkedDao().getLinkedEntities(id);
            if (this.dialects.isEmpty()) this.dialects = new VocabularyDialectDao().getLinkedEntities(id);
            if (this.linkedVocabularies.isEmpty())
                this.linkedVocabularies = new VocabularyLinkedDao().getLinkedEntities(id);
            if (this.meanings.isEmpty()) this.meanings = new MeaningDao().getAllFromVocId(id);
            if (this.examples.isEmpty()) this.examples = new ExampleDao().getAllFromVocId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
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

    public int getSyll_count() {
        return syll_count;
    }

    public void setSyll_count(int syll_count) {
        this.syll_count = syll_count;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public void setSyllables(List<Syllable> syllables) {
        this.syllables = syllables;
    }
}

