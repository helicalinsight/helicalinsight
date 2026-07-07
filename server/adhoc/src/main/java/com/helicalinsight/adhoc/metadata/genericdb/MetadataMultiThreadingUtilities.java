package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * The MetadataMultiThreadingUtilities class provides utility methods for managing metadata retrieval in multi-threaded
 * environments.
 * Created by author on 07-09-2015.
 * @author Rajasekhar
 */
public class MetadataMultiThreadingUtilities {

    private static final Logger logger = LoggerFactory.getLogger(MetadataMultiThreadingUtilities.class);
    /**
     * Retrieves the threshold value for multi-threading.
     * @return The threshold value.
     */
    public static Integer getThreshold() {
        Integer count = null;
        Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.getProjectPropertiesFile();
        String threshold = mapFromClasspathPropertiesFile.get("metadataThreadsTableThreshold");
        if (threshold != null) {
            try {
                count = Integer.parseInt(threshold);
            } catch (NumberFormatException ignore) {
            }
        }

        if (count == null) {
            count = 10;
        }
        return count;
    }
    /**
     * Checks if multi-threading is enabled for metadata retrieval.
     * @return {@code true} if multi-threading is enabled, otherwise {@code false}
     */
    public static boolean isMultiThreadingEnabled() {
        boolean enableMultiThreading = true;
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        if (settingsJson.has("enableMetadataMultiThreading")) {
            String enableMetadataMultiThreading = settingsJson.get("enableMetadataMultiThreading").getAsString();
            if (!"true".equalsIgnoreCase(enableMetadataMultiThreading)) {
                enableMultiThreading = false;
            }
        }
        return enableMultiThreading;

    }
    /**
     * Pauses the specified threads until they cease execution.
     *
     * @param threads     		 list of threads to pause.
     * @param handlerFlag 		 flag to indicate the status of thread execution.
     */
    public static void pauseThreads(List<Thread> threads, Boolean[] handlerFlag) {
        checkStatusOfRetrieval(handlerFlag);
        for (Thread thread : threads) {
            try {
                if (handlerFlag[0]) {
                    thread.interrupt();
                    continue;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Waiting for the thread {} to cease.", thread.getName());
                }
                thread.join();
            } catch (InterruptedException ex) {
                throw new MetadataRetrievalException(ex);
            }
        }
        checkStatusOfRetrieval(handlerFlag);
    }
    /**
     * Checks the status of thread execution.
     * @param handlerFlag 		 flag to indicate the status of thread execution.
     */
    private static void checkStatusOfRetrieval(Boolean[] handlerFlag) {
        if (handlerFlag[0]) {
            throw new MetadataRetrievalException("Couldn't fetch the metadata of the selected " + "catalogs and/or " +
                    "schemas");
        }
    }
    /**
     * Retrieves an uncaught exception handler for threads.
     * @param handlerFlag 		 flag to indicate the status of thread execution.
     * @return The uncaught exception handler.
     */
    public static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(final Boolean[] handlerFlag) {
        return new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(@NotNull Thread th, Throwable ex) {
                logger.error("The thread " + th.getName() + " couldn't complete the retrieval" +
                        ". The cause is ", ex);
                handlerFlag[0] = true;
            }
        };
    }
}
