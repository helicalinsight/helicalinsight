package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceDeleteUtilsDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple implementation of the data source deletion handler for database data sources.
 */
@Component
public class SimpleDataSourceDeleteHandlerDB implements IDataSourceDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDataSourceDeleteHandlerDB.class);
    /**
     * Deletes the specified data source.
     *
     * @param formDataJson      	 JSON data containing the form data for the data source deletion.
     * @param dataSourceProvider 	 provider of the data source.
     * @param id                 	 ID of the data source to be deleted.
     * @return A JSON string representing the result of the deletion operation.
     */
    @Override
    public String deleteDataSource(JsonObject formDataJson, String dataSourceProvider, String id) {
        try {
            DataSourceDeleteUtilsDB dataSourceDeleteUtilsDB = new DataSourceDeleteUtilsDB();
            String message = dataSourceDeleteUtilsDB.marshalDelete(dataSourceProvider, id, formDataJson, "delete");
            if (logger.isDebugEnabled()) {
                logger.debug("The deleted status of the global connection is " + message);
            }
            JsonObject messageJson = new Gson().fromJson(message, JsonObject.class);
            messageJson.addProperty("message", "The datasource "+id+" have been deleted successfully");
            return  messageJson.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EfwServiceException("The data source could not be deleted with the. Cause " +
                    ExceptionUtils.getRootCauseMessage(ex));
        }
    }
}
