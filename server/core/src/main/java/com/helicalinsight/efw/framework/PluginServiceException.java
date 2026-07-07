package com.helicalinsight.efw.framework;

/**
 * Created by user on 2/1/2017.
 *
 * @author Rajasekhar
 */
class PluginServiceException extends RuntimeException {
    public PluginServiceException(Exception exception) {
        super(exception);
    }

    public PluginServiceException(String message, Exception exception) {
        super(message, exception);
    }
}
