package com.helicalinsight.scheduling;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.io.delete.IDeleteRule;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * EFWSRDeleteRule implements {@link IDeleteRule}
 * A configuration class for the efwsr files to be deleted. Currently a file is
 * deletable only if the file has matching user credentials. The favourite files
 * of the efwsr files will also be deleted.
 * <p>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public class EFWSRDeleteRule implements IDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWSRDeleteRule.class);

    /**
     * EFWSRDeleteRule()
     * For Singleton structure - A private constructor
     */
    protected EFWSRDeleteRule() {
    }

    /**
     * getInstance()
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IDeleteRule getInstance() {
        return EfwsrDeleteRuleHolder.INSTANCE;
    }

    /**
     * isDeletable(@NotNull File file)
     * Returns true if user credentials are matching; which means the file is
     * deletable. The corresponding favourite file will also be checked for
     * valid user credentials.
     *
     * @param file The file under concern
     * @return true if user credentials are matching
     */
    public boolean isDeletable(@NotNull File file) {
        if (file.isFile()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            JsonObject jsonObject = processor.getJsonObject(file.toString(), false);
            String favourite = jsonObject.get("favourite").getAsString();
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
     * checkCorrespondingEFWSRCredentials(File file, String favourite)
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
        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
        /*if (deleteOperationUtility.isAuthorized(new File(path))) {
            logger.debug("File " + file + " is deletable");
            return true;
        }*/
        return false;
    }

    /**
     * delete(@NotNull File file)
     * Deletes the efwsr file by deleting the corresponding favourite file in
     * the solution directory.
     *
     * @param file The file under concern
     */
    public void delete(@NotNull File file) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject jsonObject = processor.getJsonObject(file.toString(), false);

        if (jsonObject.has("schedulingReference")) {
            String schedulingReference = jsonObject.get("schedulingReference").getAsString();
            if (com.helicalinsight.efw.utility.JsonUtils.newIsScheduleStorageTypeIsDatabase()) {
                ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
                scheduleOperation.deleteScheduleEntity(schedulingReference);
                ScheduleProcess scheduleProcess = new ScheduleProcess();
                scheduleProcess.delete(schedulingReference);
            } else
                DereferenceScheduling.deleteSchedule(schedulingReference);
        }

        String favourite;
        try {
            favourite = jsonObject.get("favourite").getAsString();
        } catch (JsonSyntaxException ex) {
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
     * EfwsrDeleteRuleHolder
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwsrDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new EFWSRDeleteRule();
    }
}
