package com.helicalinsight.adhoc.metadata.genericdb;

/**
 * Exception thrown when attempting to create a view with a name that already exists.
 * Created by author on 11-09-2015.
 * @author Rajasekhar
 */
public class ViewNameExistsException extends RuntimeException {

    public ViewNameExistsException(String message) {
        super(message);
    }
}
