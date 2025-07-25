package com.aixuniversity.maal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Evaluate BLEU using sacrebleu (Python) – assumes sacrebleu installed.
 * Usage:
 * java -cp target/maa-nmt.jar com.stagemaasai.nmt.EvaluateBLEU --ref ref.txt --hyp hyp.txt
 */
public class EvaluateBLEU {

    public static void main(String[] args) throws Exception {
        Options opt = new Options();
        opt.addOption(null, "ref", true, "Reference file");
        opt.addOption(null, "hyp", true, "Hypothesis file");
        CommandLine cl = new DefaultParser().parse(opt, args);

        Path ref = Paths.get(cl.getOptionValue("ref"));
        Path hyp = Paths.get(cl.getOptionValue("hyp"));

        ProcessBuilder pb = new ProcessBuilder("python3", "-m", "sacrebleu",
                ref.toAbsolutePath().toString(), "-i", hyp.toAbsolutePath().toString(), "-m", "bleu", "-w", "2");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            br.lines().forEach(System.out::println);
        }
    }
}
