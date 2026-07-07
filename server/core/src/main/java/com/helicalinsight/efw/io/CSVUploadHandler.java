package com.helicalinsight.efw.io;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.AbstractFileOperationsOverNetwork;
import com.helicalinsight.datasource.FileOperationOverNetworkFactory;
import com.helicalinsight.datasource.managed.jaxb.DrillConfig;
import com.helicalinsight.datasource.managed.jaxb.StorageLocation;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("csvUploadHandler")
@Scope("prototype")
public class CSVUploadHandler implements IUpload {
    private static final Logger logger = LoggerFactory.getLogger(CSVUploadHandler.class);
    private static ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination,
                                        String extensionOfFileTypeToBeImported) {
        JsonObject drillConfig = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
        drillConfig=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfig);
        JsonObject enabledTypesJSON = drillConfig.getAsJsonObject("enabledTypes");
        List<String> supportedExtensionsList = new ArrayList<>();
        if (enabledTypesJSON.has(extensionOfFileTypeToBeImported)) {
            JsonObject configsonObject = enabledTypesJSON.getAsJsonObject(extensionOfFileTypeToBeImported)
                    .getAsJsonObject("config");
            String ssupportedExtensions = configsonObject.get("extensions").getAsString();
            List<String> items = Arrays.asList(ssupportedExtensions.split("\\s*,\\s*"));
            for (String eachItem : items) {
                supportedExtensionsList.add(eachItem.replace(".", "").trim());
            }

        } else {
            throw new OperationFailedException("Select a file only with extension csv or csv2");
        }

        String fileName = FilenameUtils.getName(fileObject.getName());
        String suffix = FilenameUtils.getExtension(fileName);/* if (!supportedExtensionsList.contains(suffix)) {
            logger.error("Selected file to upload has a different extension than " + suffix + ". Aborting the process.");
            request.setAttribute("response", "Select a file only with extension " + suffix);
            throw new OperationFailedException("Select a file only with extension " + suffix);
            // return false;
        }*/


        String prefix = FilenameUtils.getBaseName(fileName);
        /*
         * Save and process the uploaded content from temp directory
		 */
        //replacing all the dots from uploaded file name.(excluding extension)
        prefix = prefix.replaceAll("\\.", "_");
        String folderName = TempDirectoryCleaner.getTempDirectory() + File.separator + prefix;
        String destinationFileString = folderName + "." + suffix;
        File destinationFile = new File(destinationFileString);

        try {
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

            File xml = new File(JsonUtils.getDrillConfigPath());
            DrillConfig drillConfigFile = JaxbUtils.unMarshal(DrillConfig.class, xml);
            String storageImpl = drillConfigFile.getStorageImpl();
            AbstractFileOperationsOverNetwork fileOperationHandlerClass = FileOperationOverNetworkFactory.getFileOperationHandlerClass(storageImpl);
            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("copyFrom", destinationFileString);
            StorageLocation value = drillConfigFile.getDrillStorageLocations().getStorageDetails().get(0);
            requestJson.addProperty("copyTo", value.getPath());
            if (fileOperationHandlerClass != null) {
                fileOperationHandlerClass.updateFiles(requestJson);
            }
            JsonObject message = new JsonObject();
            message.addProperty("message", "Upload of " + fileName + " is successful");
            message.addProperty("fileName", fileName);
            request.setAttribute("message", message);

			/*
             * If there is any password, use it to unzip it
			 */
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            logger.error("Cannot able to write the file ", e);
            return false;
        }


        return true;
    }

    public void deleteTempFile(String fileName, File destinationFile) {
        if (destinationFile.delete()) {
            logger.debug("Successfully deleted the file " + fileName + " temp");
        }
    }

}
