package com.aixuniversity.maadictionary.service.search.tokens;

sealed public interface Token permits PosToken, TokAny, TokCatFlat, TokCatPos, TokChoice, TokEnd, TokImpossible, TokPhonFlat, TokPhonPos, TokRepeat, TokStart {
    byte sylIdx();           // -1 si "any syllable"
}