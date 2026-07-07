package com.helicalinsight.efw.exceptions;

/**
 * Created by Rajesh on 12/12/2018.
 */
public class DataSourceConnectionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataSourceConnectionNotFoundException(String message) {
        super(message);
    }

}

