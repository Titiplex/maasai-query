package main.java.com.aixuniversity.maasaidictionary.dao;

import main.java.com.aixuniversity.maasaidictionary.config.DaoConfig;
import main.java.com.aixuniversity.maasaidictionary.config.SqlStringConfig;
import main.java.com.aixuniversity.maasaidictionary.model.AbstractModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDao<T extends AbstractModel> implements DatabaseInterface<T> {

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

    protected T buildEntityFromResultSet(ResultSet rs) throws SQLException {
        try {
            // 1) Instancier la classe T (via un constructeur par défaut)
            Class<T> clazz = getEntityClass(); // Méthode abstraite à implémenter
            T instance = clazz.getDeclaredConstructor().newInstance();

            // 2) Récupérer la liste des propriétés (ex: ["id", "name", "price"])
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

    private void setProperty(T instance, String property, Object value)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Ex: property = "name" -> chercher setName(String)
        String methodName = "set" + capitalize(property);

        // On suppose que value est déjà du bon type
        Class<?> paramType = value.getClass(); // par ex. String.class, Integer.class, etc.

        Method setter = instance.getClass().getMethod(methodName, paramType);
        setter.invoke(instance, value);
    }

    // Petit utilitaire
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
