package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.service.search.tokens.Token;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.sql.SQLException;

/**
 * Fournit l’ensemble d’IDs concernés par un token donné.
 */
public interface IndexLookup {
    IntSet idsFor(Token t) throws SQLException;
}

