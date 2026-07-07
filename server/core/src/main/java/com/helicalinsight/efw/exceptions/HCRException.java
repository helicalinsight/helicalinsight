package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 11/11/2019.
 *
 * @author Rajesh
 */
public class HCRException extends RuntimeException {
    private static final long serialVersionUID = 6153629785582904829L;

    public HCRException(String message) {
        super(message);
    }

    public HCRException(String message, Exception exception) {
        super(message, exception);
    }

    public HCRException(Throwable throwable) {
        super(throwable);
    }
}
