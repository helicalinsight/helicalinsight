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

package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Cleans the System/Temp directory in solution directory
 */
public class TempDirectoryCleaner {

    private final static Logger logger = LoggerFactory.getLogger(TempDirectoryCleaner.class);

    /**
     * Cleans the System/Temp directory in solution directory
     *
     * @param temporaryDirectory The location of the directory on the file system
     */
    public static void clean(File temporaryDirectory) {
        PropertiesFileReader reader = new PropertiesFileReader();
        Map<String, String> messagesMap = reader.read("message.properties");

        int maxSize = Integer.parseInt(messagesMap.get("maxSize"));

        long size = FileUtils.getFolderSize(temporaryDirectory);
        if (logger.isInfoEnabled()) {
            logger.info("Temporary directory size = " + size);
        }
        if (size > maxSize) {
            purgeTempFiles(temporaryDirectory);
        }
    }

    /**
     * Deletes the directory contents
     *
     * @param directoryName The directory under concern
     */
    private static void purgeTempFiles(File directoryName) {
        logger.info("Directory to be cleaned : " + directoryName);

        File[] files = directoryName.listFiles();

        if (files != null) {
            for (File file : files) {
                logger.info("Trying to delete " + file + "." + (file.delete() ? "Deleted." : "Couldn't be deleted."));
            }
        }
    }

    /**
     * Returns the System/Temp directory path as a string in solution directory
     *
     * @return The location of System/Temp directory
     */
    public static File getTempDirectory() {
        File file = new File(ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Temp");
        if (!file.exists()) {
            boolean status = file.mkdir();
            logger.debug("The temp directory is " + (status ? "created" : "not created"));
        }
        return file;
    }
}
