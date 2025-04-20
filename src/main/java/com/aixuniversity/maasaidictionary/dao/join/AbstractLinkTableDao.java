package main.java.com.aixuniversity.maasaidictionary.dao.join;

import main.java.com.aixuniversity.maasaidictionary.config.DaoConfig;
import main.java.com.aixuniversity.maasaidictionary.config.SqlStringConfig;
import main.java.com.aixuniversity.maasaidictionary.dao.utils.DaoRegistry;
import main.java.com.aixuniversity.maasaidictionary.dao.utils.DatabaseHelper;
import main.java.com.aixuniversity.maasaidictionary.dao.utils.LinkedTableDaoInterface;
import main.java.com.aixuniversity.maasaidictionary.model.AbstractModel;

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
     */
    @Override
    public void insertLink(Integer firstId, Integer secondId) throws SQLException {
        String baseQuery = SqlStringConfig.getInsertionString(getLinkTableKey());
        String query = baseQuery.replaceFirst("insert\\s+into", "insert ignore into");
        Connection conn = DatabaseHelper.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, firstId);
            ps.setInt(2, secondId);
            ps.executeUpdate();
        }
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
    public List<?> getLinkedEntities(Integer firstId) throws SQLException {
        // 1) Get the list of second IDs
        List<Integer> ids = getLinkedIds(firstId);

        // 2) Convert each ID to an actual object
        List<Object> entities = new ArrayList<>();
        for (Integer secondId : ids) {
            AbstractModel entity = DaoRegistry.getDao(returnEntityClass()).searchById(secondId);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     * Abstract hook: how do we fetch the actual object from an ID?
     */
    // protected abstract Object fetchEntityById(Integer id) throws SQLException;
}
