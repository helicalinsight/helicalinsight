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
 * this class implements the IResource interface and overload the getResources
 * method, the responsibility of this class convert the directories which is
 * read from root path to JSONArray String return the string
 *
 * @author Muqtar Ahmed
 */
public class XMLResourceReader implements IResourceReader {

    private static final Logger logger = LoggerFactory.getLogger(XMLResourceReader.class);

    /**
     * root path
     */
    private String path;

    /**
     * JSONObject reference
     */
    private JSONObject visibleExtensions;

    /**
     * overloaded method get the directories and convert into JSONArray and
     * return the string
     */
    public String getResources() throws ApplicationException, UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        JSONArray jsonArray;
        DirectoryValidator directoryValidator = new DirectoryValidator();
        logger.info("The requested resource is {} ", path);
        directoryValidator.setDirectory(path);
       /* if (directoryValidator.isDirectoryPresent()) {
            if (!directoryValidator.isDirectoryEmpty()) {*/
        DirectoryLoader directoryLoader = new DirectoryLoader(getVisibleExtensions());

        Object recursiveObject = getVisibleExtensions().get("isRequestedRecursive");
        boolean isRequestedRecursive = recursiveObject == null ? false : (Boolean) recursiveObject;
        this.getVisibleExtensions().remove("isRequestedRecursive");

        List<Map<String, String>> listOfDirectories = directoryLoader.getSolutionDirectory(getPath(),
                isRequestedRecursive);
        jsonArray = ApplicationUtilities.getJSONArray(listOfDirectories);
        logger.debug("The json of listOfDirectories = {} ", jsonArray);
           /* } else {
                logger.info("The solution repository directory is empty.");
            }
        } else {
            logger.info("The solution repository directory is not present.");
        }*/
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
