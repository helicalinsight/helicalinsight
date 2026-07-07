package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.datasource.managed.JdbcDaoImpl;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.GroovyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * ExtJDBCDriver class extends {@link JDBCDriver}
 * This class is responsible for obtaining the connection depending on the different users, roles, profile attributes.
 *
 * @author Nitin U
 * @since M2
 */

public class ExtJDBCDriver extends JDBCDriver {

    private static final Logger logger = LoggerFactory.getLogger(ExtJDBCDriver.class);

    /**
     * getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
     JsonObject dataMapTagContent, ApplicationProperties properties)
     * Return the json of the result of the database query
     *
     * @param requestParameterJson 			Http Request parameter
     * @param connectionDetails   			connection details from the EFWD
     * @param dataMapTagContent    			content of the data map tag from the corresponding file
     * @param properties          			singleton instanc`e of ApplicationProperties
     * @return The json of the result of the database query
     */
    public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                  JsonObject dataMapTagContent, ApplicationProperties properties) {

        String query = this.getQuery(dataMapTagContent, requestParameterJson);
        String condition_,url,user,password,driver;
        if(connectionDetails.has("Condition")) {

            condition_=connectionDetails.get("Condition").getAsString();
            url = GsonUtility.optString(connectionDetails, "Url");
            user = GsonUtility.optString(connectionDetails,"User");
            password = GsonUtility.optString(connectionDetails,"Pass");
            driver = GsonUtility.optString(connectionDetails,"Driver");

            JsonObject condition = GroovyUtils.executeGroovy(condition_, "evalCondition", JsonObject.class);
            if (condition.has("url")) {
                url = condition.get("url").getAsString();
            }
            if (condition.has("user")) {
                user = condition.get("user").getAsString();
            }
            if (condition.has("password")) {
                password = condition.get("password").getAsString();
            }
            if (condition.has("pass")) {
                password = condition.get("pass").getAsString();
            }
            if (condition.has("driver")) {
                driver = condition.get("driver").getAsString();
            }
        }
        else {
            JsonObject connDetails=connectionDetails.getAsJsonObject("connDetails");
            condition_= GsonUtility.optString(connDetails, "condition");
            url = GsonUtility.optString(connDetails, "url");
            user = GsonUtility.optString(connDetails, "user");
            password = GsonUtility.optString(connDetails, "pass");
            driver = GsonUtility.optString(connDetails, "driver");
        }

        Connection connection = getConnection(url, user, password, driver);
        //All query executions should be through IJdbcDao for the purpose of query cancellation if timeout occurs
        IJdbcDao bean = ApplicationContextAccessor.getBean(IJdbcDao.class);
        if (requestParameterJson.has("maxRows") && (bean instanceof JdbcDaoImpl)) {
            ((JdbcDaoImpl) bean).setLimit(requestParameterJson.get("maxRows").getAsInt());
            query = GsonUtility.optStringValue(requestParameterJson,"processedLimitQuery", query);
        }
        return bean.query(connection, query);
    }
    /**
     * getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson)
     * Gets the SQL query string from data map content and evaluates it if the type is "groovy."
     *
     * @param dataMapTagContent     Content of the data map tag from the corresponding file.
     * @param requestParameterJson  HTTP request parameters.
     * @return The SQL query string.
     */
    @Override
    public String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) {
        String query = super.getQuery(dataMapTagContent, requestParameterJson);
        String type;
        if(dataMapTagContent.has("type"))
            type=dataMapTagContent.get("type").getAsString();
        else
            type=dataMapTagContent.get("type").getAsString();
        if (StringUtils.isNotBlank(type) && type.toLowerCase().contains("groovy")) {
            return GroovyUtils.executeGroovy(query, "evalCondition", String.class);
        }
        return query;
    }


    /**
     * getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
     JsonObject dataMapTagContent, ApplicationProperties applicationProperties)
     * Returns a result set from the database based on the provided parameters.
     *
     * @param requestParameterJson   HTTP request parameters.
     * @param connectionDetails      Connection details from the EFWD.
     * @param dataMapTagContent      Content of the data map tag from the corresponding file.
     * @param applicationProperties  Singleton instance of ApplicationProperties.
     * @return A ResultSet containing the data from the database query.
     */
    @Override
    public ResultSet getResultSetData(JsonObject requestParameterJson, JsonObject connectionDetails,
                                      JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {

        JsonObject condition = GroovyUtils.executeGroovy(connectionDetails.get("Condition").getAsString(), "evalCondition", JsonObject.class);
        String url = connectionDetails.get("Url").getAsString();
        String user = connectionDetails.get("User").getAsString();
        String password = connectionDetails.get("Pass").getAsString();
        String driver = connectionDetails.get("Driver").getAsString();

        if (condition.has("url")) {
            url = condition.get("url").getAsString();
        }
        if (condition.has("user")) {
            user = condition.get("user").getAsString();
        }
        if (condition.has("password")) {
            password = condition.get("password").getAsString();
        }
        if (condition.has("driver")) {
            driver = condition.get("driver").getAsString();
        }

        Connection connection = getConnection(url, user, password, driver);

        JdbcDaoImpl jdbcImpl = ApplicationContextAccessor.getBean(JdbcDaoImpl.class);
        String sql=getQuery(dataMapTagContent,requestParameterJson);
        ResultSet resultSet = jdbcImpl.getResultSet(connection, sql);

        return resultSet;
    }

}