package com.helicalinsight.efw.io;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileBrowserCacheUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.EfwFolder;

/**
 * A new directory creation service is the responsibility of this handler class.
 *
 * @author Rajasekhar
 * @version 1.0
 * @since 1.1
 */
@Component
@Scope("prototype")
public class NewFolderHandler {
    private static final Logger logger = LoggerFactory.getLogger(NewFolderHandler.class);

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * The method validates the given sourceArray and handles the new folder
     * creation service in the desired locations.
     *
     * @param sourceArray      The request parameter sourceArray
     * @param targetFolderName The new folder that has to be created
     * @return true if folder is created successfully else will return false
     */
    public String[] handle(String sourceArray, String targetFolderName) {
        JsonArray sourceJSON;
        try {
            sourceJSON = JsonParser.parseString(new Gson().toJson(sourceArray)).getAsJsonArray();
        } catch (JsonIOException exception) {
            logger.error("Exception occurred ", exception);
            return null;
        }


        if ("[]".equals(sourceArray) || sourceArray == null) {
            throw new RequiredParameterIsNullException("Parameter sourceArray parameter has" + " " +
                    "no strings! Operation is aborted.");
        }

        Iterator<?> iterator = sourceJSON.iterator();
        /*
         * Get efwFolder extension
		 */
        String []  anyException =  new String[sourceJSON.size()];
        String extension = JsonUtils.getFolderFileExtension();
        int count=0;
        while (iterator.hasNext()) {
            String location = (String) iterator.next();
            File directory = new File(applicationProperties.getSolutionDirectory() + File.separator + location);
            anyException[count++] = create(targetFolderName, extension, location, directory);
        }

        return anyException;
    }

    /**
     * <p>
     * Tries to create a directory with the targetFolderName parameter. The actual
     * directory name will be current system time. Inside that directory, a file
     * with name index and with extension efwFolder will be created, in which a
     * tag will be created with the user specified directory name.
     * </p>
     *
     * @param targetFolderName The name of the new folder to be created
     * @param extension        The extension of the xml file
     * @param location         The location(relative) in which the directory has to be
     *                         created
     * @param directory        The directory in which the directory has to be created
     * @return true if folder is created successfully otherwise returns false
     */
    private String create(String targetFolderName,
                          String extension, String location,
                           File directory) {
        String anyException=null;
        if (directory.isFile()) {
            throw new OperationFailedException("Cannot create a directory in a file");
        } else if (directory.isDirectory()) {
            //Check if the directory has index file
            anyException = createDirectory(targetFolderName, extension, location, directory);

        } else if (!directory.exists()) {
            logger.error("location " + location + " doesn't satisfy a system dependent criteria");
            throw new OperationFailedException("The target location do not exists");
        }
        return anyException;
    }

    /**
     * Creates the directory and the corresponding xml meta data
     *
     * @param folderName   The new folder that has to be created
     * @param extension    The extension of the xml file
     * @param location     The location(relative) in which the directory has to be
     *                     created
     * @param directory    The directory in which the directory has to be created
     * @return true if the directory is created
     */
    private String createDirectory(String folderName, String extension, String location,
                                    File directory) {
        logger.debug("Trying to create a new directory in the location : {}", location);
        String fileName = ""+System.currentTimeMillis();
        File directoryToBeCreated = new File(directory.toString() + File.separator + fileName);
        logger.info("New directory is created with system time in {} ", directory.toString());
        if (!createXML(directoryToBeCreated, folderName, extension)) {
            logger.error("Failed to create the xml with extension {}", extension);

            fileName="";
        }
        return fileName;
    }

    /**
     * Creates the xml file index.efwfolder in the corresponding directory
     *
     * @param directory  Directory where index file has to be created
     * @param folderName The new folder that has to be created
     * @param extension  The extension of the xml file
     * @return true if successfully created
     */

    private boolean createXML(File directory, String folderName, String extension) {
        EfwFolder newFolderTemplate = getNewFolderTemplate(folderName);
        JsonObject efwFolder = new JsonObject();
        efwFolder.add("efwfolder", JsonParser.parseString(new Gson().toJson(newFolderTemplate)).getAsJsonObject());
        File xmlFile = new File(directory.toString() + File.separator + "index." + extension);
        ApplicationUtilities.addLockPathForFileBrowser(directory.getAbsolutePath());
        ApplicationUtilities.addLockPathForFileBrowser(xmlFile.getAbsolutePath());
        //FileBrowserCacheRepository fileBrowserCacheRepository = ApplicationContextAccessor.getBean(FileBrowserCacheRepository.class);
        //fileBrowserCacheRepository.processNewFolder(directory, efwFolder, folderName);
        try {
            synchronized (NewFolderHandler.class) {
                if (directory.mkdir()) {
                    JaxbUtils.marshal(newFolderTemplate, xmlFile);
                    FileBrowserCacheUtils.madeChangesDb(directory.getAbsolutePath());
                } else {
                    FileBrowserCacheUtils.deleteFromDb(directory.getAbsolutePath());
                    logger.error("New directory couldn't be created in {} ", directory.toString());
                }
            }
        } catch (Exception ex) {
            ApplicationUtilities.removeLockPathForFileBrowser(directory.getAbsolutePath());
            ApplicationUtilities.removeLockPathForFileBrowser(xmlFile.getAbsolutePath());
            logger.error("Stack trace: ", ex);
            return false;
        }
        ApplicationUtilities.removeLockPathForFileBrowser(directory.getAbsolutePath());
        ApplicationUtilities.removeLockPathForFileBrowser(xmlFile.getAbsolutePath());
        return true;
    }

    /**
     * Returns a template for the index.efwFolder to be written as an xml file
     *
     * @param folderName The new folder that will be written in the xml
     * @return <code>JSONObject</code> which contains title, visibility and
     * security related information
     */
    private EfwFolder getNewFolderTemplate(String folderName) {
        EfwFolder efwFolder = ApplicationContextAccessor.getBean(EfwFolder.class);
        efwFolder.setTitle(folderName);
        efwFolder.setVisible("true");
        efwFolder.setSecurity(SecurityUtils.securityObject());
        return efwFolder;
    }
}
