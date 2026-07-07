package com.helicalinsight.adhoc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadata;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The {@code DatabaseMetadataProvider} class is an implementation of the {@link IComponent} interface,
 * designed to provide database for metadata. It dynamically invokes a specific metadata implementation
 * based on the provided form data.
 *
 * @author Rajasekhar
 * Created by author on 27-02-2015.
 */
public class DatabaseMetadataProvider implements IComponent {

    private final static Logger logger = LoggerFactory.getLogger(DatabaseMetadataProvider.class);
    /**
     * component to provide metadata DB using form data.
     * It dynamically invokes the specified metadata implementation and returns the metadata information in JSON format.
     *
     * @param jsonFormData 		 		 form data containing parameters for retrieving database metadata.
     * @return A JSON-formatted string containing the metadata information about the database.
     * @throws ConfigurationException 	 If the application configuration (setting.xml) is incorrect or if there is an issue
     *                                	 with obtaining the metadata implementation.
     */
    @Override
    public String executeComponent(String jsonFormData) {

        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        File marshalFile = new File(TempDirectoryCleaner.getTempDirectory() + File.separator + formData.get("location")+"_temp_" + formData.get("metadataFileName"));
        if(marshalFile.exists()){
            marshalFile.delete();
        }

        String metadataImplementation;
        /*IMetadata Call*/
        if (logger.isDebugEnabled()) {
            logger.debug("The metadata implementation is being invoked.");
        }

        metadataImplementation = formData.get("metadataImplementation").getAsString();
        IMetadata iMetadata = FactoryMethodWrapper.getTypedInstance(metadataImplementation, IMetadata.class);
        if (iMetadata == null) {
            throw new ConfigurationException("The application configuration(setting.xml) is incorrect.");
        }

        String metadata = iMetadata.getMetadata(jsonFormData);
        if (logger.isDebugEnabled()) {
            logger.debug("The metadata implementation has completed processing.");
        }
        return metadata;
    }
    /**
     * it tells whether the component is thread-safe to cache.
     *
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
