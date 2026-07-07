package com.helicalinsight.datasource.managed;

/**
 * Created by user on 3/23/2016.
 *
 * @author Rajasekhar
 */
class JdbcQueryTimeoutException extends RuntimeException {

    public JdbcQueryTimeoutException(String message) {
        super(message);
    }
}
