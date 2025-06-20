package com.aixuniversity.maadictionary.service.search.tokens;

/**
 * Returned when the user typed something that is not stored in the DB.
 * The token never matches, so the whole pattern is unsatisfiable.
 */
public final class TokImpossible implements Token {

    @Override
    public byte sylIdx() {
        return -1;
    }
}