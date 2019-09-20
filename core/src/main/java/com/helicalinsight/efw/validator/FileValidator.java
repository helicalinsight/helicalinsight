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
 * The various framework related xml files like efw, efwd, and efwvf have a
 * pre-defined file structure in xsd format. The efw files are validated using
 * the xsd file present at System/XSDfile directory using an instance of this
 * class.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 */
public class FileValidator {

    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);

    /**
     * The file under concern
     */
    private String file;

    /**
     * Getter for the file
     *
     * @return The file itself
     */
    public String getFile() {
        return file;
    }

    /**
     * Setter method for the file
     *
     * @param file The file under concern
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Checks whether the file exists physically or not
     *
     * @return true if the file physically exists
     */
    public boolean isFilePresent() {
        boolean fileExist = false;
        File xmlFle = new File(file);
        if (xmlFle.isFile() && xmlFle.exists()) {
            fileExist = true;
        }
        logger.info("The file " + file + (fileExist ? " exists." : " does not exists!"));
        return fileExist;
    }
}
