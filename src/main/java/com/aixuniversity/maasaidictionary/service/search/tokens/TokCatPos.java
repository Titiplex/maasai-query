package main.java.com.aixuniversity.maasaidictionary.service.search.tokens;

import main.java.com.aixuniversity.maasaidictionary.service.search.Token;

public record TokCatPos(int cat, byte syl, byte pos) implements Token {
    public byte sylIdx() {
        return syl;
    }
}
