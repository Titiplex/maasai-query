package com.aixuniversity.maadictionary.dao.join;

import com.aixuniversity.maadictionary.config.DaoConfig;
import com.aixuniversity.maadictionary.config.SqlStringConfig;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.model.AbstractModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class MultiInterfaceDao<T extends AbstractModel> extends AbstractLinkTableDao<T> {

    /**
     * Inserts a link between two entities identified by their IDs
     * into the database using an "insert ignore into" SQL statement.
     * The insertion is performed for the table specified by the subclass-defined key.
     *
     * @param firstId  the ID of the first entity participating in the link
     * @param secondId the ID of the second entity participating in the link
     * @param args     other fields to pass into the link table
     * @return the auto-generated ID of the newly created record if the insertion succeeds,
     * or -1 if no record was created (e.g., due to a duplicate).
     * @throws SQLException if a database access error occurs or the SQL execution fails
     */
    @Override
    public Object insertLink(Integer firstId, Integer secondId, Object... args) throws SQLException {
        String baseQuery = SqlStringConfig.getInsertionString(getLinkTableKey());
        String query = baseQuery.replaceFirst("insert\\s+into", "insert ignore into");
        // System.out.println(query);
        Connection conn = DatabaseHelper.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            String id = DaoConfig.getColumnKeyByName(getLinkTableKey(), "id");
            int index = 1;
            if ( id != null && !id.isEmpty()) {
                ps.setInt(index, 0);
                index++;
            }
            ps.setInt(index, firstId);
            index++;
            ps.setInt(index, secondId);
            index++;
            if (args.length > 0)
                for (int i = 0; i < args.length; i++) ps.setInt(index + i, (int) args[i]);
            // System.out.println(ps);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
}
