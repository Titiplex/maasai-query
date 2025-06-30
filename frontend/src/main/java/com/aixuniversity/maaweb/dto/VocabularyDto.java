package com.aixuniversity.maaweb.dto;

import com.aixuniversity.maadictionary.model.Example;
import com.aixuniversity.maadictionary.model.Meaning;
import com.aixuniversity.maadictionary.model.Vocabulary;

import java.util.List;

public record VocabularyDto(
        long id,
        String form,
        List<Meaning> meanings,
        List<Example> examples
) {
    public static VocabularyDto from(Vocabulary v) {
        return new VocabularyDto(
                v.getId(),
                v.getEntry(),
                v.getMeanings(),
                v.getExamples()
        );
    }
}
