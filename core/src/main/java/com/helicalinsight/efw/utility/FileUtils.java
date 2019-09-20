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

import com.helicalinsight.efw.exceptions.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by author on 09-01-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Creates a file if it doesn't exists. Logs if the method creates file.
     *
     * @param file An abstract representation of a file
     * @throws IOException
     */
    public static void createFile(File file) throws IOException {
        if (!file.exists()) {
            if (file.createNewFile()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("The file %s does not exists. It has been " + "created.", file));
                }
            }
        }
    }


    /**
     * <p>
     * Creates a directory if it does not exists. Mostly used to create directories in System
     * directory
     *
     * @param directory a <code>File</code> which specify directory name.
     */
    public static void createDirectory(File directory) {
        if (!directory.exists()) {
            logger.debug("directory.exists() = " + directory.exists());
            if (directory.mkdir()) {
                logger.info(directory + "directory in System folder doesn't exist. Creating " +
                        directory + "directory.");
            }
        }
    }

    /**
     * <p>
     * This method is responsible to get extension of file in question.
     * <code>null</code> is returned if the file has no extension.
     * </p>
     *
     * @param file a <code>File</code> object which specifies absolute file name.
     * @return a <code>String</code> which specifies file extension if there is any. Else null.
     */
    public static String getExtensionOfFile(File file) {
        String[] array = (file.toString().split("\\.(?=[^\\.]+$)"));
        if (array.length >= 2) {
            return array[1];
        } else {
            return null;
        }
    }


    /**
     * <p>
     * This method is responsible to get extension of file in question.
     * <code>null</code> is returned if the file has no extension.
     * </p>
     *
     * @param file a <code>File</code> object which specifies absolute file name.
     * @return a <code>String</code> which specifies file extension if there is any. Else null.
     */
    public static String getExtension(String file) {
        String[] array = (file.split("\\.(?=[^\\.]+$)"));
        if (array.length >= 2) {
            return array[1];
        } else {
            return null;
        }
    }


    /**
     * Returns true if the maybeChild parameter is a child of possibleParent
     * parameter
     *
     * @param maybeChild     The supposed child directory
     * @param possibleParent The supposed parent directory
     * @return true if the maybeChild is a child of possibleParent
     * @throws java.io.IOException If some thing goes wrong during the IO Operation
     */
    public static boolean isChild(File maybeChild, File possibleParent) throws IOException {
        final File parent = possibleParent.getCanonicalFile();
        if (!parent.exists() || !parent.isDirectory()) {
            // this cannot possibly be the parent
            return false;
        }

        File child = maybeChild.getCanonicalFile();
        while (child != null) {
            if (child.equals(parent)) {
                return true;
            }
            child = child.getParentFile();
        }
        // No match found, and we've hit the root directory
        return false;
    }


    /**
     * Get the size of folder
     *
     * @param directory a <code>File</code> which specifies a directory name.
     * @return size of folder in bytes
     */
    public static long getFolderSize(File directory) {
        long length = 0;
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }
            }
        }
        return length;
    }


    /**
     * @param dir       directory to search
     * @param extension Extension to filter
     * @return List of Strings
     */
    public static List<String> getFilteredFileList(File dir, String extension) {
        List<String> filteredFileList = new ArrayList<>();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                filteredFileList.addAll(getFilteredFileList(new File(dir, child), extension));
            }
        } else {
            if (dir.getName().endsWith("." + extension)) filteredFileList.add(dir.getAbsolutePath());
        }
        return filteredFileList;
    }

    public static Properties getProperties(File modelFile) {
        String model;

        String charsetName = ApplicationUtilities.getEncoding();
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(modelFile), charsetName);
             Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A")
        ) {
            model = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException ex) {
            logger.error("Exception stack trace: ", ex);
            throw new RuntimeException("There was problem reading the model json file.", ex);
        }

        Properties info = new Properties();
        info.put("model", model);
        return info;
    }

    public static boolean safeDeleteFile(File file) {
        try {
            if (isFilePresent(file)) {
                org.apache.commons.io.FileUtils.forceDelete(file);
                return true;
            }
        } catch (IOException e) {
            logger.error("operation failed", e);
        }
        return false;
    }

    public static boolean isFilePresent(File file) {
        return file.exists();
    }
}