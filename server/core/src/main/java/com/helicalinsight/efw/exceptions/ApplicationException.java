package com.helicalinsight.efw.exceptions;

/**
 * <p>
 * An Application exception is thrown when some of the well known configurations
 * of the project are not intact
 * </p>
 *
 * @author Unknown
 * @since 1.0
 */
public class ApplicationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with a custom error message
     *
     * @param exceptionMessage The custom error message
     */
    public ApplicationException(String exceptionMessage) {
        super(exceptionMessage);
    }

    /**
     * No-arg constructor
     */
    public ApplicationException() {
    }
}
