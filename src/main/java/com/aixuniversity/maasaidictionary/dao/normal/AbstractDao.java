package main.java.com.aixuniversity.maasaidictionary.dao.normal;

import main.java.com.aixuniversity.maasaidictionary.config.DaoConfig;
import main.java.com.aixuniversity.maasaidictionary.config.SqlStringConfig;
import main.java.com.aixuniversity.maasaidictionary.dao.utils.DatabaseHelper;
import main.java.com.aixuniversity.maasaidictionary.dao.utils.DaoInterface;
import main.java.com.aixuniversity.maasaidictionary.model.AbstractModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public abstract class AbstractDao<T extends AbstractModel> implements DaoInterface<T> {

    protected abstract Class<T> getEntityClass();

    protected abstract String getEntityKey();

    @Override
    public Map<T, Integer> insertAll(Collection<T> collection) throws SQLException {
        Map<T, Integer> idMap = new HashMap<>();
        for (T item : collection) {
            idMap.put(item, insert(item));
        }

        return idMap;
    }

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

    @Override
    public boolean update(T item) throws SQLException {
        Connection conn = DatabaseHelper.getConnection();
        String query = SqlStringConfig.getUpdateString(getEntityKey(), item.getId());
        PreparedStatement stmt = conn.prepareStatement(query);

        executeBinding(item, stmt);
        int rows = stmt.executeUpdate();
        return rows > 0;
    }

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
            // Récupérer la valeur via un getter ou via un field
            Object value = getPropertyValue(item, col);

            // Déterminer le type
            String columnType = DaoConfig.getColumnType(getEntityKey(), col);

            // Binder
            bindValue(stmt, index, columnType, value);

            index++;
        }
    }

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
                setProperty(instance, prop, value);
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
            // etc., ajoutez cases pour Date, Boolean...
            default:
                // fallback
                stmt.setObject(index, value);
                break;
        }
    }


    private void setProperty(T instance, String property, Object value)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Ex: property = "name" -> chercher setName(String)
        String methodName = "set" + capitalize(property);

        // On suppose que value est déjà du bon type
        Class<?> paramType = value.getClass(); // Par ex. String.class, Integer.class, etc.

        Method setter = instance.getClass().getMethod(methodName, paramType);
        setter.invoke(instance, value);
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

    // Petit utilitaire
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
