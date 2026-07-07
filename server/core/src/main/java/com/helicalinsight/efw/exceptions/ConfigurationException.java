package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
