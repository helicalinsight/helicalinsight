package com.helicalinsight.efw.exceptions;

public class OperationFailedException extends RuntimeException {
    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(Throwable throwable) {
        super(throwable);
    }
}
