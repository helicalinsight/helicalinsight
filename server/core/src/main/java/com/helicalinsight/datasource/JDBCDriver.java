package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.datasource.managed.JdbcDaoImpl;
import com.helicalinsight.datasource.managed.PlainJdbcConnectionProvider;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.Connection;

/**
 * JDBCDriver implements {@link IDriver} interface
 * Queries the database and gives the response as a json data
 *
 * @author Rajasekhar
 * @since 1.0
 */
public class JDBCDriver implements IDriver {

    private static final Logger logger = LoggerFactory.getLogger(JDBCDriver.class);
    /**
     * getConnection(String url, String user, String password, String driver)
     * @param url      			url
     * @param user	   			user name
     * @param password			password
     * @param driver			driver Name
     * @return {@code java.sql.Connection} object for adhoc
     */
    public static Connection getConnection(String url, String user, String password, String driver) {
        PlainJdbcConnectionProvider plainJdbcConnectionProvider = ApplicationContextAccessor.getBean
                (PlainJdbcConnectionProvider.class);

        JsonObject json;
        json = new JsonObject();
        json.addProperty("jdbcUrl", url);
        json.addProperty("userName", user);
        json.addProperty("password", password);
        json.addProperty("driverName", driver);

        return plainJdbcConnectionProvider.newConnection(json.toString());
    }

    /**
     * getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) 
     * Return the json of the result of the database query
     *
     * @param requestParameterJson The Http Request parameter
     * @param connectionDetails    The connection details from the EFWD
     * @param dataMapTagContent    The content of the data map tag from the corresponding file
     * @param properties           The singleton instance of ApplicationProperties
     * @return The json of the result of the database query
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
	 * getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties)
	 * Retrieves JSON data from a data source based on provided parameters and connection details.
	 *
	 * @param requestParameterJson  object containing request parameters.
	 * @param connectionDetails    	object containing connection details, including URL, username, password, and driver.
	 * @param dataMapTagContent    	JsonObject containing data map tag content.
	 * @param applicationProperties An instance of ApplicationProperties for accessing application-specific settings.
	 * @return A JsonObject containing the retrieved data.
	 */
	@Override
	public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
		String url,user,password,driver;
    	if(connectionDetails.has("connDetails")) {
    		JsonObject connDetails=connectionDetails.getAsJsonObject("connDetails");
            url = connDetails.get("url").getAsString();
            user = connDetails.get("user").getAsString();
            password = connDetails.get("pass").getAsString();
            driver = connDetails.get("driver").getAsString();	    		
    	}else {
            url = connectionDetails.get("Url").getAsString();
            user = connectionDetails.get("User").getAsString();
            password = connectionDetails.get("Pass").getAsString();
            driver = connectionDetails.get("Driver").getAsString();	
    	}

        String query = this.getQuery(dataMapTagContent, requestParameterJson);

        if (logger.isDebugEnabled()) {
            logger.debug("EFWD query being run is " + query);
        }

        Connection connection = getConnection(url, user, password, driver);
        //All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
        IJdbcDao bean = ApplicationContextAccessor.getBean(IJdbcDao.class);

        if (requestParameterJson.has("maxRows") && (bean instanceof JdbcDaoImpl)) {
            int maxRows = requestParameterJson.get("maxRows").getAsInt();
            ((JdbcDaoImpl) bean).setLimit(maxRows);
            query = GsonUtility.optStringValue(requestParameterJson,"processedLimitQuery", query);
        }
        return bean.query(connection, query);

	}

    public ResultSet getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                       JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
        String url,user,password,driver;
        if(connectionDetails.has("connDetails")) {
            JsonObject connDetails=connectionDetails.getAsJsonObject("connDetails");
            url = connDetails.get("url").getAsString();
            user = connDetails.get("user").getAsString();
            password = connDetails.get("pass").getAsString();
            driver = connDetails.get("driver").getAsString();
        }else {
            url = connectionDetails.get("Url").getAsString();
            user = connectionDetails.get("User").getAsString();
            password = connectionDetails.get("Pass").getAsString();
            driver = connectionDetails.get("Driver").getAsString();
        }

        String query = this.getQuery(dataMapTagContent, requestParameterJson);

        if (logger.isDebugEnabled()) {
            logger.debug("EFWD query being run is " + query);
        }

        Connection connection = getConnection(url, user, password, driver);
        //All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
        IJdbcDao bean = ApplicationContextAccessor.getBean(IJdbcDao.class);

        if (requestParameterJson.has("maxRows") && (bean instanceof JdbcDaoImpl)) {
            int maxRows = requestParameterJson.get("maxRows").getAsInt();
            ((JdbcDaoImpl) bean).setLimit(maxRows);
            query = GsonUtility.optStringValue(requestParameterJson,"processedLimitQuery", query);
        }
        return bean.getResultSet(connection, query);
    }
	
    
    
	public void streamResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties, CallBack<ResultSet> callBack) {
		String url, user, password, driver;
		if (connectionDetails.has("connDetails")) {
			JsonObject connDetails = connectionDetails.getAsJsonObject("connDetails");
			url = connDetails.get("url").getAsString();
			user = connDetails.get("user").getAsString();
			password = connDetails.get("pass").getAsString();
			driver = connDetails.get("driver").getAsString();
		} else {
			url = connectionDetails.get("Url").getAsString();
			user = connectionDetails.get("User").getAsString();
			password = connectionDetails.get("Pass").getAsString();
			driver = connectionDetails.get("Driver").getAsString();
		}

		String query = this.getQuery(dataMapTagContent, requestParameterJson);

		if (logger.isDebugEnabled()) {
			logger.debug("EFWD query being run is " + query);
		}

		Connection connection = getConnection(url, user, password, driver);
		//All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
		IJdbcDao bean = ApplicationContextAccessor.getBean(IJdbcDao.class);

		if (requestParameterJson.has("maxRows") && (bean instanceof JdbcDaoImpl)) {
			int maxRows = requestParameterJson.get("maxRows").getAsInt();
			((JdbcDaoImpl) bean).setLimit(maxRows);
			query = GsonUtility.optStringValue(requestParameterJson, "processedLimitQuery", query);
		}
		bean.streamResult(connection, query, callBack);
	}
}
