package com.helicalinsight.resourcedb;

import com.helicalinsight.efw.exceptions.ClassNotConfiguredException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.io.delete.IDeleteRule;
import com.helicalinsight.efw.utility.FileBrowserCacheUtils;
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
 * @author Karthik
 * @version 5.0
 * @since 1.1
 */
public class DeleteOperationUtilityDB {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOperationUtilityDB.class);

    private final Map<String, Map<String, String>> operationsTagMap = new IOOperationsUtility()
            .getMapOfOperationSettings();

    private List<String> listOfFileExtensions;

    public void setListOfFileExtensions(List<String> listOfFileExtensions) {
        this.listOfFileExtensions = listOfFileExtensions;
    }


    public boolean tryDeleting(File directory) throws UnSupportedRuleImplementationException {
        if (directory == null || !directory.exists()) {
            logger.error("Directory " + directory + " doesn't exist!");
            throw new OperationFailedException("Directory " + directory + " doesn't exists");
            //return false;
        }

        return trySatisfyingRules(directory);
    }


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


    public void deleteFile(File file) throws UnSupportedRuleImplementationException {
        String[] array = (file.toString().split("\\.(?=[^\\.]+$)"));
        String actualExtension;
        if (array.length >= 2) {
            actualExtension = array[1];
            if (listOfFileExtensions != null && listOfFileExtensions.contains(actualExtension)) {
                logger.debug("The file has " + file + " has some concerns before deletion");
                clean(file, actualExtension);
            } else {
                FileBrowserCacheUtils.deleteFromDb(file.getAbsolutePath());
                if (file.delete()) {
                    logger.debug(file + " has no Settings. No conditions for deleting. Deleted.");
                } else {
                    FileBrowserCacheUtils.madeChangesDb(file.getAbsolutePath());
                    logger.debug(file + " couldn't be deleted.");
                }
            }
        } else {
            FileBrowserCacheUtils.deleteFromDb(file.getAbsolutePath());
            if (file.delete()) {
                logger.debug(file + " has no extension. No conditions for deleting. Deleted.");
            } else {
                FileBrowserCacheUtils.madeChangesDb(file.getAbsolutePath());
                logger.debug(file + " couldn't be deleted.");
            }
        }
    }


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


    public void deleteDirectory(File directory) throws UnSupportedRuleImplementationException {
        logger.info("Trying to delete the directory " + directory);
        if (!directory.exists()) {
            logger.info("Directory {} doesn't exist", directory);
        }
        File[] files = directory.listFiles();
        if (files != null) {
            logger.info(directory + " contents : " + Arrays.asList(files));
            for (File file : files) {
                if (file.exists()) {
                    if (file.isFile()) {
                        deleteFile(file);
                    } else {
                        deleteDirectory(file);
                    }
                }
            }
            IOOperationsUtility.deleteEmptyDirectoryWithLogs(directory);
        } else {
            logger.info("Directory " + directory + " is empty.");
            IOOperationsUtility.deleteEmptyDirectoryWithLogs(directory);
        }
    }
}
