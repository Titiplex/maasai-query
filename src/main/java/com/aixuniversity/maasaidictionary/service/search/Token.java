package main.java.com.aixuniversity.maasaidictionary.service.search;

import java.util.List;

sealed public interface Token permits TokCatPos, TokCatFlat, TokPhonPos, TokPhonFlat, TokAny, TokChoice {
    byte sylIdx();           // -1 si "any syllable"
}

public record TokCatPos(int cat, byte syl, byte pos) implements Token {
    public byte sylIdx() {
        return syl;
    }
}

public record TokCatFlat(int cat) implements Token {
    public byte sylIdx() {
        return -1;
    }
}

public record TokPhonPos(int phon, byte syl) implements Token {
    public byte sylIdx() {
        return syl;
    }
}

public record TokPhonFlat(int phon) implements Token {
    public byte sylIdx() {
        return -1;
    }
}

public record TokAny() implements Token {
    public byte sylIdx() {
        return -1;
    }
}

/**
 * Choice : liste OU joker
 */
public record TokChoice(List<Token> options, byte syl/*may=-1*/, Byte pos/*null if unknown*/)
        implements Token {
    public byte sylIdx() {
        return syl;
    }
}