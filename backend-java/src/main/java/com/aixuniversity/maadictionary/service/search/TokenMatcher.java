package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.tokens.Token;

import java.sql.SQLException;

/**
 * Vérifie si un vocabulaire remplit un token (position, choix, etc.).
 */
public interface TokenMatcher {
    boolean matches(Token t, Vocabulary v) throws SQLException;
}
