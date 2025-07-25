package com.aixuniversity.maal;

import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.util.Pair;

import java.util.Set;

/**
 * Injecte LoRA sur les Linear ciblés (q/k/v/o-proj, fc1, fc2).
 * Implémentation compatible DJL 0.33.
 */
public final class BlockFactory {

    private static final Set<String> TARGET = Set.of(
            "q_proj", "k_proj", "v_proj",
            "o_proj", "out_proj", "fc1", "fc2"
    );

    /**
     * point d’entrée
     */
    public static Block addLoRA(Block root, int rank) {
        return transform(root, rank, "");
    }

    /* ------------------------------------------------------------------ */
    private static Block transform(Block block, int rank, String logicalName) {

        /* 1️⃣ Linear : on remplace si on est dans la liste */
        if (block instanceof Linear lin
                && TARGET.stream().anyMatch(logicalName::contains)) {

            long outF = lin.getParameters().get("weight").getArray().getShape().get(0);
            long inF = lin.getParameters().get("weight").getArray().getShape().get(1);

            LoRALinear lora = new LoRALinear((int) inF, (int) outF, rank, 32f);
            lora.getParameters().get("weight")
                    .setArray(lin.getParameters().get("weight").getArray());
            return lora;
        }

        /* 2️⃣ SequentialBlock : on duplique récursivement ses enfants */
        if (block instanceof SequentialBlock seq) {
            SequentialBlock copy = new SequentialBlock();
            for (Pair<String, Block> childPair : seq.getChildren()) {
                String name = childPair.getKey();
                Block child = childPair.getValue();
                copy.add(transform(child, rank, name));
            }
            return copy;
        }

        /* 3️⃣ Autres blocs : on ne les clone pas, mais on descend quand même
               pour modifier les Linear qu’ils contiennent dans les branches seq */
        return block;
    }

    private BlockFactory() {
    }
}
