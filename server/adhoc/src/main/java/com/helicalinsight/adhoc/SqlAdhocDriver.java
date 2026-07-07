package com.helicalinsight.adhoc;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.services.QueryGeneratorAndExecutorService;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.efw.ApplicationProperties;


/**
 * Queries the database and gives the response as a json data
 *
 * @author Somen
 * @since 1.0
 */
@SuppressWarnings("unused")
public class SqlAdhocDriver implements IDriver {

    
	
	/**
	 * Retrieves a query using Gson for processing data map tag content and request parameters.
	 *
	 * @param dataMapTagContent     	 JsonObject containing data map tag content.
	 * @param requestParameterJson  	 JsonObject containing request parameters.
	 * @return String                	 resulting query obtained from EfwdQueryProcessor.
	 */
	@Override
	public String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) {
		 EfwdQueryProcessor efwdQueryProcessor = new EfwdQueryProcessor();
	        return efwdQueryProcessor.getQuery(dataMapTagContent, requestParameterJson);
	}
	
	/**
     * Return the json of the result of the database query
     *
     * @param requestParameterJson 		 Http Request parameter
     * @param connectionDetails    		 connection details from the EFWD
     * @param dataMapTagContent    		 content of the data map tag from the corresponding file
     * @param properties           		 singleton instance of ApplicationProperties
     * @return The json of the result of the database query
     */
	@Override
	public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
		String location = connectionDetails.get("location").getAsString();
        String metadataFileName = connectionDetails.get("metadataFileName").getAsString();
        String columns = this.getQuery(dataMapTagContent, requestParameterJson);

        JsonObject formData = new Gson().fromJson(columns, JsonObject.class);
        formData.addProperty("location",location);//accumulate
        formData.addProperty("metadataFileName",metadataFileName);//accumulate
        QueryGeneratorAndExecutorService visualizationService = new QueryGeneratorAndExecutorService();
        String jsonString = visualizationService.doService("adhoc", "report", "fetchData", formData.toString());

        JsonElement element = new Gson().fromJson(jsonString, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();

        JsonElement jsonElement = jsonObject.get("response");
        if (!jsonElement.isJsonNull() && !("{}".equals(jsonElement.toString()))) {
            return jsonElement.getAsJsonObject();
        } else {
            return new JsonObject();
        }
	}
}