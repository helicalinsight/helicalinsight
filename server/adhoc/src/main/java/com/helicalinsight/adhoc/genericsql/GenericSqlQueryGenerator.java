package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.FilterMetadata;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JaxbUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generates SQL queries based on metadata and form data.
 * 
 * Created by author on 06-03-13
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GenericSqlQueryGenerator implements IQueryGenerator {

    private static final Logger logger = LoggerFactory.getLogger(GenericSqlQueryGenerator.class);
    /**
     * Indicates whether it is thread-safe to cache the query generator.
     * @return {@code true} if it is thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Prepares a SQL query based on the provided metadata JSON and form data.
     * 
     * @param metadataJson 		 JSON string representing the metadata.
     * @param formData     		 JSON string representing the form data.
     * @return The prepared SQL query string.
     */
    @Override
    public String prepareQuery(String metadataJson, String formData) {
        long now = System.currentTimeMillis();
        long later;
        JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();

        JsonArray columns;
        try {
            columns = AdhocUtils.columnsArray(formDataJson);
        } catch (Exception ignore) {
            return "No Columns Selected";
        }

        Metadata metadata = JaxbUtils.jsonStringToObject(Metadata.class, metadataJson);
        if (metadata == null) {
            throw new IllegalStateException("The metadata file is parsed but the metadata object is null.");
        }
        //Apply metadata security
        FilterMetadata filterMetadata = new FilterMetadata(metadata, "query");
        filterMetadata.filter();
        //END of metadata security

        SqlQueryContext context = getSqlQueryContext(formDataJson, columns, metadata);

        SqlQuery sqlQuery = SqlDialectsFactory.getDialectOfType(metadata.getType());
        //Set the context. Otherwise NPE
        sqlQuery.setContext(context);

        String query = sqlQuery.prepare();
        //query.rep
        //


        later = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("The time taken to prepare the sql query is " + ((later - now) / 1000) +
                    " milli-seconds.");
        }

        return query;
    }

    /**
     * Gets the SQL query context based on the form data and metadata.
     * 
     * @param formDataJson 		 JSON object representing the form data.
     * @return The SQL query context.
     */
    public SqlQueryContext getSqlQueryContext(JsonObject formDataJson) {
        String metadataFileJson = formDataJson.get("metadataFileJson").getAsString();
        Metadata metadata = JaxbUtils.jsonStringToObject(Metadata.class, metadataFileJson);
        return getSqlQueryContext(formDataJson, AdhocUtils.columnsArray(formDataJson), metadata);
    }
    /**
     * Gets the SQL query context based on the form data, columns, and metadata.
     * 
     * @param formDataJson 		 JSON object representing the form data.
     * @param columns      		 JSON array representing the selected columns.
     * @param metadata     		 metadata object.
     * @return The SQL query context.
     */
    public SqlQueryContext getSqlQueryContext(JsonObject formDataJson, JsonArray columns, Metadata metadata) {
        IMetadataStore container = new MetadataStoreBuilder().setMetadata(metadata).createMetadataStore();

        ApplicationContextAccessor.getBean(ColumnsJsonValidator.class).validateJson(columns, container);

        FunctionsJsonValidator functionsJsonValidator = new FunctionsJsonValidator(formDataJson, container);

        if (formDataJson.has("functions")) {
            functionsJsonValidator.validateJson();
        }

        ApplicationContextAccessor.getBean(FiltersJsonValidator.class).validateJson(formDataJson, container,
                functionsJsonValidator);

        String queryLimit = getQueryLimit(formDataJson);

        List<String> requestedColumns = userRequestedColumns(columns);

        return new SqlQueryContext(container, metadata, requestedColumns, queryLimit, formDataJson);
    }
    /**
     * Retrieves the query limit from the form data.
     * 
     * @param formDataJson 		 JSON object representing the form data.
     * @return The query limit.
     */
    private String getQueryLimit(@NotNull JsonObject formDataJson) {
        String queryLimit;
        try {
            queryLimit = formDataJson.get("limitBy").getAsString();
        } catch (Exception ignore) {
            queryLimit = ApplicationUtilities.getDefaultsMap().get("adhoc.query.default" +
                    ".size");
        }
        return queryLimit;
    }
    /**
     * Retrieves the list of user-requested columns from the columns JSON array.
     * 
     * @param columns 		 JSON array representing the selected columns.
     * @return list of user-requested columns.
     */
    @NotNull
    private List<String> userRequestedColumns(@NotNull JsonArray columns) {
        List<String> requestedColumns;
        try {
            requestedColumns = AdhocUtils.getUserInput(columns);
        } catch (JsonIOException ex) {
            throw new MalformedJsonException("The columns json is malformed. Expecting an object with key as column.");
        }
        return requestedColumns;
    }
}