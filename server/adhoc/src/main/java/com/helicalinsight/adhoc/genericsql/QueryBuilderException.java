package com.helicalinsight.adhoc.genericsql;

/**
 * An exception class to represent errors that occur during SQL query building.
 * Created by author on 13-03-2015.
 * @author Rajasekhar
 */
public final class QueryBuilderException extends RuntimeException {
    private static final long serialVersionUID = 4667921924571136106L;

    public QueryBuilderException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public QueryBuilderException(String message) {
        super(message);
    }

    public QueryBuilderException(Throwable throwable) {
        super(throwable);
    }
}
