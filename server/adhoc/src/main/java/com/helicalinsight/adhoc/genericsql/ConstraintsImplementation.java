package com.helicalinsight.adhoc.genericsql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation class for applying constraints to SQL queries.
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
final class ConstraintsImplementation implements Constraints {

    private static final Logger logger = LoggerFactory.getLogger(ConstraintsImplementation.class);
    private final SqlQueryContext context;

    public ConstraintsImplementation(SqlQueryContext context) {
        this.context = context;
    }
    /**
     * Applies constraints such as LIMIT, OFFSET, ORDER BY, GROUP BY, HAVING, and WHERE to the SQL query.
     * @param query 	 SQL query to which constraints are applied.
     * @return The modified SQL query with applied constraints.
     */
    public String constrain(String query) {
        return limit(orderBy(having(groupBy(where(query)))));
    }
    /**
     * Applies LIMIT and OFFSET constraints to the SQL query if requested.
     * @param query 		 SQL query to which LIMIT and OFFSET are applied.
     * @return The modified SQL query with applied LIMIT and OFFSET.
     */
    private String limit(String query) {
        if (this.context.isLimitRequested()) {
            String driverClassName = this.context.getDriverClassName();
            if (driverClassName != null) {
                LimitOffsetAppender limitOffsetAppender = new LimitOffsetAppender(query, this.context);
                String queryWithLimitAndOffset = limitOffsetAppender.setLimitAndOffSet();
                if (queryWithLimitAndOffset != null) {
                    query = queryWithLimitAndOffset.trim();
                } else {
                    logger.error("The Javascript of " + driverClassName + " has returned null.");
                }
            }
        }
        return query;
    }
    /**
     * Applies ORDER BY constraint to the SQL query if requested.
     * @param query 		 SQL query to which ORDER BY is applied.
     * @return The modified SQL query with applied ORDER BY.
     */
    private String orderBy(String query) {
        if (this.context.isApplyOrderBy()) {
            OrderByClause orderByClause = new OrderByClause(this.context, query);
            return orderByClause.order();
        }
        return query;
    }
    /**
     * Applies HAVING constraint to the SQL query if requested.
     * @param query 		 SQL query to which HAVING is applied.
     * @return The modified SQL query with applied HAVING.
     */
    private String having(String query) {
        if (this.context.isApplyHaving()) {
            HavingClause havingClause = new HavingClause(this.context);
            query = query + " " + havingClause.having();
        }
        return query;
    }
    /**
     * Applies GROUP BY constraint to the SQL query if requested.
     * @param query 		 SQL query to which GROUP BY is applied.
     * @return The modified SQL query with applied GROUP BY.
     */
    private String groupBy(String query) {
        if (this.context.isApplyGroupBy()) {
            GroupByClause groupByClause = new GroupByClause(this.context);
            query = query + " " + groupByClause.group();
        }
        return query;
    }
    /**
     * Applies WHERE constraint to the SQL query if requested.
     * @param query 		 SQL query to which WHERE is applied.
     * @return The modified SQL query with applied WHERE.
     */
    private String where(String query) {
        String preFilters = this.context.getPreConstructedFilters().preConstructedWhereClause(this.context
                .getRequestedTables());
        boolean applyWhere = this.context.isApplyWhere();
        if (applyWhere) {
            WhereClause generator = new WhereClause(this.context);
            String where = generator.where();
            query = query + " " + where;
        }

        if (!("".equals(preFilters))) {
            query = addPreFilters(query, preFilters, applyWhere);
        }
        return query;
    }
    /**
     * Adds pre-constructed WHERE clause to the SQL query if available and WHERE is requested.
     * @param query 			 SQL query to which pre-filters are added.
     * @param preFilters 		 pre-constructed WHERE clause.
     * @param applyWhere 		 Flag indicating whether WHERE is requested or not.
     * @return modified SQL query with added pre-filters.
     */
    private String addPreFilters(String query, String preFilters, boolean applyWhere) {
        return query + ((!applyWhere) ? (" \nwhere\n\t(" + preFilters + ")") : (" and (") +
                preFilters + ")");
    }
}
