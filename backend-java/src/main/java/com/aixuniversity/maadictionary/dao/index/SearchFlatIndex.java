package com.aixuniversity.maadictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public interface SearchFlatIndex<F> extends SearchIndex<F>{

    IntArrayList allIds();
    long totalFreq();
}
