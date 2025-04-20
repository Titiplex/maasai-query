package main.java.com.aixuniversity.maasaidictionary.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Chargeur de fichiers .properties au format UTF‑8.
 */
public final class PropertiesLoader {
    private PropertiesLoader() {}
    public static void load(String resource, Properties props) {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resource);
        // fallback via getResourceAsStream sur la classe
        if (in == null) {
            in = IPAConfig.class.getResourceAsStream("/" + resource);
        }
        if (in == null) {
            throw new RuntimeException(
                    "Impossible de trouver " + resource + " sur le classpath");
        }

        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture de " + resource, e);
        }
    }
}
