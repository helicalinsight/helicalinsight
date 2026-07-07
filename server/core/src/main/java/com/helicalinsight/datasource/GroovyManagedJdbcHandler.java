package com.helicalinsight.datasource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.Map.Entry;
import com.fasterxml.jackson.databind.JsonNode;
import com.helicalinsight.efw.ApplicationProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.datasource.helper.EfwDatasourceHelper;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.components.EfwdDataSourceHandler;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.GroovyUtils;

/**
 * GroovyManagedJdbcHandler extends {@link EfwdDataSourceHandler} interface
 * Handles Groovy managed JDBC connections and data sources.
 * @author Rajesh
 * Created by author on 2/11/2020.
 */
public class GroovyManagedJdbcHandler extends EfwdDataSourceHandler {
    private static final Logger logger = LoggerFactory.getLogger(GroovyManagedJdbcHandler.class);

    /**
     * readDS(String id, String type)
     * Reads a Groovy managed data source based on the given ID and type.
     *
     * @param id   		ID of the data source.
     * @param type 		type of the data source.
     * @return An ObjectNode containing the data source information.
     * @throws EfwServiceException if any data is not present
     */
    @Override
	public ObjectNode readDS(String id, String type) {
		ObjectNode contentNode = readDsContent(id, type);
		String condition = contentNode.with("data").required("condition").asText();
		ObjectNode data = super.evaluateGroovyCondition(condition);
		String globalId = data.path("globalId").asText();
		String catalog = data.has("catalog") ? data.get("catalog").asText() : null;
		String dbName = data.has("database") ? data.path("database").asText() : null;
		String schema = data.has("schema") ? data.path("schema").asText(): null;
		if (globalId.isBlank()) {
			throw new EfwServiceException("You do not have privileges to access the global connections.");
		}
		ObjectNode freshReq = JsonNodeFactory.instance.objectNode();
		freshReq.put("globalId", globalId);
		freshReq.put("id", globalId);
		freshReq.put("classifier", "global");
		freshReq.put("type", "dynamicDataSource");
		DataSourceReader dataSourceReader = new DataSourceReader();
		String resultG = dataSourceReader.doService("core", "dataSource", "read", freshReq.toString());
		ObjectNode res = null;
		try {
			res = new ObjectMapper().readValue(resultG, ObjectNode.class);
		} catch (JsonProcessingException e) {
			throw new EfwServiceException(e.getMessage());
		}
		int status = res.get("status").asInt();
		if( status == 0) {
			String message = res.with("response").get("message").asText();
			message = message.replace("Error: EfwServiceException:", "");
			throw new EfwServiceException(message);
		}
		res = res.with("response");
        res = removeAtFromJsonKey(res);
        if(res.has("extraOptions")) {
    JsonNode extraOptions = res.get("extraOptions");
            Map<String, String> resultMap = new HashMap<>();
            JSONObject jsonObject = JSONObject.fromObject(extraOptions.toString());
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                resultMap.put(key,jsonObject.getString(key));
            }
            String connectionProperties =  DataSourceUtils.buildConnectionProperties(resultMap);
            res.put("connectionProperties", connectionProperties);


    if (!extraOptions.isEmpty() && extraOptions.has("dataFile")) {
        String dataFileName = String.join(File.separator, ApplicationProperties.getInstance().getFlatFilesPath(),
                res.get("id").asText(), extraOptions.get("dataFile").asText());
        String newUrl = DataSourceUtils.updateFlatFileUrl(res.get("jdbcUrl").asText(), dataFileName);
        res.put("jdbcUrl", newUrl);
    }




}


		res.remove("databaseDialect");
		res.put("username", res.get("userName").asText());
		res.put("url", res.get("jdbcUrl").asText());
		res.put("driverClassName", res.get("driverName").asText());
		if(catalog != null) res.put("catalog", catalog);
		if(dbName != null) res.put("database", dbName);
		if(schema != null) res.put("schema", schema);
		
    	return res;
    }
    /**
     * readDsContent(String id, String  type)
     * Reads the content of a Groovy managed data source based on the given ID and type.
     *
     * @param id   		ID of the data source.
     * @param type 	    type of the data source.
     * @return An ObjectNode containing the data source content.
     */
    @Override
    public ObjectNode readDsContent(String id, String  type) {
    	 EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
         PlainConnDTO connection =  dbHandler.readEFWDConnection(Integer.valueOf(id), type);
         EfwDatasourceHelper utility = ApplicationContextAccessor.getBean(EfwDatasourceHelper.class);
         List<ObjectNode> response = new ArrayList<>();
         utility.prepareResponseObject(connection,response,type);
         return response.get(0);
    }

    /**
     * writeDS(ObjectNode json)
     * Writes a Groovy managed data source.
     *
     * @param json 			JSON representation of the data source.
     * @return ID of the saved data source.
     */
    @Override
    public Integer writeDS(ObjectNode json) {
        EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        return dbHandler.saveHIEFWDConnection(json);
    }
    /**
     * updateDS(ObjectNode json)
     * Updates a Groovy managed data source.
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
     * Tests a Groovy managed data source.
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
        
        if (StringUtils.isBlank(url)) { 
        	String condition = json.path("condition").asText();
        	ObjectNode connectionDetails =  super.evaluateGroovyCondition(condition);
        	model.putPOJO("data", connectionDetails);
        	model.put("message", "The connection test is successful");
            return model;
        }


        EfwdConnection efwdConnection = getEfwdConnection(url, userName, CipherUtils.decrypt(password), driverName);

        Connection connection = efwdConnection.getConnection();
        if (connection != null) {
            model.put("message", "The connection test is successful");
            DbUtils.closeQuietly(connection);
        }

        return model;
    }
    /**
     * getManipulatedJson(String id, String type, @NotNull ObjectNode connection)
     * This method creates a new JSON object with potential modifications to its keys,
     * values, or structure .
     * @param id         		ID of the data source.
     * @param type      		type of the data source.
     * @param connection 		JSON representation of the data source connection.
     * @return An ObjectNode containing the newly add data.
     * @throws IOException If an I/O error occurs while processing the JSON.
     */
    @NotNull
    private ObjectNode getManipulatedJson(String id, String type, @NotNull ObjectNode connection) throws IOException {
        JsonObject conditionLegacy = GroovyUtils.executeGroovy(connection.get("Condition").asText(), "evalCondition", JsonObject.class);

        ObjectNode condition = new ObjectMapper().readValue(conditionLegacy.toString(), ObjectNode.class);

        if (!GlobalJdbcTypeUtils.isManagedGroovyDataSource(type)) {
            throw new EfwException("Unable to process the type of connection :" + type);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("adhoc condition: " + connection);
        }

        condition.put("classifier", "global");
        int globalId = condition.get("globalId").asInt();
        String globalString = DataSourceUtils.globalIdJson(globalId);
        ObjectNode globalJson = new ObjectMapper().readValue(globalString, ObjectNode.class); //JSONObject.fromObject(globalString);
        condition.setAll(globalJson);
        condition.put("id", globalId);
        if (condition.has("name")) {
            String name = condition.get("name").asText();
            condition.put("@name", name);
        }
        condition.put("@name", id);
        if (condition.has("userName"))
            condition.put("userName", condition.get("userName").asText());

        if (condition.has("driverClassName"))
            condition.put("driverName", condition.get("driverClassName").asText());

        if (condition.has("url"))
            condition.put("jdbcUrl", condition.get("url").asText());

        return condition;

    }
    
    /**
     * getEfwdConnection(String url, String userName, String password, String driverName)
     * Gets an EfwdConnection object based on the provided URL, username, password, and driver name.
     *
     * @param url       	JDBC URL.
     * @param userName   	username.
     * @param password   	password.
     * @param driverName 		 JDBC driver class name.
     * @return An EfwdConnection object.
     */
    public EfwdConnection getEfwdConnection(String url, String userName, String password, String driverName) {
        EfwdConnection efwdConnection = ApplicationContextAccessor.getBean(EfwdConnection.class);

        efwdConnection.setUrl(url);
        efwdConnection.setUser(userName);
        efwdConnection.setPassword(password);
        efwdConnection.setDriver(driverName);
        return efwdConnection;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * removeAtFromJsonKey(ObjectNode dataJson)
     * Removes "@" from JSON keys.
     *
     * @param dataJson 			JSON object to remove "@" from its keys.
     * @return An ObjectNode with "@" removed from JSON keys.
     */
    private ObjectNode removeAtFromJsonKey(ObjectNode dataJson) {
        Iterator<String> jsonKeys = dataJson.fieldNames();
        ObjectNode duplicateJson = JsonNodeFactory.instance.objectNode();
        jsonKeys.forEachRemaining(key -> {
        	String tempKey = "";
        	String tempValue = "";
        	if(key.startsWith("@")) {
        		tempKey = key.replace("@", "");
        	}
        	else {
        		tempKey = key;
        	}
        	tempValue = dataJson.get(key).asText();
        	duplicateJson.put(tempKey, tempValue);
        });
        if(dataJson.has("extraOptions")){
            duplicateJson.put("extraOptions",dataJson.get("extraOptions"));
        }
        return duplicateJson;
    }
    /**
     * readDSFile(String id, String type, JsonObject connection)
     * Reads a JSON representation of a data source based on the given ID, type, and connection details.
     *
     * @param id         		ID of the data source.
     * @param type       		type of the data source.
     * @param connection 		JSON representation of the data source connection.
     * @return A JsonObject containing the data source information.
     */
    @Override
	public JsonObject readDSFile(String id, String type, JsonObject connection) {
    	JsonObject condition = GroovyUtils.executeGroovy(connection.get("Condition").getAsString(), "evalCondition", JsonObject.class);
        if (!GlobalJdbcTypeUtils.isManagedGroovyDataSource(type)) {
            throw new EfwException("Unable to process the type of connection :" + type);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("adhoc condition: " + connection);
        }
        condition.addProperty("classifier", "global");
        int globalId = condition.get("globalId").getAsInt();
        String globalString = DataSourceUtils.globalIdJson(globalId);
        JsonObject globalJson =new Gson().fromJson(globalString, JsonObject.class);
        for(String key :  globalJson.keySet()) {
        	condition.add(key, globalJson.get(key));
        }
        condition.addProperty("id", globalId);
        condition.addProperty("@name", (condition.has("name") ? condition.get("name").getAsString() : String.valueOf(globalId)));

        if (condition.has("username"))
            condition.addProperty("userName", condition.get("username").getAsString());

        if (condition.has("driverClassName"))
            condition.addProperty("driverName", condition.get("driverClassName").getAsString());

        if (condition.has("url"))
            condition.addProperty("jdbcUrl", condition.get("url").getAsString());
        return condition;
	}

}
