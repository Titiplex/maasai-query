package main.java.com.aixuniversity.maasaidictionary.service.search;

import main.java.com.aixuniversity.maasaidictionary.service.search.tokens.*;

sealed public interface Token permits TokCatPos, TokCatFlat, TokPhonPos, TokPhonFlat, TokAny, TokChoice {
    byte sylIdx();           // -1 si "any syllable"
}