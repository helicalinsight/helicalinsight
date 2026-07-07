package com.helicalinsight.efw.framework;

/**
 * Created by author on 17-Jan-15.
 *
 * @author Rajasekhar
 */
class UnSupportedObjectTypeException extends RuntimeException {
    private static final long serialVersionUID = 8203770419580512273L;

    public UnSupportedObjectTypeException(String message) {
        super(message);
    }

}
