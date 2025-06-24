package com.aixuniversity.maadictionary.service.search.tokens;

public record TokStart() implements Token {
    @Override
    public byte sylIdx() {
        return 0;
    }
}