/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.EfwFolder;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;

/**
 * A new directory creation service is the responsibility of this handler class.
 *
 * @author Rajasekhar
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
    public boolean handle(String sourceArray, String targetFolderName) {
        JSONArray sourceJSON;
        try {
            sourceJSON = (JSONArray) JSONSerializer.toJSON(sourceArray);
        } catch (JSONException exception) {
            logger.error("Exception occurred ", exception);
            return false;
        }


        if ("[]".equals(sourceArray) || sourceArray == null) {
            throw new RequiredParameterIsNullException("Parameter sourceArray parameter has" + " " +
                    "no strings! Operation is aborted.");
        }

        Iterator<?> iterator = sourceJSON.iterator();
        /*
         * Get efwFolder extension
		 */
        boolean anyException = false;
        String extension = JsonUtils.getFolderFileExtension();
        while (iterator.hasNext()) {
            String location = (String) iterator.next();
            File directory = new File(applicationProperties.getSolutionDirectory() + File.separator + location);
            logger.debug("solution directory is :- " + applicationProperties.getSolutionDirectory());
            anyException = create(targetFolderName, anyException, extension, location, directory);
        }

        return !anyException;
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
     * @param anyException     a boolean which represents exception status
     * @param extension        The extension of the xml file
     * @param location         The location(relative) in which the directory has to be
     *                         created
     * @param directory        The directory in which the directory has to be created
     * @return true if folder is created successfully otherwise returns false
     */
    private boolean create(String targetFolderName, boolean anyException, String extension, String location,
                           File directory) {
        if (directory.isFile()) {
            throw new OperationFailedException("Cannot create a directory in a file");
        } else if (directory.isDirectory()) {
            //Check if the directory has index file
            anyException = createDirectory(targetFolderName, anyException, extension, location, directory);
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
     * @param anyException a boolean which represents exception status
     * @param extension    The extension of the xml file
     * @param location     The location(relative) in which the directory has to be
     *                     created
     * @param directory    The directory in which the directory has to be created
     * @return true if the directory is created
     */
    private boolean createDirectory(String folderName, boolean anyException, String extension, String location,
                                    File directory) {
        logger.debug("Trying to create a new directory in the location : {}", location);
        File directoryToBeCreated = new File(directory.toString() + File.separator + System.currentTimeMillis());
        if (directoryToBeCreated.mkdir()) {
            logger.info("New directory is created with system time in {} ", directory.toString());
            if (!createXML(directoryToBeCreated, folderName, extension)) {
                logger.error("Failed to create the xml with extension {}", extension);
                anyException = true;
            }
        } else {
            logger.error("New directory couldn't be created in {} ", directory.toString());
            anyException = true;
        }
        return anyException;
    }

    /*
     * A user is authorized to create a directory only there is such a file
     * called index.efwFolder and if his credentials match with his login
     * credentials.
     *
     * @param directory The directory under concern
     * @param extension The extension of the xml file
     * @return true if user authenticated
     */
    /*private boolean isUserAuthenticatedToCreateDirectory(File directory, String extension) {
        logger.debug("Checking user credentials before creating directory in {}", directory);
        IProcessor processor = new IProcessor();
        JSONObject jsonObject = processor.getJSONObject(directory + File.separator + "index." +
                extension, false);
        IResourceRule resourceSecurityRule = EFWFolderRuleValidator.getInstance();
        try {
            return resourceSecurityRule.validateFile(jsonObject);
        } catch (ImproperXMLConfigurationException ex) {
            logger.error("ImproperXMLConfigurationException", ex);
            return false;
        } catch (UnSupportedRuleImplementationException ex) {
            logger.error("UnSupportedRuleImplementationException", ex);
            return false;
        }
    }*/

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
        File xmlFile = new File(directory.toString() + File.separator + "index." + extension);
        try {
            synchronized (NewFolderHandler.class) {
                JaxbUtils.marshal(newFolderTemplate, xmlFile);
            }
        } catch (Exception ex) {
            logger.error("Stack trace: ", ex);
            return false;
        }
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
