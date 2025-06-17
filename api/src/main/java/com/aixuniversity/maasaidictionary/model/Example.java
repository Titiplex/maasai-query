package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.Objects;

public class Example extends AbstractModel {
    private String example;
    private String gloss;
    private Language glossLanguage;
    private int vocabularyId;

    private Dialect dialect;

    public Example() {
        this.example = "";
        this.gloss = "";
        this.glossLanguage = Language.getLanguage("en")!=null ? Language.getLanguage("en") : new Language("en", "English");
        this.dialect = new Dialect();
    }

    public Example(String example) {
        this.example = example;
        this.gloss = "";
        this.glossLanguage = Language.getLanguage("en")!=null ? Language.getLanguage("en") : new Language("en", "English");
        this.dialect = new Dialect();
    }

    public Example(String example, String gloss) {
        this.example = example;
        this.gloss = gloss;
        this.glossLanguage = Language.getLanguage("en")!=null ? Language.getLanguage("en") : new Language("en", "English");
        this.dialect = new Dialect();
    }

    public Example(String example, String gloss, String languageCode) {
        this.example = example;
        this.gloss = gloss;
        this.glossLanguage = Language.getLanguage(languageCode)!=null ? Language.getLanguage(languageCode) : new Language("en", "English");
        this.dialect = new Dialect();
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(int vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public Language getGlossLanguage() {
        return glossLanguage;
    }

    public void setGlossLanguage(Language glossLanguage) {
        this.glossLanguage = glossLanguage;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Example example1 = (Example) o;
        return vocabularyId == example1.vocabularyId && Objects.equals(example, example1.example) && Objects.equals(gloss, example1.gloss) && Objects.equals(glossLanguage, example1.glossLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(example, gloss, glossLanguage, vocabularyId);
    }

    @Override
    public String toString() {
        return "Example {" +
                "\n\texample='" + example + '\'' +
                "\n\tgloss='" + gloss + '\'' +
                "\n\tglossLanguage=" + glossLanguage +
                "\n\tdialect=" + dialect +
                //TODO les langues qui sont nulles
                //"\n\tglossLanguage='" + glossLanguage.getCode() + '\'' +
                "}\n";
    }
}
