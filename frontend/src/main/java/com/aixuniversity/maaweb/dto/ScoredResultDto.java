package com.aixuniversity.maaweb.dto;

import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

public record ScoredResultDto(
        long id,
        String form,
        String ipa,
        double score
) {
    public static ScoredResultDto from(ScoredResult sr) {
        var v = sr.vocab();
        return new ScoredResultDto(v.getId(), v.getEntry(), v.getIpa(), sr.score());
    }
}
