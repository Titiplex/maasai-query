package main.java.com.aixuniversity.maasaidictionary.config;

import java.util.ArrayList;
import java.util.List;

public abstract class SqlStringConfig {
    private static final String INSERTION_STRING = "insert into ";
    private static final String SELECTION_ALL_STRING = "select * from ";

    private static final String SELECTION_STRING = "select ";
    private static final String DELETION_STRING = "delete from ";
    private static final String UPDATE_STRING = "update ";

    public static String getInsertionString(String key) {
        String insertionString = INSERTION_STRING;
        List<String> columnsNames = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        for (String column : DaoConfig.getColumns(key)) {
            columnsNames.add(DaoConfig.getColumnName(key, column));
        }
        for (int i = 0; i < columnsNames.size(); i++) {
            placeholders.add("?");
        }
        insertionString += DaoConfig.getTableName(key) + " (" + String.join(", ", columnsNames) + ")"
        + " values (" + String.join(", ", placeholders) + ")" ;

        return insertionString;
    }

    public static String getSelectionAllString(String key) {
        return SELECTION_ALL_STRING + DaoConfig.getTableName(key);
    }

    public static String getSelectionStringById(String key, int id) {
        return SELECTION_ALL_STRING + DaoConfig.getTableName(key) + " where id=" + id;
    }

    public static String getSelectionStringByVocId(String key, int vocId) {
        return SELECTION_ALL_STRING + DaoConfig.getTableName(key) + " where vocabularyId=" + vocId;
    }

    public static String getSelectionStringSpecificWhereSpecific(String key, int selectedColumn, int whereColumn) {
        return SELECTION_STRING + DaoConfig.getColumnName(key, DaoConfig.getColumns(key).get(selectedColumn)) + " from " + DaoConfig.getTableName(key)
                + " where " + DaoConfig.getColumnName(key, DaoConfig.getColumns(key).get(whereColumn)) + " = ?";
    }

    public static String getDeletionString(String key, int id) {
        return DELETION_STRING + DaoConfig.getTableName(key) + " where id=" + id;
    }

    public static String getDeletionStringWhereAll(String key) {
        List<String> columnsForDelete = new ArrayList<>();
        for (String column : DaoConfig.getColumns(key)) {
            columnsForDelete.add(DaoConfig.getColumnName(key, column) + " = ?");
        }
        return DELETION_STRING + DaoConfig.getTableName(key) + " where " + String.join(" and ", columnsForDelete);
    }

    public static String getUpdateString(String key, int id) {
        StringBuilder updateString = new StringBuilder(UPDATE_STRING + DaoConfig.getTableName(key) + " set ");

        List<String> columns = DaoConfig.getColumns(key);
        for (String col : columns) {
            String column = DaoConfig.getColumnName(key, col);
            updateString.append(column).append(" =? ");
        }
        updateString.append(" where id=").append(id);

        return updateString.toString();
    }
}
