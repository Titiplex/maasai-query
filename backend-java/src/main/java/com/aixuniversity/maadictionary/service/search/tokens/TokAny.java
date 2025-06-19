package com.aixuniversity.maadictionary.service.search.tokens;

public record TokAny() implements Token {
    public byte sylIdx() {
        return -1;
    }
}
