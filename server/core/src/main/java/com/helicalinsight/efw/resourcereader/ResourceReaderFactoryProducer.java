package com.helicalinsight.efw.resourcereader;

/**
 * this is factory producer class of abstract factory design pattern of Resource
 *
 * @author muqtar ahmed
 * @version 1.1
 * @since 1.0
 */
public class ResourceReaderFactoryProducer {

    /**
     * this is static method which return the ProcessFactory class
     *
     * @param resource file resource
     * @param fileType file type
     * @return ProcessFactory class type
     */

    public static AbstractResourceReader getFactory(String resource, String fileType) {
        if (resource.equalsIgnoreCase(fileType)) {
            return new ResourceReaderFactory();
        }
        return null;
    }
}
