package com.aixuniversity.maal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Quick & dirty corpus cleaner + train/dev/test splitter.
 * <p>
 * Usage:
 * java -cp target/maa-nmt.jar com.stagemaasai.nmt.DataPreprocessor \
 * --input raw.tsv --train 0.8 --dev 0.1 --test 0.1
 */
public class DataPreprocessor {

    public static void main(String[] args) throws Exception {
        Options opt = new Options();
        opt.addOption("i", "input", true, "Raw TSV with src<TAB>tgt");
        opt.addOption(null, "train", true, "Train ratio");
        opt.addOption(null, "dev", true, "Dev ratio");
        opt.addOption(null, "test", true, "Test ratio");
        CommandLine cl = new DefaultParser().parse(opt, args);

        Path input = Paths.get(cl.getOptionValue("input"));
        double trainRatio = Double.parseDouble(cl.getOptionValue("train", "0.8"));
        double devRatio = Double.parseDouble(cl.getOptionValue("dev", "0.1"));
        double testRatio = Double.parseDouble(cl.getOptionValue("test", "0.1"));
        if (Math.abs(trainRatio + devRatio + testRatio - 1.0) > 1e-3) {
            throw new IllegalArgumentException("Ratios must sum to 1");
        }

        List<String> lines = Files.readAllLines(input);
        Collections.shuffle(lines, new Random(42));

        int trainEnd = (int) (lines.size() * trainRatio);
        int devEnd = trainEnd + (int) (lines.size() * devRatio);

        write(lines.subList(0, trainEnd), "train.tsv");
        write(lines.subList(trainEnd, devEnd), "dev.tsv");
        write(lines.subList(devEnd, lines.size()), "test.tsv");

        System.out.printf("Split: %d train, %d dev, %d test%n",
                trainEnd, devEnd - trainEnd, lines.size() - devEnd);
    }

    private static void write(List<String> rows, String file) throws IOException {
        Files.write(Paths.get(file), rows);
    }
}
