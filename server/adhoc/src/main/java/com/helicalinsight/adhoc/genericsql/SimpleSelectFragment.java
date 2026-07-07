package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;

import java.util.List;
import java.util.Map;

/**
 * Represents a fragment of a SELECT query statement, used for selecting columns in the SQL query.
 * This class handles the construction of the SELECT clause based on the user-selected columns.
 * 
 * Created by Rajasekhar on 07-05-2015.
 * @author Rajasekhar
 */
final class SimpleSelectFragment {

    private final SqlQueryContext context;
    @NotNull
    private final Map<String, String> columnsMap;
    @NotNull
    private final List<String> derivedTableColumns;

    public SimpleSelectFragment(SqlQueryContext context) {
        this.context = context;
        this.columnsMap = this.context.getColumnsMap();
        this.derivedTableColumns = this.context.getDerivedTableColumns();
    }
    /**
     * Constructs the SELECT clause of the SQL query based on the provided object representing a column.
     *
     * @param query  		 current state of the SQL query being constructed.
     * @param object 		 object representing a column , custom and alias value.
     * @return The updated SQL query with the SELECT clause.
     * @throws QueryBuilderException    if there is an issue constructing the SELECT clause.
     */
    @NotNull
    public String select(@NotNull String query, Object object) {
        JsonObject json = JsonParser.parseString(object.toString()).getAsJsonObject();
        if (json.has("hidden") && "true".equalsIgnoreCase(json.get("hidden").getAsString())) {
            if (GsonUtility.optBooleanValue(json,"includeInResultset", false) == false)
                return query;
        }
        if (json.has("custom")) {
            String customColumn = json.get("column").getAsString();
            String customValue = json.get("custom").getAsString();
            boolean equals = "true".equals(customValue);
            if (!equals) {
                throw new QueryBuilderException("The columns json has illegal custom key value.");
            }
            String alias = null;
            if (json.has("alias")) {
                alias = json.get("alias").getAsString();
            }

            if (json.has("databaseFunction")) {
                customColumn = this.context.databaseFunction(json);
            }
            String as;
            if (alias == null) {
                as = this.context.as(customColumn);
                query = query + "\t" + customColumn + " as " + as + ",\n ";
                saveColumn(customColumn, as);
            } else {
                saveColumn(customColumn, alias);
                as = this.context.as(alias);
                query = query + "\t" + customColumn + " as " + as + ",\n ";
            }
        } else {
            String column = json.get("column").getAsString();
            String alias;
            if (json.has("alias")) {
                alias = json.get("alias").getAsString();
            } else {
                alias = this.columnsMap.get(column);
            }
            column = AdhocUtils.sanitizeStringIfStartsWithDot(column);
            //Check if column is from type view
            if (this.derivedTableColumns.contains(column)) {
                column = AdhocUtils.stripDatabaseName(column);
            }

            if (json.has("databaseFunction")) {
                column = this.context.databaseFunction(json);
            }

            query = query + "\t" + this.context.quotes(column) + " as " + this.context.as(alias) + ",\n ";
            saveColumn(column, alias);
        }
        return query;
    }
    /**
     * Saves the selected column and its alias in the selectedColumnsMap.
     * This method saves the selected columns as key value pairs in a map. The key is the column alias and the value
     * is the column itself. The column may have one or more functions applied.
     * 
     * @param column 		 selected column.
     * @param alias  		 alias for the selected column.
     */
    void saveColumn(String column, String alias) {
        JsonObject selectedColumnsMap = this.context.getFormData().getAsJsonObject("selectedColumnsMap");
        GsonUtility.accumulate(selectedColumnsMap,alias, column);
    }
}
