package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 25-Dec-14.
 *
 * @author Rajasekhar
 */
public class RuntimeIOException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RuntimeIOException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RuntimeIOException(String message) {
        super(message);
    }
}
