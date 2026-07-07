package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 22-05-2015.
 *
 * @author Rajasekhar
 */
public class EfwException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EfwException(String message) {
        super(message);
    }

    public EfwException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
