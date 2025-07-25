package com.aixuniversity.maal;

import ai.djl.Model;
import ai.djl.basicdataset.utils.TextData;
import ai.djl.engine.Engine;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.sentencepiece.SpTokenizer;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.AdamW;
import ai.djl.training.tracker.Tracker;
import com.aixuniversity.maal.utils.ParallelTsvDataset;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Fine-tune le checkpoint NLLB-200 (ou autre) avec LoRA
 * en utilisant l'API DJL 0.33 (TextDataset refactor).
 * <p>
 * Nécessite :
 * - SpTokenizer (ai.djl.sentencepiece)
 * - ParallelTsvDataset (src<TAB>tgt par ligne)
 * - GPU PyTorch backend installé
 */
public final class TrainLoRATransformer {

    public static void main(String[] args) throws Exception {

        /* ---------- 0. CLI ---------- */
        Options opt = new Options();
        opt.addOption(null, "src", true, "Corpus TSV : source<TAB>cible");
        opt.addOption(null, "spm_model", true, "Modèle SentencePiece (*.model)");
        opt.addOption(null, "epochs", true, "Nombre d’époques (def. 5)");
        opt.addOption(null, "batch", true, "Taille de batch (def. 32)");
        CommandLine cl = new DefaultParser().parse(opt, args);

        Path corpus = Paths.get(cl.getOptionValue("src"));
        String spm = cl.getOptionValue("spm_model");
        int epochs = Integer.parseInt(cl.getOptionValue("epochs", "5"));
        int batchSize = Integer.parseInt(cl.getOptionValue("batch", "32"));

        /* ---------- 1. Dataset ---------- */
        // a) Configuration SentencePiece partagée (src & tgt)
        SpTokenizer spTokenizer = new SpTokenizer(Paths.get(spm));

        TextData.Configuration srcCfg = new TextData.Configuration()
                .setTextProcessors(List.of(spTokenizer))
                .setEmbeddingSize(512);                // Embedding/source
        TextData.Configuration tgtCfg = new TextData.Configuration()
                .setTextProcessors(List.of(spTokenizer))
                .setEmbeddingSize(512);                // Embedding/target

        // b) Dataset parallèle maison (fichier TSV local)
        ParallelTsvDataset dataset = ParallelTsvDataset.builder()
                .setFile(corpus)
                .setSourceConfiguration(srcCfg)
                .setTargetConfiguration(tgtCfg)
                .setSampling(batchSize, true)          // shuffle + batch
                .build();

        dataset.prepare();                             // pré-traitement

        RandomAccessDataset[] splits = dataset.randomSplit(8, 1, 1); // 80/10/10

        /* ---------- 2. Checkpoint pré-entraîné ---------- */
        Criteria<NDList, NDList> pretrain = Criteria.builder()
                .setTypes(NDList.class, NDList.class)
                .optEngine("PyTorch")
                .optModelUrls(
                        "djl://ai.djl.huggingface.pytorch/facebook/nllb-200-distilled-600M")
                .build();

        try (ZooModel<NDList, NDList> pretrained = pretrain.loadModel();
             Model model = Model.newInstance("maa-lora", "PyTorch")) {

            // ---------- 3. Injection LoRA ----------
            model.setBlock(BlockFactory.addLoRA(pretrained.getBlock(), 16));

            // ---------- 4. Config d’entraînement ----------
            // ... Config d’entraînement
            DefaultTrainingConfig cfg = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                    .optOptimizer(AdamW.builder().optLearningRateTracker(Tracker.fixed(5e-5f)).build())
                    .optDevices(Engine.getInstance().getDevices(1))      // ← 1 GPU (ou tous sans arg)
                    .addTrainingListeners(TrainingListener.Defaults.logging());


            /* ---------- 5. Boucle d’entraînement ---------- */
            try (Trainer trainer = model.newTrainer(cfg)) {
                trainer.setMetrics(new Metrics());
                trainer.initialize(new Shape(1, 1));              // shape fictive

                for (int e = 0; e < epochs; e++) {
                    System.out.printf("Époque %d/%d%n", e + 1, epochs);
                    EasyTrain.fit(trainer, e, splits[0], splits[1]);
                }
                /* ---------- 6. Sauvegarde ----------
                   Les poids LoRA seuls pèsent peu (rank*alpha).       */
                model.save(Paths.get("export"), "maa-nmt-lora");
                System.out.println("Modèle exporté → export/");
            }
        }
    }
}
