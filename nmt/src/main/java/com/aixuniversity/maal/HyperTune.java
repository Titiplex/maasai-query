package com.aixuniversity.maal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.Map;
import java.util.Random;

/**
 * Very small random search for learning-rate & dropout.
 * Delegates to TrainLoRATransformer with different params.
 */
public class HyperTune {

    public static void main(String[] args) throws Exception {
        Options opt = new Options();
        opt.addOption(null, "src", true, "Train TSV");
        opt.addOption(null, "spm", true, "SentencePiece model");
        opt.addOption(null, "trials", true, "Number of trials");
        CommandLine cl = new DefaultParser().parse(opt, args);

        String src = cl.getOptionValue("src");
        String spm = cl.getOptionValue("spm");
        int trials = Integer.parseInt(cl.getOptionValue("trials", "5"));

        Random rnd = new Random(0);
        for (int t = 0; t < trials; t++) {
            float lr = (float) Math.pow(10, -4 - rnd.nextFloat() * 2); // 1e-4 .. 1e-6
            float dropout = 0.1f + rnd.nextFloat() * 0.2f; // 0.1 .. 0.3
            int batch = 32 + rnd.nextInt(3) * 16;

            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "target/maa-nmt.jar",
                    "com.stagemaasai.nmt.TrainLoRATransformer",
                    "--src", src, "--spm_model", spm,
                    "--epochs", "3", "--batch", String.valueOf(batch));
            Map<String, String> env = pb.environment();
            env.put("LR", String.valueOf(lr));
            env.put("DROPOUT", String.valueOf(dropout));
            pb.inheritIO();
            pb.start().waitFor();
        }
    }
}
