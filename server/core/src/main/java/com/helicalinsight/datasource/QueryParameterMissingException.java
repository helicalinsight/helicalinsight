package com.helicalinsight.datasource;

/**
 * QueryParameterMissingException extends {@link RuntimeException}
 * This exception is thrown when a default parameter value is missing, and the query cannot be processed. 
 * Created by Somen on 8/6/2015.
 */
public class QueryParameterMissingException extends RuntimeException {
    public QueryParameterMissingException(String message) {
        super(message);
    }
}
