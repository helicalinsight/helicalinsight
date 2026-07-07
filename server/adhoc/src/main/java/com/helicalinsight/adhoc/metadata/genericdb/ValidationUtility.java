package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validation purposes related to metadata.
 * 
 * Created by author on 24-03-2015.
 * @author Rajasekhar
 */
public class ValidationUtility {

    @NotNull
    private final Map<String, String> tablesMap;

    @NotNull
    private final Map<String, String> columnsMap;

    public ValidationUtility(@NotNull Metadata metadata) {
        this.tablesMap = new HashMap<>();
        this.columnsMap = new HashMap<>();
        prepareMaps(metadata);
    }
    /**
     * Prepares tables and columns maps from the metadata.
     * @param metadata 		 metadata object provides database names, table list, column list.
     */
    private void prepareMaps(@NotNull Metadata metadata) {
        Database database = metadata.getDatabase();
        String databaseName = database.getName();

        if ("".equals(databaseName)) {
            databaseName = null;
        }

        Tables tables = database.getTables();
        if (tables != null) {
            List<Table> tableList = tables.getTableList();
            if (tableList != null) {
                for (Table actualTable : tableList) {
                    String dbAndTable;
                    String dbAndTableAlias;

                    if (databaseName == null) {
                        dbAndTable = actualTable.getName();
                        dbAndTableAlias = actualTable.getAliasName();
                        tablesMap.put(dbAndTable, actualTable.getAliasName());
                    } else {
                        dbAndTable = databaseName + "." + actualTable.getName();
                        dbAndTableAlias = databaseName + "." + actualTable.getAliasName();
                        tablesMap.put(dbAndTable, databaseName + "." + actualTable.getAliasName());
                    }

                    Columns columns = actualTable.getColumns();
                    if (columns != null) {
                        List<Column> columnList = columns.getColumn();
                        if (columnList != null) {
                            for (Column actualColumn : columnList) {
                                columnsMap.put(dbAndTable + "." + actualColumn.getName(), dbAndTableAlias  +
                                        "." + actualColumn.getAliasName());
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Retrieves the tables map.
     * @return tables map
     */
    @NotNull
    public Map<String, String> getTablesMap() {
        return tablesMap;
    }
    /**
     * Retrieves the columns map.
     * @return columns map
     */
    @NotNull
    public Map<String, String> getColumnsMap() {
        return columnsMap;
    }
}
