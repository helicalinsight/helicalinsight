package com.helicalinsight.adhoc.metadata.genericdb;

import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
public class MetadataUtility {

    private final String connectionProvider;
    private final String metadataImplementation;
    private final String type;

    public MetadataUtility(String connectionProvider, String metadataImplementation, String type) {
        this.connectionProvider = connectionProvider;
        this.metadataImplementation = metadataImplementation;
        this.type = type;
    }

    public String getConnectionProvider() {
        return connectionProvider;
    }

    public String getMetadataImplementation() {
        return metadataImplementation;
    }

    public String getType() {
        return type;
    }

    @NotNull
    @Override
    public String toString() {
        return "MetadataUtility{" +
                "connectionProvider='" + connectionProvider + '\'' +
                ", metadataImplementation='" + metadataImplementation + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
