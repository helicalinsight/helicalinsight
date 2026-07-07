package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * The ConstraintsCleaner class provides functionality to remove unselected columns from the foreign key and 
 * primary key constraints of database tables. 
 * 
 * Created by author on 27-06-2015.
 * @author Rajasekhar
 */
@Component
public class ConstraintsCleaner {
	/**
     * Removes unselected columns from the foreign key and primary key constraints of the provided database.
     * @param database 		 database object containing the tables with foreign key and primary key constraints.
     */
    public void removeUnselectedColumns(@NotNull Database database) {
        Tables tables = database.getTables();
        if (tables == null) {
            //Return if there are no tables
            return;
        }

        List<Table> tableList = tables.getTableList();
        if (tableList == null) {
            //Return if there are no tables
            return;
        }

        for (Table table : tableList) {
            Columns columns = table.getColumns();
            List<Column> columnList;
            List<String> listOfColumnNames = null;
            if (columns != null) {
                columnList = columns.getColumn();
                listOfColumnNames = getListOfColumnNames(columnList);
            }

            ForeignKeys foreignKeys = table.getForeignKeys();

            cleanForeignKeysIfNotSelected(table, listOfColumnNames, foreignKeys);

            cleanPrimaryKeysIfNotSelected(table, listOfColumnNames);
        }
    }
    /**
     * Retrieves the list of column names from the provided list of columns.
     * 
     * @param columnList 			 list of columns.
     * @return A list of column names.
     */
    @NotNull
    private List<String> getListOfColumnNames(@Nullable List<Column> columnList) {
        List<String> columns = new ArrayList<>();
        if (columnList == null) {
            return columns;
        }
        for (Column column : columnList) {
            columns.add(column.getName());
        }
        return columns;
    }
    /**
     * Removes unselected columns from the foreign key constraints of the provided table.
     * 
     * @param table             	 table object containing the foreign key constraints.
     * @param listOfColumnNames 	 list of selected column names.
     * @param foreignKeys       	 foreign key constraints to be cleaned.
     */
    private void cleanForeignKeysIfNotSelected(@NotNull Table table, @Nullable List<String> listOfColumnNames,
                                               @Nullable ForeignKeys foreignKeys) {
        List<ForeignKey> foreignKeyList;
        if (foreignKeys != null) {
            foreignKeyList = foreignKeys.getForeignKeyList();
            if (foreignKeyList != null) {
                ListIterator<ForeignKey> foreignKeyListIterator = foreignKeyList.listIterator();
                while (foreignKeyListIterator.hasNext()) {
                    ForeignKey foreignKey = foreignKeyListIterator.next();
                    if (listOfColumnNames != null && foreignKey != null) {
                        if (!listOfColumnNames.contains(foreignKey.getName())) {
                            foreignKeyListIterator.remove();
                        }
                    }

                    if (foreignKey == null) {
                        foreignKeyListIterator.remove();
                    }
                }
                //Remove unnecessary empty nodes
                if (foreignKeyList.size() == 0) {
                    table.setForeignKeys(null);
                }
            }
        }
    }
    /**
     * Removes unselected columns from the primary key constraints of the provided table.
     * 
     * @param table             	table object containing the primary key constraints.
     * @param listOfColumnNames 	 list of selected column names.
     */
    private void cleanPrimaryKeysIfNotSelected(@NotNull Table table, @Nullable List<String> listOfColumnNames) {
        PrimaryKeys primaryKeys = table.getPrimaryKeys();
        List<PrimaryKey> primaryKeyList;
        if (primaryKeys != null) {
            primaryKeyList = primaryKeys.getPrimaryKey();
            if (primaryKeyList != null) {
                ListIterator<PrimaryKey> primaryKeyListIterator = primaryKeyList.listIterator();
                while (primaryKeyListIterator.hasNext()) {
                    PrimaryKey primaryKey = primaryKeyListIterator.next();
                    if (listOfColumnNames != null && primaryKey != null) {
                        if (!listOfColumnNames.contains(primaryKey.getName())) {
                            primaryKeyListIterator.remove();
                        }
                    }

                    if (primaryKey == null) {
                        primaryKeyListIterator.remove();
                    }
                }
                //Remove unnecessary empty nodes
                if (primaryKeyList.size() == 0) {
                    table.setPrimaryKeys(null);
                }
            }
        }
    }
}
