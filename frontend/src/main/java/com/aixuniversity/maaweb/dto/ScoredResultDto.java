// maasai-web/src/main/java/com/aixuniversity/maadictionary/web/dto/ScoredResultDto.java
package com.aixuniversity.maaweb.dto;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.tfidf.ScoredResult;

public record ScoredResultDto(
        Vocabulary vocab,
        double score
) {
    public static ScoredResultDto from(ScoredResult sr) {
        return new ScoredResultDto(
                sr.vocab(),
                sr.score());
    }
}
