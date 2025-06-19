package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.service.search.tokens.Token;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import com.aixuniversity.maadictionary.dao.index.CategoryFlatIndex;
import com.aixuniversity.maadictionary.dao.index.CategoryPosIndex;
import com.aixuniversity.maadictionary.dao.index.PhonemeFlatIndex;
import com.aixuniversity.maadictionary.dao.index.PhonemePosIndex;
import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.Vocabulary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class FlexibleSearcher implements Searcher<String> {
    private final CategoryPosIndex catPos;
    private final PhonemePosIndex phPos;
    private final CategoryFlatIndex catFlat;
    private final PhonemeFlatIndex phFlat;
    private final VocabularyDao vdao = new VocabularyDao();

    public FlexibleSearcher() throws SQLException {
        catPos = new CategoryPosIndex();
        phPos = new PhonemePosIndex();
        catFlat = new CategoryFlatIndex();
        phFlat = new PhonemeFlatIndex();
    }

    @Override
    public List<Vocabulary> search(String raw) throws SQLException {
        HybridPattern pat = HybridPattern.parse(raw);
        Token pivot = pat.pickPivot(catPos, phPos, catFlat, phFlat);
        IntArrayList cand = pat.idsForPivot(pivot, catPos, phPos, catFlat, phFlat);
        List<Vocabulary> out = new ArrayList<>();
        for (int id : cand) {
            Vocabulary v = vdao.searchById(id);
            if (v != null && pat.matches(v)) out.add(v);
        }
        return out;
    }
}
