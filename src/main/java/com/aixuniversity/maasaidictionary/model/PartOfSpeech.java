package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.*;

public class PartOfSpeech extends AbstractModel {
    private String partOfSpeech;

    private final static Map<String, PartOfSpeech> partOfSpeechList = new HashMap<>();

    public PartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
        addPartOfSpeechList(this);
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public static void removePartOfSpeech(String partOfSpeech) {
        partOfSpeechList.remove(partOfSpeech);
    }

    public static void addPartOfSpeechList(PartOfSpeech partOfSpeech) {
        if (!partOfSpeechList.containsKey(partOfSpeech.getPartOfSpeech())) {
            partOfSpeechList.put(partOfSpeech.getPartOfSpeech(), partOfSpeech);
        }
    }

    public static PartOfSpeech getPartOfSpeech(String partOfSpeech) {
        return partOfSpeechList.get(partOfSpeech);
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
