package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.efw.framework.FrameworkObject;

/**
 * Interface representing a SQL query. and extends {@link FrameworkObject}.
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
public interface SqlQuery extends FrameworkObject {
	/**
     * Prepares the SQL query.
     * @return the prepared SQL query as a string
     */
    String prepare();
    /**
     * Sets the context for the SQL query.
     * @param context 		 SQL query context to be set
     */
    void setContext(SqlQueryContext context);
}
