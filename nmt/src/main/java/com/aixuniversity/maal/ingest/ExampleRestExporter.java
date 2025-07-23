package com.aixuniversity.maal.ingest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Télécharge les exemples depuis ExampleDAO (endpoint REST),
 * ne garde que ceux dont gloss_language == "EN",
 * puis génère un TSV <maa>\t<english>.
 */
public class ExampleRestExporter {
    private static final String API_URL = System.getenv()
            .getOrDefault("EX_ENDPOINT", "http://localhost:8080/examples");

    public static void main(String[] args) throws Exception {

        /* ---------- 1. Requête HTTP ---------- */
        HttpResponse<String> resp;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Accept", "application/json")
                    .build();

            resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        if (resp.statusCode() != 200) {
            throw new IllegalStateException("ExampleDAO returned " + resp.statusCode());
        }

        /* ---------- 2. Parsing JSON ---------- */
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(resp.body());

        /* ---------- 3. Écriture TSV ---------- */
        Path out = Path.of("data", "maa_en.tsv");
        Files.createDirectories(out.getParent());

        try (BufferedWriter bw = Files.newBufferedWriter(out)) {
            for (JsonNode node : root) {
                if ("EN".equalsIgnoreCase(node.path("gloss_language").asText())) {
                    String maa = node.path("example").asText();
                    String eng = node.path("gloss").asText();
                    if (!maa.isBlank() && !eng.isBlank()) {
                        bw.write(maa.replaceAll("\\s+", " ").trim()
                                + '\t' +
                                eng.replaceAll("\\s+", " ").trim());
                        bw.newLine();
                    }
                }
            }
        }
        System.out.println("[Exporter] OK → " + out.toAbsolutePath());
    }
}
