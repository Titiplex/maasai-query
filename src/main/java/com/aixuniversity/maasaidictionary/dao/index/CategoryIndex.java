package main.java.com.aixuniversity.maasaidictionary.dao.index;

import it.unimi.dsi.fastutil.ints.*;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.AbstractDao;
import main.java.com.aixuniversity.maasaidictionary.dao.utils.DatabaseHelper;
import main.java.com.aixuniversity.maasaidictionary.model.Category;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class CategoryIndex implements IndexInterface<Category> {
    private final Int2ObjectMap<IntArrayList> cat2Ids = new Int2ObjectOpenHashMap<>();
    private final Int2IntMap freq = new Int2IntOpenHashMap();

    public CategoryIndex() throws SQLException {
        Connection db = DatabaseHelper.getConnection();
        try (PreparedStatement ps = db.prepareStatement("""
                        SELECT categoryId, vocabularyId
                        FROM VocabularyPhonemeCategory
                        ORDER BY categoryId, vocabularyId
                """)) {
            ResultSet rs = ps.executeQuery();
            int currentCat = -1;
            IntArrayList list = null;

            while (rs.next()) {
                int cat = rs.getInt(1);
                int vid = rs.getInt(2);
                if (cat != currentCat) {
                    list = new IntArrayList();
                    cat2Ids.put(cat, list);
                    currentCat = cat;
                }
                assert list != null;
                list.add(vid);
            }
            // fréquence = taille de chaque liste
            cat2Ids.forEach(this::updateFrequency);
        }
    }

    @Override
    public IntArrayList idsFor(Category category) {
        return cat2Ids.getOrDefault(category.getId(), new IntArrayList());
    }

    @Override
    public IntArrayList idsFor(Token categoryId) {
        return switch (categoryId) {
            case Token.IntegerToken st -> cat2Ids.getOrDefault(st.value().intValue(), new IntArrayList());
            case Token.StringToken _ -> throw new IllegalArgumentException("Token must be a String");
        };
    }

    @Override
    public Map<Category, Integer> index(AbstractDao<Category> dao) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, SQLException {
        return IndexInterface.super.index(dao);
    }

    @Override
    public int frequency(Category category) {
        return freq.getOrDefault(category.getId(), 0);
    }

    @Override
    public int frequency(Token category) {
        return switch (category) {
            case Token.IntegerToken st -> freq.getOrDefault(st.value().intValue(), 0);
            case Token.StringToken _ -> throw new IllegalArgumentException("Token must be a String");
        };
    }

    @Override
    public void updateFrequency(Object category, IntArrayList list) {
        if (category != null && list != null) {
            freq.put(((Integer) category).intValue(), list.size());
        }
    }
}