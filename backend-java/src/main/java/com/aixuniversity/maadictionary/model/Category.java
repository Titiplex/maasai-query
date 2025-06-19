package com.aixuniversity.maadictionary.model;

import com.aixuniversity.maadictionary.config.AbbreviationConfig;
import com.aixuniversity.maadictionary.dao.normal.CategoryDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Category extends AbstractModel {

    private static final Map<String, Category> categories = new HashMap<>();

    private int freq = 0;
    private String name;
    private String abbr;

    public Category(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
        addCategory(this);
    }

    public static Category getOrCreate(String abbr, CategoryDao cDao) throws SQLException {
        if (abbr == null || abbr.isEmpty()) {
            return null;
        }

        Category existing = getCategory(abbr);
        if (existing != null) {
            return existing;
        }

        String name = AbbreviationConfig.getFromAbbreviation(abbr);
        if (name == null) name = abbr;

        synchronized (categories) {  // Thread safety
            // Double-check in case another thread created it
            existing = getCategory(abbr);
            if (existing != null) {
                return existing;
            }

            Category cat = new Category(name, abbr);
            cat.setId(cDao.insert(cat));
            addCategory(cat);
            return cat;
        }
    }

    public int getFreq() {
        if (this.freq == 0) return 0;
        return 1 / this.freq;
    }

    public void addFreq() {
        addFreq(1);
    }

    public void addFreq(int nb) {
        this.freq += nb;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public static Map<String, Category> getCategoryList() {
        return categories;
    }

    public static void addCategory(Category category) {
        if (!categories.containsKey(category.getName())) {
            categories.put(category.getName(), category);
        }
    }

    public static Category getCategory(String name) {
        if (!categories.containsKey(name)) return null;
        return categories.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Category category)) return false;
        return Objects.equals(getName(), category.getName()) && Objects.equals(getAbbr(), category.getAbbr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAbbr());
    }
}
