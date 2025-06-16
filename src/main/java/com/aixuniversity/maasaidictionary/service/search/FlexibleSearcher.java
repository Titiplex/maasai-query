package main.java.com.aixuniversity.maasaidictionary.service.search;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import main.java.com.aixuniversity.maasaidictionary.dao.index.CategoryFlatIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.index.CategoryPosIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.index.PhonemeFlatIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.index.PhonemePosIndex;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.VocabularyDao;
import main.java.com.aixuniversity.maasaidictionary.model.Vocabulary;

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
