package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * GlobalJdbcDataSource class implements {@link IDriver}
 * This class provides JDBC dataSource  data in various formats.
 * It supports retrieving data as both a Java ResultSet and a JSON object.
 *  
 * Created by author on 19-01-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GlobalJdbcDataSource implements IDriver {
	
	
	/**
	 * getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                      JsonObject dataMapTagContent, ApplicationProperties applicationProperties)
     * @param requestParameterJson    request parameter json
     * @param connectionDetails       Driver connection details
     * @param dataMapTagContent		  content of the data map tag 
     * @param applicationProperties   holds the application settings details
     * @return {@code java.sql.ResultSet} object.
	 */
	
    @Override
    public ResultSet getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                      JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
       return prepareEfwdService(requestParameterJson)
    		   .executeForResultSet(requestParameterJson.toString(),
        		connectionDetails.toString(), dataMapTagContent.toString(), applicationProperties);
    }
    
    
    private IEfwdService prepareEfwdService(JsonObject requestParameterJson) {
    	IEfwdService efwdService = ApplicationContextAccessor.getBean(IEfwdService.class);
        if (requestParameterJson.has("maxRows") && (efwdService instanceof EfwdService)) {
            ((EfwdService) efwdService).setLimit(requestParameterJson.get("maxRows").getAsInt());
            ((EfwdService) efwdService).setProcessedLimitQuery(GsonUtility.optString(requestParameterJson,"processedLimitQuery"));
        }
        return efwdService;
    }
    
    @Override
    public void streamResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                      JsonObject dataMapTagContent, ApplicationProperties applicationProperties, CallBack<ResultSet> callBack) {
    	  prepareEfwdService(requestParameterJson)
      		   .streamForResultSet(requestParameterJson.toString(),
          		connectionDetails.toString(), dataMapTagContent.toString(), applicationProperties, callBack);
    }


	/**
	 * using gson
	 * getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) 
	 * @param dataMapTagContent        content of the data map tag 
	 * @param requestParameterJson     request parameter json
	 * @return sql query in string format. 
	 */
	@Override
	public String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) {
		final EfwdQueryProcessor efwdQueryProcessor = ApplicationContextAccessor.getBean(EfwdQueryProcessor.class);
        if(dataMapTagContent.has("Query"))
        	return efwdQueryProcessor.getQuery(dataMapTagContent, requestParameterJson);
        else
        	return efwdQueryProcessor.getQuery2(dataMapTagContent, requestParameterJson);
    }
	
		/**
	 * using gson
	 * getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) 
	 * @param requestParameterJson              request parameters
	 * @param connectionDetails					data source connection details
	 * @param dataMapTagContent					data map content
	 * @param applicationProperties             object consists application settings
	 * @return query result in json object format. 
	 */
	@Override
	public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
		String httpData = requestParameterJson.toString();
        String connectionInfo = connectionDetails.toString();
        String dataMapTag = dataMapTagContent.toString();

        IEfwdService efwdService = ApplicationContextAccessor.getBean(IEfwdService.class);
        if (requestParameterJson.has("maxRows") && (efwdService instanceof EfwdService)) {
            ((EfwdService) efwdService).setLimit(requestParameterJson.get("maxRows").getAsInt());
            ((EfwdService) efwdService).setProcessedLimitQuery(GsonUtility.optString(requestParameterJson,"processedLimitQuery"));
        }
        JsonObject result = efwdService.execute(httpData, connectionInfo, dataMapTag, applicationProperties);
        return result;
	}

	

	

	
}

