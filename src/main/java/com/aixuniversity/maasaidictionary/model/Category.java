package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Category extends AbstractModel {

    private static final Map<String, Category> categories = new HashMap<>();
    private String name;
    private String abbr;

    public Category(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
        addCategory(this);
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
