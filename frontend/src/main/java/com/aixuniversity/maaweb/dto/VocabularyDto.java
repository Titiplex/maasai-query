package com.aixuniversity.maaweb.dto;

import com.aixuniversity.maadictionary.model.Example;
import com.aixuniversity.maadictionary.model.Meaning;
import com.aixuniversity.maadictionary.model.Vocabulary;

import java.util.List;

public record VocabularyDto(
        long id,
        String form,
        String ipa,
        List<Meaning> meanings,
        List<Example> examples,
        List<Vocabulary> linkedVocabs
) {
    public static VocabularyDto from(Vocabulary v) {
        if (!v.fill()) throw new RuntimeException("Impossible to fill vocabulary of id " + v.getId());
        return new VocabularyDto(
                v.getId(),
                v.getEntry(),
                v.getIpa(),
                v.getMeanings(),
                v.getExamples(),
                v.getLinkedVocabularies()
        );
    }
}
