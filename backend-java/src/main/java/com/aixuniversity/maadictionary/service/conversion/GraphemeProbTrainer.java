package com.aixuniversity.maadictionary.service.conversion;

import com.aixuniversity.maadictionary.app.ImportStatus;
import com.aixuniversity.maadictionary.dao.normal.GraphemeMapDao;
import com.aixuniversity.maadictionary.dao.normal.OrthographyVariantDao;
import com.aixuniversity.maadictionary.model.OrthographyVariant;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GraphemeProbTrainer {

    /**
     * orth → grapheme → ipa → count
     */
    private final Map<String, Map<String, Map<String, Integer>>> cnt = new HashMap<>();

    private final GraphemeMapDao gmDao = new GraphemeMapDao();

    @PostConstruct
    public void trainIfNeeded() throws SQLException {
        // 1) Si la table est vide, persiste au moins la base depuis le registre (.properties)
        if (gmDao.isEmpty()) {
            persistRegistryBaselineToDb();
            System.out.println("[GraphemeProb] baseline persisted from properties.");
        }

        // 2) Entraîne seulement si nécessaire ; sinon on garde la baseline
        if (!ImportStatus.needsTraining()) {
            System.out.println("[GraphemeProb] up-to-date (no training needed).");
            return;
        }

        train();
        ImportStatus.recordTraining();
    }

    public void train() throws SQLException {
        System.out.println("Training grapheme probabilities...");
        List<OrthographyVariant> all = OrthographyVariantDao.findAllWithIpaCache();
        int seen = 0, aligned = 0;
        for (OrthographyVariant ov : all) {
            seen++;
            int before = cnt.values().stream().mapToInt(Map::size).sum();
            accumulate(ov);
            int after = cnt.values().stream().mapToInt(Map::size).sum();
            if (after > before) aligned++;
        }
        System.out.printf("[GraphemeProb] variants seen=%d, aligned=%d%n", seen, aligned);

        injectProbabilities(); // écrit en RAM + DB
    }

    private void injectProbabilities() {
        AtomicInteger upserts = new AtomicInteger();

        // 1) Applique les probas apprises pour chaque orth/grapheme observé
        cnt.forEach((orth, mapG) -> {
            var table = OrthographyRegistry.table(orth);
            mapG.forEach((g, counts) -> {
                int tot = counts.values().stream().mapToInt(Integer::intValue).sum();
                String[] ipa = counts.keySet().toArray(new String[0]);
                float[] prob = new float[ipa.length];
                for (int i = 0; i < ipa.length; i++) prob[i] = counts.get(ipa[i]) / (float) tot;

                table.put(g, new GraphemeMapping(ipa, prob));
                try {
                    gmDao.upsert(orth, g, ipa, prob);
                    upserts.getAndIncrement();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });

        // 2) Pour les graphèmes jamais vus : probas uniformes → persiste aussi
        for (String orth : OrthographyRegistry.available()) {
            var table = OrthographyRegistry.table(orth);
            for (var e : table.entrySet()) {
                String g = e.getKey();
                GraphemeMapping gm = e.getValue();
                float[] prob = gm.prob;

                if (prob == null || prob.length != gm.ipa.length) {
                    prob = new float[gm.ipa.length];
                    Arrays.fill(prob, 1f / gm.ipa.length);
                    gm.prob = prob; // met aussi à jour en RAM
                }

                try {
                    gmDao.upsert(orth, g, gm.ipa, prob);
                    upserts.getAndIncrement();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        System.out.println("[GraphemeProb] upserts total = " + upserts);
    }

    /** Persiste la baseline (probas uniformes) pour TOUT le registre. */
    private void persistRegistryBaselineToDb() throws SQLException {
        int up = 0;
        for (String orth : OrthographyRegistry.available()) {
            var table = OrthographyRegistry.table(orth);
            for (var e : table.entrySet()) {
                String g = e.getKey();
                GraphemeMapping gm = e.getValue();
                float[] prob = gm.prob;
                if (prob == null || prob.length != gm.ipa.length) {
                    prob = new float[gm.ipa.length];
                    Arrays.fill(prob, 1f / gm.ipa.length);
                    gm.prob = prob;
                }
                gmDao.upsert(orth, g, gm.ipa, prob);
                up++;
            }
        }
        System.out.println("[GraphemeProb] baseline upserts = " + up);
    }

    private void accumulate(OrthographyVariant ov) {
        String orth = ov.getScript().toLowerCase(Locale.ROOT);           // ex. "payne"
        String form = ov.getForm().toLowerCase(Locale.ROOT).trim();      // « oro »
        String ipa = ov.getVocabulary().getIpa().replace("/", "");      // « ɔro »

        Map<String, GraphemeMapping> table = OrthographyRegistry.table(orth);

        // tokenisation graphème (mêmes règles que convert())
        List<String> gs = new ArrayList<>();
        int i = 0;
        while (i < form.length()) {
            for (int len = 3; len >= 1 && i + len <= form.length(); len--) {
                String sub = form.substring(i, i + len);
                if (table.containsKey(sub)) {
                    gs.add(sub);
                    i += len;
                    break;
                }
            }
        }

        // alignement naïf grapheme–ipa : concatène l’IPA “préféré” (premier) pour connaître la longueur
        StringBuilder rebuilt = new StringBuilder();
        List<String> phones = new ArrayList<>();
        for (String g : gs) {
            String ip = table.get(g).ipa[0];  // choix arbitraire
            phones.add(ip);
            rebuilt.append(ip);
        }
        if (!rebuilt.toString().equals(ipa)) return;   // échec d’alignement → skip

        // compter
        for (int k = 0; k < gs.size(); k++) {
            cnt.computeIfAbsent(orth, _ -> new HashMap<>())
                    .computeIfAbsent(gs.get(k), _ -> new HashMap<>())
                    .merge(phones.get(k), 1, Integer::sum);
        }
    }
}