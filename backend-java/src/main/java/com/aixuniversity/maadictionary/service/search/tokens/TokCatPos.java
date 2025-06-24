package com.aixuniversity.maadictionary.service.search.tokens;

public record TokCatPos(int cat, byte syl, byte pos) implements Token, PosToken {
    public byte sylIdx() {
        return syl;
    }
}
