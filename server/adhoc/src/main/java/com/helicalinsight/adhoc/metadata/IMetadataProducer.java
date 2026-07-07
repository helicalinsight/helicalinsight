package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;

/**
 * This interface defines the contract for classes that produce metadata.
 * Created by user on 5/13/2016.
 * @author Rajasekhar
 */
public interface IMetadataProducer {
	/**
     * Prepares metadata based on the provided form data.
     *
     * @param formData 			 JSON object containing form data.
     * @return The prepared metadata.
     */
    public Metadata prepareMetadata(JsonObject formData);

}
