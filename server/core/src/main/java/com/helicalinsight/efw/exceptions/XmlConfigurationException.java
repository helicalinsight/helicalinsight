package com.helicalinsight.efw.exceptions;

/**
 * Created by author on 12-01-2015.
 *
 * @author Rajasekhar
 */
public class XmlConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public XmlConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public XmlConfigurationException(String message) {
        super(message);
    }
}
