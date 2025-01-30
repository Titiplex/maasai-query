package main.java.com.aixuniversity.maasaidictionary.dao;

import java.util.List;

/**
 * Interface pour implémenter les fonctions des Data Access Objects.
 *
 * @param <T> Le type d'entité que l'on cherche avec les Dao.
 */
interface DaoInterface<T> {

    /**
     * Sauvegarde un élément dans la BDD
     */
    void save();

    /**
     * Trouve tous les éléments dans une table
     * à partir d'une entité model liée.
     *
     * @param item L'entité de départ dont on veut trouver les éléments liés.
     * @param <U>  Le type de l'entité de départ.
     * @return Une liste d'entités du type que l'on cherche.
     */
    <U> List<T> findAll(U item);

    /**
     * Trouve une instance selon son id.
     *
     * @param id L'id de l'objet (entier).
     * @return L'objet du type spécifié.
     */
    T findById(int id);
}
