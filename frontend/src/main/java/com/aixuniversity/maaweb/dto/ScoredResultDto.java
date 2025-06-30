package com.aixuniversity.maaweb.dto;

import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

public record ScoredResultDto(
        long id,
        String form,
        String glossPreview,  // premier gloss de la liste
        double score
) {
    public static ScoredResultDto from(ScoredResult sr) {
        var v = sr.vocab();
        String preview = v.getMeanings().isEmpty() ? "" : v.getMeanings().getFirst().getDefinition();
        return new ScoredResultDto(v.getId(), v.getEntry(), preview, sr.score());
    }
}
