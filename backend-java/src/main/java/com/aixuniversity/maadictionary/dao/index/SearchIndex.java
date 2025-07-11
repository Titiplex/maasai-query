// dao/index/SearchIndex.java
package com.aixuniversity.maadictionary.dao.index;

import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.sql.Connection;

/**
 * Contrat minimal d’un index en mémoire ou en BDD.
 */
public interface SearchIndex<K> {
    Connection db = DatabaseHelper.getConnection();
    /**
     * Liste (potentiellement vide) d’IDs de Vocabulary. Jamais null.
     */
    IntArrayList idsFor(K key);

    /**
     * Fréquence brute du token pour choisir un pivot.
     */
    int frequency(K key);
}