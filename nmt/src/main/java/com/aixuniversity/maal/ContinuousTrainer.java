package com.aixuniversity.maal;

import java.io.IOException;
import java.nio.file.*;

/**
 * Watches a data directory; when new .tsv files appear,
 * appends them to the corpus and triggers a short fine-tune.
 */
public class ContinuousTrainer {

    public static void main(String[] args) throws Exception {
        Path dataDir = Paths.get("data");
        WatchService ws = FileSystems.getDefault().newWatchService();
        dataDir.register(ws, StandardWatchEventKinds.ENTRY_CREATE);

        System.out.println("Watching " + dataDir);
        //noinspection InfiniteLoopStatement
        while (true) {
            WatchKey key = ws.take();
            for (WatchEvent<?> evt : key.pollEvents()) {
                WatchEvent.Kind<?> kind = evt.kind();
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path file = dataDir.resolve((Path) evt.context());
                    if (file.toString().endsWith(".tsv")) {
                        System.out.println("New file: " + file);
                        triggerFineTune(file);
                    }
                }
            }
            key.reset();
        }
    }

    static void triggerFineTune(Path newData) throws IOException, InterruptedException {
        // Simple call; in reality merge datasets etc.
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", "target/maa-nmt.jar",
                "com.aixuniversity.maal.TrainLoRATransformer",
                "--src", "data/merged.tsv",
                "--spm_model", "maa.model",
                "--epochs", "1",
                "--batch", "64");
        pb.inheritIO();
        pb.start().waitFor();
    }
}
