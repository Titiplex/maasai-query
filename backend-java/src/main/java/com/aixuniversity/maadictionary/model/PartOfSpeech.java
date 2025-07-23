package com.aixuniversity.maadictionary.model;

import java.util.*;

public class PartOfSpeech extends AbstractModel {
    private String pos;

    private final static Map<String, PartOfSpeech> partOfSpeechList = new HashMap<>();

    public PartOfSpeech() {
        super();
        this.pos = "";
        addPartOfSpeech(this);
    }

    public PartOfSpeech(String pos) {
        super();
        this.pos = pos;
        addPartOfSpeech(this);
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public static void removePartOfSpeech(String partOfSpeech) {
        partOfSpeechList.remove(partOfSpeech);
    }

    public static void addPartOfSpeech(PartOfSpeech partOfSpeech) {
        if (!partOfSpeechList.containsKey(partOfSpeech.getPos())) {
            partOfSpeechList.put(partOfSpeech.getPos(), partOfSpeech);
        }
    }

    public static PartOfSpeech getPartOfSpeech(String partOfSpeech) {
        return partOfSpeechList.get(partOfSpeech);
    }

    public static Map<String, PartOfSpeech> getPartOfSpeechList() {
        return partOfSpeechList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PartOfSpeech that = (PartOfSpeech) o;
        return Objects.equals(pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pos);
    }

    @Override
    public String toString() {
        return "partOfSpeech='" + pos + '\'';
    }
}
