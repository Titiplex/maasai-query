package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.tokens.*;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SimpleSequentialSearcher implements Searcher<String> {

    private final VocabularyDao vDao = new VocabularyDao();
    private final IndexLookup lookup;
    private final TokenMatcher matcher;

    public SimpleSequentialSearcher() throws SQLException {
        this.lookup = new DefaultIndexLookup();
        this.matcher = new DefaultTokenMatcher();
    }

    @Override
    public List<Vocabulary> search(String raw) throws SQLException {

        HybridPattern pat = HybridPattern.parse(raw);

        // si un symbole est inconnu, on renvoie vide immédiatement
        if (pat.tokens().stream().anyMatch(t -> t instanceof TokImpossible)) return List.of();

        // 1) ordre séquentiel tel qu’écrit dans la requête
        List<Token> tokens = pat.tokens();

        // pivot
        Token pivot = tokens.stream()
                .filter(t -> !(t instanceof TokAny ||
                        (t instanceof TokRepeat r && r.min() == 0)))
                .findFirst()
                .orElse(tokens.getFirst());

        /* --- début de mot ? -------------------------------------------- */
        if (!tokens.isEmpty() && tokens.getFirst() instanceof TokStart) {
            tokens = tokens.subList(1, tokens.size());   // on l’enlève
            // ⇒ pas besoin de test spécial : la 1ʳᵉ vraie contrainte porte
        }

        /* --- fin de mot ?  --------------------------------------------- */
        boolean needEnd = false;
        if (!tokens.isEmpty() && tokens.getLast() instanceof TokEnd) {
            tokens = tokens.subList(0, tokens.size() - 1);
            needEnd = true;
        }

        // 2) ensemble-candidat initial (copie modifiable)
        IntSet ids = lookup.idsFor(pivot);

        // 3) filtrage progressif
        for (int i = 1; i < tokens.size() && !ids.isEmpty(); i++) {
            Token t = tokens.get(i);
            if (t == pivot) continue;
            IntIterator it = ids.iterator();
            while (it.hasNext()) {
                int id = it.nextInt();
                Vocabulary v = vDao.searchById(id);
                if (!matcher.matches(t, v)) it.remove();      // on l’élimine
            }
        }

        // 4) transformation en Vocabulaire (ordre arbitraire)
        // 3) filtrage progressif — inchangé

        /* --- ancrage fin de mot (exact nbr de syllabes) --------------- */
        if (needEnd && !ids.isEmpty()) {

            int neededSyls = tokens.stream()
                    .filter(t -> t instanceof TokCatPos || t instanceof TokPhonPos)
                    .mapToInt(t -> ((PosToken) t).syl())       // interface PosToken { byte syl(); }
                    .max().orElse(-1) + 1;
            IntIterator it = ids.iterator();
            if (neededSyls == 0) {
                neededSyls = pat.segmentCount();    // nouvelle méthode exposée par HybridPattern
            }
            while (it.hasNext()) {
                int id = it.nextInt();
                String[] s = vDao.searchById(id).getSyll_pattern().split("-");
                if (s.length != neededSyls) it.remove();
            }
        }

        /* 4) transformation -------------------------------------------- */
        List<Vocabulary> out = new ArrayList<>();
        ids.forEach(id -> {
            try {
                out.add(vDao.searchById(id));
            } catch (SQLException ignored) {
            }
        });
        return out;
    }
}
