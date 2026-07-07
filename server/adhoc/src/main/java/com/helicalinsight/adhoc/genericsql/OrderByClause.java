package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

import java.util.List;
import java.util.Map;

/**
 * Represents an order by clause in an SQL query.
 * This class is responsible for generating the order by clause based on the provided context and query.
 * Created by author on 10-04-2015.
 * @author Rajasekhar
 */
final class OrderByClause {

    private final JsonArray orderByJson;
    @NotNull
    private final SqlQueryContext context;
    @NotNull
    private final List<String> derivedTableColumns;
    private final String query;

    public OrderByClause(@NotNull SqlQueryContext context, String query) {
        this.query = query;
        this.context = context;
        this.derivedTableColumns = this.context.getDerivedTableColumns();
        this.orderByJson = context.getFormData().getAsJsonObject("functions").getAsJsonArray("orderBy");
    }
    /**
     * Generates the order by clause based on the provided context and query.
     * @return The SQL query with the order by clause appended.
     */
    @NotNull
    public String order() {
        String dialect = this.context.getMetadata().getConnectionDetails().getDialect();
        boolean isStrictANSI = GroupByClause.followsStrictANSIStandard(dialect);
        JsonObject formData = this.context.getFormData();
        JsonObject selectedColumnsMap = formData.getAsJsonObject("selectedColumnsMap");
        String orderClause = "";
        int count = 0;
        Map<String, String> aliasColumnMap = this.context.getAliasColumnMap();
        for (JsonElement object : this.orderByJson) {
            JsonObject json = object.getAsJsonObject();
            boolean useColumn = GsonUtility.optBoolean(json,"orderByColumn");

            String alias = json.get("alias").getAsString();
            String order = json.get("order").getAsString();
            if (useColumn) {
                alias = aliasColumnMap.get(alias);
            }
            alias = AdhocUtils.sanitizeStringIfStartsWithDot(alias);
            if (this.derivedTableColumns.contains(alias)) {
                alias = AdhocUtils.stripDatabaseName(alias);
            }
            if (isStrictANSI) {
                if (selectedColumnsMap.has("alias")) {
                    throw new IllegalStateException("Column not found in selected columns mapping. Illegal State.");
                }
                String expression = GsonUtility.optString(selectedColumnsMap,alias);
                if (expression != null && expression.isEmpty()) {
                    expression = alias;
                }

                if (count == 0) {//First
                    if (!this.context.hasParenthesis(expression)) {
                        orderClause = orderClause + this.context.doApplyQuotes(expression) + " " + order + ", ";
                    } else {
                        orderClause = orderClause + this.context.quotes(expression) + " " + order + ", ";
                    }
                } else {
                    if (!this.context.hasParenthesis(expression)) {
                        orderClause = orderClause + this.context.doApplyQuotes(expression) + " " + order + ", ";
                    } else {
                        orderClause = orderClause + this.context.quotes(expression) + " " + order + ", ";
                    }
                }

            } else {
                if (count == 0) {
                    if (this.context.hasParenthesis(alias)) {
                        orderClause = this.context.doApplyQuotes(alias) + " " + order + ", ";
                    } else {
                        orderClause = this.context.quotes(alias) + " " + order + ", ";
                    }
                } else {
                    if (this.context.hasParenthesis(alias)) {
                        orderClause = orderClause + this.context.doApplyQuotes(alias) + " " + order + ", ";
                    } else {
                        orderClause = orderClause + this.context.quotes(alias) + " " + order + ", ";
                    }
                }
            }
            count++;

        }
        orderClause = orderClause.substring(0, orderClause.length() - 2);
        orderClause = removeDistinct(orderClause);

        return this.query + " \norder by\n\t" + orderClause;
    }
    /**
     * Removes the "distinct" keyword from the specified string.
     *
     * @param str 			 string from which to remove the "distinct" keyword.
     * @return The string with the "distinct" keyword removed.
     */
    private String removeDistinct(String str) {
        if (str.contains("distinct(") || str.contains("DISTINCT(")) {
            str = str.replaceAll("(?i)distinct\\(", "(");
        }
        return str;
    }


}
