package com.helicalinsight.adhoc.genericsql;

/**
 * Represents a context for executing SQL queries.
 * Created by helical021 on 11/14/2019.
 */
public class HISqlContext {
	/**
     * Retrieves the SQL query context associated with this HISqlContext.
     * @return The SQL query context.
     */
    public SqlQueryContext getContext() {
        return context;
    }

    private SqlQueryContext context;
}
