package com.helicalinsight.efw.components;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.DsOperation;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;


import java.util.UUID;

public class GlobalDatabaseConnectionWriter extends GlobalXmlWriter {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String formData) {
        Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
        if (!dsTypeStorageDatabase) {
            return super.executeComponent(formData);
        }

        JsonObject formDataJsonObject = new Gson().fromJson(formData,JsonObject.class);
        if (formDataJsonObject.has("driverName")) {
            String driverName = formDataJsonObject.get("driverName").getAsString();
            formDataJsonObject = JsonUtils.prepareJdbcUrlForMiddleWare(formDataJsonObject, driverName);
            if (formDataJsonObject.has("autoSave")) {
                formDataJsonObject.addProperty("name", UUID.randomUUID().toString().replace("-", ""));
            }
        }

        String dataSourceProvider = formDataJsonObject.get("dataSourceProvider").getAsString();
        if (dataSourceProvider == null) {
            throw new IllegalArgumentException("The dataSourceProvider is null.");
        }


        DsOperation dsOperation = (DsOperation) ApplicationContextAccessor.getBean(dataSourceProvider + "DsManager");
        String message = dsOperation.writeDataSource(formDataJsonObject, "create");
        if (message.isEmpty()) {
            throw new IllegalArgumentException("The dataSourceProvider is unknown. Can not " + "perform write " +
                    "operation.");
        }
        JsonObject messageObject = autoTrigger(formDataJsonObject, message);
        String globalId = messageObject.get("dataSourceId").getAsString();
        ExtraOptionsUpsertHandler extraOptionHandler =  ApplicationContextAccessor.getBean(ExtraOptionsUpsertHandler.class);

        extraOptionHandler.upsert(formDataJsonObject, globalId);

        return messageObject.toString();

    }

    private JsonObject autoTrigger(JsonObject formDataJsonObject, String message) {
        JsonObject messageObject = new Gson().fromJson(message,JsonObject.class);
        messageObject.add("data", DataSourceUtils.provideData(formDataJsonObject, "" + GsonUtility.optInt(messageObject, "dataSourceId")));
        if (GsonUtility.optBooleanValue(formDataJsonObject, "prepareCache",true)) {
            autoTriggerUnit(formDataJsonObject, messageObject);
        }
        return messageObject;
    }

    private void autoTriggerUnit(JsonObject formDataJsonObject, JsonObject messageObject) {
        AutoTriggerCatalogSchema runnable = ApplicationContextAccessor.getBean(AutoTriggerCatalogSchema.class);
        JsonObject data = messageObject.getAsJsonObject("data");
        data.addProperty("database", GsonUtility.optString(formDataJsonObject,"database"));
        data.addProperty("requestId",  GsonUtility.optString(formDataJsonObject,"requestId"));
        data.addProperty("autoTriggerMode", "create-edit");
        runnable.setData(data);
        ExecutorUtils.addTask(runnable);
        data.remove("autoTriggerMode");
    }
}
