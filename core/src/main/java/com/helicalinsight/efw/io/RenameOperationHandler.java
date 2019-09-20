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

import com.helicalinsight.datasource.managed.jaxb.EFWCE;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.Efw;
import com.helicalinsight.resourcesecurity.jaxb.EfwFolder;
import com.helicalinsight.resourcesecurity.jaxb.EfwResult;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * A helper class which handles the rename operation of specific files and
 * folders based on the configuration from the setting.xml.
 * <p>
 * Renaming is allowed if the user has matching credentials with the resources
 * he is trying to modify.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
public class RenameOperationHandler extends AbstractOperationsHandler {

    private static final Logger logger = LoggerFactory.getLogger(RenameOperationHandler.class);
    /**
     * Instance of singleton class <code>ApplicationProperties</code>
     */
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * List of <code>Map</code> objects which contain the original and new name
     * of the <code>File</code> objects
     */
    private List<Map<String, String>> listOfMaps;
    /**
     * List of file extensions for which setting.xml has configuration
     */
    private List<String> listOfExtensions;

    /**
     * <p>
     * Returns true if renaming of all the requested files and folders is
     * successful. The sourceArray should not be null or empty.
     * </p>
     *
     * @param sourceArray The request parameter sourceArray
     * @return true if the renaming is successful
     */
    public boolean handle(String sourceArray) {
        logger.debug("sourceArray : " + sourceArray);
        if (sourceArray == null || ("[]".equals(sourceArray) || "[[]]".equals(sourceArray))) {
            throw new RequiredParameterIsNullException("The source should not be blank");
            //return false;
        } else {
            // prepare list of files which are to be renamed.
            this.listOfMaps = new ArrayList<>();
        }
        return renameAll(listOfMaps, sourceArray);
    }

    /**
     * Validates the sourceArray and tries to rename. Returns false if the
     * sourceArray is invalid.
     *
     * @param listOfMaps  A <code>List<Map></code> which contains map(s) of old and new
     *                    names
     * @param sourceArray The request parameter sourceArray
     * @return false if renaming is not successful
     */
    private boolean renameAll(List<Map<String, String>> listOfMaps, String sourceArray) {
        if (!isSourceArrayValid(sourceArray)) {
            return false;
        }
        for (Map<String, String> map : listOfMaps) {
            rename(map);
        }
        return true;
    }

    /**
     * A sourceArray is validated only if it is in the form of an array.
     *
     * @param sourceArray The request parameter sourceArray
     * @return true if validated
     */
    private boolean isSourceArrayValid(String sourceArray) {
        JSONArray sourceJSON;
        try {
            sourceJSON = (JSONArray) JSONSerializer.toJSON(sourceArray);
        } catch (JSONException ex) {
            logger.error("JSONException : " + ex);
            return false;
        }
        return prepareMap(sourceJSON);
    }

    /**
     * <p>
     * Gets the original file name and new file name from <code>Map</code> and
     * check it is file or directory. Based on the whether the <code>File</code>
     * is a file or directory delegates the call to the appropriate methods.
     * <p/>
     * <code>Map</code> will consist of only one key value pair.
     * </p>
     *
     * @param map A <code>Map</code> which contains original file name as key
     *            and new File name as value.
     */
    private void rename(Map<String, String> map) {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        String original = null;
        String newName = null;
        //noinspection LoopStatementThatDoesntLoop
        for (Map.Entry<String, String> entry : entrySet) {
            original = entry.getKey();
            newName = entry.getValue();
            /*
             * Only one pair is expected. So break.
			 */
            break;
        }

        File fileToBeRenamed = new File(applicationProperties.getSolutionDirectory() + File.separator + original);
        logger.debug("Trying to rename : " + fileToBeRenamed);

        if (fileToBeRenamed.isFile()) {
            if (listOfExtensions == null) {
                this.listOfExtensions = super.getListOfExtensionsFromSettings();
            }
            renameFile(fileToBeRenamed, newName);
        } else {
            renameDirectory(fileToBeRenamed, newName);
        }
    }

    /**
     * Converts the input into convenient format for the sake of processing
     *
     * @param sourceJSON The request parameter sourceArray's json
     * @return true if the the files in the sourceArray really exist so that
     * they can be renamed
     */
    private boolean prepareMap(JSONArray sourceJSON) {
        Iterator<?> iterator = sourceJSON.iterator();
        try {
            while (iterator.hasNext()) {
                JSONArray jsonArray = (JSONArray) iterator.next();
                logger.debug("Set of original and new Names: jsonArray : " + jsonArray);
                if (!populateListOfMaps(listOfMaps, jsonArray)) {
                    return false;
                }
            }
        } catch (JSONException ex) {
            logger.error("sourceArray is not an array of arrays", ex);
            return false;
        }
        logger.debug("List on which rename operations to be performed on " + listOfMaps);
        return true;
    }

    /**
     * Renames the file fileToBeRenamed with the newName. Renames different
     * types of xml files like .result files or efwsr files
     *
     * @param fileToBeRenamed The file to be renamed
     * @param newName         The new name of the file
     */
    private void renameFile(File fileToBeRenamed, String newName) {
        String actualExtension;
        String[] array = (fileToBeRenamed.toString().split("\\.(?=[^\\.]+$)"));
        if (array.length >= 2) {
            actualExtension = array[1];
            if (!listOfExtensions.contains(actualExtension)) {
                logger.error("The file " + fileToBeRenamed + " can't be renamed.");
                throw new OperationFailedException("The file " + fileToBeRenamed + " can't be " +
                        "renamed.");
            }
            modifyXml(fileToBeRenamed, newName, actualExtension);
        }
    }

    /**
     * Renaming is not allowed if the user is not authorized to rename. The
     * index.efwFolder file should exist and its security credentials should
     * match with the currently logged in user credentials.
     *
     * @param directoryToBeRenamed A directory name which has to be renamed
     * @param newName              A <code>String</code> which specifies the new directory name.
     */
    private void renameDirectory(File directoryToBeRenamed, String newName) {
        String extension = JsonUtils.getFolderFileExtension();
        String resource = directoryToBeRenamed + File.separator + "index" + "." + extension;

        File indexFile = new File(resource);
        EfwFolder efwFolder = JaxbUtils.unMarshal(EfwFolder.class, indexFile);
        efwFolder.setTitle(newName);

        try {
            //This is a prototype object. So synchronize on class.
            synchronized (RenameOperationHandler.class) {
                JaxbUtils.marshal(efwFolder, indexFile);
            }
        } catch (Exception ex) {
            logger.error("The directory " + directoryToBeRenamed + " couldn't be renamed.", ex);
            throw new OperationFailedException("The directory " + directoryToBeRenamed +
                    " couldn't be renamed.");
        }
    }

    /**
     * Convert JSONArray into List<Map> for the sake of processing.
     *
     * @param listOfMaps A <code>List<Map></code> which contains map(s) of old and new
     *                   names
     * @param jsonArray  An array of the contents of sourceArray
     * @return true if the the files in the sourceArray really exist so that
     * they can be renamed
     */
    private boolean populateListOfMaps(List<Map<String, String>> listOfMaps, JSONArray jsonArray) {
        Iterator<?> iterator = jsonArray.iterator();
        int keyCount = 0;
        String key = null;
        String value = null;
        try {
            while (iterator.hasNext()) {
                if (keyCount == 0) {
                    key = (String) iterator.next();
                }
                if (keyCount == 1) {
                    value = (String) iterator.next();
                    break;
                }
                keyCount++;
            }
        } catch (JSONException ex) {
            logger.error("sourceArray is not an array of arrays", ex);
            return false;
        }
        Map<String, String> map = new HashMap<>();
        if (!doesFileExists(key)) {
            logger.error("The key " + key + " indicates an invalid location on file system. " +
                    "Operation aborted.");
            throw new OperationFailedException("The key " + key + " indicates an invalid location" +
                    " on file system. " +
                    "Operation aborted.");
            //return false;
        }
        map.put(key, value);
        listOfMaps.add(map);
        return true;
    }

    /**
     * The method modifies different types of xml files like .result files or
     * efwsr files. The tags of the corresponding xml files will be changed to
     * reflect the new name.
     *
     * @param fileToBeRenamed The file to be renamed
     * @param newName         The new name of the file
     * @param actualExtension The actual extension of the file to be renamed
     */
    private void modifyXml(File fileToBeRenamed, String newName, String actualExtension) {
        if (JsonUtils.getEFWSRExtension().equalsIgnoreCase(actualExtension)) {
            Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, fileToBeRenamed);
            efwsr.setReportName(newName);
            synchronizeFileOperation(fileToBeRenamed, efwsr);
        } else if (JsonUtils.getEfwResultExtension().equalsIgnoreCase(actualExtension)) {
            EfwResult efwResult = JaxbUtils.unMarshal(EfwResult.class, fileToBeRenamed);
            efwResult.setResultName(newName);
            synchronizeFileOperation(fileToBeRenamed, efwResult);
        } else if (JsonUtils.getEfwExtension().equalsIgnoreCase(actualExtension)) {
            Efw efwFile = JaxbUtils.unMarshal(Efw.class, fileToBeRenamed);
            efwFile.setTitle(newName);
            synchronizeFileOperation(fileToBeRenamed, efwFile);
        } else if (JsonUtils.getEFWCEExtension().equalsIgnoreCase(actualExtension)) {
            EFWCE efwce = JaxbUtils.unMarshal(EFWCE.class, fileToBeRenamed);
            efwce.setName(newName);
            synchronizeFileOperation(fileToBeRenamed, efwce);
        } else {
            throw new OperationFailedException("The file " + fileToBeRenamed + " can't be renamed" +
                    " as only saved results and reports are supported.");
        }
    }

    /**
     * Checks whether the given file exists or not in the solution directory
     *
     * @param file a file name
     * @return true if file exists. Otherwise false
     */
    private boolean doesFileExists(String file) {
        return new File(this.applicationProperties.getSolutionDirectory() + File.separator +
                file).exists();
    }

    private void synchronizeFileOperation(File fileToBeRenamed, IResource iResource) {
        synchronized (RenameOperationHandler.class) {
            JaxbUtils.marshal(iResource, fileToBeRenamed);
        }
    }
}
