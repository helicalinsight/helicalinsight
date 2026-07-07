package com.helicalinsight.efw.resourcereader;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this is sub class of AbstractResourceReader the responsibility of this is
 * create in instance of passed class and set the values to IResource interface
 *
 * @author muqtar ahmed
 * @version 1.1
 * @since 1.0
 */

public class ResourceReaderFactory extends AbstractResourceReader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceReaderFactory.class);

    /**
     * this is overloaded method create the instance of passed class, set the
     * values to interface and return the interface
     */
    public IResourceReader getIResource(String resource, String fileType, String objectClass, String path,
                                        JsonObject visibleExtensions) {
        IResourceReader resourceReader = null;
        if (resource.equalsIgnoreCase(fileType)) {
            if (ApplicationUtilities.isClass(objectClass)) {
                resourceReader = (IResourceReader) FactoryMethodWrapper.getUntypedInstance(objectClass);
                resourceReader.setPath(path);
                resourceReader.setVisibleExtensions(visibleExtensions);
            } else {
                logger.error(resource, " and ", fileType, " are not equal!");
            }
        }
        return resourceReader;
    }
}