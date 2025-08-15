package com.aixuniversity.maadictionary.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GraphemeMap extends AbstractModel {

    private String grapheme;
    private String orthography;

    private List<String> ipaOptions;

    private float[] likelihood;

    public String getGrapheme() {
        return grapheme;
    }

    public void setGrapheme(String g) {
        this.grapheme = g;
    }

    public List<String> getIpaOptions() {
        return ipaOptions;
    }

    public void setIpaOptions(List<String> opts) {
        this.ipaOptions = opts;
    }

    public float[] getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(float[] l) {
        this.likelihood = l;
    }

    public String getOrthography() {
        return orthography;
    }

    public void setOrthography(String orthography) {
        this.orthography = orthography;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphemeMap that)) return false;
        return Objects.equals(getGrapheme(), that.getGrapheme()) && Objects.equals(getOrthography(), that.getOrthography()) && Objects.equals(getIpaOptions(), that.getIpaOptions()) && Objects.deepEquals(getLikelihood(), that.getLikelihood());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGrapheme(), getOrthography(), getIpaOptions(), Arrays.hashCode(getLikelihood()));
    }
}
