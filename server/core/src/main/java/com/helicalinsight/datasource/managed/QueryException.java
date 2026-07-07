package com.helicalinsight.datasource.managed;

/**
 * Created by author on 01-09-2015.
 *
 * @author Rajasekhar
 */
class QueryException extends RuntimeException {

    public QueryException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
