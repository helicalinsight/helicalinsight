package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.JsonUtils;
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
import java.util.List;

/**
 * Created by author on 1/30/2020.
 *
 * @author Rajesh
 */
@Component("imageUploadHandler")
@Scope("prototype")
public class ImageUploadHandler implements IUpload {
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadHandler.class);

    @Override
    public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination, String extensionOfFileTypeToBeImported) {
        //String dir = request.getParameter("dir");
        List<String> extensionSupported = JsonUtils.supportedImageExtensions;
        String fileName = FilenameUtils.getName(fileObject.getName());
        String suffix = FilenameUtils.getExtension(fileName);
        if (suffix != null) {
            suffix = suffix.toLowerCase();
        }

        if (!extensionSupported.contains(suffix)) {
            logger.error("Selected file to upload has a different extension than " + extensionSupported +
                    ". Aborting the process.");
            request.setAttribute("response", "Select a file only with extension" + extensionSupported);
            throw new OperationFailedException("Select a file only with extension " + extensionSupported);
            //return false;
        }

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
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String targetString = applicationProperties.getSolutionDirectory() + File.separator + destination;
            File targetDestination = new File(targetString);
            FileUtils.moveFileToDirectory(destinationFile, targetDestination, false);

        } catch (Exception e) {
            logger.error("Exception", e);
            String message = e.getMessage();
            if (message.contains("Destination") && message.contains("exists")) {
                message = "The file already exists";
            }
            throw new OperationFailedException("The file could not be imported. " + message);
        }
        request.setAttribute("message", "The image file has been imported successfully");
        return true;
    }
}
