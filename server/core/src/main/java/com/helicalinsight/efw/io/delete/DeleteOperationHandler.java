package com.helicalinsight.efw.io.delete;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.io.AbstractOperationsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A handler class for the delete file operation. File and folder deletion in
 * the EFW solution directory is handled by this class.
 *
 * @author Rajasekhar
 * @author Prashansa
 * @version 1.0
 * @since 1.1
 */
@Component
@Scope("prototype")
public class DeleteOperationHandler extends AbstractOperationsHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOperationHandler.class);
    /**
     * Instance of the singleton <code>ApplicationProperties</code>
     */
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * The list of extensions for which the setting.xml has configuration
     */
    private List<String> extensionsList;
    /**
     * List from the sourceArray
     */
    private List<File> listOfFilesToBeDeleted;
    /**
     * List from the sourceArray
     */
    private List<File> listOfDirectoriesToBeDeleted;

    public boolean handle(String sourceArray) throws UnSupportedRuleImplementationException {
        /*
         * First validate the source array
		 */
        return (isSourceArrayValid(sourceArray) && deleteAll());
    }

    /**
     * Validates the request parameter sourceArray
     *
     * @param sourceArray The request parameter sourceArray
     * @return true if validated
     */
    private boolean isSourceArrayValid(String sourceArray) {
        JsonArray sourceJSON;
        try {
            sourceJSON = JsonParser.parseString(new Gson().toJson(sourceArray)).getAsJsonArray();
        } catch (JsonIOException ex) {
            logger.error("JSONException : " + ex);
            return false;
        }
        if (!prepareLists(sourceJSON)) {
            throw new OperationFailedException("The requested resource(s) do not exist in the " + "repository. " +
                    "Aborting operation.");
        }
        extensionsList = getListOfExtensionsFromSettings();
        return (extensionsList != null);
    }

    /**
     * <p>
     * Deletes all the files and folders in the specified sourceArray if the
     * user is authorized
     * </p>
     *
     * @return true if the files and directories are deleted otherwise false
     */
    private boolean deleteAll() throws UnSupportedRuleImplementationException {
        /*
         * Pick one file from the input, See if user can delete
		 */
        if (canDeleteDirectories() && canDeleteFiles()) {
            purge();
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * Prepares <code>List</code> of files and directories to be deleted.
     * </p>
     *
     * @param sourceJSON The json array of sourceArray request parameter
     * @return false if the files does not exist on the file system
     */
    private boolean prepareLists(JsonArray sourceJSON) {
        this.listOfFilesToBeDeleted = new ArrayList<>();
        this.listOfDirectoriesToBeDeleted = new ArrayList<>();
        Iterator<?> iterator = sourceJSON.iterator();
        try {
            while (iterator.hasNext()) {
                Object keyValue =  iterator.next();
                String key = new Gson().toJson(keyValue);
                File file = new File(this.applicationProperties.getSolutionDirectory() + File.separator + key);
                if (!file.exists()) {
                    logger.error("The key " + key + " indicates an invalid location on file " +
                            "system. Operation aborted.");
                    return false;
                }
                if (file.isFile()) {
                    listOfFilesToBeDeleted.add(file);
                } else if (file.isDirectory()) {
                    listOfDirectoriesToBeDeleted.add(file);
                } else {
                    logger.error("The key " + key + " is neither a directory nor file. Check " +
                            "properties. Operation aborted.");
                    return false;
                }
            }
        } catch (JsonIOException ex) {
            logger.error("sourceArray is not an array. Aborting the operation.", ex);
            return false;
        }
        return true;
    }

    /**
     * Checks whether if all the directories can be deleted or not for the sake
     * of atomicity.
     *
     * @return true if all the directories can be deleted
     */
    private boolean canDeleteDirectories() throws UnSupportedRuleImplementationException {
        DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
        deleteOperationUtility.setListOfFileExtensions(extensionsList);

        if (listOfDirectoriesToBeDeleted.isEmpty()) {
            return true;
        }
        for (File directory : listOfDirectoriesToBeDeleted) {
            if (!deleteOperationUtility.tryDeleting(directory)) {
                return false;
            } else {
                logger.info(directory + " is deletable");
            }
        }
        return true;
    }

    /**
     * Checks whether if all the files can be deleted or not for the sake of
     * atomicity.
     *
     * @return true if all the files can be deleted
     */
    private boolean canDeleteFiles() {
        /*
         * Return true if there are no files to be deleted
		 */
        if (listOfFilesToBeDeleted.isEmpty()) {
            return true;
        }
        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
        /*for (File file : listOfFilesToBeDeleted) {
            if (!isAuthorized(file)) {
                return false;
            } else {
                logger.info(file + " is deletable");
            }
        }*/
        return true;
    }

    /**
     * <p>
     * If the efwsr files have schedulingReference then before deleting the file
     * the scheduling information is also deleted from the scheduler. All the
     * files in listOfFilesToBeDeleted will be deleted.
     * </p>
     */
    private void purge() throws UnSupportedRuleImplementationException {
        DeleteOperationUtility deleteOperationUtility = new DeleteOperationUtility();
        deleteOperationUtility.setListOfFileExtensions(this.extensionsList);
        logger.info("List of files and directories to be deleted " + listOfFilesToBeDeleted + " " +
                "and " + listOfDirectoriesToBeDeleted);
        for (File file : listOfFilesToBeDeleted) {
            logger.debug("Trying to delete the file " + file);
            deleteOperationUtility.deleteFile(file);
        }
        for (File directory : listOfDirectoriesToBeDeleted) {
            logger.debug("Trying to delete the file " + directory);
            deleteOperationUtility.deleteDirectory(directory);
        }
    }
}
