package com.helicalinsight.efw.framework;

/**
 * Created by author on 21-01-2015.
 *
 * @author Rajasekhar
 */
class FrameworkException extends RuntimeException {
    private static final long serialVersionUID = -8860291644469902479L;

    public FrameworkException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FrameworkException(String message) {
        super(message);
    }
}
