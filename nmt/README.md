# Maa ↔ French NMT – Java Skeleton

This repository is a **minimal, end‑to‑end scaffold** for training and serving a neural machine translator for the Maa language in Java.

## What’s inside?

| Path                                          | Purpose                                                                                   |
|-----------------------------------------------|-------------------------------------------------------------------------------------------|
| `src/main/java/.../DataPreprocessor.java`     | Cleans the raw parallel corpus and splits into train/dev/test TSV files                   |
| `src/main/java/.../TrainLoRATransformer.java` | Fine‑tunes a Transformer with LoRA on top of a pretrained **NLLB‑200** checkpoint via DJL |
| `src/main/java/.../BackTranslation.java`      | Generates back‑translated synthetic pairs from French monolingual data                    |
| `pom.xml`                                     | Maven build file with DJL, SentencePiece and commons‑cli deps                             |
| `Dockerfile`                                  | Sample container that starts `djl-serving` with the resulting model                       |
| `README.md`                                   | You are here. Step‑by‑step instructions follow.                                           |

````mermaid
graph LR
    A[ExampleDAO] -->|Exporter| B(data/maa_en.tsv)
    B -->|Preprocessor| C(train/dev/test)
    C -->|SentencePiece| D[ParallelTsvDataset]
    D -->|LoRA fine-tune| E{NLLB-200 + LoRA}
    E --> F[export/maa-nmt-lora.params]
    F -->|Predictor| G(API / Back-Translation)
````

## Quick start

```bash
# 1. Install SentencePiece & Maven
sudo apt install sentencepiece maven

# 2. Train the tokenizer (16k BPE)
spm_train --input=data/maa_fr_train.txt --model_prefix=maa --vocab_size=16000 --character_coverage=1.0

# 3. Build & run training
mvn package
java -cp target/maa-nmt.jar com.stagemaasai.nmt.TrainLoRATransformer \
     --src data/maa_fr_train.tsv \
     --spm_model maa.model --epochs 5 --batch 64
```

*See the javadoc of each class for extended usage.*

Enjoy and feel free to tweak!


---
## Advanced features added

* **LoRA implementation from scratch** (`LoRALinear.java`) with rank & alpha control.
* **Automatic injection** into attention projections via `BlockFactory`.
* **BLEU/chrF evaluation wrapper** (`EvaluateBLEU.java`) – runs `sacrebleu`.
* **Hyper‑parameter random search** (`HyperTune.java`) – explores LR, dropout, batch.
* **Continuous training watcher** (`ContinuousTrainer.java`) – hot‑retrain on new data.
