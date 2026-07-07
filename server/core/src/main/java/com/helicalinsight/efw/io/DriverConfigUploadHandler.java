package com.helicalinsight.efw.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.sf.json.JSONObject;

@Component("driverConfigUploadHandler")
@Scope("prototype")
public class DriverConfigUploadHandler implements IUpload {

	private static final Logger logger = LoggerFactory.getLogger(DriverConfigUploadHandler.class);
	private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	private static final String HTTP_FILE_CONFIG = String.join(File.separator,
			applicationProperties.getSystemDirectory(), "Admin", "httpconfig");
	private static final String FLAT_FILE_CONFIG = String.join(File.separator,
			applicationProperties.getSystemDirectory(), "Admin", "flatfileconfig");
	private static final String DATA_PATH = String.join(File.separator, applicationProperties.getSystemDirectory(),
			"hidw", "flatfiles");
	
	@Override
	public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination,
			String extensionOfFileTypeToBeImported) {

		boolean isData = Boolean.valueOf(request.getParameter("dataFile"));
		
		String driverType = request.getParameter("driverType");
		
		if (StringUtils.isBlank(driverType)) {
			throw new EfwServiceException("Please provide driverType");
		}
		
		String dataPath = request.getParameter("dataPath");
		
		if (StringUtils.isBlank(dataPath)) {
			dataPath = DATA_PATH;
		}
		
		destination = isData ? dataPath : getDestination(driverType);
		
		String fileName = "";
		fileName = FilenameUtils.getName(fileObject.getName());
		String suffix = FilenameUtils.getExtension(fileName);
		String prefix = FilenameUtils.getBaseName(fileName);
		prefix = prefix.replaceAll("\\.", "_");

		if (isData) {
			fileName = storeDataFileAtTemp(fileObject,suffix);
		} else {
		String folderName = TempDirectoryCleaner.getTempDirectory() + File.separator + prefix;
		String destinationFileString = folderName + "." + suffix;
		File destinationFile = new File(destinationFileString);

		try {
			if (!destinationFile.exists()) {
				if (destinationFile.createNewFile()) {
					logger.debug("Created the file " + destinationFile);
				} else {
					logger.debug("Couldn't create the file " + destinationFile);
				}
			}
			fileObject.write(destinationFile);

				String copyFrom = destinationFileString;
				String copyTo = String.join(File.separator, destination, fileName);
	        File targetDestination = new File(copyTo);
	        File copyFromFile = new File(copyFrom);
	        FileUtils.copyFile(copyFromFile, targetDestination);
	        
				if (suffix.equalsIgnoreCase("zip")) {
	        	decompress(copyTo);
	        	targetDestination.delete();
	        }

		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			logger.error("Cannot able to write the file ", e);
			return false;
		}
		}

		JSONObject message = new JSONObject();
		message.put("message", "Upload of " + fileName + " is successful");
		message.put("fileName", fileName);
		request.setAttribute("message", message);

		return true;
	}

	public void deleteTempFile(String fileName, File destinationFile) {
		if (destinationFile.delete()) {
			logger.debug("Successfully deleted the file " + fileName + " temp");
		}
	}
	
	private final String storeDataFileAtTemp(FileItem fileObject, String suffix) {
		String fileName = UUID.randomUUID().toString().replaceAll("[^a-zA-Z]", "");

		if ( StringUtils.isNotBlank(suffix)) {
			fileName = fileName + "." + suffix;
		}

		File tempFile = Paths.get(TempDirectoryCleaner.getTempDirectory().toString(), fileName).toFile();
		try {
			if (!tempFile.exists()) {
				boolean created = tempFile.createNewFile();
				if (!created) {
					logger.error("Error occurred while creating temp file.");
				}
			}
			fileObject.write(tempFile);
		} catch (Exception e) {
			logger.error("Error occurred {}", e);
		}
		return fileName;
	}

	private final String getDestination(String driverType) {
		if (driverType.equalsIgnoreCase("http")) {
			return HTTP_FILE_CONFIG;
		} else {
			return FLAT_FILE_CONFIG;
		}
	}
	
	private final void decompress(String zipFilePath) throws ZipException {
		ZipFile zipFile = new ZipFile(zipFilePath);
		if (zipFile.getFile().exists() && zipFile.isValidZipFile()) {
			zipFile.extractAll(DATA_PATH);
		} else {
			throw new EfwServiceException("Invalid Zip file provided (or) Zip File corrupted.");
		}
	}

}
