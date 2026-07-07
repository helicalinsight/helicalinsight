package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 26-Jan-15.
 *
 * @author Rajasekhar
 */
public class EfwServiceException extends RuntimeException {
    private static final long serialVersionUID = 6153629785582904829L;

    public EfwServiceException(String message) {
        super(message);
    }

    public EfwServiceException(String message, Exception exception) {
        super(message, exception);
    }

    public EfwServiceException(Throwable throwable) {
        super(throwable);
    }
}
