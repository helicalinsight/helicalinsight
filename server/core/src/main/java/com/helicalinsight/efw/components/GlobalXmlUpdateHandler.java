package com.helicalinsight.efw.components;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.cache.ConnectionCacheUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 06-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GlobalXmlUpdateHandler implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(GlobalXmlUpdateHandler.class);

    @NotNull
    public static synchronized String marshal(String dataSourceProvider, String id, @NotNull JsonObject formData,
                                              String mode) {


        DataSourceSecurityUtility.validateGlobalDataSourceAccessForWriteOperation(id, mode);
        boolean autoTriggerStatus=false;
        if (!mode.equalsIgnoreCase("share") && !dataSourceProvider.equalsIgnoreCase("jndi")) {
            DataSourceMapping dataSourceMapping  = new DataSourceMapping();
            dataSourceMapping.setConnectionId(Integer.valueOf(id));
            if (!ConnectionCacheUtils.detectCacheByServiceIdMap(dataSourceMapping)) {
                autoTriggerStatus = true;
            } else {
                autoTriggerStatus = isAnyChangesDetectedInXmlFile(id, formData);
            }
        }
        String connectionsXmlFile = JsonUtils.getGlobalConnectionsPath();
        int theId = Integer.valueOf(id);
        String edit = "";
        try {
            JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
            JAXBContext jaxbContext = jaxbContexts.getContextForClass(Connections.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File xml = new File(connectionsXmlFile);
            Connections connections = (Connections) unmarshaller.unmarshal(xml);


            if ("none".equalsIgnoreCase(dataSourceProvider)) {
                List<JdbcConnection> list = connections.getJdbcConnection();
                validateConnection(list, dataSourceProvider);
                for (JdbcConnection connection : list) {
                    if (connection.getId() == theId) {
                        edit = JdbcConnectionProperties.writeNonPooledType(connection, theId, formData, mode);
                        break;
                    }
                }

            } else if ("jndi".equalsIgnoreCase(dataSourceProvider)) {
                List<JndiDataSource> list = connections.getJndiDataSource();
                validateConnection(list, dataSourceProvider);
                for (JndiDataSource dataSource : list) {
                    if (dataSource.getId() == theId) {
                        edit = JndiDataSourceProperties.writeStaticDataSourceType(dataSource, theId, formData, mode);
                        break;
                    }
                }
            } else if ("hikari".equalsIgnoreCase(dataSourceProvider)) {
                List<HikariProperties> list = connections.getHikariProperties();
                validateConnection(list, dataSourceProvider);
                for (HikariProperties dataSource : list) {
                    if (dataSource.getId() == theId) {
                        edit = HikariCpDataSourceProperties.writeHikariDataSourceType(dataSource, theId, formData,
                                mode);
                        break;
                    }
                }
            } else if ("tomcat".equalsIgnoreCase(dataSourceProvider)) {
                List<TomcatPoolProperties> list = connections.getTomcatPoolProperties();
                validateConnection(list, dataSourceProvider);
                for (TomcatPoolProperties dataSource : list) {
                    if (dataSource.getId() == theId) {
                        edit = TomcatJdbcDataSourceProperties.writeTomcatJdbcDataSourceType(dataSource, theId,
                                formData, mode);
                        break;
                    }
                }
            } else if ("noSql".equalsIgnoreCase(dataSourceProvider)) {
                List<NoSqlProperties> noSqlList = connections.getNoSqlProperties();
                for (NoSqlProperties dataSource : noSqlList) {
                    if (dataSource.getId() == theId) {
                        edit = NoSqlDataSourceProperties.writeNoSqlDataSource(dataSource, theId,
                                formData, mode);
                        break;
                    }

                }
            }


            if (!edit.isEmpty()) {
                saveChanges(jaxbContext, xml, connections);
            } else {
                throwException(dataSourceProvider);
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        JsonObject requiredData = new Gson().fromJson(edit,JsonObject.class);
        requiredData.add("data", DataSourceUtils.provideData(formData, "" + theId));
        if (autoTriggerStatus)
            autoTriggerCache(formData, theId, requiredData);
        return requiredData.toString();
    }

    private static void autoTriggerCache(JsonObject formData, int theId, JsonObject requiredData) {
        if (GsonUtility.optBooleanValue(formData, "prepareCache",true)) {
            DataSourceMapping dataSourceMapping = new DataSourceMapping();
            dataSourceMapping.setConnectionId(theId);
            ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);
            AutoTriggerCatalogSchema runnable = ApplicationContextAccessor.getBean(AutoTriggerCatalogSchema.class);
            JsonObject data = requiredData.getAsJsonObject("data");
            data.addProperty("database", GsonUtility.optString(formData, "database"));
            data.addProperty("requestId", GsonUtility.optString(formData,"requestId"));
            data.addProperty("autoTriggerMode", "create-edit");
            runnable.setData(data);
            ExecutorUtils.addTask(runnable);
            data.remove("autoTriggerMode");
        }
    }

    private static boolean isAnyChangesDetectedInXmlFile(String id, JsonObject formData) {
        String globalId = DataSourceUtils.globalIdJson(Integer.parseInt(id));
        JsonObject globalIdJson = new Gson().fromJson(globalId,JsonObject.class);
        String driverName = "";
        if (globalIdJson.has("driverClassName")) {
            driverName = globalIdJson.get("driverClassName").getAsString();
        } else if (globalIdJson.has("driverName")) {
            driverName = globalIdJson.get("driverName").getAsString();
        }
        String userName="";
        if(formData.has("userName")) {
            userName  = formData.get("userName").getAsString();
        }
        if(formData.has("username")){
            userName=formData.get("username").getAsString();
        }

        String globalUserName = "";
        if(globalIdJson.has("userName")){
            globalUserName=globalIdJson.get("userName").getAsString();
        }
        if(globalIdJson.has("username")) {

          globalUserName= globalIdJson.get("username").getAsString();
        }
        String database = GsonUtility.optString(formData, "database");
        String databaseName = GsonUtility.optString(globalIdJson, "databaseName");
        Boolean databaseReady=database!=null && databaseName!=null && database.equals(databaseName);
        String url = globalIdJson.has("url")?globalIdJson.get("url").getAsString():globalIdJson.has("jdbcUrl")?globalIdJson.get("jdbcUrl").getAsString():"";
        boolean autoTrigger = !(userName.equals(globalUserName)
                && formData.get("password").getAsString().equals(globalIdJson.get("password").getAsString())
                && formData.get("driverName").getAsString().equals(driverName)
                && formData.get("jdbcUrl").getAsString().equals(url)
                && databaseReady);


        return autoTrigger;

    }


    private static void validateConnection(Object connection, String dataSourceProvider) {
        if (connection == null) {
            throwException(dataSourceProvider + ". The given dataSource does not exists");
        }
    }


    private static void throwException(String dataSourceProvider) {
        throw new EfwServiceException(String.format("Could not update the given data " + "source details of type %s" +
                ".", dataSourceProvider));
    }

    private static void saveChanges(@NotNull JAXBContext jaxbContext, File xml, Connections connections) throws
            JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        String charsetName = ControllerUtils.defaultCharSet();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);
        marshaller.marshal(connections, xml);
    }

    @NotNull
    @Override
    public synchronized String executeComponent(String formData) {
        JsonObject formDataJson = new Gson().fromJson(formData,JsonObject.class);
        Map<String, String> parameters = new HashMap<>();

        String dataSourceProvider = formDataJson.get("dataSourceProvider").getAsString();
        String id = formDataJson.get("id").getAsString();
        String type = formDataJson.get("type").getAsString();

        parameters.put("id", id);
        parameters.put("type", type);
        parameters.put("dataSourceProvider", dataSourceProvider);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        if (!"jndi".equalsIgnoreCase(dataSourceProvider)) {
            ControllerUtils.validate(formDataJson);
        }

        try {
            String driverName = GsonUtility.optString(formDataJson, "driverName");
            formDataJson = JsonUtils.prepareJdbcUrlForMiddleWare(formDataJson, driverName);
            String message = marshal(dataSourceProvider, id, formDataJson, "edit");
            if (logger.isDebugEnabled()) {
                logger.debug("The update status of the global xml is " + message);
            }
            JsonObject messageJson = JsonParser.parseString(message).getAsJsonObject();
            messageJson.addProperty("message", "The data source is updated with the new details successfully.");
            return messageJson.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EfwServiceException("The data source could not be updated with the new details. Cause " +
                    ExceptionUtils.getRootCauseMessage(ex));
        }


    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}