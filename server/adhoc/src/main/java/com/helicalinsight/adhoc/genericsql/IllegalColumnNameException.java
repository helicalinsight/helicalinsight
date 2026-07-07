package com.helicalinsight.adhoc.genericsql;

/**
 * Exception thrown when an illegal column name is encountered during SQL query generation.
 * Created by author on 08-03-2015.
 * @author Rajasekhar
 */
class IllegalColumnNameException extends RuntimeException {
    private static final long serialVersionUID = 5187759605873312470L;

    public IllegalColumnNameException(String message) {
        super(message);
    }

}
