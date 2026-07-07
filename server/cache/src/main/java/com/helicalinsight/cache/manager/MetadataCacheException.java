package com.helicalinsight.cache.manager;

/**
 * Created by helical019 on 1/9/2019.
 */
public class MetadataCacheException extends RuntimeException {

    private static final long serialVersionUID = -5395002273188052378L;

    public MetadataCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataCacheException(Throwable throwable) {
        super(throwable);
    }

    public MetadataCacheException(String message) {
        super(message);
    }
}
