package com.helicalinsight.datasource;

/**
 * DuplicateEntryException extends {@link RuntimeException}
 * The purpose of the class is to throw an error when a duplicate entry is registered with the same identifier. 
 * @author Somen
 * Created by helical021 on 2/3/2017.
 */
public class DuplicateEntryException extends RuntimeException {
    public DuplicateEntryException(String message) {
        super(message);
    }
}
