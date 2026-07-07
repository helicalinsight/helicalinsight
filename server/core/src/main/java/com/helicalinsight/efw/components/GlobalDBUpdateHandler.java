package com.helicalinsight.efw.components;
import com.helicalinsight.efw.components.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GlobalDsGlobalIdFinder;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.Connections;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.cache.ConnectionCacheUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 06-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GlobalDBUpdateHandler extends GlobalXmlUpdateHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalDBUpdateHandler.class);

    @NotNull
    public String marshalDS(String dataSourceProvider, String id, @NotNull JsonObject formData,
                            String mode) {
        Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
        if (!dsTypeStorageDatabase) {
            String marshal = super.marshal(dataSourceProvider, id, formData, mode);
            return marshal;
        }

        GlobalDsGlobalIdFinder globalDsGlobalIdFinder = ApplicationContextAccessor.getBean(GlobalDsGlobalIdFinder.class);
        String dataSourceJson = globalDsGlobalIdFinder.getDataSourceJson(Integer.valueOf(id));
        JSONObject aDataSource = JSONObject.fromObject(dataSourceJson);
        DataSourceSecurityUtility.validateGlobalDS(id, aDataSource, mode);
        boolean autoTriggerStatus = false;

        DataSourceMapping dataSourceMapping = new DataSourceMapping();
        dataSourceMapping.setConnectionId(Integer.valueOf(id));
        ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);

        if (!mode.equalsIgnoreCase("share")) {
             dataSourceMapping = new DataSourceMapping();
            dataSourceMapping.setConnectionId(Integer.valueOf(id));
            if (!ConnectionCacheUtils.detectCacheByServiceIdMap(dataSourceMapping)) {
                autoTriggerStatus = true;
            } else {
                autoTriggerStatus = isAnyChangesDetectedInXmlFile(id, formData);
            }
        }
        int theId = Integer.valueOf(id);
        GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
        GlobalConnections globalConnectionById = globalConnectionService.findGlobalConnectionById(theId);

        DsOperation dsOperation = (DsOperation) ApplicationContextAccessor.getBean(dataSourceProvider + "DsManager");
        String message = "";
        if (null != globalConnectionById) {
            message = dsOperation.writeDataSource(formData, mode);
        }

        JsonObject requiredData = new Gson().fromJson(message,JsonObject.class);
        JsonObject dataObj = DataSourceUtils.provideData(formData, "" + theId);
        requiredData.add("data", dataObj);

        if (autoTriggerStatus) {
            autoTriggerCache(formData, theId, requiredData);

        }
        if ("jndi".equalsIgnoreCase(dataSourceProvider)) {
            dataObj.remove("requestId");
            dataObj.remove("database");
            requiredData.add("data", dataObj);
        }

        return requiredData.toString();
    }

    private static void autoTriggerCache(JsonObject formData, int theId, JsonObject requiredData) {
        if (GsonUtility.optBooleanValue(formData, "prepareCache", true)) {
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
        String userName = "";
        String name = "";
        if (formData.has("userName")) {
            userName = formData.get("userName").getAsString();
        }
        if (formData.has("username")) {
            userName = formData.get("username").getAsString();
        }
        if (formData.has("name")) {
            name = formData.get("name").getAsString();
        }

        String globalUserName = "";
        String globalName = "";
        if (globalIdJson.has("userName")) {
            globalUserName = globalIdJson.get("userName").getAsString();
        }
        if (globalIdJson.has("username")) {

            globalUserName = globalIdJson.get("username").getAsString();
        }

        if (globalIdJson.has("name")) {
            globalName = formData.get("name").getAsString();
        }

        String database = GsonUtility.optString(formData, "database");
        String databaseName = GsonUtility.optString(globalIdJson, "databaseName");
        Boolean databaseReady = database != null && databaseName != null && database.equals(databaseName);
        String url = globalIdJson.has("url") ? globalIdJson.get("url").getAsString() : globalIdJson.has("jdbcUrl") ? globalIdJson.get("jdbcUrl").getAsString() : "";

        boolean autoTrigger = false;
        String dataSourceProvider = formData.get("dataSourceProvider").getAsString();
        if (dataSourceProvider.equalsIgnoreCase("jndi")) {
            String lookUpName = GsonUtility.optString(formData,"lookUpName");
            String existingLookupName = GsonUtility.optString(globalIdJson,"lookUpName");
            if (StringUtils.isNotEmpty(existingLookupName) && StringUtils.isNotEmpty(lookUpName)) {
                autoTrigger = !(existingLookupName.equalsIgnoreCase(lookUpName) && name.equalsIgnoreCase(globalName));
            }
        } else {
            autoTrigger = !(userName.equals(globalUserName)
                    && formData.get("password").getAsString().equals(globalIdJson.get("password").getAsString())
                    && formData.get("driverName").getAsString().equals(driverName)
                    && formData.get("jdbcUrl").getAsString().equals(url)
                    && databaseReady && name.equalsIgnoreCase(globalName));
        }
        return true;
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
    public String executeComponent(String formData) {
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
            String message = marshalDS(dataSourceProvider, id, formDataJson, "edit");

            logger.debug("The update status of the global ds is " + message);

            JsonObject messageJson = new Gson().fromJson(message,JsonObject.class);
            String globalId = messageJson.get("dataSourceId").getAsString();
            ExtraOptionsUpsertHandler extraOptionHandler =  ApplicationContextAccessor.getBean(ExtraOptionsUpsertHandler.class);
            extraOptionHandler.upsert(GsonUtility.parseString(formData,JsonObject.class), id);

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