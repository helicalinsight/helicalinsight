package com.helicalinsight.adhoc.metadata.genericdb;

/**
 * Exception thrown when there is an error retrieving metadata.
 * Created by author on 25-02-2015.
 * @author Rajasekhar
 */
public class MetadataRetrievalException extends RuntimeException {
    private static final long serialVersionUID = -5395002273188052378L;

    public MetadataRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataRetrievalException(Throwable throwable) {
        super(throwable);
    }

    public MetadataRetrievalException(String message) {
        super(message);
    }
}
