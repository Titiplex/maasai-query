package com.aixuniversity.maadictionary.service.search.tokens;

public record TokPhonPos(int phon, byte syl) implements Token, PosToken {
    public byte sylIdx() {
        return syl;
    }
}
