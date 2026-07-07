package com.helicalinsight.efw.components;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.calcite.CalciteConnectionProvider;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class SqlCalciteHandler extends EfwdDataSourceHandler {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public ObjectNode readDS(String id, String type) {
        return null;
    }

    @NotNull
    private ObjectNode getManipulatedJson(String id, String type, @NotNull ObjectNode connection) {
        ObjectNode jdbcConnection = JsonNodeFactory.instance.objectNode();
        jdbcConnection.put("@id", id);
        jdbcConnection.put("@name", (connection.has("@name") ? connection.get("@name").asText() : id));
        jdbcConnection.put("@type", type);
        jdbcConnection.put("@baseType", type);
        jdbcConnection.put("driverName", connection.get("driverName").asText());
        jdbcConnection.put("model", connection.get("model").asText());
        if(connection.has("databaseDialect")){
            jdbcConnection.put("databaseDialect", connection.get("databaseDialect").asText());
        }
        return jdbcConnection;
    }

    @Override
    public Integer writeDS(ObjectNode json) {
        return null;
    }

    @Override
    public Integer updateDS(ObjectNode json) {
        return null;
    }

    @Override
    public ObjectNode testDS(ObjectNode json) {
        ObjectNode response = JsonNodeFactory.instance.objectNode();

        String model;
        String driverName;
        model = json.get("model").asText();
        driverName = json.get("driverName").asText();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("model", model);
        parameters.put("driverName", driverName);

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        CalciteConnectionProvider connectionProvider = ApplicationContextAccessor.getBean(CalciteConnectionProvider
                .class);

        Connection connection = connectionProvider.getConnection(model);
        if (connection != null) {
            response.put("message", "The connection test is successful");
            DbUtils.closeQuietly(connection);
        } else {
            throw new EfwdServiceException("Couldn't get connection.");
        }

        return response;
    }

	@Override
	public ObjectNode readDsContent(String id, String type) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * using gson
	 * readDSFile(String id, String type, JsonObject connection)
	 */
	@Override
	public JsonObject readDSFile(String id, String type, JsonObject connection) {
		JsonObject jdbcConnection = new JsonObject();
        jdbcConnection.addProperty("@id", id);
        jdbcConnection.addProperty("@name", (connection.has("@name") ? connection.get("@name").getAsString() : id));
        jdbcConnection.addProperty("@type", type);
        jdbcConnection.addProperty("@baseType", type);
        jdbcConnection.addProperty("driverName", connection.get("driverName").getAsString());
        jdbcConnection.addProperty("model", connection.get("model").getAsString());
        if(connection.has("databaseDialect")){
        jdbcConnection.addProperty("databaseDialect", connection.get("databaseDialect").getAsString());
        }
        return jdbcConnection;
	}
}
