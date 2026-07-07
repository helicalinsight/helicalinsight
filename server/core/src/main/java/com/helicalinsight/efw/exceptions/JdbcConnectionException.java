package com.helicalinsight.efw.exceptions;

public class JdbcConnectionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JdbcConnectionException(String message) {
        super(message);
    }

    public JdbcConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcConnectionException(Throwable throwable) {
        super(throwable);
    }
}
