package com.helicalinsight.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.datasource.helper.EfwDatasourceHelper;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.components.EfwdDataSourceHandler;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.GroovyUtils;


/**
 * GroovySqlJdbcHandler extends {@link EfwdDataSourceHandler} 
 * Handles Groovy SQL JDBC connections and data sources.
 * Created by user on 07/24/2017.
 * @author Nitin Uttarwar
 */

public class GroovySqlJdbcHandler extends EfwdDataSourceHandler {
    private static final Logger logger = LoggerFactory.getLogger(GroovySqlJdbcHandler.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * readDS(String id, String type)
     * Reads a Groovy Sql jdbc data source based on the given ID and type.
     *
     * @param id   		ID of the data source.
     * @param type 		type of the data source.
     * @return An ObjectNode containing the data source information.
     * @throws EfwServiceException if any data is not present
     */
    @Override
	public ObjectNode readDS(String id, String type) {
    	ObjectNode result = JsonNodeFactory.instance.objectNode();
    	ObjectNode contentNode = readDsContent(id, type);
    	String condition = contentNode.with("data").required("condition").asText();
    	ObjectNode data = super.evaluateGroovyCondition(condition);
    	result.putPOJO("data", data);
    	return result;
    }
    /**
     * readDsContent(String id, String  type)
     * Reads the content of a Groovy Sql Jdbc data source based on the given ID and type.
     *
     * @param id   		ID of the data source.
     * @param type 	    type of the data source.
     * @return An ObjectNode containing the data source content.
     */
    @Override
    public ObjectNode readDsContent(String id, String  type) {
    	 EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
         PlainConnDTO connection = dbHandler.readEFWDConnection(Integer.valueOf(id), type);
         EfwDatasourceHelper utility = ApplicationContextAccessor.getBean(EfwDatasourceHelper.class);
         List<ObjectNode> response = new ArrayList<>();
         utility.prepareResponseObject(connection,response,type);
         return response.get(0);

    }
    /**
     * getManipulatedJson(String id, String type, @NotNull ObjectNode connection)
     * This method creates a new JSON object with potential modifications to its keys,
     * values, or structure based on certain conditions and requirements.
     *
     * @param id         		ID of the data source.
     * @param type      		type of the data source.
     * @param connection 		JSON representation of the data source connection.
     * @return An ObjectNode containing the newly added data.
     * @throws IOException If an I/O error occurs while processing the JSON.
     */
    @NotNull
    private ObjectNode getManipulatedJson(String id, String type, @NotNull ObjectNode connection) throws  IOException {

        ObjectNode jdbcConnection = JsonNodeFactory.instance.objectNode();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode connectionJson =  mapper.readValue(connection.toString(),ObjectNode.class);
        ObjectNode connectionData =  connectionJson.with("data");

        String groovyCondition = connectionData.path("condition").asText();
        JsonObject conditionObj = GroovyUtils.executeGroovy(groovyCondition, "evalCondition", JsonObject.class);
        ObjectNode condition = mapper.readValue(conditionObj.toString(), ObjectNode.class);
        if (logger.isDebugEnabled()) {
            logger.debug("adhoc condition: {}",connection);
        }

        jdbcConnection.put("@id", id);
        jdbcConnection.put("@name", (connection.has("name") ? connection.path("name").asText() : id));
        jdbcConnection.put("@type", type);
        jdbcConnection.put("@baseType", type);
        if (connection.has("databaseDialect")) {
            jdbcConnection.put("databaseDialect", connection.path("databaseDialect").asText());
        }
        if (condition.has("url")) {
            String url = condition.path("url").asText();
            jdbcConnection.put("jdbcUrl", url);
        } else {
            jdbcConnection.put("jdbcUrl", connectionData.path("url").asText());
        }

        if (condition.has("driver")) {
            String driver = condition.get("driver").asText();
            jdbcConnection.put("driverName", driver);
        } else {
            jdbcConnection.put("driverName", connectionData.path("driver").asText());
        }

        if (condition.has("user")) {
            String user = condition.path("user").asText();
            jdbcConnection.put("userName", user);
        } else {
            jdbcConnection.put("userName", connectionData.path("userName").asText());
        }

        if (condition.has("password")) {
            String password = CipherUtils.decrypt(condition.path("password").asText());
            jdbcConnection.put("password", password);
        } else {
            jdbcConnection.put("password",connectionData.path("password").asText());
        }


        return jdbcConnection;
    }
    /**
     * writeDS(ObjectNode json)
     * Writes a Sql Jdbc data source.
     *
     * @param json 			JSON containing details of data source type 
     * @return ID of the saved data source.
     */
    @Override
    public Integer writeDS(ObjectNode json) {
        EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        return dbHandler.saveHIEFWDConnection(json);
    }
    /**
     * updateDS(ObjectNode json)
     * Updates a Groovy Sql Jdbc data source.
     *
     * @param json 			JSON representation of the data source.
     * @return ID of the updated data source.
     */
    @Override
    public Integer updateDS(ObjectNode json) {
        String type = json.get("type").asText();
        if(null == type) {
            throw new EfwdServiceException("Required Parameter type missing");
        }
        EfwDatasourceHelper utility = ApplicationContextAccessor.getBean(EfwDatasourceHelper.class);
        EFWDConnGroovy object = utility.buildPlainGroovyObject(json);
        EFWDDBHandler handler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        EFWDConnGroovy returned = handler.updateGroovy(object);
        return returned.getHiEfwdConnection().getId();
    }
    /**
     * testDS(ObjectNode json)
     * Tests a Groovy Sql jdbc data source.
     *
     * @param json 	    JSON provides username, password, url, driver details
     * @return An ObjectNode with the test result message.
     */
    @Override
    public ObjectNode testDS(ObjectNode json) {

        ObjectNode model = JsonNodeFactory.instance.objectNode();
        String url;
        String userName;
        String password;
        String driverName;
        String dir;
        
        if(json.has("data") ) {
        	ObjectNode objectNode = null;
        	String jsonString = json.get("data").asText();
        	ObjectMapper objectMapper = new ObjectMapper();
        	 try {
        		 objectNode = (ObjectNode) objectMapper.readTree(jsonString);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	
			json = objectNode;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Test DataSource Form Data: " + json);
        }
        try {
        	url = json.path("jdbcUrl").asText();
        	userName = json.path("userName").asText();
        	password = json.path("password").asText();
        	driverName = json.path("driverName").asText();
        	dir = json.path("directory").asText();
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }

        	EfwdDatasourceUtils.validatePermission(dir);

        	String condition = json.required("condition").asText();
        	ObjectNode connectionDetails =  super.evaluateGroovyCondition(condition);

        String jdbcUrl = connectionDetails.path("jdbcUrl").asText();
        String userNameG = connectionDetails.path("userName").asText();
        String passwordG = connectionDetails.path("password").asText();
        String driverNameG = connectionDetails.path("driverName").asText();
        url = StringUtils.isBlank(jdbcUrl)?url:jdbcUrl;
        userName = StringUtils.isBlank(userNameG)?userName:userNameG;
        password = StringUtils.isBlank(passwordG)?CipherUtils.decrypt(password):passwordG;
        driverName = StringUtils.isBlank(driverNameG)?driverName:driverNameG;


        EfwdConnection efwdConnection = getEfwdConnection(url, userName, password, driverName);

        Connection connection = efwdConnection.getConnection();
        if (connection != null) {
            model.put("message", "The connection test is successful");
            DbUtils.closeQuietly(connection);
        }

        return model;
    }
    /**
     * readDSFile(String id, String type, JsonObject connection)
     * Reads a JSON representation of a data source based on the given ID, type, and connection details.
     *
     * @param id         		ID of the data source.
     * @param type       		type of the data source.
     * @param connection 		JSON representation of the data source connection.
     * @return A JsonObject containing the jdbc connection information.
     * 
     */
    @Override
	public JsonObject readDSFile(String id, String type, JsonObject connection) {
    	JsonObject jdbcConnection = new JsonObject();
        String groovyCondition = connection.get("Condition").getAsString();
        JsonObject condition = GroovyUtils.executeGroovy(groovyCondition, "evalCondition", JsonObject.class);
        if (logger.isDebugEnabled()) {
            logger.debug("adhoc condition: " + connection);
        }
        jdbcConnection.addProperty("@id", id);
        jdbcConnection.addProperty("@name", (connection.has("@name") ? connection.get("@name").getAsString() : id));
        jdbcConnection.addProperty("@type", type);
        jdbcConnection.addProperty("@baseType", type);
        if (connection.has("databaseDialect")) {
            jdbcConnection.addProperty("databaseDialect", connection.get("databaseDialect").getAsString());
        }
        if (condition.has("url")) {
            String url = condition.get("url").getAsString();
            jdbcConnection.addProperty("jdbcUrl", url);
        } else {
            jdbcConnection.addProperty("jdbcUrl", connection.get("Url").getAsString());
        }
        if (condition.has("driver")) {
            String driver = condition.get("driver").getAsString();
            jdbcConnection.addProperty("driverName", driver);
        } else {
            jdbcConnection.addProperty("driverName", connection.get("Driver").getAsString());
        }
        if (condition.has("user")) {
            String user = condition.get("user").getAsString();
            jdbcConnection.addProperty("userName", user);
        } else {
            jdbcConnection.addProperty("userName", connection.get("User").getAsString());
        }
        if (condition.has("password")) {
            String password = CipherUtils.decrypt(condition.get("password").getAsString());
            jdbcConnection.addProperty("password", password);
        } else {
            jdbcConnection.addProperty("password", CipherUtils.decrypt(connection.get("Pass").getAsString()));
        }
        return jdbcConnection;
	}

}