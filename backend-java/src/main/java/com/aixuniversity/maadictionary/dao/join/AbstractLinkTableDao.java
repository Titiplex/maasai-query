package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.config.DaoConfig;
import com.aixuniversity.maadictionary.config.SqlStringConfig;
import com.aixuniversity.maadictionary.dao.utils.DaoRegistry;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.dao.utils.LinkedTableDaoInterface;
import com.aixuniversity.maadictionary.model.AbstractModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLinkTableDao<T extends AbstractModel> implements LinkedTableDaoInterface<Integer, Integer> {

    protected abstract String getLinkTableKey();

    protected abstract Class<T> returnEntityClass();

    /**
     * Inserts a row (first -> second).
     *
     * @return void
     */
    @Override
    public Object insertLink(Integer firstId, Integer secondId, Object... args) throws SQLException {
        try {
            if (firstId == null || secondId == null) {
                throw new RuntimeException("Cannot insert link with null IDs : " + firstId + " " + secondId);
            }
        } catch (RuntimeException e) {
            System.err.println(returnEntityClass().getName() + " : " + e.getMessage());
            return void.class;
        }
        String baseQuery = SqlStringConfig.getInsertionString(getLinkTableKey());
        String query = baseQuery.replaceFirst("insert\\s+into", "insert ignore into");
        Connection conn = DatabaseHelper.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, firstId);
            ps.setInt(2, secondId);
            ps.executeUpdate();
        }
        return void.class;
    }

    /**
     * Deletes a row (first -> second).
     */
    @Override
    public void deleteLink(Integer firstId, Integer secondId) throws SQLException {
        String query = SqlStringConfig.getDeletionStringWhereAll(getLinkTableKey());
        Connection conn = DatabaseHelper.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setInt(1, firstId);
        ps.setInt(2, secondId);
        ps.executeUpdate();
    }

    /**
     * Returns all 'secondId' values linked to 'firstId'.
     */
    @Override
    public List<Integer> getLinkedIds(Integer firstId) throws SQLException {
        String query = SqlStringConfig.getSelectionStringSpecificWhereSpecific(getLinkTableKey(), 1, 0);
        List<Integer> result = new ArrayList<>();
        Connection conn = DatabaseHelper.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setInt(1, firstId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            result.add(rs.getInt(DaoConfig.getColumnName(getLinkTableKey(), DaoConfig.getColumns(getLinkTableKey()).get(1))));
        }

        return result;
    }

    /**
     * Optionally, returns the actual domain objects (e.g. Vocabulary)
     * corresponding to the second IDs.
     * Subclasses must define how to fetch the entity from its ID (e.g. via another DAO).
     */
    public List<T> getLinkedEntities(Integer firstId) throws SQLException {
        // 1) Get the list of second IDs
        List<Integer> ids = getLinkedIds(firstId);

        // 2) Convert each ID to an actual object
        List<T> entities = new ArrayList<>();
        for (Integer secondId : ids) {
            AbstractModel entity = DaoRegistry.getDao(returnEntityClass()).searchById(secondId);
            if (entity != null) {
                entities.add((T) entity);
            }
        }
        return entities;
    }

    // TODO
    /**
     * Abstract hook: how do we fetch the actual object from an ID?
     */
    // protected abstract Object fetchEntityById(Integer id) throws SQLException;
}
