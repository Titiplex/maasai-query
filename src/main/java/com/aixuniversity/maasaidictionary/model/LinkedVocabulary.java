package main.java.com.aixuniversity.maasaidictionary.model;

public class LinkedVocabulary extends AbstractModel{
    private int vocabularyId;
    private int linkedVocabularyId;

    public LinkedVocabulary() {
    }

    public LinkedVocabulary(int vocabId, int linkedVocabId) {
        this.vocabularyId = vocabId;
        this.linkedVocabularyId = linkedVocabId;
    }

    public int getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(int vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public int getLinkedVocabularyId() {
        return linkedVocabularyId;
    }

    public void setLinkedVocabularyId(int linkedVocabularyId) {
        this.linkedVocabularyId = linkedVocabularyId;
    }
}