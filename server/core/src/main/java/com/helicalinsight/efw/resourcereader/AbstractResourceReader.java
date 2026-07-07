package com.helicalinsight.efw.resourcereader;

import com.google.gson.JsonObject;

/**
 * this is abstract class for implementing the abstract factory design pattern
 * for resources like json, xml
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

public abstract class AbstractResourceReader {

    /**
     * abstract method which return the IResource interface type
     *
     * @param resource          resource type
     * @param fileType          file type
     * @param objectClass       class type
     * @param path              root path
     * @param visibleExtensions jsonobject
     * @return IResource interface
     */

    public abstract IResourceReader getIResource(String resource, String fileType, String objectClass, String path,
                                                 JsonObject visibleExtensions);
}
