package com.aixuniversity.maadictionary.dao.utils;

import java.sql.SQLException;
import java.util.List;

public interface LinkedTableDaoInterface<A, B> {

    /**
     * Insère une liaison dans la table pivot.
     *
     * @param firstId  l'ID de la première entité (ex: vocabulary_id)
     * @param secondId l'ID de la seconde entité (ex: linked_vocabulary_id)
     * @param args     des arguments éventuels
     */
    Object insertLink(A firstId, B secondId, Object... args) throws SQLException;

    /**
     * Supprime une liaison dans la table pivot.
     */
    void deleteLink(A firstId, B secondId) throws SQLException;

    /**
     * Récupère toutes les 'secondId' liées à 'firstId'.
     */
    List<B> getLinkedIds(A firstId) throws SQLException;
}
