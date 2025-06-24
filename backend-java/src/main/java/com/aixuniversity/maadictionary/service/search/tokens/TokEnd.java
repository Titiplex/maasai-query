package com.aixuniversity.maadictionary.service.search.tokens;

public record TokEnd() implements Token {
    @Override
    public byte sylIdx() {
        return 0;
    }
}
