package com.helicalinsight.adhoc.genericsql;

/**
 * Builds and returns the SELECT clause of the SQL query.
 * Created by author on 10/15/2015.
 *
 * @author Rajasekhar
 */
interface Select {
	/**
     * Builds and returns the SELECT clause of the SQL query.
     *
     * @return the SELECT clause of the SQL query
     * @throws QueryBuilderException if no columns are present in the select clause
     */
    String select();

}
