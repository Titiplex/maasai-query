package com.aixuniversity.maadictionary.service.textitometric;

import com.aixuniversity.maadictionary.model.Vocabulary;

public interface Metrics {
    /**
     * entropie de Shannon de la distribution des catégories dans un mot
     */
    static double entropy(Vocabulary v) {
        var counts = new java.util.HashMap<String, Integer>();
        for (String s : v.getSyll_pattern().split("[| -]"))
            for (String c : s.split("/"))
                counts.merge(c, 1, Integer::sum);
        double n = counts.values().stream().mapToInt(i -> i).sum();
        return counts.values().stream()
                .mapToDouble(cnt -> cnt / n * Math.log(n / cnt))
                .sum();
    }

    /**
     * ratio voyelle/consonne
     */
    static double vowelRatio(Vocabulary v) {
        int vCnt = 0, total = 0;
        for (String s : v.getSyll_pattern().split("[| -]")) {
            total++;
            if (s.contains("V")) vCnt++;
        }
        return (double) vCnt / Math.max(1, total);
    }
}
