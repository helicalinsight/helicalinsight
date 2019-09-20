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
