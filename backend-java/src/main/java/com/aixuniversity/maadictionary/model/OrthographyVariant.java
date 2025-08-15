package com.aixuniversity.maadictionary.model;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Objects;

public class OrthographyVariant extends AbstractModel {

    private static final HashMap<Vocabulary, HashMap<String, OrthographyVariant>> variants = new HashMap<>();
    private Vocabulary vocabulary;

    private String form;
    private String script = "payne";
    private boolean isPrimary = false;
    private float ambiguityScore = 0f;

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    private String ipaCache;

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary v) {
        this.vocabulary = v;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public float getAmbiguityScore() {
        return ambiguityScore;
    }

    public void setAmbiguityScore(float val) {
        this.ambiguityScore = val;
    }

    public String getIpaCache() {
        return ipaCache;
    }

    public void setIpaCache(String ipaCache) {
        this.ipaCache = ipaCache;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrthographyVariant that)) return false;
        return isPrimary() == that.isPrimary() && Float.compare(getAmbiguityScore(), that.getAmbiguityScore()) == 0 && Objects.equals(getVocabulary(), that.getVocabulary()) && Objects.equals(getForm(), that.getForm()) && Objects.equals(getScript(), that.getScript()) && Objects.equals(getIpaCache(), that.getIpaCache()) && Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVocabulary(), getForm(), getScript(), isPrimary(), getAmbiguityScore(), getIpaCache(), getCreatedAt());
    }

    public static HashMap<Vocabulary, HashMap<String, OrthographyVariant>> getVariants() {
        return variants;
    }

    public static void addVariant(OrthographyVariant variant) {
        if (!variants.containsKey(variant.getVocabulary())) {
            HashMap<String, OrthographyVariant> newHash = new HashMap<>();
            newHash.put(variant.getScript(), variant);
            variants.put(variant.getVocabulary(), newHash);
        } else {
            if (!variants.get(variant.getVocabulary()).containsKey(variant.getScript())) {
                variants.get(variant.getVocabulary()).put(variant.getScript(), variant);
            }
        }
    }

    public static HashMap<String, OrthographyVariant> getVariantsByVocabulary(Vocabulary vocabulary) {
        return variants.get(vocabulary);
    }

    public static HashMap<String, OrthographyVariant> getVariantsByVocabulary(int id) {
        try {
            return variants.get(new VocabularyDao().searchById(id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public OrthographyVariant(Vocabulary vocabulary, String form, String script) {
        this.vocabulary = vocabulary;
        this.form = form;
        this.script = script;
        addVariant(this);
    }

    public OrthographyVariant() {
    }
}
