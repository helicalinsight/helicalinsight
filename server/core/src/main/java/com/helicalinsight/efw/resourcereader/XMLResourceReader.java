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
 * this class implements the IResource interface and overload the getResources
 * method, the responsibility of this class convert the directories which is
 * read from root path to JSONArray String return the string
 *
 * @author muqtar ahmed
 * @version 1.1
 * @since 1.0
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
    private JsonObject visibleExtensions;

    /**
     * overloaded method get the directories and convert into JSONArray and
     * return the string
     */

    private boolean discardEmptyFolders;


    public String getResources() throws ApplicationException, UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException {
        JsonArray jsonArray;
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
                isRequestedRecursive, this.discardEmptyFolders);
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

    public JsonObject getVisibleExtensions() {
        return visibleExtensions;
    }

    /**
     * setter method for JSONObject
     */
    public void setVisibleExtensions(JsonObject visibleExtensions) {
        this.visibleExtensions = visibleExtensions;
    }


    public boolean getDiscardEmptyFolders() {
        return this.discardEmptyFolders;
    }


    public void setDiscardEmptyFolders(boolean discardEmptyFolders) {
        this.discardEmptyFolders = discardEmptyFolders;
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
