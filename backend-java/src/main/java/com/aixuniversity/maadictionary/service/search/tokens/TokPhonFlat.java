package main.java.com.aixuniversity.maasaidictionary.service.search.tokens;

import main.java.com.aixuniversity.maasaidictionary.service.search.Token;

public record TokPhonFlat(int phon) implements Token {
    public byte sylIdx() {
        return -1;
    }
}
