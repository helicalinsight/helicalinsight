package com.helicalinsight.adhoc.metadata.genericdb;

/**
 * DuplicateColumnException class extends {@link RuntimeException}
 * If duplicate columns are present then this exception will be thrown .
 * @author Somen
 * Created by helical021 on 7/6/2018.
 */
public class DuplicateColumnException extends RuntimeException {
    public DuplicateColumnException(String message) {
        super(message);
    }
}
