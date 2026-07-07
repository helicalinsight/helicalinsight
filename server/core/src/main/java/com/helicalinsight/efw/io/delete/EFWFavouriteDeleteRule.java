package com.helicalinsight.efw.io.delete;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.io.FileOperationsUtility;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A configuration class for the efw favourite files to be deleted. Currently a
 * file is deletable only if the file has matching user credentials.
 * <p/>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class EFWFavouriteDeleteRule implements IDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWFavouriteDeleteRule.class);

    /**
     * Private for the purpose of singleton pattern
     */
    private EFWFavouriteDeleteRule() {
    }

    /**
     * Singleton getter
     *
     * @return An instance of the same class
     */
    @NotNull
    public static IDeleteRule getInstance() {
        return EfwFavouriteRuleHolder.INSTANCE;
    }

    /**
     * Returns true if user credentials are matching which means the file is
     * deletable
     *
     * @param file The file under concern
     * @return true if user credentials are matching
     */
    public boolean isDeletable(File file) {
        return true;
        //Notes: Removed as on 10-08-2015 as AuthorizationFilter takes care of authorization
        /*if (new DeleteOperationUtility().isAuthorized(file)) {
            logger.debug("The file " + file + " is deletable");
            return true;
        } else {
            logger.debug("The file " + file + " is not deletable");
            return false;
        }*/
    }

    /**
     * Deletes the favourite file and updates the corresponding efwsr file from
     * which it is created.
     *
     * @param file The file under concern
     */
    public void delete(@NotNull File file) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject jsonObject = processor.getJsonObject(file.toString(), false);
        String efwsrFile = null;
        try {
            efwsrFile = jsonObject.get("savedReportFileName").getAsString();
        } catch (JsonIOException ex) {
            logger.error("JSONException", ex);
        }
        if (efwsrFile != null) {
            updateCorrespondingFile(file, efwsrFile);
        }
        IOOperationsUtility.deleteWithLogs(file);
    }

    /**
     * The efwsr file referenced by the favourite file will be updated(Its
     * favourite tag will be false)
     *
     * @param file      The file under concern
     * @param efwsrFile The corresponding efwsr file for the favourite file
     */
    private void updateCorrespondingFile(File file, String efwsrFile) {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        String efwsrFilePath = new FileOperationsUtility().search(applicationProperties.getSolutionDirectory(),
                efwsrFile);

        logger.debug("EFWSR file for " + file + " is " + efwsrFilePath);

        if (efwsrFilePath == null) {
            logger.debug("EFWSR file for " + file + " is already deleted. Simply deleting the " +
                    "favourite file.");
            IOOperationsUtility.deleteWithLogs(file);
            return;
        }

        File location = new File(efwsrFilePath);
        Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, location);
        if (efwsr == null) {
            throw new OperationFailedException("The file " + efwsrFilePath + " doesn't exists.");
        }

        //Unmark as favourite
        efwsr.setFavourite("false");

        try {
            synchronized (this) {
                JaxbUtils.marshal(efwsr, location);
            }
        } catch (Exception ex) {
            logger.error("Exception: ", ex);
        }

        logger.info("The file " + efwsrFilePath + " is successfully updated.");
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwFavouriteRuleHolder {
        public static final IDeleteRule INSTANCE = new EFWFavouriteDeleteRule();
    }
}
