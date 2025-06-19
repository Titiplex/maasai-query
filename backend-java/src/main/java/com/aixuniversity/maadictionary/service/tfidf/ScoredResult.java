package com.aixuniversity.maadictionary.service.tfidf;

import com.aixuniversity.maadictionary.model.Vocabulary;

public record ScoredResult(Vocabulary vocab, double score) {
}