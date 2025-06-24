package com.aixuniversity.maadictionary.service.search.tokens;

public record TokRepeat(Token base, int min, int max) implements Token {
    @Override
    public byte sylIdx() {
        return 0;
    }
    // max == Integer.MAX_VALUE pour *
}