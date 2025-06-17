package main.java.com.aixuniversity.maasaidictionary.model;

import main.java.com.aixuniversity.maasaidictionary.config.IPAConfig;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.PhonemeDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Phoneme extends AbstractModel {
    private static final Map<String, Phoneme> phonemes = new HashMap<>();

    private long freq = 0;
    private String code;
    private String ipa;

    public Phoneme(String code, String ipa) {
        super();
        this.code = code;
        this.ipa = ipa;
        addPhoneme(this);
    }

    public static Phoneme getOrCreateSQL(String tok, PhonemeDao pDao) throws SQLException {
        if (tok == null || tok.isEmpty()) {
            return null;
        }

        Phoneme existing = getPhoneme(tok);
        if (existing != null) {
            return existing;
        }

        String letter = IPAConfig.getLetterFromIPA(tok);

        if (letter == null) {
            letter = tok;
        }

        synchronized (phonemes) {  // Thread safety
            // Double-check in case another thread created it
            existing = getPhoneme(tok);
            if (existing != null) {
                return existing;
            }

            Phoneme phon = new Phoneme(letter, tok);
            phon.setId(pDao.insert(phon));
            addPhoneme(phon);
            return phon;
        }
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

    public static void addPhoneme(Phoneme phoneme) {
        if (!phonemes.containsKey(phoneme.getIpa())) {
            phonemes.put(phoneme.getIpa(), phoneme);
        }
    }

    public static Phoneme getPhoneme(String ipa) {
        if (!phonemes.containsKey(ipa)) return null;
        return phonemes.get(ipa);
    }

    public long getFreq() {
        return 1 / this.freq;
    }

    public void addFreq() {
        addFreq(1);
    }

    public void addFreq(int nb) {
        this.freq += nb;
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
