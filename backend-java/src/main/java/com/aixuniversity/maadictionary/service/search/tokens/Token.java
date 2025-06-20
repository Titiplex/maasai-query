package com.aixuniversity.maadictionary.service.search.tokens;

sealed public interface Token permits TokAny, TokCatFlat, TokCatPos, TokChoice, TokImpossible, TokPhonFlat, TokPhonPos {
    byte sylIdx();           // -1 si "any syllable"
}