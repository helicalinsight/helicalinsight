package com.helicalinsight.datasource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ConnectionProviderFactory
 * This class provides connection details for the respective type .
 *
 * Created by user on 11/26/2015.
 * @author Rajasekhar
 */
public class ConnectionProviderFactory {
    private static  final int MAX_ENTRIES = 1000;
    private static  Map<String,Boolean> localRegistry = (Map<String,Boolean>) Collections.synchronizedMap(new LinkedHashMap(MAX_ENTRIES+1, .75F, true) {
        // This method is called just after a new entry has been added
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    });
	  
//    /**
//	 * getConnection
//	 * @deprecated
//	 * This method is no longer acceptable 
//	 * <p> Use {@link ConnectionProviderFactory#getConnection(JsonObject formJson,String type)} instead.</p>
//	 * @param JsonObject formJson
//	 * @param String type
//	 * @return Object 
//	 */
//	@Deprecated
//    @Nullable
//    public static Object getConnection(@NotNull JSONObject formJson, String type) {
//        String uniqueId = formJson.optString("requestId");
//
//        Object o = localRegistry.get(uniqueId);
//
//        String access = formJson.optString("access");
//        if (access != null && access.length() == 0) {
//
//            formJson.put("access", DataSourceSecurityUtility.READ);
//        }
//        if (o == null) {
//            DataSourceSecurityUtility.isDataSourceAuthenticated(formJson);
//            localRegistry.put(uniqueId, Boolean.TRUE);
//        }
//        //Todo put it inside factory
//        String id = formJson.optString("id");
//        if (formJson.has("cache") || "0".equalsIgnoreCase(id)) {
//            return new SparkConnectionFactory().getConnection(null, null);
//        }
//        IConnectionFactory iConnectionFactory = connectionFactory(formJson);
//        return iConnectionFactory.getConnection(type, formJson.toString());
//
//    }

	/**
	 * getConnection using gson
	 * getConnection(@NotNull JsonObject formJson, String type)
	 * @param JsonObject formJson        formData   
	 * @param String type				connection type
	 * @returns Object of DriverConnection for Apache Calcite if type and jsonObject not null, otherwise
	 * {@returns DriverConnection object of Spark Connection}
	 * 
	 */
	@Nullable
    public static Object getConnection(@NotNull JsonObject formJson, String type) {
        String uniqueId = GsonUtility.optString(formJson,"requestId");

        Object o = localRegistry.get(uniqueId);

        String access = GsonUtility.optString(formJson,"access");
        if (access != null && access.length() == 0) {

            formJson.addProperty("access", DataSourceSecurityUtility.READ);
        }
        if (o == null) {
            DataSourceSecurityUtility.isDataSourceAuthenticated(formJson);
            localRegistry.put(uniqueId, Boolean.TRUE);
        }
        //Todo put it inside factory
        String id = GsonUtility.optString(formJson,"id");
        if (formJson.has("cache") || "0".equalsIgnoreCase(id)) {
            return new SparkConnectionFactory().getConnection(null, null);
        }
        IConnectionFactory iConnectionFactory = connectionFactory(formJson);
        return iConnectionFactory.getConnection(type, formJson.toString());

    }
	
	
    /**
     * getConnectionFromTemp using gson
     * getConnectionFromTemp(@NotNull JsonObject formJson, String type)
     * This method provides connection details
     * @param JsonObject formJson       formData 
     * @param String type               connection type
     * @return Object of DriverConnection
     */
    @Nullable
    public static Object getConnectionFromTemp(@NotNull JsonObject formJson, String type) {
        String access = GsonUtility.optString(formJson,"access");
        if (access != null && access.length() == 0) {

            formJson.addProperty("access", DataSourceSecurityUtility.READ);
        }
        JSONObject jsonObject = JSONObject.fromObject(formJson.toString());

      //  DataSourceSecurityUtility.isDataSourceAuthenticatedFromTemp(jsonObject);
        //Todo put it inside factory
        String id = GsonUtility.optString(formJson,"id");
        if (formJson.has("cache") || "0".equalsIgnoreCase(id)) {
            return new SparkConnectionFactory().getConnection(null, null);
        }
        IConnectionFactory iConnectionFactory = connectionFactory(formJson);
        return iConnectionFactory.getConnection(type, formJson.toString());
    }
   
    /**
     * connectionFactory using gson
     * connectionFactory(@NotNull JsonObject formData)
     * @param JsonObject formData    formData
     * @return instance of IConnectionFactory
     */
    @NotNull
    private static IConnectionFactory connectionFactory(@NotNull JsonObject formData) {
        if (!formData.has("connectionProvider")) {
            throw new ImproperMethodCallException("Parameter connectionProvider is missing in the Json.");
        }

        String connectionProvider = formData.get("connectionProvider").getAsString();
        IConnectionFactory iConnectionFactory = FactoryMethodWrapper.getTypedInstance(connectionProvider,
                IConnectionFactory.class);

        if (iConnectionFactory == null) {
            throw new ConfigurationException("The application configuration(setting.xml) is incorrect.");
        }

        return iConnectionFactory;
    }
    /**
     * ImproperMethodCallException 
     * This exception is thrown when connection provider details cannot be retrieved.
     */
    private static class ImproperMethodCallException extends RuntimeException {
        public ImproperMethodCallException(String message) {
            super(message);
        }
    }
}
