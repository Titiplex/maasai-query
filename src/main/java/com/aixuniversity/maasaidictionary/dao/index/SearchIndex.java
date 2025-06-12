// dao/index/SearchIndex.java
package main.java.com.aixuniversity.maasaidictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Contrat minimal d’un index en mémoire ou en BDD.
 */
public interface SearchIndex<K> {
    /**
     * Liste (potentiellement vide) d’IDs de Vocabulary. Jamais null.
     */
    IntArrayList idsFor(K key);

    /**
     * Fréquence brute du token pour choisir un pivot.
     */
    int frequency(K key);
}