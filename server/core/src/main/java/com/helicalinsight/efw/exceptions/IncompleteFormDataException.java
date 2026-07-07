package com.helicalinsight.efw.exceptions;

public class IncompleteFormDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IncompleteFormDataException(String message) {
        super(message);
    }

    public IncompleteFormDataException(Throwable throwable) {
        super(throwable);
    }

    public IncompleteFormDataException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
