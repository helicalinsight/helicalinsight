package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * class MetadataStore implements {@link IMetadataStore}  
 * This class represents a store for metadata information.
 * Created by author on 08-03-2015.
 * @author Rajasekhar
 */
public final class MetadataStore implements IMetadataStore {

    private static final String VIEW = "view";
    @NotNull
    //Consists of all the columns from the views(user created views)
    private final List<String> derivedTablesColumnsList;
    @NotNull
    //Consists of all the original names of the columns
    private final List<String> fullyQualifiedColumnsList;
    @NotNull
    //Consists of the original name of the column and the alias name
    private final Map<String, String> fullyQualifiedColumnsMap;
    //Consists of the original name of the column and the alias name
    private final Map<String, Set<String>> aliasToOriginalsSetMapping;
    //All column aliases
    private final Set<String> allTablesAndColumnsSet;
    //Map of full column names and data types
    private final Map<String, String> allColumnsDataTypesMap;
    private final Map<String, String> fullyQualifiedNamesToAliasNamesMap;

    private MetadataStore(@NotNull Metadata metadata) {
        this.fullyQualifiedColumnsMap = new HashMap<>();
        this.aliasToOriginalsSetMapping = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.fullyQualifiedColumnsList = new ArrayList<>();
        this.derivedTablesColumnsList = new ArrayList<>();
        this.allTablesAndColumnsSet = new TreeSet<>();
        this.allColumnsDataTypesMap = new HashMap<>();
        this.fullyQualifiedNamesToAliasNamesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        updateStore(metadata);
    }

    public static MetadataStore createMetadataStore(@NotNull Metadata metadata) {
        return new MetadataStore(metadata);
    }
    /**
     * Updates the metadata store with information from the provided Metadata object.
     * Populates data structures with details of database tables, columns, aliases, and data types.
     * Handles derived tables (views) and maintains sets for all tables and columns.
     *
     * @param metadata 		 Metadata object containing the metadata information which provides database, tables.
     */
    private void updateStore(@NotNull Metadata metadata) {
        Database database = metadata.getDatabase();
        String databaseName = database.getName();

        List<String> originalTables = AdhocUtils.allVertices(database);

        Tables tables = database.getTables();
        if (tables != null) {
            List<Table> databaseTables = tables.getTableList();
            if (databaseTables != null) {
                for (String string : originalTables) {
                    for (Table table : databaseTables) {
                        if (table.getName().equals(string)) {
                            String tableOriginal;
                            String tableAlias;
                            if ("".equals(databaseName) || (databaseName == null)) {
                                tableOriginal = table.getName();
                                tableAlias = table.getAliasName();
                            } else {
                                tableOriginal = databaseName + "." + table.getName();
                                tableAlias = databaseName + "." + table.getAliasName();
                            }
                            Columns columns = table.getColumns();
                            if (columns != null) {
                                List<Column> columnList = columns.getColumn();
                                boolean isDerived = false;
                                if (VIEW.equalsIgnoreCase(table.getType())) {
                                    isDerived = true;
                                }

                                //Add table names to entities
                                this.allTablesAndColumnsSet.add(table.getAliasName());
                                this.allTablesAndColumnsSet.add(table.getName());
                                if(columnList!=null)
                                for (Column column : columnList) {
                                    String columnName = column.getOriginalName()!=null?column.getOriginalName():column.getName();
                                    String original = tableOriginal + "." + columnName;
                                    String columnAliasName = column.getAliasName();
                                    String alias = tableAlias + "." + columnAliasName;

                                    this.fullyQualifiedColumnsList.add(original);
                                    this.fullyQualifiedColumnsMap.put(original, alias);

                                    //Add the original, alias to the set
                                    this.allTablesAndColumnsSet.add(columnAliasName);
                                    this.allTablesAndColumnsSet.add(columnName);

                                    Set<String> originalsSet = getSet(original, columnAliasName);
                                    Set<String> set = getSet(original, columnName);

                                    this.aliasToOriginalsSetMapping.put(columnAliasName, originalsSet);
                                    this.aliasToOriginalsSetMapping.put(columnName, set);

                                    if (isDerived) {
                                        this.derivedTablesColumnsList.add(original);
                                    }

                                    this.allColumnsDataTypesMap.put(original, column.getType());

                                    this.fullyQualifiedNamesToAliasNamesMap.put(original, columnAliasName);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Set<String> getSet(String original, String columnAliasName) {
        Set<String> originalSet = this.aliasToOriginalsSetMapping.get(columnAliasName);
        if (originalSet == null) {
            originalSet = new HashSet<>();
        }
        originalSet.add(original);
        return originalSet;
    }

    /**
     * @return Returns the mapping of the original fully qualified column names to alias names
     */
    @Override
    @NotNull
    public Map<String, String> getFullyQualifiedColumnsMap() {
        return fullyQualifiedColumnsMap;
    }

    /**
     * @return Returns a list of all the original column names fully qualified
     */
    @Override
    @NotNull
    public List<String> getFullyQualifiedColumnsList() {
        return fullyQualifiedColumnsList;
    }

    /**
     * Returns the fully qualified names of the views stored in the metadata
     *
     * @return Returns a list of all views stored in the metadata
     */
    @Override
    @NotNull
    public List<String> getDerivedTablesColumnsList() {
        return derivedTablesColumnsList;
    }

    /**
     * @return Returns Set of all original, aliases of columns and tables
     */
    @Override
    public Set<String> getAllTablesAndColumnsSet() {
        return allTablesAndColumnsSet;
    }

    /**
     * @return Returns a map of column names (not fully qualified) to Set of fully qualified
     * column names
     */
    @Override
    public Map<String, Set<String>> getAliasToOriginalsSetMapping() {
        return aliasToOriginalsSetMapping;
    }

    /**
     * A map of original column names and their corresponding data types
     *
     * @return A HashMap of original column names and their data types
     */
    @Override
    public Map<String, String> getAllColumnsDataTypesMap() {
        return allColumnsDataTypesMap;
    }


    /**
     * A map of original column names and their corresponding alias names
     *
     * @return A HashMap of original column names and their alias names
     */
    @Override
    public Map<String, String> getFullyQualifiedNamesToAliasNamesMap() {
        return fullyQualifiedNamesToAliasNamesMap;
    }
}