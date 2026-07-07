package com.helicalinsight.adhoc.genericsql;

import org.hibernate.dialect.PostgreSQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Custom Hibernate dialect for Teradata databases.
 * Extends the PostgreSQLDialect and provides custom behavior for limit queries.
 */
@SuppressWarnings("deprecation")
public class TeradataHIDialect extends PostgreSQLDialect {

	private static final Logger logger =LoggerFactory.getLogger(TeradataHIDialect.class);
	/**
     * Gets the SQL query string with the LIMIT clause applied.
     * Overrides the parent method to customize limit query behavior for Teradata databases.
     *
     * @param query  		 original SQL query string.
     * @param offset 		 offset value for pagination.
     * @param limit  		 maximum number of records to retrieve.
     * @return The modified SQL query string with the LIMIT clause applied.
     */
	public String getLimitString(String query, int offset, int limit) {
		logger.debug("SqlQuery  "+query  +"   Limit  "+ limit+"  offset "+offset);
		String replaceQuery =" t1 QUALIFY row_number() over(order by CURRENT_TIMESTAMP asc ) BETWEEN 1 and 1";
		query =query.replace("foo", replaceQuery);
		
		return query;
	}
}

