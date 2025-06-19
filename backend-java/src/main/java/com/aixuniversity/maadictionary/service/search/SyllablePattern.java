package com.aixuniversity.maadictionary.service.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record SyllablePattern(List<List<List<String>>> syllables) {
    /**
     * BA/OM|LI-VO  ⇒  [[[BA,OM],[LI]], [[VO]]]
     */
    public static SyllablePattern parse(String raw) {
        List<List<List<String>>> out = new ArrayList<>();
        for (String syllable : raw.split("-")) {
            List<List<String>> phonemes = parseUniqueSyllable(syllable);
            out.add(phonemes);
        }
        return new SyllablePattern(out);
    }

    public static List<List<String>> parseUniqueSyllable(String raw) {

        List<List<String>> phonemes = new ArrayList<>();
        for (String phoneme : raw.split("\\|")) {
            phonemes.add(List.of(phoneme.split("/")));
        }

        return phonemes;
    }

    /**
     * Renvoie l’ensemble (sans doublon) des étiquettes utilisées
     */
    public Set<String> labels() {
        return syllables.stream()
                .flatMap(List::stream)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }
}
