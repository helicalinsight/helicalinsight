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

import com.helicalinsight.efw.resourceloader.rules.IRule;

import java.io.File;

/**
 * Designed for file import facility
 * <p/>
 * Created by author on 21-10-2014.
 *
 * @author Rajasekhar
 */

public interface IImportRule extends IRule {
    /**
     * Imports the absolute file represented by the file parameter in to the
     * corresponding destination
     *
     * @param file        The file to be imported
     * @param destination The destination file path as string
     * @return false if an IOException occurs
     */
    boolean importFile(File file, String destination);

    /**
     * Returns true if the directory is validated i.e. if the directory further
     * doesn't consist of any other directories and only consists of efwsr files
     *
     * @param directory The directory to be validated
     * @return true if the directory is validated
     */
    boolean validateDirectory(File directory);
}
