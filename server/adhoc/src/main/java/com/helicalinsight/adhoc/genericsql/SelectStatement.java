package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Represents a SELECT statement builder for SQL queries.
 * This class constructs the SELECT clause of the SQL query based on the provided context.
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
class SelectStatement implements Select {

    private final SqlQueryContext context;

    public SelectStatement(SqlQueryContext context) {
        this.context = context;
    }
    /**
     * Builds and returns the SELECT clause of the SQL query.
     *
     * @return the SELECT clause of the SQL query
     * @throws QueryBuilderException if no columns are present in the select clause
     */
    public String select() {
        String query = "select \n";

        if (this.context.isDistinctResults()) {
            query = "select distinct \n";
        }

        JsonObject formData = this.context.getFormData();
        formData.add("selectedColumnsMap", new JsonObject());
        JsonArray columns = formData.getAsJsonArray("columns");
        if (!this.context.isApplyAggregation()) {
            SimpleSelectFragment simpleSelectFragment = new SimpleSelectFragment(this.context);
            for (Object column : columns) {
                query = simpleSelectFragment.select(query, column);
            }
        } else {
            SelectFragmentWithAggregate selectFragmentWithAggregate = new SelectFragmentWithAggregate(this.context);
            query = query + selectFragmentWithAggregate.select();
        }

        if ("select ".equals(query)) {
            throw new QueryBuilderException("No columns are present in the select clause. Choose at least one column " +
                    "" + "or custom column.");
        }
        // Remove extra ',\n '
        query = query.substring(0, query.length() - 3);
        return query;
    }
}
