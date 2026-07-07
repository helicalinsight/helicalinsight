package com.helicalinsight.datasource;

/**
 * ConnectionException
 * This exception is thrown when database connection details cannot be retrieved.
 * Created by author on 27-02-2015.
 * @author Rajasekhar
 */
public class ConnectionException extends RuntimeException {

	/**
     * Constructor for ConnectionException
     * @param message The error message.
     */
    private static final long serialVersionUID = 1L;

    public ConnectionException(String message) {
        super(message);
    }
}
