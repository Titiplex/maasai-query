package com.aixuniversity.maal;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.sentencepiece.jni.SentencePieceLibrary;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Produce back‑translated corpus from monolingual French text.
 */
public class BackTranslation {

    public static void main(String[] args) throws Exception {
        Options opt = new Options();
        opt.addOption(null, "model_dir", true, "Path to exported Maa→Fr model");
        opt.addOption(null, "mono", true, "French monolingual file, one sentence per line");
        opt.addOption(null, "output", true, "Output TSV for synthetic pairs");
        CommandLine cl = new DefaultParser().parse(opt, args);

        Path modelDir = Paths.get(cl.getOptionValue("model_dir"));
        Path mono = Paths.get(cl.getOptionValue("mono"));
        Path out = Paths.get(cl.getOptionValue("output", "backtranslated.tsv"));

        Criteria<String, String> criteria = Criteria.builder()
                .setTypes(String.class, String.class)
                .optModelPath(modelDir)
                .optEngine("PyTorch")
                .optTranslator(new MyTranslator())
                .build();

        try (ZooModel<String, String> m = criteria.loadModel();
             Predictor<String, String> pred = m.newPredictor()) {

            try (Stream<String> lines = Files.lines(mono);
                 BufferedWriter bw = Files.newBufferedWriter(out)) {

                lines.forEachOrdered(fr -> {
                    try {
                        String maa = pred.predict(fr);
                        bw.write(maa + "\t" + fr);
                        bw.newLine();
                    } catch (Exception ex) {
                        System.err.println("Failed to translate " + fr + " : " + ex.getMessage());
                    }
                });
            }
        }
    }

    // Minimal translator assuming SentencePiece model in resources
    static class MyTranslator implements Translator<String, String> {
        private long tok;
        private final SentencePieceLibrary spLib = SentencePieceLibrary.LIB;

        public MyTranslator() {
            try {
                tok = spLib.createSentencePieceProcessor();
                spLib.loadModel(tok, "maa.model");
            } catch (Exception e) {
                System.err.println("Failed to load SentencePiece model : " + e.getMessage());
            }
        }

        public NDList processInput(TranslatorContext ctx, String input) {
            return new NDList(ctx.getNDManager().create(spLib.encode(tok, input)));
        }

        public String processOutput(TranslatorContext ctx, NDList list) {
            return spLib.decode(tok, list.singletonOrThrow().toIntArray());
        }
    }
}
