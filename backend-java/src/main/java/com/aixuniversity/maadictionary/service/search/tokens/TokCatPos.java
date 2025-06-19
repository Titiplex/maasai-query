package com.aixuniversity.maadictionary.service.search.tokens;

public record TokCatPos(int cat, byte syl, byte pos) implements Token {
    public byte sylIdx() {
        return syl;
    }
}
