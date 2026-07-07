package com.helicalinsight.adhoc.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.AdhocViewJsonProvider;
import com.helicalinsight.adhoc.metadata.genericdb.FilterMetadata;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadata;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import java.io.File;
import java.util.UUID;

/**
 * WorkflowDatabaseMetadataProvider implements the {@link IMetadata} interface.
 * This class provides metadata for database .
 * Created by user on 5/13/2016.
 * @author Rajasekhar
 */
@SuppressWarnings("UnusedDeclaration")
public class WorkflowDatabaseMetadataProvider implements IMetadata {
	/**
     * Retrieves metadata based on the provided JSON information.
     * @param jsonInformation 		 JSON string containing metadata details such as joins , columns etc to prepare it .
     * @return A string representing the metadata.
     */
    @Override
    public String getMetadata(String jsonInformation) {
        JsonObject formData = JsonParser.parseString(jsonInformation).getAsJsonObject();

        String uuid = UUID.randomUUID().toString();

        String classifier = formData.get("classifier").getAsString();
        IMetadataProducer metadataProducer = MetadataProducerBeanFactory.getMetadataProducer(classifier);
        Metadata metadata = metadataProducer.prepareMetadata(new Gson().fromJson(formData.toString(),JsonObject.class));

        Boolean joinsOnly= GsonUtility.optBoolean(formData,"joinsOnly");
        Boolean columnsOnly= GsonUtility.optBoolean(formData,"fetchColumnsOnly");
        if(!joinsOnly && !columnsOnly) {
            String location = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
            String metadataExtension = JsonUtils.getMetadataExtension();
            File file = new File(location + File.separator + uuid + "." + metadataExtension);
            if (file.getAbsolutePath().contains("_temp_")) {
                file.getParentFile().mkdirs();
            }
            synchronized (this) {
                JaxbUtils.marshal(metadata, file);
            }
        }

        //Send response
        JsonObject response = prepareResponse(formData, metadata);
        GsonUtility.accumulate(response,"uniqueId", uuid);
        return response.toString();
    }
    /**
     * Prepares the response with the given form data and metadata.
     *
     * @param formData 		 JSON object containing joins, view data .
     * @param metadata 		 metadata object.
     * @return A JsonObject representing the response.
     */
    public static JsonObject prepareResponse(JsonObject formData, Metadata metadata) {
        JsonObject response;
        response = new JsonObject();

        GsonUtility.accumulate(formData,"provideJoins", "true");

        FilterMetadata filterMetadata = new FilterMetadata(metadata);
        filterMetadata.filter();

        AdhocViewJsonProvider jsonProvider = ApplicationContextAccessor.getBean(AdhocViewJsonProvider.class);
        String adhocViewJson = jsonProvider.adhocViewJson(metadata, formData);
        JsonObject asJsonObject = JsonParser.parseString(adhocViewJson).getAsJsonObject();
        GsonUtility.accumulate(response,"metadata", asJsonObject);
        return response;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
