package com.helicalinsight.adhoc.metadata.genericdb;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
 * Provides functionality to check the cache status of database connections.
 * This component can be used to determine whether the metadata for a database connection is cached or not.
 * @author Somen
 */
@SuppressWarnings("unused")
public class ConnectionCacheStatusProvider implements IComponent {
	
	/**
     * Indicates whether this component is thread-safe to cache or not.
     * @return true if thread-safe to cache.
     */
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}
	/**
     * Executes the component logic to check the cache status of database connections.
     *
     * @param jsonFormData 		 JSON data containing the form parameters
     * @return a JSON string containing the cache status information
     */
	@SuppressWarnings("ConstantConditions")
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
		String id = GsonUtility.optString(formJson, "id");
		String dir = GsonUtility.optString(formJson, "dir");
		DatabaseCacheService dbService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);
		JsonObject response = new JsonObject();
		JsonArray dataArray = new JsonArray();
		if (StringUtils.isBlank(id)) {
			List<DataSourceMapping> allConnectionEntries = dbService.getAllConnectionEntries();
			for (DataSourceMapping dsM : allConnectionEntries) {
				JsonObject item = isCached(dbService, dsM);
				dataArray.add(item);
			}
		} else {
			DataSourceMapping dsM = new DataSourceMapping();
			dsM.setDir(dir);
			dsM.setConnectionId(Integer.valueOf(id));
			JsonObject item = isCached(dbService, dsM);
			dataArray.add(item);
		}
		response.add("data", dataArray);
		return response.toString();
	}
	 /**
     * Checks whether the metadata for a database connection is cached or not.
     * and fetch the value form dataSourceMapping and stores in jsonObject
     * @param dbService 		 database cache service tell isCached or not
     * @param dsM			     data source mapping provides id and directory.
     * @return a JSON object containing the cache status information
     */
	private JsonObject isCached(DatabaseCacheService dbService, DataSourceMapping dsM) {
		Boolean isCached = dbService.isAConnectionCached(dsM);
		JsonObject item = new JsonObject();
		item.addProperty("id", dsM.getConnectionId());
		String dir = dsM.getDir();
		if (StringUtils.isNotBlank(dir)) {
			item.addProperty("dir", dir);
		}
		item.addProperty("isDatabaseMetadataCached", isCached);
		return item;
	}

}