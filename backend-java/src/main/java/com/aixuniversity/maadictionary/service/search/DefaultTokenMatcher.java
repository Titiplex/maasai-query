package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.tokens.Token;

public class DefaultTokenMatcher implements TokenMatcher {
    public boolean matches(Token t, Vocabulary v) {
        return HybridPattern.tokenOkStatic(t, v.getSyll_pattern().split("-"));
    }
}
