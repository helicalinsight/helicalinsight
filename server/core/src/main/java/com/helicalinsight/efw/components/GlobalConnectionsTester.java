package com.helicalinsight.efw.components;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.managed.TestConnectionProvider;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by author on 09-02-2015.
 *
 * @author Rajasekhar
 */
public class GlobalConnectionsTester implements IComponent {

    @NotNull
    @Override
    public String executeComponent(String formData) {
        JsonObject formDataJson = new Gson().fromJson(formData,JsonObject.class);
        if (formDataJson.has("id")) {
            formDataJson.addProperty("access",DataSourceSecurityUtility.EXECUTE);
            DataSourceSecurityUtility.isDataSourceAuthenticated(formDataJson);
        }
        String dataSourceProvider = new Gson().fromJson(formData,JsonObject.class).get("dataSourceProvider").getAsString();
        if (!DataSourceProviders.JNDI.equalsIgnoreCase(dataSourceProvider)) {
            if (!formDataJson.has("model")) {
                ControllerUtils.validate(formDataJson);
            }
        }


        //Code to check the connection testing for MongoDb and other NoSql
        if (DataSourceProviders.NOSQL.equalsIgnoreCase(dataSourceProvider)) {
            return DataSourceUtils.testNosqlDS(formDataJson);
        }


        TestConnectionProvider testConnectionProvider = ApplicationContextAccessor.getBean(TestConnectionProvider
                .class);

        return DataSourceUtils.connectionTestResultBuilder(testConnectionProvider.testConnection(formData));
    }





    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
