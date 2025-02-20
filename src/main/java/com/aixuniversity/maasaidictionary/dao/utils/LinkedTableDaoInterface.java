package main.java.com.aixuniversity.maasaidictionary.dao.utils;

import java.sql.SQLException;
import java.util.List;

public interface LinkedTableDaoInterface<A, B> {

    /**
     * Insère une liaison dans la table pivot.
     *
     * @param firstId  l'ID de la première entité (ex: vocabulary_id)
     * @param secondId l'ID de la seconde entité (ex: linked_vocabulary_id)
     */
    void insertLink(A firstId, B secondId) throws SQLException;

    /**
     * Supprime une liaison dans la table pivot.
     */
    void deleteLink(A firstId, B secondId) throws SQLException;

    /**
     * Récupère toutes les 'secondId' liées à 'firstId'.
     */
    List<B> getLinkedIds(A firstId) throws SQLException;
}
