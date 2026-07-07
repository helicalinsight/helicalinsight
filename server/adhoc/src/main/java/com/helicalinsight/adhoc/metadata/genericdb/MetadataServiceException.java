package com.helicalinsight.adhoc.metadata.genericdb;

/**
 * Exception thrown when there is an error in the metadata service .
 * 
 * Created by author on 28-02-2015.
 * @author Rajasekhar
 */
public class MetadataServiceException extends RuntimeException {
    private static final long serialVersionUID = 26833102858801101L;

    public MetadataServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MetadataServiceException(String message) {
        super(message);
    }
}
