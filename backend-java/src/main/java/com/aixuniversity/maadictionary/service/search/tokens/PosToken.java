package com.aixuniversity.maadictionary.service.search.tokens;

/**
 * Marque les tokens qui imposent une syllabe précise.
 * (ne contient qu’un getter, déjà présent dans les records)
 */
public sealed interface PosToken extends Token permits TokCatPos, TokPhonPos {
    /** index de la syllabe (0-based) où le token doit se trouver */
    byte syl();
}
