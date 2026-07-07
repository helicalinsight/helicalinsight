package com.helicalinsight.efw.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.efw.exceptions.DuplicateDatasourceConnectionException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.parallelprocessor.cache.ConnectionCacheUtils;

/**
 * Created by author on 30-01-2015.
 *
 * @author Rajasekhar
 */
public class EfwdWriter implements IComponent {

    @Override
    public String executeComponent(String formData) {
        ObjectNode formDataJson = null;
        ObjectMapper mapper = new ObjectMapper();
        String type = "";
        String directory = "";
        String driverName = "";

        try {
            formDataJson = mapper.readValue(formData, ObjectNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != formDataJson) {
            type = formDataJson.required("type").asText();
            if(formDataJson.has("driverName"))
            driverName = formDataJson.get("driverName").asText();
            if(formDataJson.has("directory"))
            directory = formDataJson.get("directory").asText();
        }
        EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);
        Integer connectionId = handler.writeDS(formDataJson);

        ObjectNode model = JsonNodeFactory.instance.objectNode();
        try {
            if (null != connectionId) {
                model.put("message", "The data source has been saved successfully.");
                model.put("dataSourceId", connectionId);
            } else {
                model.put("message", "Error Occurred!");
            }
            formDataJson.put("prepareCache", true);
            if(connectionId!=null)
            autoTriggerCache(formDataJson, connectionId, true);
            DataSourceUtils.addDataObj(type, directory, driverName, model, "" + connectionId);
        } catch (DuplicateDatasourceConnectionException e) {
            throw new RuntimeException("The given data source already exists in the directory " + directory, e);
        }
        return model.toString();
    }



    public static void autoTriggerCache(ObjectNode formData, int theId, Boolean shouldTrigger) {
        DataSourceMapping dataSourceMapping = new DataSourceMapping();
        dataSourceMapping.setConnectionId(theId);
        dataSourceMapping.setDir(formData.get("directory").asText());

        if (!shouldTrigger && ConnectionCacheUtils.detectCacheByServiceIdMap(dataSourceMapping)) {
            return;
        }

        ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);
        if (formData.get("prepareCache").asBoolean()) {
            AutoTriggerCatalogSchema runnable = ApplicationContextAccessor.getBean(AutoTriggerCatalogSchema.class);
            ObjectNode data = JsonNodeFactory.instance.objectNode();
            data.put("name", formData.get("name").asText());
            data.put("id", "" + theId);
            data.put("type", formData.get("type").asText());
            data.put("dir", formData.get("directory").asText());
            if(formData.has("database")) {
                data.put("database", formData.get("database").asText());
            }
            data.put("requestId", formData.path("requestId").asText());
            data.put("autoTriggerMode", "create-edit");
            runnable.setData(new Gson().fromJson(data.toString(),JsonObject.class));
            ExecutorUtils.addTask(runnable);
            data.remove("autoTriggerMode");
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
