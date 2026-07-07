package com.helicalinsight.efw.exceptions;

/**
 * @author Rajesh
 *         Created by helical019 on 4/22/2019.
 */
public class EfwceException extends RuntimeException {

    private static final long serialVersionUID = 6153629785582904829L;

    public EfwceException(String message) {
        super(message);
    }

    public EfwceException(String message, Exception exception) {
        super(message, exception);
    }

    public EfwceException(Throwable throwable) {
        super(throwable);
    }

}
