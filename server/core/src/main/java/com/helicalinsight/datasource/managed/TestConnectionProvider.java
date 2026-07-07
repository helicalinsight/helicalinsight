package com.helicalinsight.datasource.managed;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.DataSourceEncrypt;
import com.helicalinsight.datasource.AbstractFileOperationsOverNetwork;
import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.datasource.DatabaseConnectionFactory;
import com.helicalinsight.datasource.FileOperationOverNetworkFactory;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.DrillConfig;
import com.helicalinsight.datasource.managed.jaxb.StorageLocation;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;

/**
 * Created by author on 09-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class TestConnectionProvider {

    @Autowired
    @Qualifier("plainJdbcConnectionProvider")
    private IConnectionProvider plainJdbcConnections;

    @Autowired
    @Qualifier("dataSourceConnectionProvider")
    private IConnectionProvider dataSourceConnections;

    public boolean testConnection(String formData) {
        String dataSourceProvider;
        try {
            dataSourceProvider = new Gson().fromJson(formData,JsonObject.class).get("dataSourceProvider").getAsString();
        } catch (Exception e) {
            throw new IllegalArgumentException("The required parameter dataSourceProvider is " + "absent in the " +
                    "request.", e);
        }

        Connection connection = getConnection(formData, dataSourceProvider);

        if (connection == null) {
            return false;
        } else {
            DbUtils.closeQuietly(connection);
            return true;
        }
    }
    
    private Connection getConnection(String formData, String dataSourceProvider) {
        Connection connection;

        JsonObject formDataJson = new Gson().fromJson(formData,JsonObject.class);
        String driverName = GsonUtility.optString(formDataJson, "driverName");
        formData = minusJson(formData, formDataJson, driverName);

        if (DataSourceProviders.JNDI.equals(dataSourceProvider)) {
            String lookUpName = formDataJson.get("lookUpName").getAsString();
            if (!lookUpName.startsWith("java:comp/env/")) {
                formDataJson.remove("lookUpName");
                formDataJson.addProperty("lookUpName", "java:comp/env/" + lookUpName);
            }
            connection = dataSourceConnections.newConnection(formDataJson.toString());
        } else {
            connection = plainJdbcConnections.newConnection(formData);
        }
        return connection;
    }
//    /**
//     * minusJson(String formData, JSONObject formDataJson, String driverName)
//     * @deprecated
//     * This method is no longer acceptable 
//	 * <p> Use {@link TestConnectionProvider#minusJson(String formData, JsonObject formDataJson, String driverName)} instead.</p>
//     * @param formData
//     * @param formDataJson
//     * @param driverName
//     * @return
//     */
//    @Deprecated
//    public static String minusJson(String formData, JSONObject formDataJson, String driverName) {
//        if (driverName.startsWith(com.helicalinsight.efw.utility.JsonUtils.getHiMiddleWareName())) {
//
//            String file = formDataJson.optString("jdbcUrl");
//            if(file!=null && !file.isEmpty()){
//                DrillConfig drillConfigFile = JaxbUtils.unMarshal(DrillConfig.class,new File(JsonUtils.getDrillConfigPath()));
//                if(drillConfigFile.getUrlConfig()!=null) {
//                	String pwd=drillConfigFile.getUrlConfig().getPassword();
//                	if(pwd!=null && !pwd.equals(""))
//                		drillConfigFile.getUrlConfig().setPassword(DataSourceEncrypt.decrypt(pwd));
//                }
//                String storageImpl = drillConfigFile.getStorageImpl();
//                StorageLocation value = drillConfigFile.getDrillStorageLocations().getStorageDetails().get(0);
//                file=value.getPath()+"/"+file;
//                AbstractFileOperationsOverNetwork fileOperationHandlerClass = FileOperationOverNetworkFactory.getFileOperationHandlerClass(storageImpl);
//               if(!fileOperationHandlerClass.isFileExists(file)){
//                   throw new OperationFailedException("No Such destination exists :" + file);
//               }
//
//            }
//
//            String formDataMinus = DatabaseConnectionFactory.getMinus1DataSource();
//            JSONObject formDataMinusJson = JSONObject.fromObject(formDataMinus);
//            formDataJson.put("jdbcUrl", formDataMinusJson.getString("url"));
//            formDataJson.put("driverName", formDataMinusJson.getString("driverClassName"));
//            formDataJson.put("userName", formDataMinusJson.getString("username"));
//            formDataJson.put("username", formDataMinusJson.getString("username"));
//            formDataJson.put("password", formDataMinusJson.getString("password"));
//            formData = formDataJson.toString();
//}
//        return formData;
//    }
    /**using gson
     * minusJson(String formData, JSONObject formDataJson, String driverName)
     * @param formData
     * @param formDataJson
     * @param driverName
     * @return
     */
    public static String minusJson(String formData, JsonObject formDataJson, String driverName) {
        if (driverName.startsWith(com.helicalinsight.efw.utility.JsonUtils.getHiMiddleWareName())) {

            String file = GsonUtility.optString(formDataJson,"jdbcUrl");
            if(file!=null && !file.isEmpty()){
                DrillConfig drillConfigFile = JaxbUtils.unMarshal(DrillConfig.class,new File(JsonUtils.getDrillConfigPath()));
                if(drillConfigFile.getUrlConfig()!=null) {
                	String pwd=drillConfigFile.getUrlConfig().getPassword();
                	if(pwd!=null && !pwd.equals(""))
                		drillConfigFile.getUrlConfig().setPassword(DataSourceEncrypt.decrypt(pwd));
                }
                String storageImpl = drillConfigFile.getStorageImpl();
                StorageLocation value = drillConfigFile.getDrillStorageLocations().getStorageDetails().get(0);
                file=value.getPath()+"/"+file;
                AbstractFileOperationsOverNetwork fileOperationHandlerClass = FileOperationOverNetworkFactory.getFileOperationHandlerClass(storageImpl);
               if(!fileOperationHandlerClass.isFileExists(file)){
                   throw new OperationFailedException("No Such destination exists :" + file);
               }

            }

            String formDataMinus = DatabaseConnectionFactory.getMinus1DataSource();
            JsonObject formDataMinusJson = new Gson().fromJson(formDataMinus, JsonObject.class);
            formDataJson.addProperty("jdbcUrl", formDataMinusJson.get("url").getAsString());
            formDataJson.addProperty("driverName", formDataMinusJson.get("driverClassName").getAsString());
            formDataJson.addProperty("userName", formDataMinusJson.get("username").getAsString());
            formDataJson.addProperty("username", formDataMinusJson.get("username").getAsString());
            formDataJson.addProperty("password", formDataMinusJson.get("password").getAsString());
            formData = formDataJson.toString();
}
        return formData;
    }

}
