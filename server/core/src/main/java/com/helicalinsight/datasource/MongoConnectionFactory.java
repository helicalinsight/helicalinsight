package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;

import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;


@SuppressWarnings("unused")
public class MongoConnectionFactory extends DatabaseConnectionFactory {
	
    private final HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
	private EFWDConnectionService efwdService=ApplicationContextAccessor.getBean(EFWDConnectionService.class);
    
    @Override
    public DriverConnection getConnection(String type, String jsonInfo) {
        String driverClassName = null;
        JsonObject formJson = new Gson().fromJson(jsonInfo,JsonObject.class);
        if (formJson.has("isTemp")) {
            return super.getConnectionFromTemp(type, jsonInfo);
        }
		if (!formJson.has("efwd") && formJson.has("dir") && formJson.has("uuid")) {
			HIResource hiResource = serviceDB
					.getResourceByUrl(formJson.get("dir").getAsString() + "/" + formJson.get("uuid").getAsString());
			HIResourceHCR hiResourceHCR = hiResource.getHiResourceHCR();
			List<HIHcrConnections> hcrConnections=efwdService.fetchAllHcrConnectionsByResourceId(hiResource.getResourceId());
			JsonObject efwd = com.helicalinsight.datasource.managed.JsonUtils.prepareEfwJsonByHcr(hcrConnections);
			formJson.add("efwd", efwd.getAsJsonObject("efwd"));
			jsonInfo = formJson.toString();
		}
		if (!type.equalsIgnoreCase(GlobalJdbcType.TYPE)) {
			JsonObject connectionDetails = null;
			if (formJson.has("connectionJson")) {
				connectionDetails = formJson.getAsJsonObject("connectionJson");
			} else {
				connectionDetails = DataSourceUtils.getConnectionJson(formJson);
			}
			if (connectionDetails.has("driverClassName")) {
				driverClassName = connectionDetails.get("driverClassName").getAsString();
			}
			if (connectionDetails.has("Driver")) {
				driverClassName = connectionDetails.get("Driver").getAsString();
			}
			if (connectionDetails.has("driverName")) {
				driverClassName = connectionDetails.get("driverName").getAsString();
			}

			if ("mongodb.jdbc.MongoDriver".equalsIgnoreCase(driverClassName) || "com.helical.mongodb.MongoJdbcDriver".equalsIgnoreCase(driverClassName)) {
				DriverConnection driverConnection = new DriverConnection();
				driverConnection.setConnection(null);
				driverConnection.setDriverClass(driverClassName);
				return driverConnection;

			}
			if (driverClassName != null && driverClassName.startsWith(JsonUtils.getHiMiddleWareName())) {
				formJson.addProperty("id", "-1");
				jsonInfo = formJson.toString();
			}

		}
        return super.getConnection(type, jsonInfo);

    }

    @Override
    public boolean isThreadSafeToCache() {

        return true;
    }

}
