package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A component responsible for deleting data sources.
 */
@Component
public class DatasourceDSDeleteComponent extends DatasourceDeleteComponent {

    private static final Logger logger = LoggerFactory.getLogger(DatasourceDSDeleteComponent.class);
    /**
     * Executes the component to delete a data source.
     *
     * @param formData 		 form data containing parameters such as id, type .
     * @return A message indicating the result of the deletion operation.
     */
    @NotNull
    @Override
    public String executeComponent(String formData) {
        JsonObject formDataJson = new Gson().fromJson(formData,JsonObject.class);
        if(!JsonUtils.isDSTypeStorageDatabase()){
            String s = super.executeComponent(formData);
            return s;
        }
        String dataSourceProvider = GsonUtility.optString(formDataJson,"dataSourceProvider");
        String id = formDataJson.get("id").getAsString();
        String type = formDataJson.get("type").getAsString();
        IComponent managedDsShutdown = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.admin.management.ManagedDataSourceShutdownHandler", IComponent.class);
        JsonArray ids = new JsonArray();
        JsonObject cacheObj = new JsonObject();
        cacheObj.addProperty("id", id);
        cacheObj.addProperty("baseType", "global.jdbc");
        ids.add(cacheObj);
        JsonObject formD = new JsonObject();
        formD.add("ids", ids);
        managedDsShutdown.executeComponent(formD.toString());

        IDataSourceDeleteRule iDataSourceDeleteClass = DatasourceDeleteClassFactory.getIDataSourceDeleteClass(type);
        String message = iDataSourceDeleteClass.deleteDataSource(formDataJson, dataSourceProvider, id);
        return message;
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}