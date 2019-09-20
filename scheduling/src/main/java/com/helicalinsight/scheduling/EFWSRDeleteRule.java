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

package com.helicalinsight.scheduling;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.io.delete.IDeleteRule;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A configuration class for the efwsr files to be deleted. Currently a file is
 * deletable only if the file has matching user credentials. The favourite files
 * of the efwsr files will also be deleted.
 * <p/>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 */
public final class EFWSRDeleteRule implements IDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWSRDeleteRule.class);

    /**
     * For Singleton structure - A private constructor
     */
    private EFWSRDeleteRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    public static IDeleteRule getInstance() {
        return EfwsrDeleteRuleHolder.INSTANCE;
    }

    /**
     * Returns true if user credentials are matching; which means the file is
     * deletable. The corresponding favourite file will also be checked for
     * valid user credentials.
     *
     * @param file The file under concern
     * @return true if user credentials are matching
     */
    public boolean isDeletable(File file) {
        if (file.isFile()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JSONObject jsonObject = processor.getJSONObject(file.toString(), false);
            String favourite = jsonObject.getString("favourite");
            if ("false".equalsIgnoreCase(favourite)) {
                logger.debug("File " + file + " is deletable");
                return true;
            } else {
                /*
                 * Verify the corresponding efwsr has valid credentials
				 */
                return checkCorrespondingEFWSRCredentials(file, favourite);
            }
        }
        return false;
    }

    /**
     * Returns true if the favourite file of the efwsr credentials match
     *
     * @param file      The file under concern
     * @param favourite The favourite file of the efwsr
     * @return true if the favourite file credentials match
     */
    private boolean checkCorrespondingEFWSRCredentials(File file, String favourite) {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        String path = new FileOperationsUtility().search(applicationProperties.getSolutionDirectory(), favourite);
        logger.debug("Favourite for the file " + file + " is " + path);
        if (path == null) {
            logger.debug("No need to update the corresponding efwsr file as favourite file " +
                    "deleted. " + file + " is deletable");
            return true;
        }
        return false;
    }

    /**
     * Deletes the efwsr file by deleting the corresponding favourite file in
     * the solution directory.
     *
     * @param file The file under concern
     */
    public void delete(File file) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonObject = processor.getJSONObject(file.toString(), false);

        if (jsonObject.has("schedulingReference")) {
            String schedulingReference = jsonObject.getString("schedulingReference");
            DereferenceScheduling.deleteSchedule(schedulingReference);
        }

        String favourite;
        try {
            favourite = jsonObject.getString("favourite");
        } catch (JSONException ex) {
            logger.debug("JSONException occurred as favourite tag is not present. Simply deleting");
            IOOperationsUtility.deleteWithLogs(file);
            return;
        }

        if (!"false".equalsIgnoreCase(favourite)) {
            ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
            String path = new FileOperationsUtility().search(applicationProperties.getSolutionDirectory(), favourite);
            logger.debug("Favourite file for the file " + file + " is " + path);
            if (path == null) {
                logger.debug("Favourite file for the file " + file + " is already deleted.");
                IOOperationsUtility.deleteWithLogs(file);
                return;
            }
            /*
             * Delete the favourite first and then delete the efwsr
			 */
            if (new File(path).delete()) {
                logger.debug("Successfully deleted the favourite file for the file " + file + " " +
                        "for which the path is " + path);
            } else {
                logger.debug("Couldn't delete the favourite file for the file " + file + " for " +
                        "which the path is " + path);
            }
        }
        IOOperationsUtility.deleteWithLogs(file);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwsrDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new EFWSRDeleteRule();
    }
}
