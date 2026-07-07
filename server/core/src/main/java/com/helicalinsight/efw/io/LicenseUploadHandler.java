package com.helicalinsight.efw.io;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;


@Component("licenseUploadHandler")
@Scope("prototype")
public class LicenseUploadHandler implements IUpload {
	private static final Logger logger = LoggerFactory.getLogger(LicenseUploadHandler.class);

	@Override
	public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination,
			String extensionOfFileTypeToBeImported) {
        String extensionSupported = "licence";

        String fileName = FilenameUtils.getName(fileObject.getName());
        String suffix = FilenameUtils.getExtension(fileName);
        if (!("licence".equalsIgnoreCase(suffix))) {
            logger.error("Selected file to upload has a different extension than " + extensionSupported +
                    ". Aborting the process.");
            request.setAttribute("response", "Select a file only with extension" + extensionSupported);
            throw new OperationFailedException("Select a file only with extension " + extensionSupported);
            //return false;
        }
        String prefix = FilenameUtils.getBaseName(fileName);
        /*
         * Save and process the uploaded content from temp directory
		 */
        String folderName = TempDirectoryCleaner.getTempDirectory() + File.separator + prefix;
        File destinationFile = new File(folderName + "." + suffix);
      try{
            // Prepare unique local file based on file name of uploaded file.
            if (!destinationFile.exists()) {
                if (destinationFile.createNewFile()) {
                    logger.debug("Created the file " + destinationFile);
                } else {
                    logger.debug("Couldn't create the file " + destinationFile);
                }
           }
            // Write uploaded file to local file.
            fileObject.write(destinationFile);
            /*
             * If there is any password, use it to unzip it
			 */
      }catch(Exception e){
    	  logger.error("cannot able to write the file");
    	  return false;
      }
      try{
            if(IOOperationsUtility.isBinaryFile(destinationFile)){
           // ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
           // String targetString = request.getContextPath();
            String targetString = request.getSession().getServletContext().getRealPath("/");
            File targetDestination = new File(targetString);

            if(suffix.equalsIgnoreCase("licence")){
                FileUtils.copyFileToDirectory(destinationFile, targetDestination);
            }
           if( destinationFile.delete()){
        	   logger.debug("Successfully deleted the file "+ fileName +" temp");
           }
           request.setAttribute("message", "Upload of "+fileName+" is successful and this may get reflected in next 6 hours. In case it does not gets Reflected then please Restart the Application");
            }
            else{

            	logger.error("Selected file to upload is not a valid " + fileName +
                        ". Aborting the process.");
            	if( destinationFile.delete()){
              	   logger.debug("Successfully deleted the file "+ fileName +" temp");
                 }
                request.setAttribute("response", "Selected file to upload is not a valid " + fileName +
                        ". Aborting the process.");
                throw new OperationFailedException("Selected file to upload is not a valid " + fileName +
                        ". Aborting the process.");
            }
      }catch(IOException ie){
    	  logger.debug("file operation failed");
    	  return false;
      }

        return true;
	}

}
