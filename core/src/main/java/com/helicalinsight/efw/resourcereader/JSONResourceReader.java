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

import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.DirectoryLoader;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.validator.DirectoryValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * this class implements the IRsource interface and overload the getResources
 * method, the responsibility of this class convert the directories which is
 * read from root folder to JSONArray String return the string
 *
 * @author Muqtar Ahmed
 */
public class JSONResourceReader implements IResourceReader {

    private static final Logger logger = LoggerFactory.getLogger(JSONResourceReader.class);

    /**
     * root path
     */
    private String path;

    /**
     * JSONObject reference
     */

    private JSONObject visibleExtensions;

    /**
     * Overloaded method get the directories and convert into JSONArray and
     * return the string
     */

    public String getResources() throws ApplicationException, UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        JSONArray jsonArray = null;
        DirectoryValidator dValidator = new DirectoryValidator();
        dValidator.setDirectory(path);
        if (dValidator.isDirectoryPresent()) {
            if (!dValidator.isDirectoryEmpty()) {
                DirectoryLoader dirLoader = new DirectoryLoader(getVisibleExtensions());
                List<Map<String, String>> dirList = dirLoader.getSolutionDirectory(getPath(),
                        getVisibleExtensions().getBoolean("isRequestedRecursive"));
                jsonArray = ApplicationUtilities.getJSONArray(dirList);
                if (logger.isDebugEnabled()) {
                    logger.debug("The return value from the directory loader is " + jsonArray);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Directory is empty");
                }
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Directory is not present");
            }
        }
        if (jsonArray != null) {
            return jsonArray.toString();
        } else {
            return "[]";
        }
    }

    /**
     * getter method for JSONObject
     */

    public JSONObject getVisibleExtensions() {
        return visibleExtensions;
    }

    /**
     * setter method for JSONObject
     */
    public void setVisibleExtensions(JSONObject visibleExtensions) {
        this.visibleExtensions = visibleExtensions;
    }

    /**
     * getter method for root path
     */

    public String getPath() {
        return path;
    }

    /**
     * setter method for root path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
