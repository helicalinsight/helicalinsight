package com.helicalinsight.efw.io;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.jaxb.EfwResult;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A handler class for move operation of files and folders. Files and folders
 * can be moved across the solution directory.
 *
 * @author Rajasekhar
 * @version 1.0
 * @since 1.1
 */
@Component
@Scope("prototype")
public class MoveToOperationHandler extends AbstractOperationsHandler {

    private static final Logger logger = LoggerFactory.getLogger(MoveToOperationHandler.class);
    /**
     * Instance of the singleton <code>ApplicationProperties</code>
     */
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * A list of the content of sourceArray
     */
    private List<String> sourceArrayList;
    /**
     * The list of extensions for which the setting.xml has configuration
     */
    private List<String> listOfExtensions;

    /**
     * <p>
     * The source array should not be null or empty. The method handles the
     * moving of files and folders across the solution directory.
     * </p>
     *
     * @param sourceArray The request parameter sourceArray
     * @param destination The destination file name which is relative to the solution
     *                    directory
     * @return true if files and folders are moved successfully
     */
    public boolean handle(String sourceArray, String destination) {
        logger.info("Source array is {}", sourceArray);
        if (sourceArray == null || "[]".equals(sourceArray) || destination == null || "[null]".equals(sourceArray)) {
            throw new RequiredParameterIsNullException("The required parameter is null");
        }
        return (isSourceArrayValid(sourceArray, destination)) && moveAll(destination);
    }

    /**
     * Validates the source array,all the files ad folders should exist in file
     * system. Destination should not be child of parent directory.
     *
     * @param sourceArray a <code>String</code> which specify name of file or directory
     * @param destination a <code>String</code> which specify the path of file.
     * @return true if the source is valid
     */
    private boolean isSourceArrayValid(String sourceArray, String destination) {
        JsonArray sourceJSON;
        try {
            sourceJSON =  JsonParser.parseString(new Gson().toJson(sourceArray)).getAsJsonArray();
        } catch (JsonIOException ex) {
            logger.error("JSONException : " + ex);
            return false;
        }
        File destinationDirectory = new File(this.applicationProperties.getSolutionDirectory() +
                File.separator + destination);
        this.sourceArrayList = new ArrayList<>();
        Iterator<?> iterator = sourceJSON.iterator();
        try {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                File file = new File(this.applicationProperties.getSolutionDirectory() + File.separator + key);
                if (!file.exists()) {
                    logger.error("The key " + key + " indicates an invalid location on file " +
                            "system. Operation aborted.");
                    throw new OperationFailedException("The file does not exists");
                }
                try {
                    if (!com.helicalinsight.efw.utility.FileUtils.isChild(destinationDirectory, file)) {
                        sourceArrayList.add(key);
                    } else {
                        logger.error("You are trying to move a parent directory into the child " + "directory. " +
                                "Aborting the operation.");
                        throw new OperationFailedException("Cannot move parent directory into " +
                                "the" + "child directory. Aborting the operation");
                    }
                } catch (IOException e) {
                    logger.error("An IOException has occurred.", e);
                    return false;
                }
            }
        } catch (JsonIOException ex) {
            logger.error("sourceArray is not an array", ex);
            return false;
        }
        return true;
    }

    /**
     * <p>
     * Moves all the files and folder from source to destination only if user is
     * authorized and source and destination exist on the file system in the
     * solution directory
     * </p>
     *
     * @param destination a <code>String</code> which specifies the path where the
     *                    folder has to be moved
     * @return true if files and folders are moved successfully
     */
    private boolean moveAll(String destination) {
        logger.debug("Destination :" + destination);
        File destinationDirectory = new File(this.applicationProperties.getSolutionDirectory() +
                File.separator + destination);
        if (!destinationDirectory.exists() || destinationDirectory.isFile()) {
            logger.error("Can't perform the move operation to the location " +
                    destinationDirectory + ". Operation failed.");
            throw new OperationFailedException("Destination directory does not exists");
            //return false;
        }

        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
        /*if (!isUserAuthorizedToMoveToDestination(destinationDirectory)) {
            logger.error("The user is not authorized to move any file to the location " +
                    destinationDirectory);
            throw new OperationFailedException("The user is not authorized to move any file to "
                    + "the requested location");
            //return false;
        }
        */

        for (String location : sourceArrayList) {
            File fileToBeMoved = new File(this.applicationProperties.getSolutionDirectory() +
                    File.separator + location);
            if (fileToBeMoved.isFile()) {
                if (listOfExtensions == null) {
                    this.listOfExtensions = super.getListOfExtensionsFromSettings();
                }
                moveFile(fileToBeMoved, destinationDirectory);
            } else if (fileToBeMoved.isDirectory()) {
                moveDirectory(fileToBeMoved, destinationDirectory);
            }
        }
        return true;
    }

    /**
     * Moves efwResult files as well as other types of files. Checks the user
     * credentials before moving file.
     *
     * @param fileToBeMoved        The file under concern
     * @param destinationDirectory The destination directory
     */
    private void moveFile(File fileToBeMoved, File destinationDirectory) {
        String actualExtension;
        String[] array = (fileToBeMoved.toString().split("\\.(?=[^\\.]+$)"));
        if (array.length >= 2) {
            actualExtension = array[1];
            if (!listOfExtensions.contains(actualExtension)) {
                logger.error("The file " + fileToBeMoved + " is not movable.");
                return;
            }

            //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
            /*if (!isAuthorized(fileToBeMoved)) {
                logger.error("Can't move the file " + fileToBeMoved + " due to insufficient " +
                        "privileges.");
                throw new OperationFailedException("Can't move the file. You may not have " +
                        "sufficient privileges.");

            }*/
            /*
             * Move the saved result file also to the destinationDirectory in
			 * case of .result files
			 */
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JsonObject jsonObject = processor.getJsonObject(fileToBeMoved.getAbsolutePath(), false);
            if (jsonObject.has("resultDirectory")) {
                EfwResult efwresult = JaxbUtils.unMarshal(EfwResult.class, fileToBeMoved);
                String destinationDirectoryPath = destinationDirectory.getAbsolutePath();
                String solutionDirectory = this.applicationProperties.getSolutionDirectory() + File.separator;
                String newResultDirectory = destinationDirectoryPath.substring(solutionDirectory.length() - 1);
                efwresult.setReportDir(newResultDirectory);
                efwresult.setResultDirectory(newResultDirectory);
                JaxbUtils.marshal(efwresult, fileToBeMoved);
                moveEFWResultFile(fileToBeMoved, destinationDirectory, jsonObject);
                return;
            }
            try {
                FileUtils.moveFileToDirectory(fileToBeMoved, destinationDirectory, false);
            } catch (FileExistsException ex) {
                logger.error("An IOException occurred while moving " + fileToBeMoved + " to " +
                        destinationDirectory + ". Probably the file already exists in destination");
                throw new OperationFailedException("An Error occurred. Couldn't move as the " + "destination consists" +
                        " of a file/folder with same physical name.");
            } catch (IOException ex) {
                throw new OperationFailedException("An Error occurred. Couldn't move. Please" + "" +
                        " check logs for more details.");
            }
        }
    }

    /**
     * <p>
     * If index.efwFolder file exists and user credentials for each directory
     * are matching and if the source and destination are not the same and the
     * destination is not a child of source then only files will be moved.
     * </p>
     *
     * @param fileToBeMoved        a <code>File</code> which has to be moved
     * @param destinationDirectory a <code>File</code> where the file has to be moved.
     */
    private void moveDirectory(File fileToBeMoved, File destinationDirectory) {
        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
        /*if (!isIndexFilePresent(fileToBeMoved)) {
            logger.error("Can't move the directory " + fileToBeMoved + " as the index file is not" +
                    " present");
            return;
        }
        if (!isAuthorized(fileToBeMoved)) {
            logger.error("Can't move the directory " + fileToBeMoved + " due to insufficient " +
                    "privileges.");
            return;
        }*/
        try {
            if (!fileToBeMoved.toString().equals(destinationDirectory.toString())) {
                FileUtils.moveDirectoryToDirectory(fileToBeMoved, destinationDirectory, false);
            } else {
                logger.error("Source and destination are the same. Operation can't be performed.");
            }
        } catch (IOException e) {
            logger.error("An exception occurred while moving directory " + fileToBeMoved + " to " +
                    destinationDirectory);
        }
    }

    //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
    /*
     * Checks whether the index.efwFolder file exists and user credentials
     * matching or not
     *
     * @param destinationDirectory The directory into which the files are being moved
     * @return true if credentials are matching
     *//*
    private boolean isUserAuthorizedToMoveToDestination(File destinationDirectory) {
        return isIndexFilePresent(destinationDirectory) && isAuthorized(destinationDirectory);
    }*/

    /**
     * If the fileToBeMoved has extension .result then the corresponding saved
     * result referenced the file will also be moved to requisite destination
     * directory. In case if the destination already consists of the file to be
     * moved then an IO exception occurs, which will be caught and logged to log
     * file.
     *
     * @param fileToBeMoved        The file under concern
     * @param destinationDirectory The directory into which the files are being moved
     * @param jsonObject           The json of the fileToBeMoved
     */
    private void moveEFWResultFile(File fileToBeMoved, File destinationDirectory, JsonObject jsonObject) {
        logger.debug("Parameters: file " + fileToBeMoved + " has resultDirectory and it is an " +
                "efwresult file");
        if (jsonObject.has("resultFile")) {
            logger.debug("file " + fileToBeMoved + " has resultFile");
            String resultFile = jsonObject.get("resultFile").getAsString();
            String resultDirectory = jsonObject.get("resultDirectory").getAsString();
            if (resultDirectory.matches("^\\[.*\\]$")) {
                resultDirectory = "";
            }
            File savedResultFileToBeMoved = new File(this.applicationProperties.getSolutionDirectory() + File
                    .separator + resultDirectory + File.separator +
                    resultFile);
            try {
                FileUtils.moveFileToDirectory(savedResultFileToBeMoved, destinationDirectory, false);
            } catch (FileExistsException ex) {
                logger.error("An IOException occurred while moving " + savedResultFileToBeMoved +
                        " to " + destinationDirectory + ". Probably the file already exists in " +
                        "destination");
                throw new OperationFailedException("An Error occurred. Couldn't move as the " + "destination consists" +
                        " of a file/folder with same physical name.");
            } catch (IOException ex) {
                throw new OperationFailedException("An Error occurred. Couldn't move. Please" + "" +
                        " check logs for more details.");
            }
            /*
             * if the savedResultFileToBeMoved is not moved, don't move .result
			 * file
			 */
            try {
                FileUtils.moveFileToDirectory(fileToBeMoved, destinationDirectory, false);
            } catch (FileExistsException ex) {
                logger.error("An IOException occurred while moving " + fileToBeMoved + " to " +
                        destinationDirectory + ". Probably the file already exists in destination");
                throw new OperationFailedException("An Error occurred. Couldn't move as the " + "destination consists" +
                        " of a file/folder with same physical name.");
            } catch (IOException ex) {
                throw new OperationFailedException("An Error occurred. Couldn't move. Please" + "" +
                        " check logs for more details.");
            }
        }
    }
}
