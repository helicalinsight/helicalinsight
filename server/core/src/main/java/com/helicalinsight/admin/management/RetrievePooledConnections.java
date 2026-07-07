package com.helicalinsight.admin.management;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.datasource.DataSourceProviders;
import com.helicalinsight.datasource.managed.DataSourcePool;
import com.helicalinsight.datasource.managed.HashMapKey;
import com.helicalinsight.datasource.managed.IDataSourcePool;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.cache.ConnectionCacheUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by user on 1/29/2016.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class RetrievePooledConnections implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(RetrievePooledConnections.class);
    DatabaseCacheService dbService = ApplicationContextAccessor.getBean(DatabaseCacheService.class);

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject model = new JSONObject();
        List<String> idList = new ArrayList<>();

        JSONArray array = new JSONArray();
        IDataSourcePool dataSourcePool = DataSourcePool.getInstance();
        Map<HashMapKey, DataSource> pooledReferences = dataSourcePool.getPooledReferences();
        List<DataSourceMapping> allConnectionEntries=dbService.getAllConnectionEntries();
        Map<Integer, List<DataSourceMapping>> connectionStatus = new HashMap<>();
        Map<Integer, List<DataSourceMapping>> connectionStatus2 = new HashMap<>();
        Set<Integer> partiallyCachedConn=new HashSet<>();
        Set<Integer> partiallyCachedConn2=new HashSet<>();
        
        if (allConnectionEntries.size() > 0) {
        	
            GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean
                    (GlobalConnectionService.class);
            
            for (DataSourceMapping dsMapping : allConnectionEntries) {
				Integer connectionId = dsMapping.getConnectionId();
				if (StringUtils.isBlank(dsMapping.getDir())) {
					List<DataSourceMapping> strings = connectionStatus.get(connectionId);
					if (strings == null) {
						strings = new ArrayList<>();
					}
					strings.add(dsMapping);
					connectionStatus.put(connectionId, strings);
					if (dsMapping.getType().equals("partial_column") || dsMapping.getType().equals("partial_joins"))
						partiallyCachedConn.add(connectionId);
				} else {
					List<DataSourceMapping> strings = connectionStatus2.get(connectionId);
					if (strings == null) {
						strings = new ArrayList<>();
					}
					strings.add(dsMapping);
					connectionStatus2.put(connectionId, strings);
					if (dsMapping.getType().equals("partial_column") || dsMapping.getType().equals("partial_joins"))
						partiallyCachedConn2.add(connectionId);
				}
            }

            List<GlobalConnections> globalConns=globalConnectionService.getAllRequiredConnections(connectionStatus.keySet());
            for (GlobalConnections conn:globalConns) {
                JSONObject dataSource = new JSONObject();
                String type = "dynamicDatasource";
                String isCached = "connection";
                try {
                        dataSource.put("baseType", conn.getBaseType());
                        dataSource.put("dataSourceProvider", globalConnectionService.getDataSourceProvider(conn.getGlobalId(),
                        		JsonUtils.getDSTypeClassJson(conn.getDsType()).get("name").getAsString()));
                        idList.add("" + conn.getGlobalId());
                        dataSource.put("id", "" + conn.getGlobalId());
						dataSource.accumulate("name", conn.getName());
						if(partiallyCachedConn.contains(conn.getId()))
							isCached = "partial";
                        dataSource.accumulate("type", type);
                        dataSource.put("isDatabaseMetadataCached", isCached);
                        array.add(dataSource);
                        
                } catch (ConfigurationException ce) {
                    DataSourceMapping dataSourceMapping = new DataSourceMapping();
                    dataSourceMapping.setConnectionId(conn.getGlobalId());
                    ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);
                }
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("The number of data sources with connections pooled in memory are 0");
        }


        Set<Map.Entry<HashMapKey, DataSource>> entries = pooledReferences.entrySet();
        Map<HashMapKey, String> jsonMap = dataSourcePool.getJsonMap();

        for (Map.Entry<HashMapKey, DataSource> entry : entries) {
            HashMapKey key = entry.getKey();
            String json = jsonMap.get(key);

            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            String dataSourceProvider = jsonObject.get("dataSourceProvider").getAsString();

            JsonElement globalId = jsonObject.get("globalId");
            String id = globalId != null ? globalId.getAsString() : null;
            if (StringUtils.isBlank(id)) {
                id = jsonObject.get("id").getAsString();
            }
            if (!DataSourceProviders.HIKARI.equalsIgnoreCase(dataSourceProvider) && !DataSourceProviders.TOMCAT
                    .equalsIgnoreCase(dataSourceProvider)) {
                continue;
            }
            if (!idList.contains(id)) {

                JSONObject dataSource = new JSONObject();
                dataSource.accumulate("id", id);
                dataSource.accumulate("name", jsonObject.get("name").getAsString());
                dataSource.accumulate("type", jsonObject.get("type").getAsString());
                dataSource.accumulate("baseType", jsonObject.get("baseType").getAsString());
                dataSource.accumulate("dataSourceProvider", dataSourceProvider);
                dataSource.put("isDatabaseMetadataCached", "dataSource");
                array.add(dataSource);
            }
        }
        JSONArray efwdCache =  getEfwdConnectionCache(connectionStatus2,partiallyCachedConn2);
        array.addAll(efwdCache);
        model.accumulate("dataSources", array);
       

        return model.toString();
    }
    
    
    private JSONArray getEfwdConnectionCache(Map<Integer, List<DataSourceMapping>> connectionStatus,
    		Set<Integer> partiallyCachedConn) {
    	JSONArray array = new JSONArray();
        List<String> idList = new ArrayList<>();
		if (!connectionStatus.isEmpty()) {

			EFWDConnectionService plainConnectionService = ApplicationContextAccessor 
					.getBean(EFWDConnectionService.class);
			List<HIEfwdConnection> plainConnections = plainConnectionService.findAllRequiredEFWDConnection(connectionStatus.keySet());
            for (HIEfwdConnection forBaseType : plainConnections) {
                JSONObject dataSource = new JSONObject();
				try {
					if (forBaseType != null) {
						idList.add("" + forBaseType.getId());
						List<DataSourceMapping> dataSourceMappings = connectionStatus.get(forBaseType.getId());
						if (dataSourceMappings != null) {
							DataSourceMapping dsMapping = dataSourceMappings.get(0);
							if (StringUtils.isNoneEmpty(dsMapping.getDir())) {
								dataSource.put("id", forBaseType.getId());
								dataSource.put("baseType", forBaseType.getType());
								dataSource.put("dir", dsMapping.getDir());
								dataSource.put("dataSourceProvider", "EFWD");
							} else
								continue;
							dataSource.accumulate("name", dsMapping.getConnectionName());
						}
						dataSource.accumulate("type", forBaseType.getType());
						String isCached = "connection";
						if(partiallyCachedConn.contains(forBaseType.getId()))
								isCached = "partial";
						dataSource.put("isDatabaseMetadataCached", isCached);
						array.add(dataSource);
					}
				} catch (ConfigurationException ce) {
					DataSourceMapping dataSourceMapping = new DataSourceMapping();
					dataSourceMapping.setConnectionId(forBaseType.getId());
					ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);
				}
            }
		}

		return array;
    }
    

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
