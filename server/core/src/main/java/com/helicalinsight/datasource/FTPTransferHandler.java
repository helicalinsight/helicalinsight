package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * FTPTransferHandler extends {@link AbstractFileOperationsOverNetwork}
 * Handles FTP file operations for server interaction.
 */
@Component("FTPTransferHandler")
public class FTPTransferHandler extends AbstractFileOperationsOverNetwork {
    private static final Logger logger = LoggerFactory.getLogger(SFTPTransferHandler.class);
    /**
     * addFile(JsonObject parameters)
     * Adds a file from the source location to the destination location on the remote server.
     * @param parameters       object that provides the source and destination locations for the file transfer.
     * @return string message whether file uploaded successfully or not.  
     * @throws OperationFailedException If the file upload operation encounters an error.   
     */
    @Override
    public String addFile(JsonObject parameters) {
        String message = null;
        boolean status;
        try {

            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();
            ftpConnector();
            disconnect();
            status = uploadFile(copyFrom, copyTo);
            if (status) {
                message = "File Uploaded successfully..";
            } else {
                message = "Can't able to Upload the file.";
            }

            logger.debug("Done");

        } catch (Exception e) {
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;
    }
    /**
     * prepareDrillConfig() 
     * @return jsonObject with drill configuration details
     */
    private static JsonObject prepareDrillConfig() {
        JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
        drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
        return drillConfig.getAsJsonObject("fileSystemConfiguration").getAsJsonObject("ftp");
    }
    /**
     * getFile(JsonObject parameters)
     * Retrieves  a file from the server and stores it at the specified destination location.
     *
     * @param parameters 			object that provides the source and destination locations for the file retrieval.
     * @return A string message indicating whether the file was retrieved successfully or not.
     * @throws OperationFailedException If the file retrieval operation encounters an error.
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
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;
    }
    /**
     * updateFiles(JsonObject parameters)
     * Updates a file on the server by appending its content from a specified source location.
     *
     * @param parameters				object that provides the source and destination locations for the file update.
     * @return A string message indicating whether the file was updated successfully or not.
     * @throws OperationFailedException If the file update operation encounters an error.
     */
    @Override
    public String updateFiles(JsonObject parameters) {
        String message = null;
        boolean status;
        try {

            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();
            ftpConnector();
            status = appendFile(copyFrom, copyTo);
            disconnect();
            if (status) {
                message = "File updated successfully..";
            } else {
                message = "Can't able to update the file.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;
    }
    /**
     * deleteFile(JsonObject parameters)
     * Deletes a file on the server at the specified location.
     *
     * @param parameters A JSON object that provides the path of the file to be deleted.
     * @return A string message indicating whether the file was deleted successfully or not.
     * @throws OperationFailedException If the file deletion operation encounters an error.
     */
    @Override
    public String deleteFile(JsonObject parameters) {
        String message = null;
        boolean status;
        try {
            String deletefilePath = parameters.get("deletefilePath").getAsString();
            ftpConnector();
            status = deleteFile(deletefilePath);
            disconnect();
            if (status) {
                message = "File deleted successfully..";
            } else {
                message = "Can't able to delete the file.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;
    }
    /**
     * setDrillConfigConnection(JsonObject parameter)
     * @param parameter   to set connection propertie
     */
    @Override
    public void setDrillConfigConnection(JsonObject parameter) {
        parameter.addProperty("connection", "file:///");
    }
    /**
     * isFileExists(String fileName)
     * @param fileName       file name
     * @return always {@code false}
     */
    @Override
    public boolean isFileExists(String fileName) {
        return false;
    }
    /**
     * createFolder(JsonObject parameters)
     * Creates a folder on the server at the specified location.
     *
     * @param parameters			object that provides the path of the folder to be created.
     * @return A string message indicating whether the folder was created successfully or not.
     * @throws OperationFailedException If the folder creation operation encounters an error.
     */
    @Override
    public String createFolder(JsonObject parameters) {
        String message = null;
        boolean status;
        try {
            String createFolderPath = parameters.get("createFolderPath").getAsString();
            ftpConnector();
            status = createFolder(createFolderPath);
            disconnect();
            if (status) {
                message = "Folder created successfully..";
            } else {
                message = "Can't able to create folder.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;
    }
    /**
     * deleteFolder(JsonObject parameters)
     * Deletes a folder on the server at the specified location.
     *
     * @param parameters	       object that provides the path of the folder to be deleted.
     * @return A string message indicating whether the folder was deleted successfully or not.
     * @throws OperationFailedException If the folder deletion operation encounters an error.
     */
    @Override
    public String deleteFolder(JsonObject parameters) {
        String message = null;
        boolean status;
        try {
            String deleteFolderPath = parameters.get("deleteFolderPath").getAsString();
            ftpConnector();
            status = deleteFolder(deleteFolderPath);
            disconnect();
            if (status) {
                message = "Folder deleted successfully..";
            } else {
                message = "Can't able to delete the folder.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;
    }
    /**
     * renameFolder(JsonObject parameters)
     * Renames a folder on the server from the source path to the destination path.
     *
     * @param parameters 		object that provides the source and destination paths for renaming.
     * @return A string message indicating whether the folder was renamed successfully or not.
     * @throws OperationFailedException If the folder renaming operation encounters an error.
     */
    @Override
    public String renameFolder(JsonObject parameters) {
        String message = null;
        boolean status;
        try {
            String fromFilePath = parameters.get("fromFilePath").getAsString();
            String toFilePath = parameters.get("toFilePath").getAsString();
            ftpConnector();
            status = renameFolder(fromFilePath, toFilePath);
            disconnect();
            if (status) {
                message = "Folder deleted successfully..";
            } else {
                message = "Can't able to delete the folder.";
            }
            logger.debug("Done");
        } catch (Exception e) {
            throw new OperationFailedException("Filed to perform the operation");
        }
        return message;

    }
    /**
     * renameFolder(String fromFilePath, String toFilePath)
     * Renames a folder or directory on the remote server.
     *
     * @param fromFilePath 				current path of the folder to be renamed.
     * @param toFilePath   				new path and name for the folder after renaming.
     * @return {@code true} if the folder was renamed successfully; otherwise, {@code false}
     * @throws IOException If an I/O error occurs during the folder renaming operation.
     */
    private boolean renameFolder(String fromFilePath, String toFilePath) throws IOException {

        return ftp.rename(fromFilePath, toFilePath);

    }

    public static FTPClient ftp = null;
    /**
     * ftpConnector()
     * Establishes a connection to an FTP server using the provided configuration parameters.
     * @throws Exception If an error occurs during the FTP server connection setup.
     */
    public static void ftpConnector() throws Exception {
        JsonObject parameters = prepareDrillConfig();
        String hostname = parameters.get("host").getAsString();
        String username = parameters.get("username").getAsString();
        String password = parameters.get("password").getAsString();
        Integer port = parameters.get("port").getAsInt();
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.connect(hostname, port);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }

        ftp.login(username, password);
        ftp.setFileType(FTP.LOCAL_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }
    /**
     * uploadFile(String localFileFullName, String hostDir) 
     * Uploads a local file to the specified host directory on the FTP server.
     *
     * @param localFileFullName 	 path of the local file to upload.
     * @param hostDir           	 target directory on the FTP server where the file will be uploaded.
     * @return True if the file upload is successful, false otherwise.
     * @throws Exception If an error occurs during the file upload process.
     */
    public static boolean uploadFile(String localFileFullName, String hostDir) throws Exception {
        try (InputStream inputStream = new FileInputStream(new File(localFileFullName))) {
            return ftp.storeFile(hostDir, inputStream);
        }
    }
    /**
     * retrieveFile(String downloadFileLocation, String hostDir)
     * Retrieves a file from the specified host directory on the FTP server and saves it to the local file system.
     *
     * @param downloadFileLocation 			local file path where the retrieved file will be saved.
     * @param hostDir              			source directory on the FTP server from which to retrieve the file.
     * @return {@code true} if the file retrieval is successful, {@code false} otherwise.
     * @throws Exception If an error occurs during the file retrieval process.
     */
    public static boolean retrieveFile(String downloadFileLocation, String hostDir) throws Exception {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFileLocation));) {
            return ftp.retrieveFile(hostDir, outputStream);
        }
    }
    /**
     * appendFile(String localFileFullName, String hostDir)
     * Appends the contents of a local file to an existing file in the specified host directory on the FTP server.
     *
     * @param localFileFullName 		full path of the local file to append.
     * @param hostDir           		target directory on the FTP server where the file will be appended.
     * @return {@code true} if the file append operation is successful, {@code false} otherwise.
     * @throws Exception If an error occurs during the file append process.
     */
    public static boolean appendFile(String localFileFullName, String hostDir) throws Exception {
        try (InputStream inputStream = new FileInputStream(new File(localFileFullName))) {
            return ftp.appendFile(hostDir, inputStream);
        }
    }
    /**
     * deleteFile(String deleteFileLocation)
     * Deletes a file from the specified location on the FTP server.
     *
     * @param deleteFileLocation 		path of the file to be deleted on the FTP server.
     * @return {@code true} if the file deletion is successful, {@code false} otherwise.
     * @throws Exception If an error occurs during the file deletion process.
     */
    public static boolean deleteFile(String deleteFileLocation) throws Exception {
        return ftp.deleteFile(deleteFileLocation);

    }
    /**
     * createFolder(String createFolderLocation)
     * Creates a new folder at the specified location on the FTP server.
     *
     * @param createFolderLocation 			 path where the new folder will be created on the FTP server.
     * @return {@code true} if the folder creation is successful, {@code false} otherwise.
     * @throws Exception If an error occurs during the folder creation process.
     */
    public static boolean createFolder(String createFolderLocation) throws Exception {
        return ftp.makeDirectory(createFolderLocation);

    }
    /**
     * deleteFolder(String deleteFolderLocation)
     * Removes a directory on the FTP server.
     * @param deleteFolderLocation                    path of the folder to be deleted
     * @return {@code true} if the folder deletion is successful, {@code false} otherwise.
     * @throws Exception     If an error occurs during the folder deletion process.
     */
    public static boolean deleteFolder(String deleteFolderLocation) throws Exception {
        return ftp.removeDirectory(deleteFolderLocation);

    }
    /**
     * disconnect()
     * Disconnects from the FTP server if a connection is established
     * Logout of the FTP server.
     * Closes the connection to the FTP server and restores
     * connection parameters to the default values.
     */
    public static void disconnect() {
        if (ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already saved to server
            }
        }
    }
}
