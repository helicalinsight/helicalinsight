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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The class uses RandomAccessFile api to write a file with a particular
 * encoding
 *
 * @author Rajasekhar
 */
public class FileStatusInspectionUtility {

    private static final Logger logger = LoggerFactory.getLogger(FileStatusInspectionUtility.class);

    /**
     * returns true only if the file is completely written
     *
     * @param file       The file to written
     * @param htmlString The content of the file to be written
     * @param encoding   Encoding of the content. Usually utf-8
     * @return true only if the file is completely written
     */
    public boolean isCompletelyWritten(File file, String htmlString, String encoding) {
        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(file, "rw");
            byte[] html = htmlString.getBytes(encoding);
            stream.write(html);
            return true;
        } catch (IOException ex) {
            logger.error("Exception stack trace is ", ex);
            logger.info("Skipping file " + file.getName() + " as it's not completely written");
        } finally {
            ApplicationUtilities.closeResource(stream);
        }
        return false;
    }
}
