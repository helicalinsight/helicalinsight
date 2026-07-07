package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface representing a metadata store.
 * Created by user on 11/3/2016.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public interface IMetadataStore {
	/**
     * Retrieves the mapping of fully qualified column names to their aliases.
     * @return a map of fully qualified column names to their aliases
     */
    @NotNull
    Map<String, String> getFullyQualifiedColumnsMap();
    /**
     * Retrieves a list of fully qualified column names.
     * @return a list of fully qualified column names
     */
    @NotNull
    List<String> getFullyQualifiedColumnsList();
    /**
     * Retrieves a list of columns from derived tables.
     * @return a list of columns from derived tables
     */
    @NotNull
    List<String> getDerivedTablesColumnsList();
    /**
     * Retrieves a set containing all tables and columns.
     * @return a set containing all tables and columns
     */
    Set<String> getAllTablesAndColumnsSet();
    /**
     * Retrieves a mapping of aliases to original column names.
     * @return a mapping of aliases to original column names
     */
    Map<String, Set<String>> getAliasToOriginalsSetMapping();
    /**
     * Retrieves a mapping of all columns to their data types.
     * @return a mapping of all columns to their data types
     */
    Map<String, String> getAllColumnsDataTypesMap();
    /**
     * A map of original column names and their corresponding alias names
     * @return A HashMap of original column names and their alias names
     */
    Map<String, String> getFullyQualifiedNamesToAliasNamesMap();
}
