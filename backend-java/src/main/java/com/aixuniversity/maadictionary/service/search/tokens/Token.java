package com.aixuniversity.maadictionary.service.search.tokens;

sealed public interface Token permits TokCatPos, TokCatFlat, TokPhonPos, TokPhonFlat, TokAny, TokChoice {
    byte sylIdx();           // -1 si "any syllable"
}