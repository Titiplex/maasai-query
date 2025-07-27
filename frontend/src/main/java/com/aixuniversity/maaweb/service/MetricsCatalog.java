package com.aixuniversity.maaweb.service;

import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.textitometric.Metrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsCatalog {
    Vocabulary v;
    public MetricsCatalog(Vocabulary v) {
        this.v = v;
    }
    public double entropy() {
        return Metrics.entropy(v);
    }

    public double vowelRatio() {
        return Metrics.vowelRatio(v);
    }
}
