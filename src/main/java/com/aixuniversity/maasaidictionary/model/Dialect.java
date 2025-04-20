package main.java.com.aixuniversity.maasaidictionary.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dialect extends AbstractModel {
    private String dialectName;

    private static final Map<String, Dialect> dialects = new HashMap<>();

    public Dialect() {
        super();
        this.dialectName = "Arusa";
        addDialect(this);
    }

    public Dialect(String dialect) {
        super();
        this.dialectName = dialect;
        addDialect(this);
    }

    public String getDialectName() {
        return dialectName;
    }

    public void setDialectName(String dialect) {
        this.dialectName = dialect;
    }

    public static Map<String, Dialect> getDialects() {
        return dialects;
    }

    public static void addDialect(Dialect dialect) {
        if (!dialects.containsKey(dialect.getDialectName())) {
            dialects.put(dialect.getDialectName(), dialect);
        }
    }

    public static Dialect getDialect(String dialect) {
        if (!dialects.containsKey(dialect)) return null;
        return dialects.get(dialect);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dialect dialect1 = (Dialect) o;
        return Objects.equals(dialectName, dialect1.dialectName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dialectName);
    }

    @Override
    public String toString() {
        return
                "dialect='" + dialectName + '\'';
    }
}
