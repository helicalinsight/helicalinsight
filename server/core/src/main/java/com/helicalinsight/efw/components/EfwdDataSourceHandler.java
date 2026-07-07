package com.helicalinsight.efw.components;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.EfwdConnection;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.FrameworkObject;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.GroovyUtils;


/**
 * Created by user on 11/27/2015.
 *`
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public abstract class EfwdDataSourceHandler implements FrameworkObject {

    public abstract ObjectNode readDS(String id, String type);
    /**
     * using gson
     * readDSFile(String id, String type, JsonObject json)
     * @param id
     * @param type
     * @param json
     * @return
     */
    public abstract JsonObject readDSFile(String id, String type, JsonObject json);

    public abstract Integer writeDS(ObjectNode json);

    public abstract Integer updateDS(ObjectNode json);
    
    public  abstract ObjectNode readDsContent(String id, String type);
    
    public ObjectNode testDS(ObjectNode json) {
        ObjectNode model = JsonNodeFactory.instance.objectNode();
        String url;
        String userName;
        String password;
        String driverName;
        String dir;
        try {
            url = json.required("jdbcUrl").asText();
            userName = json.required("userName").asText();
            password = json.required("password").asText();
            driverName = json.required("driverName").asText();
            dir = json.path("directory").asText();
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }
        
        EfwdDatasourceUtils.validatePermission(dir);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("jdbcUrl", url);

        parameters.put("driverName", driverName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);


        EfwdConnection efwdConnection = getEfwdConnection(url, userName, CipherUtils.decrypt(password), driverName);

        Connection connection = efwdConnection.getConnection();
        if (connection != null) {
            model.put("message", "The connection test is successful");
            DbUtils.closeQuietly(connection);
        }
        return model;
    }

    public Integer deleteDatasource(Integer id) {
        EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        return dbHandler.deleteConnection(id);
    }

    public EfwdConnection getEfwdConnection(String url, String userName, String password, String driverName) {
        EfwdConnection efwdConnection = ApplicationContextAccessor.getBean(EfwdConnection.class);

        efwdConnection.setUrl(url);
        efwdConnection.setUser(userName);
        efwdConnection.setPassword(password);
        efwdConnection.setDriver(driverName);
        return efwdConnection;
    }
    
    public  ObjectNode evaluateGroovyCondition(String condition) {
		JsonObject executionResult = new JsonObject();
		try {
			executionResult = GroovyUtils.executeGroovy(condition, "evalCondition", JsonObject.class);
			if (!executionResult.isJsonNull()) {
				return prepareConnectionDetails(executionResult.toString());
			}
		} catch (Exception e) {
			throw new EfwServiceException("Error while executing groovy condition");
		}
		return null;
	}

	private ObjectNode prepareConnectionDetails(String connectionString) {
		
		try {
			ObjectNode resultNode = new ObjectMapper().readValue(connectionString, ObjectNode.class);
			if (resultNode.has("url")) {
				resultNode.put("jdbcUrl", resultNode.get("url").asText());
				resultNode.remove("url");
			}
			if (resultNode.has("user")) {
				resultNode.put("userName", resultNode.get("user").asText());
				resultNode.remove("user");
			}
			if (resultNode.has("pass")) {
				resultNode.put("password", resultNode.get("pass").asText());
				resultNode.remove("pass");
			}
			if (resultNode.has("driver")) {
				resultNode.put("driverName", resultNode.get("driver").asText());
				resultNode.remove("driver");
			}

			return resultNode;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}


}
