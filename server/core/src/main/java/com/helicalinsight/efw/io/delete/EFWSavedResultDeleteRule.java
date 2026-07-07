package com.helicalinsight.efw.io.delete;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
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
 * @version 1.1
 * @since 1.1
 */
@Deprecated
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
    @NotNull
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
        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
        /*return new DeleteOperationUtility().isAuthorized(file);*/
    }

    /**
     * Simply deletes the file after deleting the saved result file by reading
     * the file content
     *
     * @param file The file under inspection to be deleted of type .result
     */
    public void delete(@NotNull File file) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject jsonObject = processor.getJsonObject(file.toString(), false);
        String efwSaveResultExtension = JsonUtils.getEfwResultExtension();

        if (efwSaveResultExtension == null) {
            logger.error("The tag efwResult is not found in the xml");
        }

        if (jsonObject.has("resultDirectory")) {
            if (jsonObject.has("resultFile")) {
                String resultFile = jsonObject.get("resultFile").getAsString();
                String resultDirectory = jsonObject.get("resultDirectory").getAsString();
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
