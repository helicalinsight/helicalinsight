package com.helicalinsight.adhoc.genericsql;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Represents a class for handling the generation of the GROUP BY clause in SQL queries.
 * This class provides methods to construct the GROUP BY clause based on the provided metadata and user selections.
 * 
 * Created by author on 10-04-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
final class GroupByClause {

    private final JsonArray groupByJson;

    @NotNull
    private final SqlQueryContext context;
    @NotNull
    private final List<String> derivedTableColumns;

    public GroupByClause(@NotNull SqlQueryContext context) {
        this.context = context;
        this.derivedTableColumns = this.context.getDerivedTableColumns();
        JsonObject formData = this.context.getFormData();
        this.groupByJson = formData.getAsJsonObject("functions").getAsJsonArray("groupBy");
    }
    /**
     * Generates the GROUP BY clause for the SQL query based on the configured metadata and user selections.
     * @return A string representing the generated GROUP BY clause.
     */
    @NotNull
    public String group() {
        String dialect = this.context.getMetadata().getConnectionDetails().getDialect();
        boolean isStrictANSI = followsStrictANSIStandard(dialect);
        JsonObject formData = this.context.getFormData();
        JsonObject selectedColumnsMap = formData.getAsJsonObject("selectedColumnsMap");
        String query = "\ngroup by\n\t";
        int counter = 0;
        for (JsonElement object : this.groupByJson) {
            JsonObject json = object.getAsJsonObject();

            String alias = json.get("column").getAsString();
            alias = AdhocUtils.sanitizeStringIfStartsWithDot(alias);

            if (this.derivedTableColumns.contains(alias)) {
                alias = AdhocUtils.stripDatabaseName(alias);
            }

            if (isStrictANSI) {
                if (selectedColumnsMap.has("alias")) {
                    throw new IllegalStateException("Column not found in selected columns mapping. Illegal State.");
                }

                String expression = GsonUtility.optString(selectedColumnsMap,alias);
                if(expression !=null && expression.isEmpty()){
                    expression=alias;
                }

                if (counter == 0) {//First
                    if (!this.context.hasParenthesis(expression)) {
                        query = query + this.context.doApplyQuotes(expression);
                    } else {
                        query = query + this.context.quotes(expression);
                    }
                } else {
                    if (!this.context.hasParenthesis(expression)) {
                        query = query + ", " + this.context.doApplyQuotes(expression);
                    } else {
                        query = query + ", " + this.context.quotes(expression);
                    }
                }
            } else {
                if (counter == 0) {//First
                    if (!this.context.hasParenthesis(alias)) {
                        query = query + this.context.doApplyQuotes(alias);
                    } else {
                        query = query + this.context.quotes(alias);
                    }
                } else {
                    if (!this.context.hasParenthesis(alias)) {
                        query = query + ", " + this.context.doApplyQuotes(alias);
                    } else {
                        query = query + ", " + this.context.quotes(alias);
                    }
                }
            }
            counter++;
        }
        return query;
    }
    /**
     * Checks whether the specified SQL dialect follows the strict ANSI SQL standard for GROUP BY clauses.
     *
     * @param dialect 		 SQL dialect to check.
     * @return {@code true} if the dialect follows strict ANSI SQL standard, {@code false} otherwise.
     */
    public static boolean followsStrictANSIStandard(String dialect) {
        if (dialect == null) {
            return false;
        }
        JsonObject adhocSqlSettings = JsonUtils.newGetAdhocSqlSettings();
        JsonArray ansiGroupByDialects = adhocSqlSettings.getAsJsonObject("ansiGroupByDialects").getAsJsonArray("dialect");
        return ansiGroupByDialects.contains(new JsonPrimitive(dialect));
    }
}