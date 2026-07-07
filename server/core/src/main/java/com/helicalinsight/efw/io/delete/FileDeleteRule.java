package com.helicalinsight.efw.io.delete;

import com.helicalinsight.efw.io.IOOperationsUtility;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by author on 12-08-2015.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class FileDeleteRule implements IDeleteRule {

    /**
     * Private for the purpose of singleton pattern
     */
    private FileDeleteRule() {
    }

    /**
     * Singleton getter
     *
     * @return An instance of the same class
     */
    @NotNull
    public static IDeleteRule getInstance() {
        return FileDeleteRuleHolder.INSTANCE;
    }

    /**
     * @param file The file under concern
     * @return Always true
     */
    public boolean isDeletable(File file) {
        return true;
    }

    /**
     * @param file The file under concern
     */
    public void delete(File file) {
        if (file != null && file.exists()) {
            IOOperationsUtility.deleteWithLogs(file);
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
    private static class FileDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new FileDeleteRule();
    }
}
