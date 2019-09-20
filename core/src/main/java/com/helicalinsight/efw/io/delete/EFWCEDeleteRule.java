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

import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajesh
 *         Created by helical019 on 6/21/2019.
 */
public class EFWCEDeleteRule implements IDeleteRule {
    @Override
    public boolean isDeletable(File file) {
        return true;
    }

    @Override
    public void delete(File file) {
        deleteRelatedFiles(file);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    private void deleteRelatedFiles(File efwceFile) {
        List<File> listOfFiles = new ArrayList<>();
        String efwcePath = efwceFile.getPath();
        String directory = efwceFile.getParent();
        String uuid = FilenameUtils.getBaseName(efwcePath);

        File efwFile = new File(directory + File.separator + uuid + "." + JsonUtils.getEfwExtension());

        File efwdFile = new File(directory + File.separator + uuid + "." + JsonUtils.getEfwdExtension());

        File efwvfFile = new File(directory + File.separator + uuid + "." + JsonUtils.getEfwvfExtension());

        File htmlFile = new File(directory + File.separator + uuid + ".html");

        listOfFiles.add(efwceFile);
        listOfFiles.add(efwFile);
        listOfFiles.add(efwdFile);
        listOfFiles.add(efwvfFile);
        listOfFiles.add(htmlFile);
        listOfFiles.forEach(file -> IOOperationsUtility.safeDeleteWithLogs(file));

    }

}
