package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
/**
 * StandAlone is a class that provides file operations over a network, such as copying, retrieving, updating, deleting,
 * and creating files and folders. It implements the {@link AbstractFileOperationsOverNetwork} interface.
 *
 */
@Component("StandAlone")
public class StandAlone extends AbstractFileOperationsOverNetwork {
    private static final Logger logger = LoggerFactory.getLogger(StandAlone.class);
    /**
     * addFile(JsonObject parameters)
     * @param parameters              provides current and target location of path
     * @return Message with {@code null}.
     * @throws OperationFailedException    if failure occurs in adding file .
     */
    @Override
    public String addFile(JsonObject parameters) {
        String message = null;
        boolean status;
        try {

            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();

        } catch (Exception e) {
            throw new OperationFailedException("Failed to perform the operation");
        }
        return message;
    }
    /**
     * prepareDrillConfigParameters()
     * This method is responsible for getting Drill configuration details from drillConfig.xml
     * @return jsonObject with SFTP file configuration details .
     */
    private static JsonObject prepareDrillConfig() {
        JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
        drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
        return drillConfig.getAsJsonObject("fileSystemConfiguration").getAsJsonObject("ftp");
    }
    /**
     * getFile(JsonObject parameters)
     * Retrieves a file from a remote server using SFTP and saves it locally.
     * @param parameters 			JSON with remote source and local destination paths.
     * @return Message indicating success or failure.
     * 
     */
    @Override
    public String getFile(JsonObject parameters) {
        String message = null;
        boolean status;
        try {

            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();
            ftpConnector();
            status = retrieveFile(copyTo, copyFrom);
            disconnect();
            if (status) {
                message = "File Retrieved successfully..";
            } else {
                message = "Can't able to retrieve the file.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Failed to perform the operation");
        }
        return message;
    }
    /**
     * updateFiles(JsonObject parameters)
     * @param parameters	provide path of source and destination details.
     * @return Message indicating {@code null}.
     * @throws OperationFailedException   if any failure occurs while uploading file
     */
    @Override
    public String updateFiles(JsonObject parameters) {
        String message = null;
        boolean status;
        try {

            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();
            ApplicationProperties instance = ApplicationProperties.getInstance();
            File targetDestination = new File(instance.getSystemDirectory() + File.separator + copyTo);
            File copyFromFile = new File(copyFrom);
            FileUtils.copyFileToDirectory(copyFromFile, targetDestination);

        } catch (Exception e) {
            throw new OperationFailedException("Failed to perform the operation copy operation. " + e.getCause());
        }
        return message;
    }
    /**
     * deleteFile(JsonObject parameters)
     * @param  parameters    provides path to delete file
     * @return Message indicating success. 
     * @throws OperationFailedException   If the file delete operation fails due to any reason.
     */
    @Override
    public String deleteFile(JsonObject parameters) {
        String message ;

        try {
            String deletefilePath = parameters.get("deletefilePath").getAsString();
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String systemDirectory = applicationProperties.getSystemDirectory();
            String targetString = systemDirectory + File.separator + deletefilePath;
            File targetDestination = new File(targetString);
            if (targetDestination.exists()) {
                targetDestination.delete();

                if (!targetDestination.exists()) {
                    message = "File deleted successfully..";
                } else {
                    message = "Can't able to delete the file.";
                }
            } else {
                message = "No Such File To delete.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Failed to perform the operation");
        }
        return message;
    }
    /**
     * setDrillConfigConnection(JsonObject parameter)
     * @param parameter           Sets the Drill configuration connection in the provided JSON object.
     */
    @Override
    public void setDrillConfigConnection(JsonObject parameter) {
        parameter.addProperty("connection", "file:///");
    }
    /**
     * isFileExists(String fileName)
     * Checks if a file exists at the specified path using an SFTP connection.
     *
     * @param fileName 			path to the file to check.
     * @return {@code true} if the file exists, {@code false} otherwise.
     * @throws OperationFailedException If there is an error while detecting the file using the SFTP connection.
     */
    @Override
    public boolean isFileExists(String fileNameWithPath) {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        String systemDirectory = applicationProperties.getSystemDirectory();
        String targetString = systemDirectory + File.separator + fileNameWithPath;
        File targetDestination = new File(targetString);
        return targetDestination.exists();

    }
    /**
     * createFolder(JsonObject parameters) 
     * it Creates a folder on specified path
     * @param parameters 		specifying the folder path to create.
     * @return Message indicating success .
     * @throws OperationFailedException If there is a failure to create the folder.
     */
    @Override
    public String createFolder(JsonObject parameters) {
        try {
            String createFolderPath = parameters.get("createFolderPath").getAsString();
            if(createFolderPath.contains(":")){
                throw new OperationFailedException("Invalid Path . Please remove the drive Path. Provide relative path to hi-repository");
            }
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String systemDirectory = applicationProperties.getSystemDirectory();
            String targetString = systemDirectory + File.separator + createFolderPath;
             targetString = targetString.replaceAll("\\\\", "/");
            File targetDestination = new File(targetString);
            boolean status = targetDestination.mkdirs();
            parameters.addProperty("createFolderPath", targetString);
            return "Folder created successfully..";

        } catch (Exception e) {
            throw new OperationFailedException("Could not create the folder");
        }
    }
    /**
     * deleteFolder(JsonObject parameters)
     * Deletes a folder on specified path
     *
     * @param parameters 		provides the folder path to be deleted.
     * @return Message indicating successful deletion .
     * @throws OperationFailedException If there is a failure to delete the folder .
     */
    @Override
    public String deleteFolder(JsonObject parameters) {
        String message ;

        try {
            String deleteFolderPath = parameters.get("deleteFolderPath").getAsString();
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String systemDirectory = applicationProperties.getSystemDirectory();
            String targetString = systemDirectory + File.separator + deleteFolderPath;
            File targetDestination = new File(targetString);
            if (targetDestination.exists()) {
                targetDestination.delete();

                if (!targetDestination.exists()) {
                    message = "File deleted successfully..";
                } else {
                    message = "Can't able to delete the file.";
                }
                logger.debug("Done");

            } else {
                message = "No such file to delete.";
            }
        } catch (Exception e) {
            throw new OperationFailedException("Could not create the folder");
        }
        return message;

    }

    @Override
    public String renameFolder(JsonObject parameters) {
        //Nop
        return null;

    }

    private boolean renameFolder(String fromFilePath, String toFilePath) throws IOException {

        return true;
    }


    public static void ftpConnector() throws Exception {

    }

    public static boolean retrieveFile(String downloadFileLocation, String hostDir) throws Exception {
        return true;
    }

    public static boolean appendFile(String localFileFullName, String hostDir) throws Exception {
        return true;
    }

    public static boolean deleteFile(String deleteFileLocation) throws Exception {
        return true;
    }

    public static boolean createFolder(String createFolderLocation) throws Exception {
        return true;
    }

    public static boolean deleteFolder(String deleteFolderLocation) throws Exception {
        return true;
    }

    public static void disconnect() {
    }
}
