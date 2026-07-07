package com.helicalinsight.efw.components;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.datasource.EfwdConnection;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.helper.EfwDatasourceHelper;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;



/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class SqlJdbcHandler extends EfwdDataSourceHandler {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    
    @Override
    public ObjectNode readDS(String id, String type) {

        EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        PlainConnDTO connection =  dbHandler.readEFWDConnection(Integer.valueOf(id), type);
        EfwDatasourceHelper helper = ApplicationContextAccessor.getBean(EfwDatasourceHelper.class);
        List<ObjectNode> response = new ArrayList<>();
        helper.prepareResponseObject(connection,response,type);
        return getManipulatedJson(id,type,response.get(0));
    }

    @NotNull
    private ObjectNode  getManipulatedJson(String id, String type, @NotNull ObjectNode connection){

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jdbcConnection = JsonNodeFactory.instance.objectNode();
        ObjectNode connectionData = connection.with("data");
        jdbcConnection.put("@id", id);
        jdbcConnection.put("@name", (connection.has("name") ? connection.path("name").asText() : id));
        jdbcConnection.put("@type", type);
        jdbcConnection.put("@baseType", type);
        jdbcConnection.put("jdbcUrl", connectionData.path("jdbcUrl").asText());
        jdbcConnection.put("userName", connectionData.path("userName").asText());
        jdbcConnection.put("password", connectionData.path("password").asText());
        jdbcConnection.put("driverName", connectionData.path("driverName").asText());
        if (connection.has("databaseDialect")) {
            jdbcConnection.put("databaseDialect", connection.path("databaseDialect").asText());
        }
        jdbcConnection.putPOJO("data", connectionData);
        return jdbcConnection;
    }

    @Override
    public Integer writeDS(ObjectNode formData) {
        EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
        return dbHandler.saveHIEFWDConnection(formData);
    }

    @Override
    public Integer updateDS(ObjectNode json) {

        JsonNode typeNode = json.get("type");
        if(null == typeNode) {
        	throw new EfwdServiceException("Required Parameter type missing");
        }
		String type = typeNode.asText();
		EfwDatasourceHelper helper = ApplicationContextAccessor.getBean(EfwDatasourceHelper.class);
		EFWDDBHandler dbHandler = ApplicationContextAccessor.getBean(EFWDDBHandler.class);
		EFWDConnSqlJDBC jdbcObj = helper.buildPlainJdbcObject(json);
		return dbHandler.updatePlainJDBC(jdbcObj).getHiEfwdConnection().getId();
    }

    @Override
    public ObjectNode testDS(ObjectNode json) {
        return super.testDS(json);
    }

	@Override
	public ObjectNode readDsContent(String id, String type) {
		return readDS(id, type);
	}
	
	/**
	 * using gson 
	 * readDSFile(String id, String type, JsonObject connection)
	 */
	@Override
	public JsonObject readDSFile(String id, String type, JsonObject connection) {
		JsonObject jdbcConnection = new JsonObject();
	        jdbcConnection.addProperty("@id", id);
	        jdbcConnection.addProperty("@name", (connection.has("name") ? connection.get("name").getAsString() : id));
	        jdbcConnection.addProperty("@type", type);
	        jdbcConnection.addProperty("@baseType", type);
	        jdbcConnection.addProperty("jdbcUrl", connection.get("Url").getAsString());
	        jdbcConnection.addProperty("userName", connection.get("User").getAsString());
	        jdbcConnection.addProperty("password", CipherUtils.decrypt(connection.get("Pass").getAsString()));
	        jdbcConnection.addProperty("driverName", connection.get("Driver").getAsString());
	        if(connection.has("databaseDialect")){
	        jdbcConnection.addProperty("databaseDialect", GsonUtility.optString(connection,"databaseDialect"));
	        }
	        return jdbcConnection;
	}
}
