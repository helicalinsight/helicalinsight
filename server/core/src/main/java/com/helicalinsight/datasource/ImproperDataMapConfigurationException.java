package com.helicalinsight.datasource;

/**
 * ImproperDataMapConfigurationException 
 * This exception extends {@link RuntimeException} and is typically thrown when a duplicate DataMap ID is found
 * in the Efwd file, indicating an incorrect configuration.
 * Created by Somen on 6/28/2015.
 */
public class ImproperDataMapConfigurationException extends RuntimeException {
    public ImproperDataMapConfigurationException(String message) {
        super(message);
    }
}
