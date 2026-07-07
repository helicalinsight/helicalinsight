package com.helicalinsight.efw.resourcereader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.resourceloader.DirectoryLoader;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.validator.DirectoryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * this class implements the IRsource interface and overload the getResources
 * method, the responsibility of this class convert the directories which is
 * read from root folder to JSONArray String return the string
 *
 * @author muqtar ahmed
 * @version 1.1
 * @since 1.0
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

    private JsonObject visibleExtensions;

    /**
     * Overloaded method get the directories and convert into JSONArray and
     * return the string
     */

    public String getResources() throws ApplicationException, UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        JsonArray jsonArray = null;
        DirectoryValidator dValidator = new DirectoryValidator();
        dValidator.setDirectory(path);
        if (dValidator.isDirectoryPresent()) {
            if (!dValidator.isDirectoryEmpty()) {
                DirectoryLoader dirLoader = new DirectoryLoader(getVisibleExtensions());
                List<Map<String, String>> dirList = dirLoader.getSolutionDirectory(getPath(),
                        getVisibleExtensions().get("isRequestedRecursive").getAsBoolean(), false);
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

    public JsonObject getVisibleExtensions() {
        return visibleExtensions;
    }

    /**
     * setter method for JSONObject
     */
    public void setVisibleExtensions(JsonObject visibleExtensions) {
        this.visibleExtensions = visibleExtensions;
    }

    @Override
    public boolean getDiscardEmptyFolders() {
        return false;
    }

    @Override
    public void setDiscardEmptyFolders(boolean discardEmptyFolders) {

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
