package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Represents the implementation of the FROM clause in a SQL query without any JOINs.
 * This class is responsible for constructing the FROM clause based on the specified tables in the context.
 * If there are no derived tables or aliases involved, it directly uses the table names from the context.
 * If derived tables or aliases are present, it constructs appropriate sub-queries or uses aliases.
 * it extends {@link  AbstractFromImpl} and implements {@link FromClause}
 * 
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
final class FromClauseWithoutJoins extends AbstractFromImpl implements FromClause {

    private final SqlQueryContext context;
    private StringBuilder query;

    public FromClauseWithoutJoins(SqlQueryContext context) {
        this.context = context;
        this.query = new StringBuilder();
    }
    /**
     * Retrieves the original table name from the duplicate table map if available.
     * 
     * @param table    		 table name to be checked for aliasing.
     * @param formData 		 JSON object containing form data.
     * @return original table name if found; otherwise, returns the input table name.
     */
    public static String getOriginalTableName(String table, JsonObject formData) {
        JsonObject duplicateTableMap = formData.getAsJsonObject("duplicateTableMap");
        return GsonUtility.optString(duplicateTableMap,table);
    }
    /**
     * Constructs and returns the FROM clause for the SQL query based on the specified tables.
     * 
     * @param query 		 base SQL query to which the FROM clause will be appended.
     * @return The complete SQL query string with the FROM clause appended.
     */
    @Override
    public String from(String query) {
        if (!context.getUnWrapSelect()) {
            this.query.append(query).append(" \nfrom\n\t");
        }
        String table = this.context.getRequestedTables().get(0);
        table = AdhocUtils.sanitizeStringIfStartsWithDot(table);
        String tableName = this.context.getTableName(table);
        if (this.context.getDerivedTableNames().contains(tableName)) {
            this.query.append(subQuery(tableName, this.context)).append(" ");
        } else {
            JsonObject formData = this.context.getFormData();
            String originalTableName = getOriginalTableName(table, formData);
            if (originalTableName != null && !originalTableName.isEmpty()) {
                String databaseName = this.context.getDatabaseName();
                String quotes = databaseName.equals("") ? "" : this.context.quotes(databaseName);
                this.query.append(quotes).append(databaseName.equals("") ? "" : ".").append(this.context.getOpenQuote()).append(this.context.getTableName(originalTableName)).append(this.context.getCloseQuote()).append(" ");
                if(originalTableName.contains("_#1_")) this.query.append(" ").append(this.context.quotes(this.context.getAliasName(table)));                	
                else this.query.append(" ").append(this.context.quotes(this.context.getTableName(table)));
                
            } else {
                this.query.append(this.context.quotes(table)).append(" ");
                if(table.contains("_#1_")) this.query.append(this.context.quotes(this.context.getAliasName(table))).append(" ");

            }
        }
        return this.query.toString();
    }
}
