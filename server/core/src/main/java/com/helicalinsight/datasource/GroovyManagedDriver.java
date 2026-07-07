package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.GroovyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
/**
 * GroovyManagedDriver extends {@link JDBCDriver}
 * This class is responsible for fetching data using Groovy managed data source.
 * @author Rajesh
 * Created by author on 2/11/2020.
 */
public class GroovyManagedDriver extends JDBCDriver {
    private static final Logger logger = LoggerFactory.getLogger(GroovyManagedDriver.class);
    /**
     * getJSONData(JSONObject requestParameterJson, JSONObject connectionDetails,
                                  JSONObject dataMapTagContent, ApplicationProperties properties)
     * @param requestParameterJson		Http Request parameter
     * @param connectionDetails   		connection details from the EFWD
     * @param dataMapTagContent    		content of the data map tag from the corresponding file
     * @param properties          		singleton instance of ApplicationProperties
     * @return jsonObject of the result of the database query
     */
    public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                  JsonObject dataMapTagContent, ApplicationProperties properties) {

        String query = this.getQuery(dataMapTagContent, requestParameterJson);


        JsonObject condition = GroovyUtils.executeGroovy(connectionDetails.get("Condition").getAsString(), "evalCondition", JsonObject.class);
        String globalId = null;
        String type = null;

        globalId = GsonUtility.optString(condition, "globalId");
        type = GsonUtility.optString(condition, "type");


        JsonObject globalConnectionDetails = null;
        globalConnectionDetails = connectionDetails;
        globalConnectionDetails.addProperty("@type", type);
        globalConnectionDetails.addProperty("globalId", globalId);
        dataMapTagContent.addProperty("Query", query);
        dataMapTagContent.addProperty("@type", "sql");
        GlobalJdbcDataSource globalJdbcDataSource = new GlobalJdbcDataSource();
        return globalJdbcDataSource.getJSONData(requestParameterJson, globalConnectionDetails, dataMapTagContent, properties);

//        Connection connection = getConnection(connectionDetails.getString("Url"), connectionDetails.getString("User"),
//        		connectionDetails.getString("Pass"),connectionDetails.getString("Driver"));
//        //All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
//        IJdbcDao bean = ApplicationContextAccessor.getBean(IJdbcDao.class);
//
//        if (requestParameterJson.has("maxRows") && (bean instanceof JdbcDaoImpl)) {
//            int maxRows = requestParameterJson.getInt("maxRows");
//            ((JdbcDaoImpl) bean).setLimit(maxRows);
//            query = requestParameterJson.optString("processedLimitQuery", query);
//        }
//        return bean.query(connection, query);
    }
    /**
     * getQuery(JSONObject dataMapTagContent, JSONObject requestParameterJson)
     * @param dataMapTagContent           content of the data map tag 	
	 * @param  requestParameterJson		  Http Request parameter
	 * @return sql query      
     */
    @Override
    public String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) {
        String query = super.getQuery(dataMapTagContent, requestParameterJson);
        String type =dataMapTagContent.has("@type")? dataMapTagContent.get("@type").getAsString():dataMapTagContent.get("type").getAsString();
        if (StringUtils.isNotBlank(type) && type.toLowerCase().contains("groovy")) {
            return GroovyUtils.executeGroovy(query, "evalCondition", String.class);
        }
        return query;
    }


    public ResultSet getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                       JsonObject dataMapTagContent, ApplicationProperties properties) {
        String query = this.getQuery(dataMapTagContent, requestParameterJson);


        JsonObject condition = GroovyUtils.executeGroovy(connectionDetails.get("Condition").getAsString(), "evalCondition", JsonObject.class);
        String globalId = null;
        String type = null;

        globalId = GsonUtility.optString(condition, "globalId");
        type = GsonUtility.optString(condition, "type");


        JsonObject globalConnectionDetails = null;
        globalConnectionDetails = connectionDetails;
        globalConnectionDetails.addProperty("@type", type);
        globalConnectionDetails.addProperty("globalId", globalId);
        dataMapTagContent.addProperty("Query", query);
        dataMapTagContent.addProperty("@type", "sql");
        GlobalJdbcDataSource globalJdbcDataSource = new GlobalJdbcDataSource();
        return globalJdbcDataSource.getResultSetData(requestParameterJson, globalConnectionDetails, dataMapTagContent, properties);
    }
}
