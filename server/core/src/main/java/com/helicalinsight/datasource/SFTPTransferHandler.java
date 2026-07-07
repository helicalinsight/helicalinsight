package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.JsonUtils;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SFTPTransferHandler extends {@link AbstractFileOperationsOverNetwork}}
 * Secure File Transfer Protocol(SFTP) handles file in secure way.
 */
@Component("SFTPTransferHandler")
public class SFTPTransferHandler extends AbstractFileOperationsOverNetwork {

    private static final Logger logger = LoggerFactory.getLogger(SFTPTransferHandler.class);
    /**
     * SftpProgressMonitor implementation used for monitoring SFTP file transfer progress.
     */
    final static SftpProgressMonitor monitor = new SftpProgressMonitor() {
        public void init(final int op, final String source, final String target, final long max) {
            logger.debug("sftp start uploading file from:" + source + " to:" + target);
        }

        public boolean count(final long count) {
            logger.debug("sftp sending bytes: " + count);
            return true;
        }

        public void end() {
            logger.debug("sftp uploading is done.");
        }
    };
    /**
     * prepareDrillConfigParameters()
     * This method is responsible for getting Drill configuration details from drillConfig.xml
     * @return jsonObject with SFTP file configuration details .
     */
    public static JsonObject prepareDrillConfigParameters() {
        JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
        drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
        return drillConfig.getAsJsonObject("fileSystemConfiguration").getAsJsonObject("sftp");
    }
    /**
     * getSession()
     * This method creates session object using port,host, username, password
     * @returns {@code com.jcraft.jsch.Session} object 
     * @throws JSchException   if username or host is invalid.
     * @throws SftpException   
     */
    public Session getSession() throws JSchException, SftpException {
        JsonObject parameters = prepareDrillConfigParameters();
        Integer defaultPort = GsonUtility.optInt(parameters, "port");
        String hostname = parameters.get("host").getAsString();
        String username = parameters.get("username").getAsString();
        String password = parameters.get("password").getAsString();

        logger.debug("Initiate sending file to Server...");
        JSch jsch = new JSch();
        Session session = null;
        logger.debug("Trying to connect.....");
        session = jsch.getSession(username, hostname, defaultPort != 0 ? defaultPort : 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        return session;

    }
    /**
     * getChannel(Session session, JsonObject parameters)
     * @param session			to initialize {@code com.jcraft.jsch.Channel}
     * @param parameters        provides channel type
     * @return {@code com.jcraft.jsch.ChannelSftp} object
     * @throws JSchException  if  any exception occurs in opening channel
     * @throws SftpException
     */
    public ChannelSftp getChannel(Session session, JsonObject parameters) throws JSchException, SftpException {
        logger.debug("is server connected? " + session.isConnected());
        String defaultOpenChannelType = "sftp";
        String openChannelType = GsonUtility.optString(parameters, "openChannelType");

        Channel channel = session.openChannel(!openChannelType.isEmpty() ? openChannelType : defaultOpenChannelType);
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        logger.debug("Server's home directory: " + sftpChannel.getHome());
        return sftpChannel;

    }

    /**
     * addFile(JsonObject parameters)
     * @param parameters              provides current and target location of path
     * @return string message of "File Added successfully.."
     * @throws OperationFailedException    if failure occurs in adding file to sftp connection.
     */
    @Override
    public String addFile(JsonObject parameters) {

        String message;
        try {
            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();
            Session session = getSession();

            ChannelSftp sftpChannel = getChannel(session, parameters);
            sftpChannel.put(copyFrom, copyTo, monitor, ChannelSftp.OVERWRITE);

            closeSessionChannel(session, sftpChannel);
            logger.debug("File Added successfully..");
            message = "File Added successfully..";
        } catch (JSchException | SftpException ex) {
            throw new OperationFailedException("Failed to add the file using sftp connection.");
        }
        return message;
    }
    /**
     * closeSessionChannel(Session session, ChannelSftp sftpChannel)
     * @param session			  to close the session connection
     * @param sftpChannel		  to close sftp connection . 
     */
    public void closeSessionChannel(Session session, ChannelSftp sftpChannel) {
        if (sftpChannel != null)
            sftpChannel.exit();

        if (session != null)
            session.disconnect();
    }
    /**
     * getFile(JsonObject parameters)
     * Retrieves a file from a remote server using SFTP and saves it locally.
     * @param parameters 			JSON with remote source and local destination paths.
     * @return Message indicating success or throws an exception on failure.
     */
    @Override
    public String getFile(JsonObject parameters) {
        String message = null;
        try {
            String copyFrom = parameters.get("copyFrom").getAsString();
            String copyTo = parameters.get("copyTo").getAsString();
            Session session = getSession();

            ChannelSftp sftpChannel = getChannel(session, parameters);
            sftpChannel.get(copyFrom, copyTo, monitor, ChannelSftp.OVERWRITE);

            closeSessionChannel(session, sftpChannel);
            logger.debug("File Retrieved successfully.");
            message = "File Retrieved successfully.";
        } catch (JSchException | SftpException ex) {
            throw new OperationFailedException("Failed to retrieve the file using sftp connection.");
        }
        return message;
    }
    /**
     * updateFiles(JsonObject parameters)
     * @param parameters	provide file details.
     * @return Message indicating success.
     */
    @Override
    public String updateFiles(JsonObject parameters) {
        return addFile(parameters);
    }
    /**
     * deleteFile(JsonObject parameters)
     * @param  parameters    provides path to delete file
     * @return Message indicating success. 
     * @throws OperationFailedException   If the file delete operation fails due to any reason.
     */
    @Override
    public String deleteFile(JsonObject parameters) {
        String message = null;
        try {
            String deletefilePath = parameters.get("deletefilePath").getAsString();

            Session session = getSession();

            ChannelSftp sftpChannel = getChannel(session, parameters);
            boolean deleteFlag;
            sftpChannel.rm(deletefilePath);
            deleteFlag = true;
            if (deleteFlag) {
                logger.debug("File deleted successfully.");
            }
            closeSessionChannel(session, sftpChannel);
            logger.debug("File deleted successfully.");
            message = "File deleted successfully.";
        } catch (JSchException | SftpException ex) {
            throw new OperationFailedException("Failed to delete the file using sftp connection.");
        }
        return message;
    }
    /**
     * createFolder(JsonObject parameters) 
     * Creates a folder on a remote server using SFTP.
     * @param parameters 		specifying the folder path to create.
     * @return Message indicating success .
     * @throws OperationFailedException If there is a failure to create the folder using SFTP connection.
     */
    @Override
    public String createFolder(JsonObject parameters) {
        String message = null;
        try {
            String createFolderPath = parameters.get("createFolderPath").getAsString();

            Session session = getSession();

            ChannelSftp sftpChannel = getChannel(session, parameters);

            try {

                sftpChannel.cd(createFolderPath);
                logger.debug("The folder already exists " + createFolderPath);

            } catch (SftpException e) {
                String[] complPath = createFolderPath.split("/");
                sftpChannel.cd("/");
                for (String dir : complPath) {
                    if (dir.length() > 0) {
                        try {

                            sftpChannel.cd(dir);
                        } catch (SftpException e2) {
                            sftpChannel.mkdir(dir);
                            sftpChannel.cd(dir);
                        }
                    }
                }
                sftpChannel.cd("/");


            }
          
            closeSessionChannel(session, sftpChannel);
            message = "Folder created successfully.";
            logger.debug("Folder created successfully.");

        } catch (JSchException | SftpException ex) {
            throw new OperationFailedException("Failed to create the folder using sftp connection");
        }
        return message;
    }
    /**
     * deleteFolder(JsonObject parameters)
     * Deletes a folder on a server using SFTP.
     *
     * @param parameters 		provides the folder path to be deleted.
     * @return Message indicating successful deletion .
     * @throws OperationFailedException If there is a failure to delete the folder using SFTP connection.
     */
    @Override
    public String deleteFolder(JsonObject parameters) {
        String message = null;
        try {
            String deleteFolderPath = parameters.get("deleteFolderPath").getAsString();

            Session session = getSession();

            ChannelSftp sftpChannel = getChannel(session, parameters);

            sftpChannel.rmdir(deleteFolderPath);

            closeSessionChannel(session, sftpChannel);
            message = "Directory deleted successfully.";
            logger.debug("Directory deleted successfully.");

        } catch (JSchException | SftpException ex) {
            throw new OperationFailedException("Failed to perform delete operation using sftp connection.");
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
    public boolean isFileExists(String fileName) {

        try {
            Session session = getSession();

            String defaultOpenChannelType = "sftp";
            Channel channel = session.openChannel(defaultOpenChannelType);
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            SftpATTRS lstat = sftpChannel.lstat(fileName);
            if (lstat != null) {
                return true;
            }
        } catch (JSchException e) {
            throw new OperationFailedException("Failed to detect the file using sftp connection.");
        } catch (SftpException ex) {
            if (ex.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            } else {
                // something else went wrong
                throw new OperationFailedException("Failed to detect the file using sftp connection.");
            }

        }
        return false;
    }
    /**
     * renameFolder(JsonObject parameters)
     * Renames a directory or file using an SFTP connection.
     *
     * @param parameters 		object with fields specifying the source and target paths.
     * @return A message indicating the result of the renaming operation.
     * @throws OperationFailedException If there is an error while renaming the directory or file using the SFTP connection.
     */
    @Override
    public String renameFolder(JsonObject parameters) {
        String message = null;
        try {
            String fromFilePath = parameters.get("fromFilePath").getAsString();
            String toFilePath = parameters.get("toFilePath").getAsString();
            boolean renameFlag;
            Session session = getSession();

            ChannelSftp sftpChannel = getChannel(session, parameters);

            sftpChannel.rename(fromFilePath, toFilePath);
            renameFlag = true;
            if (renameFlag) {
                logger.debug("File renamed successfully.");
            }
            closeSessionChannel(session, sftpChannel);
            message = "Directory renamed successfully.";
            logger.debug("Directory renamed successfully.");

        } catch (JSchException | SftpException ex) {
            throw new OperationFailedException("Failed to rename the file using sftp connection.");
        }
        return message;
    }

}
