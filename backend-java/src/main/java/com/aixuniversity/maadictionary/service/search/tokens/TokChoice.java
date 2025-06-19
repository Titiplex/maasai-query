package com.aixuniversity.maadictionary.service.search.tokens;

import java.util.List;

/**
 * Choice : liste OU joker
 */
public record TokChoice(List<Token> options, byte syl/*may=-1*/, Byte pos/*null if unknown*/)
        implements Token {

    public byte sylIdx() {
        return syl;
    }
}
