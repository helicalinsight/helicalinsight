package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Represents a utility class for constructing SELECT fragments with aggregate functions in SQL queries.
 * Created by author on 10-04-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
final class SelectFragmentWithAggregate {

    private final JsonArray aggregateJson;
    private final JsonArray columns;
    @NotNull
    private final StringBuilder query;
    @NotNull
    private final List<Aggregate> aggregatesList;
    @NotNull
    private final SimpleSelectFragment simpleSelectFragment;
    @NotNull
    private final SqlQueryContext context;
    @NotNull
    private final List<String> derivedTableColumns;

    public SelectFragmentWithAggregate(@NotNull SqlQueryContext context) {
        this.context = context;
        this.derivedTableColumns = this.context.getDerivedTableColumns();
        this.simpleSelectFragment = new SimpleSelectFragment(context);
        JsonObject formData = this.context.getFormData();
        this.columns = formData.getAsJsonArray("columns");
        this.aggregateJson = formData.getAsJsonObject("functions").getAsJsonArray("aggregate");
        this.aggregatesList = new ArrayList<>();
        prepareAggregates();
        this.query = new StringBuilder();
    }
    /**
     * Prepares aggregate functions based on the provided aggregate JSON.
     */
    private void prepareAggregates() {
        for (JsonElement object : this.aggregateJson) {
            JsonObject json = object.getAsJsonObject();
            Aggregate aggregate = new Aggregate();
            aggregate.setAlias((!json.has("alias") ? null : json.get("alias").getAsString()));
            String column = json.get("column").getAsString();
            column = AdhocUtils.sanitizeStringIfStartsWithDot(column);
            aggregate.setColumn(column);
            aggregate.setFunction(json.get("function").getAsString());
            if (json.has("custom")) {
                String custom = json.get("custom").getAsString();
                if (!"true".equalsIgnoreCase(custom)) {
                    throw new QueryBuilderException("The aggregate json custom key value is illegal.");
                }
                aggregate.setCustom(custom);
            }
            aggregate.setApplyBeforeAggregate(GsonUtility.optBoolean(json,"applyBeforeAggregate"));
            this.aggregatesList.add(aggregate);
        }
    }
    /**
     * Generates the SELECT fragment with aggregate functions.
     * @return SELECT fragment string.
     */
    @NotNull
    public String select() {
        for (JsonElement object : this.columns) {
            JsonObject json = object.getAsJsonObject();
            if (json.has("hidden") && "true".equalsIgnoreCase(json.get("hidden").getAsString())) {
                if (GsonUtility.optBooleanValue(json,"includeInResultset", false) == false)
                    continue;
            }
            // Get the column, if user requested aggregate function to be applied on this column,
            // delegate sql formation to applyFunction method. Else use the column as it is based
            // on whether it is custom or not
            String column = json.get("column").getAsString();
            column = AdhocUtils.sanitizeStringIfStartsWithDot(column);
            if (json.has("aggregate") && "true".equalsIgnoreCase(json.get("aggregate").getAsString())) {
                Aggregate aggregate = getAggregate(column);
                if (this.derivedTableColumns.contains(column)) {
                    column = AdhocUtils.stripDatabaseName(column);
                }
                if (json.has("databaseFunction")) {
                    column = this.context.databaseFunction(json);
                }
                applyFunction(aggregate, column);
            } else {
                this.query.append(this.simpleSelectFragment.select("", object));
            }
        }
        return this.query.toString();
    }
    /**
     * Retrieves the aggregate function for the specified column.
     *
     * @param column 		 column name.
     * @return corresponding Aggregate object.
     * @throws QueryBuilderException if the column is not present in the aggregates list.
     */
    private Aggregate getAggregate(String column) {
        Aggregate required = null;
        for (Aggregate aggregate : this.aggregatesList) {
            if (aggregate.getColumn().equals(column)) {
                required = aggregate;
                break;
            }
        }
        if (required != null) {
            this.aggregatesList.remove(required);
            return required;
        }
        throw new QueryBuilderException("The selected column is not present in the aggregates but was set to " +
                "apply aggregate function. Can not prepare valid SQL.");
    }
    /**
     * Applies the aggregate function to the specified column and constructs the SELECT fragment.
     *
     * @param aggregate 		 Aggregate object representing the aggregate function.
     * @param column    		 column name.
     */
    private void applyFunction(@NotNull Aggregate aggregate, @NotNull String column) {
        this.query.append("\t");
        String originalColumn = column;


        String aggColumn = aggregate.getColumn();

        List<String> derivedTableNames = this.context.getDerivedTableNames();
        String databaseName = this.context.getDatabaseName();
        for (String derivedTableName : derivedTableNames) {
            String expectedName = databaseName + "." + derivedTableName;
            if(aggColumn.contains(expectedName)) {
                aggColumn=aggColumn.replace(databaseName+".","");
            }

        }
        if (aggregate.getApplyBeforeAggregate()) {
            column = aggColumn;
        }

        String function = aggregate.getFunction();
        String as = "";
        if (function.contains("_")) {
            List<String> functions = Arrays.asList(function.split("_"));
            int last = functions.size() - 1;
            String complexFunction = "";
            for (int index = last; index >= 0; index--) {
                String functionName = functions.get(index);
                if (index == last) {
                    as = complexFunction + functionName + "(" + column + ")";
                    if ("true".equals(aggregate.getCustom())) {
                        complexFunction = complexFunction + functionName + "(" + column + ")";
                    } else {
                        complexFunction = complexFunction + functionName + "(" + this.context.quotes(column) + ")";
                    }
                    continue;
                }
                complexFunction = functionName + "(" + complexFunction + ")";
                as = functionName + "(" + as + ")";

            }
            if (aggregate.getApplyBeforeAggregate()) {
                as = originalColumn.replace(this.context.quotes(AdhocUtils.stripDatabaseName(aggColumn)), complexFunction);
                complexFunction = originalColumn.replace(this.context.quotes(AdhocUtils.stripDatabaseName(aggColumn)), complexFunction);
            }
            this.query.append(addAs(aggregate, complexFunction, as));
        } else {
            as = function + "(" + column + ")";
            String aggregateFunction;
            if ("true".equals(aggregate.getCustom())) {
                aggregateFunction = function + "(" + column + ")";
            } else {
                aggregateFunction = function + "(" + this.context.quotes(column) + ")";
            }
            if (aggregate.getApplyBeforeAggregate()) {
                as = originalColumn.replace(this.context.quotes(aggColumn), aggregateFunction);
                aggregateFunction = originalColumn.replace(this.context.quotes(aggColumn), aggregateFunction);
            }
            String addAs = addAs(aggregate, aggregateFunction, as);
            this.query.append(addAs);
        }
        this.query.append(",\n ");
    }
    /**
     * Adds an alias to the SELECT fragment for the specified aggregate function.
     *
     * @param aggregate      		 Aggregate object representing the aggregate function.
     * @param selectFunction 		 SELECT function string.
     * @param as             		 alias string.
     * @return The SELECT function string with alias.
     */
    private String addAs(@NotNull Aggregate aggregate, String selectFunction, String as) {
        String alias = aggregate.getAlias();
        if (alias != null) {
            this.simpleSelectFragment.saveColumn(selectFunction, alias);
            alias = this.context.as(alias);

        } else {

            alias = this.context.as(as);
            this.simpleSelectFragment.saveColumn(selectFunction, alias);
        }

        selectFunction = selectFunction + " as " + alias;
        return selectFunction;
    }
}