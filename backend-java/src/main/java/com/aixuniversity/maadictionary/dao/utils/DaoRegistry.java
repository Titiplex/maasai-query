package com.aixuniversity.maadictionary.dao.utils;

import com.aixuniversity.maadictionary.dao.normal.AbstractDao;
import com.aixuniversity.maadictionary.model.AbstractModel;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DaoRegistry {
    private static final Map<Class<?>, AbstractDao<?>> daoMap = new HashMap<>();

    static {
        registerAllDaos("com.aixuniversity.maadictionary.dao.normal");
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractModel> AbstractDao<T> getDao(Class<T> entityClass) throws SQLException {
        if (daoMap.isEmpty()) registerAllDaos("com.aixuniversity.maadictionary.dao.normal");
        AbstractDao<T> dao = (AbstractDao<T>) daoMap.get(entityClass);
        if (dao == null) {
            System.out.println("Map : "+daoMap);
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

    private static Class<? extends AbstractModel> getEntityClassFromDao(Class<? extends AbstractDao> daoClass) {
        try {
            return (Class<? extends AbstractModel>) daoClass.getDeclaredMethod("getEntityClass").invoke(daoClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            System.err.println("Could not get entity class from : " + daoClass.getName());
            return null;
        }
    }
}
