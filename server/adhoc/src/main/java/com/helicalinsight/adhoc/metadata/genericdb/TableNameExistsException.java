package com.helicalinsight.adhoc.metadata.genericdb;

/**
 * Exception thrown when a table name already exists.
 * 
 * @author Somen
 * Created  on 7/4/2018.
 */
public class TableNameExistsException extends RuntimeException {
    public TableNameExistsException(String message) {
        super(message);
    }
}
