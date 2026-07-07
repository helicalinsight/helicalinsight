package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 11-Jan-15.
 *
 * @author Rajasekhar
 */
public class RequiredParameterIsNullException extends RuntimeException {
    private static final long serialVersionUID = -1792826750032248953L;

    public RequiredParameterIsNullException(String message) {
        super(message);
    }

    public RequiredParameterIsNullException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RequiredParameterIsNullException(Throwable throwable) {
        super(throwable);
    }
}
