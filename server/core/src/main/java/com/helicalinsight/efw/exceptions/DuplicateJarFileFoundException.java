package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 10-Jan-15.
 *
 * @author Rajasekhar
 */
public class DuplicateJarFileFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateJarFileFoundException(String message) {
        super(message);
    }
}
