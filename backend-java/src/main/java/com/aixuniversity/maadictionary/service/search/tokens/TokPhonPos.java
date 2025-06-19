package com.aixuniversity.maadictionary.service.search.tokens;

public record TokPhonPos(int phon, byte syl) implements Token {
    public byte sylIdx() {
        return syl;
    }
}
