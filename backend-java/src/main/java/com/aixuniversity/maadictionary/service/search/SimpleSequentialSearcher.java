package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.service.search.tokens.TokImpossible;
import com.aixuniversity.maadictionary.service.search.tokens.Token;
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

        // 2) ensemble-candidat initial (copie modifiable)
        IntSet ids = lookup.idsFor(tokens.getFirst());

        // 3) filtrage progressif
        for (int i = 1; i < tokens.size() && !ids.isEmpty(); i++) {
            Token t = tokens.get(i);
            IntIterator it = ids.iterator();
            while (it.hasNext()) {
                int id = it.nextInt();
                Vocabulary v = vDao.searchById(id);
                if (!matcher.matches(t, v)) it.remove();      // on l’élimine
            }
        }

        // 4) transformation en Vocabulaire (ordre arbitraire)
        List<Vocabulary> out = new ArrayList<>();
        ids.forEach(id -> {
            try {
                out.add(vDao.searchById(id));
            } catch (SQLException e) { /* noop */ }
        });
        return out;
    }
}
