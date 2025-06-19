package main.java.com.aixuniversity.maasaidictionary.service.tfidf;

import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

public record ScoredResult(Vocabulary vocab, double score) {
}