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

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by author on 22-10-2014.
 * <p/>
 * This singleton class handles the deletion of specific files i.e. .result
 * files
 *
 * @author Rajasekhar
 */
public final class EFWSavedResultDeleteRule implements IDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWSavedResultDeleteRule.class);

    /**
     * Private for the purpose of singleton pattern
     */
    private EFWSavedResultDeleteRule() {
    }

    /**
     * The method returns the singleton object of the same class
     *
     * @return Returns the instance of the same class.
     */

    public static IDeleteRule getInstance() {
        return EfwSavedResultDeleteRuleHolder.INSTANCE;
    }

    /**
     * Tells whether the file is deletable or not. Deletable only if the user
     * credentials are matching.
     *
     * @param file The file under inspection to be deleted of type .result
     * @return true if security credentials are matching
     */
    public boolean isDeletable(File file) {
        return true;
    }

    /**
     * Simply deletes the file after deleting the saved result file by reading
     * the file content
     *
     * @param file The file under inspection to be deleted of type .result
     */
    public void delete(File file) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonObject = processor.getJSONObject(file.toString(), false);
        String efwSaveResultExtension = JsonUtils.getEfwResultExtension();

        if (efwSaveResultExtension == null) {
            logger.error("The tag efwResult is not found in the xml");
        }

        if (jsonObject.has("resultDirectory")) {
            if (jsonObject.has("resultFile")) {
                String resultFile = jsonObject.getString("resultFile");
                String resultDirectory = jsonObject.getString("resultDirectory");
                if (!deleteSavedResult(resultDirectory, resultFile)) {
                    logger.error("Couldn't delete the " + resultFile + " from " + resultDirectory);
                }
                IOOperationsUtility.deleteWithLogs(file);
            } else {
                logger.error("The file doesn't seem to have resultFile.");
            }
        } else {
            logger.error("The file doesn't seem to have resultDirectory.");
        }
    }

    /**
     * The method deletes the saved result file from the directory mentioned in
     * the file
     *
     * @param resultDirectory The directory in which the result is saved
     * @param resultFile      The file to be deleted
     * @return false if already deleted or not found
     */
    private boolean deleteSavedResult(String resultDirectory, String resultFile) {
        File resultFileObject = new File(ApplicationProperties.getInstance().getSolutionDirectory() + File.separator
                + resultDirectory +
                File.separator + resultFile);
        if (!resultFileObject.exists()) {
            logger.info("The file " + resultFile + " has already been deleted");
            return false;
        } else {
            IOOperationsUtility.deleteWithLogs(resultFileObject);
        }
        return true;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwSavedResultDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new EFWSavedResultDeleteRule();
    }
}
