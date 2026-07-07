package com.helicalinsight.efw.exceptions;

/**
 * @author Somen
 * Created by helical021 on 12/3/2017.
 */
public class DatabaseConnectionFailedException extends RuntimeException {
    public DatabaseConnectionFailedException(String message) {
        super(message);
    }
}
