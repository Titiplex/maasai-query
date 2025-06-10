package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class Phoneme extends AbstractModel {
    private static final Map<String, Phoneme> phonemes = new HashMap<>();
    private String code;
    private String ipa;

    public Phoneme(String code, String ipa) {
        super();
        this.code = code;
        this.ipa = ipa;
        addPhoneme(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public static Map<String, Phoneme> getPhonemeList() {
        return phonemes;
    }

    public static boolean addPhoneme(Phoneme phoneme) {
        if (!phonemes.containsKey(phoneme.getIpa())) {
            phonemes.put(phoneme.getIpa(), phoneme);
            return true;
        }
        return false;
    }

    public static Phoneme getPhoneme(String ipa) {
        if (!phonemes.containsKey(ipa)) return null;
        return phonemes.get(ipa);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Phoneme phoneme)) return false;
        return Objects.equals(getCode(), phoneme.getCode()) && Objects.equals(getIpa(), phoneme.getIpa());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getIpa());
    }
}
