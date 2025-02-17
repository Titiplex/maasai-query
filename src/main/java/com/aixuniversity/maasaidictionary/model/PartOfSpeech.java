package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.Objects;

public class PartOfSpeech {
    private String partOfSpeech;

    public PartOfSpeech() {
        this.partOfSpeech = "";
    }

    public PartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PartOfSpeech that = (PartOfSpeech) o;
        return Objects.equals(partOfSpeech, that.partOfSpeech);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(partOfSpeech);
    }

    @Override
    public String toString() {
        return "partOfSpeech='" + partOfSpeech + '\'';
    }
}
