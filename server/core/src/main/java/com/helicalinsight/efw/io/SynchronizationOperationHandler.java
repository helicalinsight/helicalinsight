package com.helicalinsight.efw.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.MalformedXmlException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The class is able to move saved reports (which are referenced by the .result
 * file) and folders(the saved reports in the directory) across the solution
 * directory.
 * <p/>
 * Created by author on 01-Nov-14.
 *
 * @author Rajasekhar
 * @version 1.0
 * @since 1.1
 */
public class SynchronizationOperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizationOperationHandler.class);
    /**
     * Instance of singleton class <code>ApplicationProperties</code>
     */
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * A configuration setting from the setting.xml
     */
    private final String createSeparateDirectoryForEachReportSource = getSettingFromXML();
    /**
     * A list of sourceArray parameter contents
     */
    private List<String> sourceArrayList;
    /**
     * efwsr extension from the setting.xml
     */
    private String efwsrExtension = JsonUtils.getEfwResultExtension();

    /**
     * Moves all the saved reports (which are referenced by the .result file)
     * and folders(the saved reports in the directory) into the destination
     * represented as string.
     *
     * @param sourceArray The source request parameter
     * @param destination The destination request parameter
     * @param request     The Http request object
     * @return true if all the files and folders can be synced
     */
    public boolean handle(String sourceArray, String destination, HttpServletRequest request) throws
            MalformedXmlException, IOException {
        return isSourceArrayValid(sourceArray, destination, request) && moveAll(destination, request);
    }

    /**
     * Verifies whether the request parameter sourceArray can be processed or
     * not.
     *
     * @param sourceArray The source request parameter
     * @param destination The destination request parameter
     * @param request     The Http request object
     * @return true or false based whether the parameter is valid or not
     */
    private boolean isSourceArrayValid(String sourceArray, String destination, HttpServletRequest request) {
        JsonArray sourceJSON;
        try {
            sourceJSON = JsonParser.parseString(new Gson().toJson(sourceArray)).getAsJsonArray();
        } catch (JsonIOException ex) {
            logger.error("JSONException : " + ex);
            request.setAttribute("response", "The source is not properly formatted. It is not an " +
                    "" + "array.");
            return false;
        }

        File destinationDirectory = new File(applicationProperties.getSolutionDirectory() + File.separator +
                destination);
        this.sourceArrayList = new ArrayList<>();
        Iterator<?> iterator = sourceJSON.iterator();
        try {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                File file = new File(applicationProperties.getSolutionDirectory() + File.separator + key);
                if (!file.exists()) {
                    logger.error("The key " + key + " indicates an invalid location on file " +
                            "system. Operation aborted.");
                    request.setAttribute("response", key + " indicates an invalid location on " +
                            "file system. Operation aborted.");
                    return false;
                }

                //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
                /*if (file.isFile()) {
                    if (!isAuthorized(file)) {
                        request.setAttribute("response", "Aborting the operation due to " +
                                "insufficient privileges");
                        return false;
                    }
                } else {
                    if (!validateDirectoryAndItsChildren(file)) {
                        request.setAttribute("response", "Aborting the operation due to " +
                                "insufficient privileges");
                        return false;
                    }
                }*/
                if (!validateDirectoryAndItsChildren(file)) {
                    request.setAttribute("response", "Aborting the operation due to " + "insufficient privileges");
                    return false;
                }
                try {
                    if (!com.helicalinsight.efw.utility.FileUtils.isChild(destinationDirectory, file)) {
                        sourceArrayList.add(key);
                    } else {
                        logger.error("You are trying to move a parent directory into the child " + "directory. " +
                                "Aborting the operation.");
                        request.setAttribute("response", "You are trying to move a parent " +
                                "directory into the child " + "directory. " +
                                "Aborting the operation.");
                        return false;
                    }
                } catch (IOException e) {
                    logger.error("An IOException has occurred.", e);
                    request.setAttribute("response", "Couldn't process the request. An error " + "occurred.");
                    return false;
                }
            }
        } catch (JsonIOException ex) {
            logger.error("sourceArray is not an array", ex);
            request.setAttribute("response", "The source is not properly formatted. It is not an " +
                    "" + "array.");
            return false;
        }
        return true;
    }

    /**
     * Validates whether all directories and all .result files in them consist
     * of valid credentials i.e. have matching credentials with the currently
     * logged in user
     *
     * @param directory The directory which consists of the saved results
     * @return true if all the directories, sub-directories and all the .result
     * files in them consists of valid security credentials
     */
    private boolean validateDirectoryAndItsChildren(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String[] array = (file.toString().split("\\.(?=[^\\.]+$)"));
                    if (array.length >= 2) {
                        if (!array[1].equalsIgnoreCase(this.efwsrExtension)) {
                            throw new OperationFailedException("Only saved reports can be " +
                                    "imported (with extension " + this.efwsrExtension + ")");
                            //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care
                            // of authorization
                            /*if (!isAuthorized(file)) {
                                logger.warn("Aborting the operation due to insufficient " +
                                        "privileges for the file " + file);
                                throw new OperationFailedException("Aborting the operation due to" +
                                        " insufficient " +
                                        "privileges for the file");
                                //return false;
                            }*/

                        }
                    }
                } else {
                    //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of
                    // authorization
                    /*if (isIndexFilePresent(file)) {
                        if (!isAuthorized(file)) {
                            logger.warn("Aborting the operation due to insufficient privileges " +
                                    "for the file " + file);
                            return false;
                        }
                    }*/
                    if (!validateDirectoryAndItsChildren(file)) {
                        logger.warn("Aborting the operation due to insufficient privileges for " +
                                "the file " + file);
                        return false;
                    }
                }
            }
        } else {
            logger.debug(directory + " is empty. Nothing to move");
        }
        return true;
    }

    /**
     * Tries to move all the files and folders requested to the destination
     *
     * @param destination The destination request parameter
     * @param request     The Http request object
     * @return true if move operation is successful
     */
    private boolean moveAll(String destination, HttpServletRequest request) throws MalformedXmlException, IOException {
        logger.debug("destination : " + destination);
        File destinationDirectory = new File(this.applicationProperties.getSolutionDirectory() +
                File.separator + destination);
        if (!destinationDirectory.exists() || destinationDirectory.isFile()) {
            logger.error("Can't perform the move operation to the location " +
                    destinationDirectory + ". Operation failed.");
            request.setAttribute("response", "The destination doesn't exists!");
            return false;
        }

        for (String location : sourceArrayList) {
            File fileToBeMoved = new File(this.applicationProperties.getSolutionDirectory() +
                    File.separator + location);
            if (fileToBeMoved.isFile()) {
                if (!copyFile(request, destinationDirectory, fileToBeMoved)) {
                    return false;
                }
            } else {
                if (!copySavedResultsInDirectoryToDestination(fileToBeMoved, destinationDirectory, request)) {
                    request.setAttribute("response", "Couldn't move a directory to the " + "destination");
                    logger.warn("Couldn't move " + fileToBeMoved + " to the destination");
                    return false;
                } else {
                    logger.debug("Moved all the result files of the directory " + fileToBeMoved);
                }
            }
        }
        return true;
    }

    /**
     * Copies the saved result referenced by the .result file content
     *
     * @param request              The Http request object
     * @param destinationDirectory The request parameter destination
     * @param fileToBeMoved        The current file of type .result
     * @return true or false based on whether the saved result file could be
     * moved to the destination or not
     * @throws MalformedXmlException In case if the .result file is malformed
     * @throws IOException           If an IOException is thrown while copying the saved report
     *                               with the requisite hierarchy
     */
    private boolean copyFile(HttpServletRequest request, File destinationDirectory,
                             File fileToBeMoved) throws MalformedXmlException, IOException {
        String actualExtension;
        String[] array = (fileToBeMoved.toString().split("\\.(?=[^\\.]+$)"));
        if (array.length >= 2) {
            actualExtension = array[1];
            if (actualExtension.equalsIgnoreCase(efwsrExtension)) {
                if (!copySavedResultToDestination(fileToBeMoved, destinationDirectory, request)) {
                    logger.warn("Couldn't move the file " + fileToBeMoved);
                    request.setAttribute("response", "Couldn't move the file");
                    return false;
                } else {
                    logger.debug("Moved the result file of the file " + fileToBeMoved);
                }
            }
        }
        return true;
    }

    /**
     * Copies the saved result files referenced by the .result file content.
     *
     * @param fileToBeMoved        The current file of type .result
     * @param destinationDirectory The request parameter destination
     * @param request              The Http request object
     * @return true or false based on whether the saved result file could be
     * moved to the destination or not
     * @throws MalformedXmlException In case if the .result file is malformed
     * @throws IOException           If an IOException is thrown while copying the saved report
     *                               with the requisite hierarchy
     */

    private boolean copySavedResultsInDirectoryToDestination(File fileToBeMoved, File destinationDirectory,
                                                             HttpServletRequest request) throws
            MalformedXmlException, IOException {
        File[] files = fileToBeMoved.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!copyFile(request, destinationDirectory, file)) {
                        logger.warn("Couldn't move the file " + file);
                        return false;
                    }
                } else {
                    if (!copySavedResultsInDirectoryToDestination(file, destinationDirectory, request)) {
                        logger.warn("Couldn't move the files in directory " + file);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Reads the property 'createSeparateDirectoryForEachReportSource' from
     * setting.xml and if it is not present then the reports are created in the
     * same directory mentioned in the .result file. If it is present and its
     * value is true, then a separate directory will be created for each
     * different 'reportFile'. The associated reports will be copied with the
     * user given name. Else if the user has not given any name, then the
     * default file name will be used.
     *
     * @param fileToBeMoved        The current file of type .result
     * @param destinationDirectory The request parameter destination
     * @param request              The Http request object
     * @return true or false based on whether the saved result file could be
     * moved to the destination or not
     * @throws MalformedXmlException In case if the .result file is malformed
     * @throws IOException           If an IOException is thrown while copying the saved report
     *                               with the requisite hierarchy
     */
    private boolean copySavedResultToDestination(File fileToBeMoved, File destinationDirectory,
                                                 HttpServletRequest request) throws MalformedXmlException, IOException {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject jsonObject = processor.getJsonObject(fileToBeMoved.getAbsolutePath(), false);
        if (jsonObject.has("resultDirectory") && jsonObject.has("resultFile")) {
            logger.debug("The {} is a saved result file", fileToBeMoved);
            File reportDir = new File(this.applicationProperties.getSolutionDirectory() + File.separator + jsonObject
                    .get("reportDir").getAsString());
            if (!reportDir.exists()) {
                request.setAttribute("response", "The reportDir is not a directory and it " +
                        "doesn't" + " exist");
                throw new MalformedXmlException("The reportDir is not a directory and it doesn't " +
                        "" + "exist");
            }

            String hierarchy = jsonObject.get("reportDir").getAsString();
            if (createSeparateDirectoryForEachReportSource.equalsIgnoreCase("true")) {
                hierarchy = hierarchy + File.separator + jsonObject.get("reportFile").getAsString();
            }

            File theDirectory = new File(destinationDirectory.getAbsolutePath() + File.separator + hierarchy);
            FileUtils.forceMkdir(theDirectory);
            if (theDirectory.exists()) {
                logger.debug("Successfully created the directories. " + hierarchy);
            } else {
                request.setAttribute("response", "Can't create the directory structure");
                throw new MalformedXmlException("Can't create the directory structure");
            }

            File actualFileToBeMoved = new File(applicationProperties.getSolutionDirectory() +
                    File.separator + jsonObject.get("resultDirectory").getAsString() + File.separator +
                    jsonObject.get("resultFile").getAsString());
            String resultName;
            if (jsonObject.has("resultName")) {
                resultName = jsonObject.get("resultName").getAsString();
            } else {
                resultName = jsonObject.get("resultFile").getAsString();
            }
            String fileExtension = com.helicalinsight.efw.utility.FileUtils.getExtensionOfFile(actualFileToBeMoved);
            File renamedFile = new File(theDirectory.getAbsolutePath() + File.separator +
                    resultName + "." + fileExtension);
            FileUtils.copyFile(actualFileToBeMoved, renamedFile);
            if (renamedFile.exists()) {
                logger.debug("Successfully created the renamedFile " + renamedFile);
            }
        } else {
            request.setAttribute("response", "The move operation can't be performed except with " + ".result files");
            throw new MalformedXmlException("The move operation can't be performed except with " + ".result files");
        }
        return true;
    }

    /**
     * Obtains the parameter createSeparateDirectoryForEachReportSource from
     * setting.xml
     *
     * @return Returns the parameter
     * createSeparateDirectoryForEachReportSource(true or false as
     * string)
     */
    private String getSettingFromXML() {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject settingsJSON = processor.getJsonObject(this.applicationProperties.getSettingPath(), false);
        if (!settingsJSON.has("createSeparateDirectoryForEachReportSource")) {
            return "false";
        } else {
            return settingsJSON.get("createSeparateDirectoryForEachReportSource").getAsString();
        }
    }
}