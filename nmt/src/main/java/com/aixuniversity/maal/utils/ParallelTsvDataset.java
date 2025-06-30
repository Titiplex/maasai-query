package com.aixuniversity.maal.utils;

import ai.djl.Application;
import ai.djl.basicdataset.nlp.TextDataset;
import ai.djl.basicdataset.utils.TextData;
import ai.djl.modality.nlp.embedding.EmbeddingException;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.MRL;
import ai.djl.repository.Repository;
import ai.djl.training.dataset.Record;
import ai.djl.util.Progress;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Dataset parallèle TSV (src <TAB> tgt) – DJL 0.33
 */
public class ParallelTsvDataset extends TextDataset {

    /* ------------------------------------------------------------------ */
    private final Path tsvFile;
    private final TextData srcData;
    private final TextData tgtData;

    protected ParallelTsvDataset(Builder b) {
        super(b);
        tsvFile = b.tsvFile;
        srcData = new TextData(b.srcCfg);
        tgtData = new TextData(b.tgtCfg);
    }

    /* -------------------------- Pré-traitement ------------------------ */
    @Override
    public void prepare(Progress p) throws IOException, EmbeddingException {
        if (prepared) return;

        List<String> src = new ArrayList<>(), tgt = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(tsvFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\t", 2);
                if (parts.length == 2) {
                    src.add(parts[0]);
                    tgt.add(parts[1]);
                }
            }
        }
        preprocess(src, true);      // côté source
        preprocess(tgt, false);     // côté cible
        prepared = true;
    }

    /* ----------------------------- Accès ----------------------------- */
    @Override      // **public** pour matcher RandomAccessDataset
    public Record get(NDManager mgr, long idx) {
        return new Record(
                new NDList(srcData.getEmbedding(mgr, idx)),
                new NDList(tgtData.getEmbedding(mgr, idx)));
    }

    @Override
    protected long availableSize() {
        return srcData.getSize();
    }

    /* ----------------------------- Builder --------------------------- */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends TextDataset.Builder<Builder> {
        Path tsvFile;
        TextData.Configuration srcCfg, tgtCfg;

        public Builder setFile(Path p) {
            tsvFile = p;
            return this;
        }

        public Builder setSourceConfiguration(TextData.Configuration c) {
            srcCfg = c;
            return this;
        }

        public Builder setTargetConfiguration(TextData.Configuration c) {
            tgtCfg = c;
            return this;
        }

        /**
         * Construit le dataset (pas d’annotation @Override → plus d’erreur)
         */
        public ParallelTsvDataset build() {
            if (tsvFile == null || srcCfg == null || tgtCfg == null)
                throw new IllegalStateException("file/srcCfg/tgtCfg manquants");
            return new ParallelTsvDataset(this);
        }

        /* MRL local obligatoire mais jamais téléchargé */
        private MRL getMrl() {
            return Repository.newInstance("local", "http://localhost")
                    .dataset(Application.NLP.ANY, "local", "parallel-tsv", "1.0");
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}
