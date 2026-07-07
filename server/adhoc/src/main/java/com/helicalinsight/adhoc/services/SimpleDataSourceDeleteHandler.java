package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceDeleteUtils;
import com.helicalinsight.datasource.DataSourceDeleteUtilsDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of the {@link IDataSourceDeleteRule} interface.
 * Responsible for deleting data sources.
 */
@Component
public class SimpleDataSourceDeleteHandler implements IDataSourceDeleteRule {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDataSourceDeleteHandler.class);
    /**
     * Deletes the data source based on the provided form data.
     *
     * @param formDataJson     		 JSON object containing the form data.
     * @param dataSourceProvider 	 data source provider.
     * @param id               		 ID of the data source to be deleted.
     * @return A message indicating the status of the deletion operation.
     * @throws EfwServiceException If an error occurs during the deletion process.
     */
    @Override
    public String deleteDataSource(JsonObject formDataJson, String dataSourceProvider, String id) {
        try {
            Boolean isDsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
            if(!isDsTypeStorageDatabase){
                return DataSourceDeleteUtils.marshal(dataSourceProvider,id,formDataJson,"delete");
            }
            DataSourceDeleteUtilsDB dataSourceDeleteUtilsDB = new DataSourceDeleteUtilsDB();
            String message = dataSourceDeleteUtilsDB.marshalDelete(dataSourceProvider, id, formDataJson, "delete");
            if (logger.isDebugEnabled()) {
                logger.debug("The deleted status of the global xml is " + message);
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
