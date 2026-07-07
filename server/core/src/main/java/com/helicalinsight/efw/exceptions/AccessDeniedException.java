package com.helicalinsight.efw.exceptions;

/**
 * @author Somen
 *         Created by  on 9/30/2016.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
