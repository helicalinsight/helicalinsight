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

package com.helicalinsight.efw.io.delete;

import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.io.IOOperationsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is an utility class for Delete operation. Supports
 * <code>DeleteOperationHandler</code>.
 * </p>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 */
public class DeleteOperationUtility {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOperationUtility.class);
    /**
     * The content of the operations tag of setting.xml
     */
    private final Map<String, Map<String, String>> operationsTagMap = new IOOperationsUtility()
            .getMapOfOperationSettings();
    /**
     * The list of extensions for which setting.xml has configuration
     */
    private List<String> listOfFileExtensions;

    /**
     * Setter for listOfFileExtensions
     *
     * @param listOfFileExtensions a <code>List</code> of extensions
     */
    public void setListOfFileExtensions(List<String> listOfFileExtensions) {
        this.listOfFileExtensions = listOfFileExtensions;
    }

    /**
     * Before physically deleting files and folders, the methods checks whether
     * all files and folders could be deleted. If yes it will be returning true.
     *
     * @param directory The directory under concern
     * @return true if all the files and folders in directory can be deleted
     */
    public boolean tryDeleting(File directory) throws UnSupportedRuleImplementationException {
        if (directory == null || !directory.exists()) {
            logger.error("Directory " + directory + " doesn't exist!");
            throw new OperationFailedException("Directory " + directory + " doesn't exists");
            //return false;
        }

        return trySatisfyingRules(directory);
    }

    /**
     * <p>
     * Applies the rules specific to the directory deletion. If the user has no
     * authority to delete he will not be allowed to delete.
     * </p>
     *
     * @param directory The directory under concern
     * @return true if the credentials are matching
     */
    private boolean trySatisfyingRules(File directory) throws UnSupportedRuleImplementationException {
        File[] files = directory.listFiles();
        if (files != null) {
            if (!inspect(files)) {
                return false;
            }
        } else {
            logger.debug(directory + " is empty. No conditions for deletion.");
        }
        return true;
    }

    /**
     * <p>
     * Applies the file and folder specific deletion rules to files array.
     * </p>
     *
     * @param files An array of <code>File</code>s
     */
    private boolean inspect(File[] files) throws UnSupportedRuleImplementationException {
        for (File file : files) {
            if (file.isFile()) {
                if (!trySatisfyingRulesForFile(file)) {
                    logger.info("Can't delete the file " + file + ". Aborting the operation.");
                    return false;
                }
            } else {
                if (!trySatisfyingRules(file)) {
                    logger.info("Can't delete the file " + file + ". Aborting the operation.");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>
     * The file is deletable only if its extension has configuration in
     * setting.xml. File specific deletion rule is applied by this method.
     * </p>
     *
     * @param file The file under concern
     * @return true if the rules are satisfied
     */
    private boolean trySatisfyingRulesForFile(File file) throws UnSupportedRuleImplementationException {
        String[] array = (file.toString().split("\\.(?=[^\\.]+$)"));
        String actualExtension;
        if (array.length >= 2) {
            actualExtension = array[1];
            if (listOfFileExtensions != null && listOfFileExtensions.contains(actualExtension)) {
                return applyFileSpecificRule(file, actualExtension);
            } else {
                logger.debug("File with out extension in Settings. No conditions for deleting. " +
                        "Its extension was " + actualExtension);
            }
        } else {
            logger.debug("File with out any extension. No conditions for deleting.");
            return true;
        }
        return true;
    }

    /**
     * File specific deletion rule is applied by this method by invoking the
     * configuration class from setting.xml
     *
     * @param file            a <code>File</code> which specifies the file name that is to
     *                        be deleted
     * @param actualExtension a <code>String</code> which specifies the file extension.
     * @return true if file is deletable else return false
     */
    private boolean applyFileSpecificRule(File file, String actualExtension) throws
            UnSupportedRuleImplementationException {
        if (file != null && actualExtension != null) {
            String key = findCorrespondingKey(actualExtension);
            String clazz = findCorrespondingClass(key, "delete");
            logger.debug("key : " + key + " clazz : " + clazz);
            if (key != null && clazz != null) {
                IDeleteRule iDeleteRule = FactoryMethodWrapper.getTypedInstance(clazz, IDeleteRule.class);
                if (iDeleteRule != null) {
                    return iDeleteRule.isDeletable(file);
                } else {
                    throw new ClassNotConfiguredException("The class " + clazz + " instance could" +
                            " not be obtained.");
                }
            }
            logger.error("Can't delete the file " + file + ". Aborting the operation.");
        }
        return false;
    }

    /**
     * <p>
     * Based on the file key type, the corresponding class from the setting.xml
     * is returned for that key
     * </p>
     *
     * @param key    The key for the file extension in setting.xml
     * @param action file operation under concern.
     * @return a <code>String</code> which specifies the class name.
     */
    public String findCorrespondingClass(String key, String action) {
        if (operationsTagMap != null && key != null) {
            Map<String, String> map = operationsTagMap.get(key);
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (action.equalsIgnoreCase(entry.getKey())) {
                        return entry.getValue();
                    }
                }
            }
        }
        logger.error("Couldn't find the value for key delete. Returning null.");
        return null;
    }

    /**
     * <p>
     * Obtains the corresponding key for the file with extension actualExtension
     * </p>
     *
     * @param actualExtension a <code>String</code> which is extension of a file
     * @return The corresponding key for the actualExtension
     */
    public String findCorrespondingKey(String actualExtension) {
        IOOperationsUtility ioOperationUtility = new IOOperationsUtility();

        Map<String, String> keyValuePairs = ioOperationUtility.getVisibleExtensionsKeyValuePairs();
        if (keyValuePairs != null && actualExtension != null) {
            for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
                if (actualExtension.equalsIgnoreCase(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        logger.error("Couldn't find the value for key {}. Returning null.", actualExtension);
        return null;
    }

    /**
     * <p>
     * Deletes the files by invoking the specific classes for the corresponding
     * files. No conditions are considered for files with no configuration from
     * setting.xml. They are simply deleted.
     * <p/>
     * </p>
     *
     * @param file a <code>File</code> which has to be deleted.
     */
    public void deleteFile(File file) throws UnSupportedRuleImplementationException {
        String[] array = (file.toString().split("\\.(?=[^\\.]+$)"));
        String actualExtension;
        if (array.length >= 2) {
            actualExtension = array[1];
            if (listOfFileExtensions != null && listOfFileExtensions.contains(actualExtension)) {
                logger.debug("The file has " + file + " has some concerns before deletion");
                clean(file, actualExtension);
            } else {
                if (file.delete()) {
                    logger.debug(file + " has no Settings. No conditions for deleting. Deleted.");
                } else {
                    logger.debug(file + " couldn't be deleted.");
                }
            }
        } else {
            if (file.delete()) {
                logger.debug(file + " has no extension. No conditions for deleting. Deleted.");
            } else {
                logger.debug(file + " couldn't be deleted.");
            }
        }
    }

    /**
     * <p>
     * Delete operation is handled by the specific object which is configured in
     * setting.xml for the specific type of extension.
     * </p>
     *
     * @param file            The file under concern
     * @param actualExtension a <code>String</code> which specifies extension of file
     */
    private void clean(File file, String actualExtension) throws UnSupportedRuleImplementationException {
        String key = findCorrespondingKey(actualExtension);
        String clazz = findCorrespondingClass(key, "delete");
        logger.debug("key : " + key + " clazz : " + clazz);
        if (key != null && clazz != null) {
            /*
             * Ideally expecting a singleton
			 */
            IDeleteRule iDeleteRule = FactoryMethodWrapper.getTypedInstance(clazz, IDeleteRule.class);
            logger.debug("Invoking the file specific delete operation handler for file " + file);
            if (iDeleteRule != null) {
                iDeleteRule.delete(file);
            } else {
                throw new ClassNotConfiguredException("The class " + clazz + " instance could" +
                        " not be obtained.");
            }
        }
    }

    /**
     * <p>
     * Deletes the directories by applying the same rules as that of file
     * deletion. No conditions are considered for files with no configuration
     * from setting.xml
     * </p>
     *
     * @param directory a <code>File</code> which contains directory name
     */
    public void deleteDirectory(File directory) throws UnSupportedRuleImplementationException {
        logger.info("Trying to delete the directory " + directory);
        if (!directory.exists()) {
            logger.info("Directory {} doesn't exist", directory);
        }
        File[] files = directory.listFiles();
        if (files != null) {
            logger.info(directory + " contents : " + Arrays.asList(files));
            for (File file : files) {
                if (file.isFile()) {
                    deleteFile(file);
                } else {
                    deleteDirectory(file);
                }
            }
            IOOperationsUtility.deleteEmptyDirectoryWithLogs(directory);
        } else {
            logger.info("Directory " + directory + " is empty.");
            IOOperationsUtility.deleteEmptyDirectoryWithLogs(directory);
        }
    }
}
