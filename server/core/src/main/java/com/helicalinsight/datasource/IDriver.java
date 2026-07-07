package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.ApplicationProperties;
import java.sql.ResultSet;

/**
 * Implementation classes method return the json of the result of the database
 * query
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @author Somen
 * @since 1.0
 */
public interface IDriver {
	
	
	/**
	 * getJSONData using gson
	 * @param JsonObject requestParameterJson
	 * @param JsonObject connectionDetails
	 * @param JsonObject dataMapTagContent
	 * @param  JsonObject requestParameterJson
	 * @return JsonObject 
	 */
	JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
            JsonObject dataMapTagContent, ApplicationProperties applicationProperties);

	

    

	/**
	 * getQuery using gson
	 * @param JsonObject dataMapTagContent
	 * @param  JsonObject requestParameterJson
	 * @return String 
	 */
	String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson);


	/**
	 * getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) using gson
	 * @param requestParameterJson
	 * @param connectionDetails
	 * @param dataMapTagContent
	 * @param applicationProperties
	 * @return
	 */
	default ResultSet getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
		return null;
	}
	
	default void streamResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties, CallBack<ResultSet> callBack) {
	}

}
