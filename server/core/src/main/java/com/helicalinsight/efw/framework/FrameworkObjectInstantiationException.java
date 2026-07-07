package com.helicalinsight.efw.framework;

/**
 * Created by author on 18-01-2015.
 *
 * @author Rajasekhar
 */
class FrameworkObjectInstantiationException extends RuntimeException {
    private static final long serialVersionUID = -2564629343949268976L;

    public FrameworkObjectInstantiationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
