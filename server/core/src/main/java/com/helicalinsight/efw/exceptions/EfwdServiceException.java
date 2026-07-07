package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 19-01-2015.
 *
 * @author Rajasekhar
 */
public class EfwdServiceException extends RuntimeException {
    private static final long serialVersionUID = -8190961280917887270L;

    public EfwdServiceException(String message) {
        super(message);
    }

    public EfwdServiceException(String message, Exception exception) {
        super(message, exception);
    }

    public EfwdServiceException(Exception exception) {
        super(exception);
    }
}
