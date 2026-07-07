package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author Somen
 */

@Component("databaseJarZipHandler")
@Scope("prototype")
public class DatabaseJarZipHandler implements IUpload {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseJarZipHandler.class);


    public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination,
                                        String extensionOfFileTypeToBeImported) {


        String extensionSupported = "Zip or Jar";

        String fileName = FilenameUtils.getName(fileObject.getName());
        String suffix = FilenameUtils.getExtension(fileName);
        if (!("zip".equalsIgnoreCase(suffix) || "jar".equalsIgnoreCase(suffix))) {
            logger.error("Selected file to upload has a different extension than " + extensionSupported +
                    ". Aborting the process.");
            request.setAttribute("response", "Select a file only with extension" + extensionSupported);
            throw new OperationFailedException("Select a file only with extension " + extensionSupported);
            //return false;
        }
        //Principal principal = AuthenticationUtils.getUserDetails();
        //String prefix = FilenameUtils.getBaseName(fileName) + "_" + principal.getUsername();
        /*
         * Save and process the uploaded content from temp directory
		 */
        String folderName = TempDirectoryCleaner.getTempDirectory() + File.separator;
        File destinationFile = new File(folderName + fileName);
        try {
            // Prepare unique local file based on file name of uploaded file.
            if (destinationFile.exists()) {
                destinationFile.delete();
            }
            if (destinationFile.createNewFile()) {
                logger.debug("Created the file " + destinationFile);
                fileObject.write(destinationFile);
            } else {
                logger.debug("Couldn't create the file " + destinationFile);
            }
            // Write uploaded file to local file.
            /*
             * If there is any password, use it to unzip it
			 */
            String password = request.getParameter("zipFilePassword");


            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String targetString = suffix.equalsIgnoreCase("zip") ? applicationProperties.getPluginPath() : applicationProperties.getDriverPath();
            File targetDestination = new File(targetString);

            if (suffix.equalsIgnoreCase("zip")) {
                File tempFolder = new File(folderName + File.separator + System.currentTimeMillis());
                FileArchive fileArchive = new FileArchive();
                if (!fileArchive.unzip(destinationFile.getAbsolutePath(), tempFolder.getAbsolutePath(), password)) {
                    logger.error("Selected file to upload couldn't be unzipped. Aborting the process.");
                    request.setAttribute("response", "Selected file to upload couldn't be unzipped.");
                    return false;
                }

                for (File items : tempFolder.listFiles()) {

                    if (items.isDirectory()) {
                        FileUtils.moveDirectoryToDirectory(items, targetDestination, false);
                    } else {
                        FileUtils.moveFileToDirectory(items, targetDestination, false);
                    }
                }

                tempFolder.delete();

            }

            if (suffix.equalsIgnoreCase("jar")) {
                //FileUtils.copyFileToDirectory(destinationFile, targetDestination);
                FileUtils.moveFileToDirectory(destinationFile, targetDestination, false);
            }


        } catch (Exception e) {
            logger.error("Exception", e);
            String message = e.getMessage();
            if (message != null && (message.contains("already") && message.contains("exists"))) {
                message = "The file already exists";
            }
            throw new OperationFailedException("The file could not be imported. " + message);
        }
        request.setAttribute("message", "The file has been imported successfully");
        return true;
    }


}
