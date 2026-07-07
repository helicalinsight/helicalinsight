package com.helicalinsight.efw.io.delete;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * The efw files are not supposed to be deleted. So this class though configured
 * in the setting.xml does not allow the user to delete the efw files.
 * <p/>
 * Created by author on 16-10-2014.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class EFWDeleteRule implements IDeleteRule {

    /**
     * Private for the purpose of singleton pattern
     */
    private EFWDeleteRule() {
    }

    /**
     * Singleton getter
     *
     * @return An instance of the same class
     */
    @NotNull
    public static IDeleteRule getInstance() {
        return EfwDeleteRuleHolder.INSTANCE;
    }

    /**
     * EFW files can't be deleted
     *
     * @param file The file under concern
     * @return Always true
     */
    public boolean isDeletable(File file) {
        return true;
    }

    /**
     * Deletes the template of the efw and the efw itself.
     *
     * @param file The file under concern
     */
    public void delete(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        final JsonObject jsonObject = processor.getJsonObject(file.getAbsolutePath(), false);
        if (!jsonObject.has("template")) {
            IOOperationsUtility.deleteWithLogs(file);
        } else {
            final String fullPathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(file.getAbsolutePath());
            if (fullPathNoEndSeparator != null) {
                File template = new File(fullPathNoEndSeparator + File.separator + jsonObject.get("template").getAsString());
                IOOperationsUtility.deleteWithLogs(template);
                IOOperationsUtility.deleteWithLogs(file);
            }
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new EFWDeleteRule();
    }
}
