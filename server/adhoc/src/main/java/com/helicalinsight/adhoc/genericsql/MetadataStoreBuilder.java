package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.adhoc.metadata.jaxb.Metadata;

/**
 * Builder class for creating an instance of MetadataStore.
 * Allows setting the Metadata object before creating the MetadataStore instance.
 */
public class MetadataStoreBuilder {
    private Metadata metadata;
    /**
     * Sets the Metadata object for creating the MetadataStore instance.
     * @param metadata 			 Metadata object to be used for creating the MetadataStore.
     * @return The MetadataStoreBuilder instance with the specified Metadata object set.
     */
    public MetadataStoreBuilder setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }
    /**
     * Creates an instance of MetadataStore using the set Metadata object.
     * @return An instance of IMetadataStore created using the specified Metadata object.
     */
    public IMetadataStore createMetadataStore() {
        return MetadataStore.createMetadataStore(metadata);
    }
}