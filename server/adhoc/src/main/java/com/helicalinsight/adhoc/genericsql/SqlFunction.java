package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonObject;

/**
 * Interface for representing SQL functions.
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
interface SqlFunction {
	/**
     * Generates SQL function based on the provided database function JSON object.
     *
     * @param databaseFunction 		 JSON object representing the database function
     * @return the SQL function as a string
     */
    String sqlFunction(JsonObject databaseFunction);

}
