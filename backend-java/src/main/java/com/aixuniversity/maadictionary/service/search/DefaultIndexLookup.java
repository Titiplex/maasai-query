package com.aixuniversity.maadictionary.service.search;

import com.aixuniversity.maadictionary.dao.index.CategoryFlatIndex;
import com.aixuniversity.maadictionary.dao.index.CategoryPosIndex;
import com.aixuniversity.maadictionary.dao.index.PhonemeFlatIndex;
import com.aixuniversity.maadictionary.dao.index.PhonemePosIndex;
import com.aixuniversity.maadictionary.service.search.tokens.*;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.sql.SQLException;

public class DefaultIndexLookup implements IndexLookup {
    private final CategoryPosIndex catPos;
    private final PhonemePosIndex phPos;
    private final CategoryFlatIndex catFlat;
    private final PhonemeFlatIndex phFlat;

    public DefaultIndexLookup() throws SQLException {
        catPos = new CategoryPosIndex();
        phPos = new PhonemePosIndex();
        catFlat = new CategoryFlatIndex();
        phFlat = new PhonemeFlatIndex();
    }

    public IntSet idsFor(Token t) {
        return switch (t) {
            case TokCatPos cp -> new IntOpenHashSet(catPos.idsFor(
                    new CategoryPosIndex.Key(cp.cat(), cp.syl(), cp.pos())));
            case TokPhonPos pp -> new IntOpenHashSet(phPos.unionIdsFor(pp.phon()));
            case TokCatFlat cf -> new IntOpenHashSet(catFlat.idsFor(cf.cat()));
            case TokPhonFlat pf -> new IntOpenHashSet(phFlat.idsFor(pf.phon()));
            case TokChoice ch -> ch.options().stream()
                    .map(this::idsFor)
                    .reduce(new IntOpenHashSet(), (a, b) -> {
                        a.addAll(b);
                        return a;
                    });
            default /* TokAny */ -> new IntOpenHashSet(phFlat.allIds());
        };
    }
}
