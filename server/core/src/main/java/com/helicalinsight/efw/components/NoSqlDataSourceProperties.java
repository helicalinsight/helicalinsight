package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.NoSqlProperties;
import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.utility.NoSqlUtils;

/**
 * @author Somen
 *         Created  on 11/9/2017.
 */
public class NoSqlDataSourceProperties extends TomcatJdbcDataSourceProperties {
    public synchronized static String writeNoSqlDataSource(NoSqlProperties connection, int theId, JsonObject formData, String mode) {
        writeTomcatJdbcDataSourceType(connection, theId, formData, mode);
        formData.addProperty("mode",mode);
        formData.addProperty("theId",theId);
        String driverName=null;
        if(formData.has("driverName")){
            driverName=formData.get("driverName").getAsString();
        }
        if(formData.has("driverClassName")){
            driverName=formData.get("driverClassName").getAsString();
        }
        String subType = getSubType(formData);
        if (subType != null && subType.trim().length() > 0) {
            connection.setSubType(subType);
            NoSQLLoader noSqlImplementation = NoSqlUtils.getNoSqlImplementation(subType);
            noSqlImplementation.loadToMiddleWare(formData);
        }
        if(driverName!=null &&driverName.contains("nosql")) {
            connection.setType(GlobalJdbcType.DYNAMIC_DATASOURCE);
            connection.setHiveReferenceId(-1);
            connection.setDataSourcePoolId("nosql_" + theId);
        }else{
            connection.setType(GlobalJdbcType.NOSQL_DATASOURCE);
            connection.setHiveReferenceId(0);
            connection.setDataSourcePoolId(null);
        }
        if(!mode.equalsIgnoreCase("share")) {
            String collection = GsonUtility.optString(formData, "collection");
            connection.setCollection(collection);
        }
        return returnMessage("A NoSql DataSource of Type " + subType + "created successfully",theId);
    }

    public static String getSubType(JsonObject formData) {
        String subType = GsonUtility.optString(formData, "subType");
        String driverClassName = GsonUtility.optString(formData,"driverName");
        if (driverClassName != null && driverClassName.trim().length() > 0) {
            subType = driverClassName;
        }
        return subType;
    }
}
