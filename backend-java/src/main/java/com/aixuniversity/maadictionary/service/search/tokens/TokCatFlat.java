package com.aixuniversity.maadictionary.service.search.tokens;

public record TokCatFlat(int cat) implements Token {
    public byte sylIdx() {
        return -1;
    }
}
