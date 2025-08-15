package com.aixuniversity.maadictionary.service.conversion;

public final class GraphemeMapping {
    public final String[] ipa;
    public float[] prob;

    public GraphemeMapping(String[] ipa, float[] prob) {
        this.ipa = ipa;
        this.prob = prob;
    }
}