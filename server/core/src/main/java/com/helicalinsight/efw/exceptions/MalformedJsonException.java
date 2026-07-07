package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 21-Dec-14.
 *
 * @author Rajasekhar
 */
public class MalformedJsonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MalformedJsonException(String message) {
        super(message);
    }

    public MalformedJsonException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
