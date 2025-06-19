package com.aixuniversity.maadictionary.service.search.tokens;

public record TokPhonFlat(int phon) implements Token {
    public byte sylIdx() {
        return -1;
    }
}
