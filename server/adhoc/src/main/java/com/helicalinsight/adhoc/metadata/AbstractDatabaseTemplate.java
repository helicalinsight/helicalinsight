package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created by user on 5/14/2016.
 *
 * @author Rajasekhar
 */
public abstract class AbstractDatabaseTemplate {
	/**
     * Retrieves the database metadata by combining information from multiple catalogs and schemas.
     *
     * @param connection 		 connection to get database.
     * @param formData   		 JSON object containing metadata information such as catalogs, schemas etc.
     * @return combined database metadata.
     */
    public abstract Database getDatabase(Connection connection, @NotNull JsonObject formData);

}
