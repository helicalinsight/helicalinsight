package com.helicalinsight.efw.exceptions;

/**
 * @author Rajesh
 * Created by helical019 on 1/21/2019.
 */
public class TaskTimeoutException extends RuntimeException {
    public TaskTimeoutException(String message) {
        super(message);
    }
}
