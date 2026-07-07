package com.helicalinsight.efw.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The solution directory consists of directories which will be displayed in the
 * main view. All the directories will be validated by using this object.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @version 1.0
 * @since 1.0
 */
public class DirectoryValidator {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryValidator.class);
    /**
     * The directory under concern
     */
    private String directory;

    /**
     * Checks whether a directory is empty or not
     *
     * @return true if found empty
     */
    public boolean isDirectoryEmpty() {
        boolean isEmpty = false;
        File dir = new File(directory);
        if (dir.isDirectory()) {
            isEmpty = dir.list().length <= 0;
        }
        logger.debug(String.format("The directory %s is %s ", directory, (isEmpty ? "empty" : "not empty.")));
        return isEmpty;
    }

    /**
     * Checks whether the directory exists
     *
     * @return true if physically exists
     */
    public boolean isDirectoryPresent() {
        boolean isPresent = false;
        File dir = new File(directory);
        if (dir.isDirectory() && dir.exists()) {
            isPresent = true;
        }
        return isPresent;
    }

    /**
     * Getter method
     *
     * @return Returns the current directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * A simple setter
     *
     * @param directory The directory under concern
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
