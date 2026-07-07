package com.helicalinsight.efw.exceptions;

public class NoJarFileFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoJarFileFoundException(String message) {
        super(message);
    }
}
