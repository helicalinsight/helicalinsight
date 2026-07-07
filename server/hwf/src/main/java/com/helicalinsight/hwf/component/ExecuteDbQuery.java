package com.helicalinsight.hwf.component;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.JDBCDriver;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.hwf.core.IJobProcess;
import com.helicalinsight.hwf.core.api.ExecutionStatus;
import com.helicalinsight.hwf.util.ComponentUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 *         Created  on 5/7/2016.
 */

public class ExecuteDbQuery implements IJobProcess {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteDbQuery.class);
    private Boolean hasNext = ExecutionStatus.HAS_NEXT_NOT_SET;
    private Integer executionState = ExecutionStatus.NOT_SET;

    /**
     * changed to gson
     */
    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject jobProcess) {


        JDBCDriver jdbcDriver = new JDBCDriver();

        JsonObject connectionDetails = new JsonObject();
        connectionDetails.addProperty("Url", input.getString("Url"));
        connectionDetails.addProperty("User", input.getString("User"));
        connectionDetails.addProperty("Pass", input.getString("Pass"));
        connectionDetails.addProperty("Driver", input.getString("Driver"));
        JsonObject requsetParam = new JsonObject();
        JsonObject dataMap = new JsonObject();
        dataMap.addProperty("Query", jobProcess.getString("Query"));
        executionState = ExecutionStatus.SUCCESS;

        return JSONObject.fromObject(jdbcDriver.getJSONData(requsetParam, connectionDetails, dataMap,
                ApplicationProperties.getInstance()).toString());


        /*DatabaseQueryExecutor databaseQueryExecutor = new DatabaseQueryExecutor();
        String jsonFormData="{\"location\":\"1462620946474\"," +
                "\"metadataFileName\":\"300121fd-1667-4208-b391-945e840b092e.metadata\"," +
                "\"query\":\"select * from h_users\"}";
        String result = databaseQueryExecutor.executeComponent(jsonFormData);
return JSONObject.fromObject(result);
        */














       /* String type="dynamicDataSource";
        JSONObject formJson = new JSONObject();
        formJson.accumulate("id", input.getString("globalConnectionId"));
        formJson.accumulate("type", "db.generic");
        formJson.accumulate("classifier", "db.generic");

        DriverConnection databaseConnection = (DriverConnection) ConnectionProvider.getConnection(formJson, type);
        Connection connection = null;
        if (databaseConnection != null) {
            connection = databaseConnection.getConnection();
        }
        String query = input.getString("query");

        IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
        return JSONObject.fromObject(jdbcDao.query(connection, query).toString());*/

        /*logger.info("inputs are "+input);
        logger.info("Inside this jobprocess");
        JSONObject testJson = new JSONObject();
        testJson.put("test", "hello");
        testJson.put("nextKey", "test");
        //testJson.put("name", "begampet");
        return testJson;*/
    }

    @Override
    public JSONObject executionStatus() {

        return ComponentUtils.setExecutionStatus(executionState);
    }
}
