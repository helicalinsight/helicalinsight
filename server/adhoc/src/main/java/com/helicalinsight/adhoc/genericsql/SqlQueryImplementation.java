package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

/**
 * Implementation of the {@link SqlQuery} interface that generates SQL queries based on the provided context.
 * This class handles the construction of the SQL query by utilizing various components such as the select statement,
 * from clause, and constraints.
 * 
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
public final class SqlQueryImplementation implements SqlQuery {

    private SqlQueryContext context;
    private Select select;
    private FromClause fromClause;
    private Constraints constraints;

    /**
     * To avoid NPE setContext sets the other fields
     */
    public SqlQueryImplementation() {
    }

    public SqlQueryContext getContext() {
        return context;
    }

    public void setContext(SqlQueryContext context) {
        this.context = context;
        this.select = new SelectStatement(context);
        this.fromClause = fromClauseImplementation();
        this.constraints = new ConstraintsImplementation(context);
    }
    /**
     * Creates the appropriate implementation of the FROM clause based on the number of requested tables.
     * @return The constructed FromClause object.
     */
    @NotNull
    private FromClause fromClauseImplementation() {
        int size = this.context.getRequestedTables().size();
        FromClause fromClause;
        if (context.getUnWrapSelect()) {
            return new FromClauseWithoutJoins(this.context);
        }
        if (size == 0) {
            throw new QueryBuilderException("Couldn't detect any table(s) that can be used in the from " + "clause of" +
                    " SQL. Please choose at least one column.");
        } else if (size == 1) {
            fromClause = new FromClauseWithoutJoins(this.context);
        } else {
            fromClause = new FromClauseWithJoins(this.context);
        }
        return fromClause;
    }
    /**
     * Prepares the SQL query by combining the select statement, from clause, and constraints.
     * @return The prepared SQL query string.
     */
    @Override
    public String prepare() {
        if (this.context.getUnWrapSelect()) {
            return this.fromClause.from(this.select.select()).trim();
        }
        return this.constraints.constrain(this.fromClause.from(this.select.select()).trim());
    }
    /**
     * Specifies whether this SQL query implementation is thread-safe for caching purposes.
     * @return {@code false} as the implementation is not thread-safe.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }
}
