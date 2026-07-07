package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * HDFSTransferHandler extends {@link AbstractFileOperationsOverNetwork}
 * HDFSTransferHandler class is responsible for handling file operations . This
 * class uses the Apache Hadoop library to work with HDFS, and it requires a
 * valid Hadoop configuration and connection details to perform these
 * operations.
 * 
 * @see AbstractFileOperationsOverNetwork
 */
@Component("HDFSTransferHandler")
public class HDFSTransferHandler extends AbstractFileOperationsOverNetwork {
	private static final Logger logger = LoggerFactory.getLogger(HDFSTransferHandler.class);

	/**
	 * addFile(JsonObject parameters)
	 * Uploads a file from the local system to HDFS.
	 * 
	 * @param parameters       object specifying the source and destination paths.
	 * @return "File Uploaded successfully." on success.
	 * @throws OperationFailedException if the upload operation fails.
	 */
	@Override
	public String addFile(JsonObject parameters) {
		String message;

		Configuration conf = prepareConfigurationForHadoop();
		try {

			FileSystem hdfs = FileSystem.get(conf);

			String sourceFilePath = parameters.get("copyFrom").getAsString();
			String destinationFilePath = parameters.get("copyTo").getAsString();

			Path sourcePath = new Path(sourceFilePath);
			Path destPath = new Path(destinationFilePath);

			if (!(hdfs.exists(destPath))) {
				logger.debug("No Such destination exists :" + destPath);
				throw new OperationFailedException("No Such destination exists :" + destPath);
			}

			hdfs.copyFromLocalFile(sourcePath, destPath);
			message = "File Uploaded successfully..";

		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;
	}
	/**
	 * prepareDrillConfig()
	 * Returns the HDFS configuration from the Drill configuration.
	 * @return JSON object containing HDFS configuration.
	 */
	private static JsonObject prepareDrillConfig() {
		JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
		drillConfig = JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
		return drillConfig.getAsJsonObject("fileSystemConfiguration").getAsJsonObject("hdfs");
	}
	/**
	 * prepareConfigurationForHadoop()
	 * @return {@code org.apache.hadoop.conf.Configuration} object
	 */
	private Configuration prepareConfigurationForHadoop() {
		String hdfsPath = getHdfsUrl();
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", hdfsPath);
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		return conf;
	}
	/**
	 * getHdfsUrl()
	 * Constructs and returns the HDFS URL using the host and port from the Drill configuration.
	 * @return HDFS URL in the format "hdfs://host:port".
	 */
	private String getHdfsUrl() {
		JsonObject hdfsConfig = prepareDrillConfig();
		String host = hdfsConfig.get("host").getAsString();
		String port = hdfsConfig.get("port").getAsString();
		return "hdfs://" + host + ":" + port;
	}
	/**
	 * getFile(JsonObject parameters)
	 * Retrieves a file from the Hadoop Distributed File System (HDFS) and saves it to the local file system.
	 * @param parameters        object with "copyFrom" (HDFS source) and "copyTo" (local destination) paths.
	 * @return A success message.
	 * @throws OperationFailedException If file retrieval fails, an exception is thrown.
	 */
	@Override
	public String getFile(JsonObject parameters) {
		String message;
		Configuration conf = prepareConfigurationForHadoop();
		try {
			FileSystem fs = FileSystem.get(conf);
			String sourceFilePath = parameters.get("copyFrom").getAsString();
			String destinationFilePath = parameters.get("copyTo").getAsString();
			Path sourcePath = new Path(sourceFilePath);
			Path destPath = new Path(destinationFilePath);
			if (!(fs.exists(sourcePath))) {
				logger.debug("No Such Source exists :" + sourcePath);
				throw new OperationFailedException("No Such target path exists :" + sourcePath);
			}
			fs.copyToLocalFile(false, sourcePath, destPath);
			message = "File Retrieved successfully.";
		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;
	}
	/**
	 * updateFiles(JsonObject parameters) 
	 * Updates files by copying from HDFS to the local file system.
	 *
	 * @param parameters JSON object with "copyFrom" (HDFS source) and "copyTo" (local destination) paths.
	 * @return A success message.
	 * @throws OperationFailedException If the file update operation fails, an exception is thrown with an error message.
	 */
	@Override
	public String updateFiles(JsonObject parameters) {

		String message;

		Configuration conf = prepareConfigurationForHadoop();
		try {

			FileSystem hdfs = FileSystem.get(conf);

			String sourceFilePath = parameters.get("copyFrom").getAsString();
			String destinationFilePath = parameters.get("copyTo").getAsString();

			Path sourcePath = new Path(sourceFilePath);
			Path destPath = new Path(destinationFilePath);

			if (!(hdfs.exists(destPath))) {
				logger.debug("No Such destination exists :" + destPath);
				throw new OperationFailedException("No Such destination exists :" + destPath);
			}

			hdfs.copyFromLocalFile(false, true, sourcePath, destPath);
			message = "File Added successfully..";

		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;

	}
	/**
	 * deleteFile(JsonObject parameters)
	 * Deletes a file from the File System (HDFS).
	 *
	 * @param parameters 		object containing  the path of the file to be deleted.
	 * @return A message indicating the result of the file deletion. Returns "File deleted successfully." on success.
	 * @throws OperationFailedException If the file deletion operation fails for any reason, an exception is thrown with an error message.
	 */
	@Override
	public String deleteFile(JsonObject parameters) {
		String message = "Can't able to delete the file.";
		Configuration conf = prepareConfigurationForHadoop();
		try {
			FileSystem fs = FileSystem.get(conf);
			String deletefilePath = parameters.get("deletefilePath").getAsString();
			Path sourcePath = new Path(deletefilePath);
			if (!(fs.exists(sourcePath))) {
				logger.debug("No Such Source exists :" + sourcePath);
				throw new OperationFailedException("No Such target path exists :" + sourcePath);
			}

			boolean deleteStatus = fs.delete(sourcePath, false);
			if (deleteStatus) {
				logger.debug("deleted successfully...");
				message = "File deleted successfully.";
			}
		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;
	}
	/**
	 * handleError(IOException ex)
	 * @param ex         provides cause of error
	 * @throws OperationFailedException Always throws an exception with an error message.
	 */
	private String handleError(IOException ex) {
		Throwable rootCause = ex.getCause();
		String cause = rootCause != null ? rootCause.getCause() + " " + rootCause.getMessage()
				: "Please check the log files";
		throw new OperationFailedException("There was an error. " + ex.getMessage());
	}
	/**
	 * setDrillConfigConnection(JsonObject parameter) 
	 * Sets the connection parameter in the given JSON object to the HDFS URL.
	 * @param parameter 	     object in which the HDFS URL parameter will be added 
	 */
	@Override
	public void setDrillConfigConnection(JsonObject parameter) {
		parameter.addProperty("connection", getHdfsUrl());
	}
	/**
	 * isFileExists(String fileName)
	 * Checks if a file with the given name exists in HDFS.
	 * @param fileName 			 file to check for existence 
	 * @return true if the file exists, false otherwise.
	 */
	@Override
	public boolean isFileExists(String fileName) {
		Configuration conf = prepareConfigurationForHadoop();
		try {

			FileSystem hdfs = FileSystem.get(conf);

			Path destPath = new Path(fileName);

			if (!(hdfs.exists(destPath))) {
				logger.debug("No Such destination exists :" + destPath);
				throw new OperationFailedException("No Such destination exists :" + destPath);
			} else {
				return true;
			}

		} catch (IOException ex) {
			return false;
		}

	}
	/**
	 * createFolder(JsonObject parameters)
	 * Creates a new folder in HDFS.
	 * @param parameters 		provides folder path
	 * @return a message indicating the result of the folder creation. Returns "Folder created successfully." on success.
	 * @throws OperationFailedException If the folder creation operation fails, an exception is thrown with an error message.
	 */
	@Override
	public String createFolder(JsonObject parameters) {
		String message = "Can't able to crate the Folder ";
		Configuration conf = prepareConfigurationForHadoop();
		try {
			FileSystem fs = FileSystem.get(conf);
			String createFolderPath = parameters.get("createFolderPath").getAsString();
			Path sourcePath = new Path(createFolderPath);

			boolean createDirStatus = fs.mkdirs(sourcePath);
			if (createDirStatus) {
				logger.debug("Directory created successfully...");
				message = "Folder created successfully.";
			}

		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;
	}
	/**
	 * deleteFolder(JsonObject parameters)
	 * Deletes a folder from HDFS.
	 *
	 * @param parameters 					provides the path to delete the folder.
	 * @return A message indicating the result of the folder deletion. Returns "Directory deleted successfully." on success.
	 * @throws OperationFailedException If the folder deletion operation fails, an exception is thrown with an error message.
	 */
	@Override
	public String deleteFolder(JsonObject parameters) {
		String message = "Can't able to delete the directory..";
		Configuration conf = prepareConfigurationForHadoop();
		try {
			FileSystem fs = FileSystem.get(conf);
			String deleteFolderPath = parameters.get("deleteFolderPath").getAsString();
			Path sourcePath = new Path(deleteFolderPath);
			if (!(fs.exists(sourcePath))) {
				logger.debug("No Such Source exists :" + sourcePath);
				throw new OperationFailedException("No Such target path exists :" + sourcePath);
			}
			boolean createDirStatus;
			if (fs.isDirectory(sourcePath)) {
				createDirStatus = fs.delete(sourcePath, false);
			} else {
				logger.debug("Provided path is not a directory");
				throw new OperationFailedException("Provided path is not a directory:" + sourcePath);
			}

			if (createDirStatus) {
				logger.debug("Directory deleted successfully...");
				message = "Directory deleted successfully.";
			}

		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;
	}
	/**
	 * renameFolder(JsonObject parameters)
	 * Renames a folder in HDFS.
	 * @param parameters 			provides current path and new path
	 * @return A message indicating the result of the folder rename. Returns "Directory renamed successfully." on success.
	 * @throws OperationFailedException If the folder rename operation fails, an exception is thrown with an error message.
	 */
	@Override
	public String renameFolder(JsonObject parameters) {
		String message = "Can't able to rename the directory..";
		boolean renameStatus;
		Configuration conf = prepareConfigurationForHadoop();
		try {
			FileSystem fs = FileSystem.get(conf);
			String fromFilePath = parameters.get("fromFilePath").getAsString();
			String toFilePath = parameters.get("toFilePath").getAsString();
			Path sourcePath = new Path(fromFilePath);
			Path toFile = new Path(toFilePath);
			if (!(fs.exists(sourcePath))) {
				logger.debug("No Such Source exists :" + sourcePath);
				throw new OperationFailedException("No Such Source exists :" + sourcePath);
			}

			renameStatus = fs.rename(sourcePath, toFile);

			if (renameStatus) {
				logger.debug("Directory renamed successfully...");
				message = "Directory renamed successfully...";
			}

		} catch (IOException ex) {
			return handleError(ex);
		}
		return message;
	}

}
