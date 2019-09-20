/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.resourcereader;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this is sub class of AbstractResourceReader the responsibility of this is
 * create in instance of passed class and set the values to IResource interface
 *
 * @author Muqtar Ahmed
 */

public class ResourceReaderFactory extends AbstractResourceReader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceReaderFactory.class);

    /**
     * this is overloaded method create the instance of passed class, set the
     * values to interface and return the interface
     */
    public IResourceReader getIResource(String resource, String fileType, String objectClass, String path,
                                        JSONObject visibleExtensions) {
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