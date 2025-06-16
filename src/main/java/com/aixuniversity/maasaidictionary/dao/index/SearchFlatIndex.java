package main.java.com.aixuniversity.maasaidictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public interface SearchFlatIndex<F> extends SearchIndex<F>{

    IntArrayList allIds();
    long totalFreq();
}
