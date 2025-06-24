package com.aixuniversity.maadictionary.dao.normal;

import com.aixuniversity.maadictionary.config.DaoConfig;
import com.aixuniversity.maadictionary.config.SqlStringConfig;
import com.aixuniversity.maadictionary.dao.utils.DaoInterface;
import com.aixuniversity.maadictionary.dao.utils.DatabaseHelper;
import com.aixuniversity.maadictionary.model.AbstractModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public abstract class AbstractDao<T extends AbstractModel> implements DaoInterface<T> {

    protected abstract Class<T> getEntityClass();

    protected abstract String getEntityKey();

    // TODO vérifier l'insertion de l'id du vocabulaire

    @Override
    public Map<T, Integer> insertAll(Collection<T> collection) throws SQLException {
        Map<T, Integer> idMap = new HashMap<>();
        for (T item : collection) {
            idMap.put(item, insert(item));
        }

        return idMap;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public Integer insert(T item) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        String query = SqlStringConfig.getInsertionString(this.getEntityKey());
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        // 2) Lister les propriétés
        executeBinding(item, stmt);

        // 4) Exécuter la requête
        stmt.executeUpdate();

        // 5) Récupérer l'ID généré
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1; // ou 0, selon la convention
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public T searchById(int id) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        String query = SqlStringConfig.getSelectionStringById(getEntityKey(), id);
        PreparedStatement stmt = conn.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return buildEntityFromResultSet(rs);
        }
        return null;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public Integer searchIdOfUniqueElement(Object element, String columnKey) throws SQLException {
        String query = SqlStringConfig.getSelectionStringSpecificWhereSpecific(getEntityKey(), 0, DaoConfig.getColumns(getEntityKey()).indexOf(columnKey));
        Connection conn = DatabaseHelper.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setObject(1, element);

        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            return null;
        }
//        if (rs.getFetchSize() != 1) {
//            throw new IllegalArgumentException("Multiple or no ids for element " + element.toString() + " in column " + columnKey);
//        }

        return (rs.getInt(1));
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public List<T> getAll() throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        List<T> list = new ArrayList<>();
        String query = SqlStringConfig.getSelectionAllString(getEntityKey());
        PreparedStatement stmt = conn.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(buildEntityFromResultSet(rs));
        }

        return list;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public boolean update(T item) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        String query = SqlStringConfig.getUpdateString(getEntityKey(), item.getId());
        PreparedStatement stmt = conn.prepareStatement(query);

        executeBinding(item, stmt);
        int rows = stmt.executeUpdate();
        return rows > 0;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public List<T> getAllFromVocId(int vocId) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        List<T> list = new ArrayList<>();
        String query = SqlStringConfig.getSelectionStringByVocId(getEntityKey(), vocId);
        PreparedStatement stmt = conn.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(buildEntityFromResultSet(rs));
        }

        return list;
    }

    private void executeBinding(T item, PreparedStatement stmt) throws SQLException {
        List<String> columns = DaoConfig.getColumns(getEntityKey());
        int index = 1;
        for (String col : columns) {
            String columnName = DaoConfig.getColumnName(getEntityKey(), col);
            // Récupérer la valeur via un getter ou via un field
            Object value = getPropertyValue(item, columnName);

            // Déterminer le type
            String columnType = DaoConfig.getColumnType(getEntityKey(), col);

            // Binder
            bindValue(stmt, index, columnType, value);

            index++;
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Override
    public boolean delete(T item) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        String query = SqlStringConfig.getDeletionString(getEntityKey(), item.getId());
        PreparedStatement stmt = conn.prepareStatement(query);
        int rows = stmt.executeUpdate();
        return rows > 0;
    }

    protected T buildEntityFromResultSet(ResultSet rs) throws SQLException {
        try {
            // 1) Instancier la classe T (via un constructeur par défaut)
            Class<T> clazz = getEntityClass(); // Méthode abstraite à implémenter
            T instance = clazz.getDeclaredConstructor().newInstance();

            // 2) Récupérer la liste des propriétés (ex : ["id", "name", "price"])
            List<String> properties = DaoConfig.getColumns(getEntityKey());

            // 3) Pour chaque propriété, lire la colonne SQL et le type
            for (String prop : properties) {
                String columnName = DaoConfig.getColumnName(getEntityKey(), prop);
                String columnType = DaoConfig.getColumnType(getEntityKey(), prop);

                // 4) Extraire la valeur du ResultSet
                Object value = extractValue(rs, columnName, columnType);

                // 5) Affecter la valeur dans l’objet instance (via un setter ou le champ)
                setProperty(instance, columnName, value);
            }

            return instance;
        } catch (Exception e) {
            throw new SQLException("Impossible de construire l'entité " + getEntityClass(), e);
        }
    }

    private Object extractValue(ResultSet rs, String columnName, String columnType) throws SQLException {
        return switch (columnType) {
            case "int" -> rs.getInt(columnName);
            case "double" -> rs.getDouble(columnName);
            case "string" -> rs.getString(columnName);
            // etc. si vous avez d’autres types (date, bool, etc.)
            default ->
                // fallback: getObject
                    rs.getObject(columnName);
        };
    }

    private void bindValue(PreparedStatement stmt, int index, String columnType, Object value)
            throws SQLException {
        switch (columnType) {
            case "int":
                // value sera typiquement un Integer
                if (value == null) {
                    stmt.setNull(index, Types.INTEGER);
                } else {
                    stmt.setInt(index, (Integer) value);
                }
                break;
            case "double":
                if (value == null) {
                    stmt.setNull(index, Types.DOUBLE);
                } else {
                    stmt.setDouble(index, (Double) value);
                }
                break;
            case "string":
                stmt.setString(index, (value == null) ? null : value.toString());
                break;
            default:
                // fallback
                stmt.setObject(index, value);
                break;
        }
    }

    private Object getPropertyValue(T item, String property) {
        try {
            String getterName = "get" + capitalize(property); // ex. "getId"
            Method getter = item.getClass().getMethod(getterName);
            return getter.invoke(item);
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'accéder à la propriété " + property, e);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Checks if the two classes are compatible, taking the primitive / wrapper
     * correspondence into account (e.g. int ↔ Integer).
     */
    private boolean isTypeCompatible(Class<?> paramType, Class<?> valueType) {
        if (paramType.isAssignableFrom(valueType)) return true;          // exact / superclass
        // primitive ↔ wrapper table
        return (paramType == int.class && valueType == Integer.class) ||
                (paramType == Integer.class && valueType == int.class) ||
                (paramType == double.class && valueType == Double.class) ||
                (paramType == Double.class && valueType == double.class) ||
                // (paramType == long.class && valueType == Long.class) ||
                // (paramType == Long.class && valueType == long.class) ||
                (paramType == boolean.class && valueType == Boolean.class) ||
                (paramType == Boolean.class && valueType == boolean.class);
    }

    private void setProperty(T instance, String property, Object value)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        String methodName = "set" + capitalize(property);
        Class<?> valueType = (value == null) ? Object.class : value.getClass();

        Method chosen = null;
        for (Method m : instance.getClass().getMethods()) {
            if (!m.getName().equals(methodName) || m.getParameterCount() != 1) continue;
            Class<?> paramType = m.getParameterTypes()[0];
            if (value == null || isTypeCompatible(paramType, valueType)) {
                chosen = m;
                break;
            }
        }

        if (chosen == null) {           // keep previous behaviour
            throw new NoSuchMethodException(instance.getClass() + "." + methodName);
        }
        chosen.invoke(instance, value);
    }
}