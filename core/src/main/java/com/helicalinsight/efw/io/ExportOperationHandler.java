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
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * When a user requests for exporting files, the directories which he has chosen
 * will be searched for the efwsr files and all those files will be zipped with
 * an extension crt sent to the user as the response.
 * <p/>
 * Along with the folders the user may choose files also. Only efwsr files are
 * supported to be exported.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
public class ExportOperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExportOperationHandler.class);
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * efwsr extension from setting.xml
     */
    private final String efwsrKeyValue = JsonUtils.getEFWSRExtension();

    /**
     * Validates the sourceArray. All the file and folders in the source array
     * should actually exist and the then a list of efwsr files will be prepared
     * which need to be exported.
     *
     * @param sourceArray The request parameter sourceArray
     * @return A list of efwsr files to be exported
     */
    public List<File> validateSource(String sourceArray) {
        JSONArray sourceJSON;
        try {
            sourceJSON = (JSONArray) JSONSerializer.toJSON(sourceArray);
        } catch (JSONException ex) {
            logger.error("JSONException : " + ex);
            return null;
        }
        Iterator<?> iterator = sourceJSON.iterator();
        List<File> listOfFilesToBeZipped = new ArrayList<>();
        try {
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                File file = new File(this.applicationProperties.getSolutionDirectory() + File.separator + key);
                if (!file.exists()) {
                    logger.error("The value " + key + " indicates an invalid location on file " +
                            "system. Operation aborted.");
                    throw new OperationFailedException("The file does not exists");
                    //return null;
                }
                if (file.isFile()) {
                    if (!inspectExtension(file, efwsrKeyValue)) {
                        /*
                         * Do not entertain any other extension values
						 */
                        return null;
                    } else {
                        listOfFilesToBeZipped.add(file);
                        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of
                        // authorization
                        /*if (isAuthorized(file)) {
                            listOfFilesToBeZipped.add(file);
                        } else {
                            return null;
                        }*/
                    }
                } else if (file.isDirectory()) {
                    logger.info("The file " + file + " is a directory. Currently directory " +
                            "zipping is not supported. " +
                            "Instead collecting the efwsr files in it.");
                    collectEFWSRFiles(file, listOfFilesToBeZipped);
                } else {
                    logger.error("The key " + key + " is neither a directory nor file. Check " +
                            "properties. Operation aborted.");
                    return null;
                }
            }
        } catch (JSONException ex) {
            logger.error("SourceArray is not an array. Aborting the operation.", ex);
            return null;
        }
        return listOfFilesToBeZipped;
    }

    /**
     * <p>
     * Iterates over the given directory and collects the efwsr files
     * </p>
     *
     * @param directory             A directory in the solution directory
     * @param listOfFilesToBeZipped The list of efwsr files to be zipped
     */
    private void collectEFWSRFiles(File directory, List<File> listOfFilesToBeZipped) {
        File[] files = directory.listFiles();
        if (files != null && efwsrKeyValue != null) {
            for (File file : files) {
                if (file.isFile() && efwsrKeyValue.equalsIgnoreCase(FileUtils.getExtensionOfFile(file))) {
                    listOfFilesToBeZipped.add(file);
                    //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of
                    // authorization
                    /*if (isAuthorized(file)) {
                        listOfFilesToBeZipped.add(file);
                    }*/
                } else if (file.isDirectory()) {
                    collectEFWSRFiles(file, listOfFilesToBeZipped);
                }
            }
        }
    }

    /**
     * <p>
     * Currently only efwsr files exporting is supported. So, this method checks
     * whether the file extension is actually matching with the value
     * corresponding to the efwsr key value from the setting.xml.
     * </p>
     *
     * @param file          The file under concern
     * @param efwsrKeyValue efwsr key value from the setting.xml
     * @return true if the file is an efwsr file
     */
    private boolean inspectExtension(File file, String efwsrKeyValue) {
        String[] array = (file.toString().split("\\.(?=[^\\.]+$)"));
        String actualExtension;
        if (array.length >= 2) {
            actualExtension = array[1];
            if (efwsrKeyValue.equalsIgnoreCase(actualExtension)) {
                return true;
            } else {
                logger.debug("Can't zip the " + actualExtension + " files. Aborting the operation" +
                        ".");
                return false;
            }
        } else {
            logger.debug("File with out any extension. No conditions for deleting.");
            return false;
        }
    }
}
