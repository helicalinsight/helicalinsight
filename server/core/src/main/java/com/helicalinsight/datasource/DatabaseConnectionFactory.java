package com.helicalinsight.datasource;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.IJdbcConnectionService;
import com.helicalinsight.datasource.managed.TestConnectionProvider;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.EfwdDataSourceHandler;
import com.helicalinsight.efw.components.EfwdReader;
import com.helicalinsight.efw.components.SqlJdbcHandler;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.DrillConfigUrlContext;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.HISparkContext;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;


import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;

/**
 * DatabaseConnectionFactory implementing {@link IConnectionFactory} interface
 * Manages database connections and provides methods for connecting to various data sources.
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class DatabaseConnectionFactory implements IConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionFactory.class);
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * getMinus1DataSource()
     * This method created a data source configuration based on the Drill middleware settings
     * and replaces placeholders with actual values such as username, password, and URL.
     *
     * @return A data source configuration as a string with placeholders replaced by actual values.
     * @throws EfwdServiceException If the Drill middleware is disabled.
     */
    public static String getMinus1DataSource() {
        String password = DrillConfigUrlContext.getPassword();
        String username = DrillConfigUrlContext.getUsername();
        String url = DrillConfigUrlContext.getJdbcUrl();
        if (!DrillConfigUrlContext.isEnabled()) {
            throw new EfwdServiceException("The Drill Middleware is disabled.");
        }
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();

        String minus1DataSource = GsonUtility.optString(settingsJson, "dsTemplate")
                .replaceAll("_username_", username)
                .replaceAll("_password_", password)
                .replaceAll("_url_", url)
                .replaceAll("_tomcatid_", "-1")
                .replaceAll("_id_", "-1")
                .replaceAll("_driverClass_", "org.apache.drill.jdbc.Driver");

        return minus1DataSource;
    }

    /**
     * getConnectionFromTemp(String type, String jsonInfo)
     * Retrieves a database connection for the given data source type and JSON information.
     *
     * @param type     data source type
     * @param jsonInfo formData
     * @return DriverConnection object for the database connection.
     */
    @Nullable
    public DriverConnection getConnectionFromTemp(String type, String jsonInfo) {
        Connection connection = null;

        JsonObject formData = new Gson().fromJson(jsonInfo, JsonObject.class);

        DriverConnection driverConnection = new DriverConnection();
        boolean isEfwd = GsonUtility.optStringValue(formData, "dir", null) != null;
        boolean isTemp = GsonUtility.optBooleanValue(formData, "isTemp", false);
        if (!GlobalJdbcTypeUtils.isTypeGlobal(type)) {


            JsonObject efwdJson;

            if (formData.get("id").getAsInt() == -1) {
                TestConnectionProvider.minusJson(null, formData, JsonUtils.getHiMiddleWareName());
                efwdJson = formData;
            } else {
                if (isTemp && !isEfwd) {
                    formData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
                }
                String efwdConnectionJson;
                if (!isTemp)
                    efwdConnectionJson = efwdJson(formData);
                else
                    efwdConnectionJson = efwdJsonForTemp(formData);
                efwdJson = new Gson().fromJson(efwdConnectionJson, JsonObject.class);
            }
            String jdbcUrl ;
            String userName ;
            String password;
            String driverName ;

            if(efwdJson.has("jdbcUrl")){
                jdbcUrl = efwdJson.get("jdbcUrl").getAsString();
                userName = GsonUtility.optString(efwdJson, "userName");
                password = GsonUtility.optString(efwdJson, "password");
                driverName = efwdJson.get("driverName").getAsString();

            }else{
                JsonObject datad=efwdJson.get("data").getAsJsonObject();
                jdbcUrl = datad.get("jdbcUrl").getAsString();
                userName = GsonUtility.optString(datad, "userName");
                password = GsonUtility.optString(datad, "password");
                driverName = datad.get("driverName").getAsString();
            }

            driverConnection.setDriverClass(driverName);

            SqlJdbcHandler sqlJdbcHandler = new SqlJdbcHandler();

            EfwdConnection efwdConnection = sqlJdbcHandler.getEfwdConnection(jdbcUrl, userName, password, driverName);

            if (logger.isDebugEnabled()) {
                logger.debug("EFWD details are" + efwdConnection);
            }

            connection = efwdConnection.getConnection();
            driverConnection.setConnection(connection);
        } else {
            boolean nonPooled = GlobalJdbcType.NON_POOLED.equalsIgnoreCase(type);
            boolean staticDataSource = GlobalJdbcType.STATIC_DATASOURCE.equalsIgnoreCase(type);
            boolean dynamicDataSource = GlobalJdbcType.DYNAMIC_DATASOURCE.equalsIgnoreCase(type);
            boolean noSqldataSource = GlobalJdbcType.NOSQL_DATASOURCE.equalsIgnoreCase(type);
            boolean globalDataSource = GlobalJdbcType.TYPE.equalsIgnoreCase(type);
            boolean groovyManaged = GlobalJdbcTypeUtils.isManagedGroovyDataSource(type);
            if (isTemp && !globalDataSource) {
                formData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
                String efwdConnectionJson = efwdJsonForTemp(formData);
                JsonObject efwdJson;

                efwdJson = new Gson().fromJson(efwdConnectionJson, JsonObject.class);
                if (efwdJson.has("data")) {
                    formData.addProperty("id", efwdJson.get("data").getAsJsonObject().get("id").getAsString());
                }
            }

            String formId = formData.get("id").getAsString();
            if (globalDataSource && (isEfwd || isTemp)) {
                connection = getGlobalTempConnection(connection, formData, driverConnection, noSqldataSource, formId);
            }
            if (nonPooled || staticDataSource || dynamicDataSource || noSqldataSource) {
                JsonObject connectionJson = getConnectionJson(formData);
                connection = getConnection(driverConnection, noSqldataSource, formId, connectionJson);
            }
            if (groovyManaged) {
                connection = getGroovyManagedConnection(formData.toString(), connection, driverConnection, noSqldataSource, groovyManaged);
            }
        }

        if (connection == null) {
            throw new ConnectionException("Could not get database connection");
        }

        return driverConnection;
    }

    /**
     * efwdJsonForTemp(JSONObject formData)
     * It returns efwd datasource details.
     *
     * @param formData json formData
     * @return jsonObject in string format
     */
    private String efwdJsonForTemp(JsonObject formData) {
        ObjectNode efwdConnection = EfwdDatasourceUtils.getEfwdConnection(formData.get("id").getAsString(), formData.get("type").getAsString());
        return efwdConnection.toString();
    }

    /**
     * getConnectionJson(JSONObject formData)
     * Extracts the connection data object from formData.
     *
     * @param formData formData
     * @return connection JSON object, if available; otherwise, an empty JSON object.
     */
    private JsonObject getConnectionJson(JsonObject formData) {
        JsonObject connectionJson = new JsonObject();
        if (formData.has("connectionJson")) {
            connectionJson = formData.getAsJsonObject("connectionJson");
        }
        return connectionJson;
    }

    /**
     * getConnection(String type, String jsonInfo)
     * Gets a database connection for the Apache drill using type and JSON information.
     *
     * @param type     data source type
     * @param jsonInfo formData
     * @return DriverConnection object for the database connection.
     */
    @Nullable
    @Override
    public DriverConnection getConnection(String type, String jsonInfo) {
        Connection connection = null;

        JsonObject formData = new Gson().fromJson(jsonInfo, JsonObject.class);
        if (formData.has("isTemp")) {
            return getConnectionFromTemp(type, jsonInfo);
        }

        DriverConnection driverConnection = new DriverConnection();
        boolean isEfwd = GsonUtility.optStringValue(formData, "dir", null) != null;

        if (!GlobalJdbcTypeUtils.isTypeGlobal(type)) {

            JsonObject efwdJson;

            if (formData.get("id").getAsInt() == -1) {
                TestConnectionProvider.minusJson(null, formData, JsonUtils.getHiMiddleWareName());
                efwdJson = formData;
            } else {
                String efwdConnectionJson = efwdJson(formData);
                efwdJson = new Gson().fromJson(efwdConnectionJson, JsonObject.class);
                String driverName = GsonUtility.optString(efwdJson, "driverName");
                if (!StringUtils.isBlank(driverName) && driverName.startsWith(JsonUtils.getHiMiddleWareName())) {
                    String json = TestConnectionProvider.minusJson(null, efwdJson, driverName);
                    efwdJson = new Gson().fromJson(json, JsonObject.class);
                    JsonObject data = efwdJson.getAsJsonObject("data");
                    data.addProperty("driverName", "org.apache.drill.jdbc.Driver");
                    data.addProperty("jdbcUrl", GsonUtility.optString(efwdJson, "jdbcUrl"));
                    data.addProperty("userName", GsonUtility.optString(efwdJson, "userName"));
                    data.addProperty("password", GsonUtility.optString(efwdJson, "password"));
                }
            }

            JsonObject data = efwdJson.has("data") ? efwdJson.getAsJsonObject("data") : efwdJson;

            String jdbcUrl = data.get("jdbcUrl").getAsString();
            String userName = GsonUtility.optString(data, "userName");
            String password = GsonUtility.optString(data, "password");
            String driverName = data.get("driverName").getAsString();

            driverConnection.setDriverClass(driverName);

            SqlJdbcHandler sqlJdbcHandler = new SqlJdbcHandler();

            EfwdConnection efwdConnection = sqlJdbcHandler.getEfwdConnection(jdbcUrl, userName, password, driverName);

            if (logger.isDebugEnabled()) {
                logger.debug("EFWD details are" + efwdConnection);
            }

            connection = efwdConnection.getConnection();
            driverConnection.setConnection(connection);
        } else {
            boolean nonPooled = GlobalJdbcType.NON_POOLED.equalsIgnoreCase(type);
            boolean staticDataSource = GlobalJdbcType.STATIC_DATASOURCE.equalsIgnoreCase(type);
            boolean dynamicDataSource = GlobalJdbcType.DYNAMIC_DATASOURCE.equalsIgnoreCase(type);
            boolean noSqldataSource = GlobalJdbcType.NOSQL_DATASOURCE.equalsIgnoreCase(type);
            boolean globalDataSource = GlobalJdbcType.TYPE.equalsIgnoreCase(type);
            boolean groovyManaged = GlobalJdbcTypeUtils.isManagedGroovyDataSource(type);
            String formId = formData.get("id").getAsString();
            if (globalDataSource && isEfwd) {
                connection = getGlobalConnection(connection, formData, driverConnection, noSqldataSource, formId);
            }
            if (nonPooled || staticDataSource || dynamicDataSource || noSqldataSource) {
                JsonObject connectionJson = getConnectionJson(formData);
                connection = getConnection(driverConnection, noSqldataSource, formId, connectionJson);
            }
            if (groovyManaged) {
                connection = getGroovyManagedConnection(jsonInfo, connection, driverConnection, noSqldataSource, groovyManaged);
            }
        }

        if (connection == null) {
            throw new ConnectionException("Could not get database connection");
        }
        // driverConnection.setConnection(connection);
        return driverConnection;
    }

    /**
     * getGroovyManagedConnection(String jsonInfo, Connection connection, DriverConnection driverConnection,
     * boolean noSqldataSource, boolean groovyManaged)
     * it gets the details of groovy managed connection
     *
     * @param jsonInfo         formData
     * @param connection       jdbc driver connection
     * @param driverConnection to set connection
     * @param noSqldataSource  true or false
     * @param groovyManaged    true or false
     * @return connection object
     */
    private Connection getGroovyManagedConnection(String jsonInfo, Connection connection, DriverConnection driverConnection, boolean noSqldataSource, boolean groovyManaged) {
        JsonObject formJson = new Gson().fromJson(jsonInfo, JsonObject.class);
        String dir = null, id = null;
        if (groovyManaged && formJson.has("dir")) {
            dir = formJson.get("dir").getAsString();
            id = formJson.get("id").getAsString();

        } else {
            if (groovyManaged && formJson.has("metadataFileJson")) {
                JsonObject metadataFileJson = formJson.getAsJsonObject("metadataFileJson");
                JsonObject connectionDetails = metadataFileJson.getAsJsonObject("connectionDetails");
                dir = connectionDetails.get("directory").getAsString();
                id = connectionDetails.get("connectionId").getAsString();
            }
        }
        connection = dir != null && id != null ? getGroovyManagedConnection(driverConnection, formJson, dir, id) : connection;
        return connection;
    }

    /**
     * getGroovyManagedConnection(DriverConnection driverConnection, JSONObject formJson, String dir, String id)
     *
     * @param driverConnection to set connection details
     * @param formJson         formData
     * @param dir              dir
     * @param id               to find efwd connection details
     * @return Connection object
     */
    private Connection getGroovyManagedConnection(DriverConnection driverConnection, JsonObject formJson, String dir, String id) {
        EfwdDataSourceHandler handler = new GroovyManagedJdbcHandler();
        ObjectNode conNode = handler.readDS(id, GlobalJdbcType.MANAGED_GROOVY_DATASOURCE);
        JsonObject con = new Gson().fromJson(conNode.toString(), JsonObject.class);
        return getConnection(driverConnection, false, con.get("id").getAsString(), con);
    }

    /**
     * getGlobalConnection(Connection connection, JSONObject formData,
     * DriverConnection driverConnection, boolean noSqldtaSource, String formId)
     *
     * @param connection       database connection
     * @param formData         form data
     * @param driverConnection DriverConnection object
     * @param noSqldtaSource   indicating whether it's a NoSQL data source or not
     * @param formId           ID
     * @return connection object with global settings
     */
    private Connection getGlobalConnection(Connection connection, JsonObject formData, DriverConnection driverConnection, boolean noSqldtaSource, String formId) {
        String globalId;
        JsonObject efwdJson = new JsonObject();
        QueryExecutor executor = new QueryExecutor(null, applicationProperties);
        String efwdFile = null;
        if (formData.has("efwd"))
            efwdJson = formData.getAsJsonObject("efwd");
        if (efwdJson.has("file"))
            efwdFile = efwdJson.get("file").getAsString();
        JsonObject connectionJson = getConnectionJson(formData);
        if (efwdFile != null) {
            efwdJson = executor.newReadEFWD(formData.get("dir").getAsString(), efwdFile,
                    applicationProperties.getSolutionDirectory());
            for (Object eachJson : efwdJson.getAsJsonArray("DataSources")) {
                JsonObject eachJSON = (JsonObject) eachJson;
                if (eachJSON.get("@id").getAsString().equals(formId)) {
                    globalId = eachJSON.get("globalId").getAsString();
                    connection = getConnection(driverConnection, noSqldtaSource, globalId, connectionJson);
                }
            }
        } else {
            JsonArray connections = efwdJson.getAsJsonObject("dataSources").getAsJsonArray("connections");
            for (Object eachJson : connections) {
                JsonObject eachJSON = (JsonObject) eachJson;
                eachJSON = eachJSON.getAsJsonObject("connection");
                if (eachJSON.get("id").getAsString().equals(formId)) {
                    globalId = eachJSON.getAsJsonObject("connDetails").get("globalId").getAsString();
                    connection = getConnection(driverConnection, noSqldtaSource, globalId, connectionJson);
                }
            }
        }
        return connection;
    }

    /**
     * getGlobalTempConnection(Connection connection, JSONObject formData, DriverConnection driverConnection, boolean noSqldtaSource, String formId)
     *
     * @param connection       database connection
     * @param formData         form data
     * @param driverConnection DriverConnection object
     * @param noSqldtaSource   indicating whether it's a NoSQL data source or not
     * @param formId           ID
     * @return connection object with global settings applied for a temporary data source.
     */
    private Connection getGlobalTempConnection(Connection connection, JsonObject formData, DriverConnection driverConnection, boolean noSqldtaSource, String formId) {
        String globalId;
        EnhancedQueryExecutor executor = new EnhancedQueryExecutor(null, applicationProperties);
        String efwdFile = null;
        if (formData.has("efwd")) {
            efwdFile = formData.getAsJsonObject("efwd").get("file").getAsString();
        }
        String dir = GsonUtility.optStringValue(formData, "dir", null);
        if (dir == null)
            dir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
        JsonObject efwdJson = executor.newReadTempEFWD(dir, efwdFile);
        JsonObject connectionJson = getConnectionJson(formData);
        JsonObject dataSources = efwdJson.getAsJsonObject("DataSources");
        JsonObject eachJSON = dataSources.getAsJsonObject("Connection");
        if (eachJSON.get("id").getAsString().equals(formId)) {
            globalId = eachJSON.get("globalId").getAsString();
            connection = getConnection(driverConnection, noSqldtaSource, globalId, connectionJson);
        }

        return connection;
    }

    /**
     * getConnection(DriverConnection driverConnection, boolean noSqldtaSource, String formId, JSONObject connectionJson)
     * Retrieves a database connection based on the provided parameters and connection JSON.
     *
     * @param driverConnection DriverConnection object to configure.
     * @param noSqlDataSource  boolean indicating whether it's a NoSQL data source.
     * @param formId           ID of the form.
     * @param connectionJson   JSON object containing connection details.
     * @return database connection with the specified configuration.
     * @throws IllegalArgumentException if the formId is not a valid integer.
     */
    private Connection getConnection(DriverConnection driverConnection, boolean noSqldtaSource, String formId, JsonObject connectionJson) {
        Connection connection;
        int id;
        try {
            id = Integer.parseInt(formId);

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The parameter id should be an integer.");
        }
        String json = connectionJson.toString();
        if (noSqldtaSource) {
            json = getZeroDataSource();
        }
        if (connectionJson.entrySet().isEmpty()) {
            json = DataSourceUtils.globalIdJson(id);
            connectionJson = new Gson().fromJson(json, JsonObject.class);
        }
        if (connectionJson.has("driverClassName") && connectionJson.get("driverClassName").getAsString().startsWith(JsonUtils.getHiMiddleWareName())) {
            json = getMinus1DataSource();
        }


        driverConnection.setDriverClass(JsonUtils.extractDriverName
                (json));

        IJdbcConnectionService connectionService = ApplicationContextAccessor.getBean(IJdbcConnectionService
                .class);

        connection = connectionService.getDatabaseConnection(json);
        driverConnection.setConnection(connection);
        return connection;
    }

    /**
     * efwdJson(@NotNull JSONObject formData)
     *
     * @param formData formData
     * @return returns efwd connection details
     */
    private String efwdJson(@NotNull JsonObject formData) {
        EfwdReader efwdReader = new EfwdReader();
        return efwdReader.evaluateEFWDCondition(formData.toString());
    }

    /**
     * getZeroDataSource()
     * Retrieves the data source configuration for Apache hive driver
     *
     * @return The data source configuration JSON as a string.
     */
    private String getZeroDataSource() {
        String username = HISparkContext.getUsername();
        String password = HISparkContext.getPassword();
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        String datasourceUrl = HISparkContext.getDatasourceUrl();
        String zeroDatasource = GsonUtility.optString(settingsJson, "dsTemplate")
                .replaceAll("_username_", username)
                .replaceAll("_password_", password)
                .replaceAll("_url_", datasourceUrl)
                .replaceAll("_tomcatid_", "0")
                .replaceAll("_id_", "0")
                .replaceAll("_driverClass_", "org.apache.hive.jdbc.HiveDriver");

        return zeroDatasource;


    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}