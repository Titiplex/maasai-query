package com.aixuniversity.maal.ingest;

import com.aixuniversity.maadictionary.dao.normal.ExampleDao;
import com.aixuniversity.maadictionary.model.Example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class ExampleExporter {
    public static void main(String[] args) throws Exception {

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("data/maa_en.tsv"))) {

            List<Example> examples = new ExampleDao().getAll();

            for (Example ex : examples) {
                if (!Objects.equals(ex.getGlossLanguage().getCode(), "en") || ex.getGloss() == null) continue;
                String maa = ex.getExample();
                String eng = ex.getGloss();
                bw.write(maa + "\\t" + eng);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Export OK → data/maa_en.tsv");
        }
    }
}
