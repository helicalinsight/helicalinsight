package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * A component responsible for handling the deletion of data sources.
 */
@Component
public class DatasourceDeleteComponent implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(DatasourceDeleteComponent.class);
    /**
     * Executes the data source deletion process.
     *
     * @param formData 		  form data provides id, type,dataSource etc..
     * @return A message indicating the outcome of the deletion process.
     */
    @NotNull
    @Override
    public String executeComponent(String formData) {
        JsonObject formDataJson = new Gson().fromJson(formData,JsonObject.class);

        String dataSourceProvider = formDataJson.get("dataSourceProvider").getAsString();
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