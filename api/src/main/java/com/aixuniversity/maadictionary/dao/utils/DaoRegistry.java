package com.aixuniversity.maadictionary.dao.utils;

import main.java.com.aixuniversity.maasaidictionary.dao.normal.AbstractDao;
import main.java.com.aixuniversity.maasaidictionary.model.AbstractModel;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DaoRegistry {
    private static final Map<Class<?>, AbstractDao<?>> daoMap = new HashMap<>();

    static {
        registerAllDaos("main.java.com.aixuniversity.maadictionary.dao");  // Adjust the package name
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractModel> AbstractDao<T> getDao(Class<T> entityClass) throws SQLException {
        AbstractDao<T> dao = (AbstractDao<T>) daoMap.get(entityClass);
        if (dao == null) {
            throw new SQLException("No registered DAO for " + entityClass.getSimpleName());
        }
        return dao;
    }

    private static void registerAllDaos(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);

        // Find all classes extending AbstractDao
        Set<Class<? extends AbstractDao>> daoClasses = reflections.getSubTypesOf(AbstractDao.class);

        for (Class<? extends AbstractDao> daoClass : daoClasses) {
            try {
                AbstractDao<?> daoInstance = daoClass.getDeclaredConstructor().newInstance();

                // Use reflection to determine the entity class (via generics or another mechanism)
                Class<?> entityClass = getEntityClassFromDao(daoClass);
                if (entityClass != null) {
                    daoMap.put(entityClass, daoInstance);
                }
            } catch (Exception e) {
                System.err.println("Could not register DAO: " + daoClass.getName());
                throw new RuntimeException(e);
            }
        }
    }

    private static Class<?> getEntityClassFromDao(Class<? extends AbstractDao> daoClass) {
        try {
            // Assuming each DAO has a method getEntityClass()
            return (Class<?>) daoClass.getDeclaredMethod("getEntityClass").invoke(daoClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            return null;
        }
    }
}
