package com.helicalinsight.efw.serviceframework;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.components.AutoTriggerCatalogSchema;
import com.helicalinsight.efw.components.ExecutorUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Somen
 * Created by helical021 on 3/27/2019.
 */
@Component
@Scope("prototype")
public class AutoTriggerCheckImpl implements PreExecution {
    private static final Logger logger = LoggerFactory.getLogger(AutoTriggerCheckImpl.class);
    @Autowired
    private AutoTriggerCatalogSchema autoTriggerCatalogSchema;


    @Autowired
    private DatabaseCacheService databaseCacheService;

    @Override
    public void preProcess(JsonObject formData) {
        String id = formData.has("id")?formData.get("id").getAsString():null;
        String file = formData.has("file")?formData.get("file").getAsString():null;
        String dir = formData.has("dir")?formData.get("dir").getAsString():null;

        if (formData.has("refresh") && formData.has("parameters") && formData.get("parameters").getAsJsonObject().has("fetchCatalogs")) {

                    DataSourceMapping dataSourceMapping = new DataSourceMapping();
            dataSourceMapping.setConnectionId(Integer.valueOf(id));
            dataSourceMapping.setDir(dir);
            dataSourceMapping.setFile(file);
            databaseCacheService.deleteAllCacheWithId(dataSourceMapping);
        }
        if (formData.has("parameters") && formData.getAsJsonObject("parameters").has("fetchCatalogs") && !formData.has("autoTriggerMode")) {
            JsonObject connectionJson = DataSourceUtils.getConnectionJson(formData);
            JsonObject autoTriggerFormData = new JsonObject();
            autoTriggerFormData.addProperty("id", id);
            autoTriggerFormData.addProperty("type", formData.get("type").getAsString());
            autoTriggerFormData.addProperty("database", GsonUtility.optString(connectionJson, "databaseName"));
            autoTriggerFormData.addProperty("skipNext", "true");
            autoTriggerFormData.addProperty("requestId", GsonUtility.optString(formData,"requestId"));
            if (formData.has("dir"))
                autoTriggerFormData.addProperty("dir", formData.get("dir").getAsString());
            autoTriggerCatalogSchema.setData(autoTriggerFormData);
            if (GsonUtility.optBooleanValue(formData, "prepareCache", false)) {
                ExecutorUtils.addTask(autoTriggerCatalogSchema);
            }

        }
        long then = System.currentTimeMillis();
    }
}
