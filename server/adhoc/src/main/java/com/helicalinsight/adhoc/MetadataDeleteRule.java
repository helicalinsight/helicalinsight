package com.helicalinsight.adhoc;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.io.delete.IDeleteRule;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * Created by author on 12-08-2015.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class MetadataDeleteRule implements IDeleteRule {

    /**
     * Private for the purpose of singleton pattern
     */
    private MetadataDeleteRule() {
    }

    /**
     * Singleton getter
     *
     * @return An instance of the same class
     */
    @NotNull
    public static IDeleteRule getInstance() {
        return MetadataDeleteRuleHolder.INSTANCE;
    }

    /**
     * @param file The file under concern
     * @return Always true
     */
    public boolean isDeletable(File file) {
        return true;
    }

    /**
     * Deletes the specified metadata file.
     * @param file The file under concern
     */
    public void delete(File file) {
        //Use same cached object
        IComponent deleteHandler = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.adhoc.metadata.genericdb" +
                ".MetadataDeleteHandler", IComponent.class);
        final String absolutePath = file.getAbsolutePath();

        final String metadataFileName = FilenameUtils.getName(absolutePath);

        final String withSolutionDirectory = FilenameUtils.getFullPathNoEndSeparator(absolutePath);
        final String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();

        final String location = StringUtils.removeStart(withSolutionDirectory, solutionDirectory + File.separator);

        JsonObject formData;
        formData = new JsonObject();
        GsonUtility.accumulate(formData,"metadataFileName", metadataFileName);
        GsonUtility.accumulate(formData,"location", location);

        if (deleteHandler != null) {
            deleteHandler.executeComponent(formData.toString());
        } else {
            throw new EfwException("Couldn't get an instance of the IComponent.");
        }
    }
    /**
     * Indicates whether this component is thread-safe to be cached.
     * @return {@code true} if the component is thread-safe to be cached, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class MetadataDeleteRuleHolder {
        public static final IDeleteRule INSTANCE = new MetadataDeleteRule();
    }
}
