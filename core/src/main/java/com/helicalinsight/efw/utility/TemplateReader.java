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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The instance of this class reads the file mentioned in the efw template tag.
 *
 * @author Avi
 * @author Muqtar Ahmed
 */
public class TemplateReader {

    private static final Logger logger = LoggerFactory.getLogger(TemplateReader.class);

    /**
     * The template file to be read
     */
    private final File file;
    /**
     * For processing purposes
     */
    private StringBuilder sb = null;

    /**
     * Constructs an instance of the same class with the file
     *
     * @param file The file under concern
     */
    public TemplateReader(File file) {
        this.file = file;
    }

    /**
     * Returns the file content as a string by reading it
     *
     * @return The file content as string
     */
    public String readTemplate() {
        String currentLine;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            sb = new StringBuilder();
            while ((currentLine = bufferedReader.readLine()) != null) {
                sb.append(currentLine);
                sb.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            logger.error("An Exception occurred", e);
            //handle error
        } finally {
            ApplicationUtilities.closeResource(bufferedReader);
        }
        logger.debug("Reading html file is completed.");
        return sb.toString();
    }
}