package main.java.com.aixuniversity.maasaidictionary.service.search.tokens;

import main.java.com.aixuniversity.maasaidictionary.service.search.Token;

public record TokPhonPos(int phon, byte syl) implements Token {
    public byte sylIdx() {
        return syl;
    }
}
