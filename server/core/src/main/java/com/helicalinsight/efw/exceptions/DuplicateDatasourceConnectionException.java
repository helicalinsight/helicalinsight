package com.helicalinsight.efw.exceptions;

public class DuplicateDatasourceConnectionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateDatasourceConnectionException(String message) {
        super(message);
    }

}
