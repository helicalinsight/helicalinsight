package com.helicalinsight.admin.management;

import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.managed.DataSourcePool;
import com.helicalinsight.datasource.managed.HashMapKey;
import com.helicalinsight.datasource.managed.IDataSourcePool;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.parallelprocessor.cache.ConnectionCacheUtils;
import com.jcraft.jsch.Logger;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by author on 08-09-2015.
 *
 * @author Rajasekhar
 */
@Slf4j
@SuppressWarnings("unused")
public class ManagedDataSourceShutdownHandler implements IComponent {


	@Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        JSONObject model = new JSONObject();

        JSONArray ids = getJsonArray(formData);

        List<Integer> integerList = new ArrayList<>();


        for (Object object : ids) {

            JSONObject jsonObj = (JSONObject) object;
            Map<String,String> parameters = new HashMap<>();
            String id = jsonObj.optString("id");
            String baseType = jsonObj.optString("baseType");
            parameters.put("id", id);
            parameters.put("baseType", baseType);
            ControllerUtils.checkForNullsAndEmptyParameters(parameters);
			if (GlobalJdbcTypeUtils.checkOtherConnections(baseType)) {
				DataSourceMapping dataSourceMapping = new DataSourceMapping();
				dataSourceMapping.setConnectionId(Integer.valueOf(id));
				String dir = jsonObj.optString("dir");
				if (StringUtils.isBlank(dir)) {
					EFWDConnectionService service = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
					EfwdConnDTO efwdConnection = service.getConnectionById(id);
					if(efwdConnection == null) continue;
					dir = efwdConnection.getResource().getResourceUrl();
				}
				dataSourceMapping.setDir(dir);
				ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);
			} else {

                Integer integer;
                try {
                    integer = Integer.valueOf(id);
                } catch (NumberFormatException ex) {
                    throw new EfwServiceException(ex);
                }
                integerList.add(integer);
            }


        }

        for (Integer integer : integerList) {
            DataSourceMapping dataSourceMapping = new DataSourceMapping();
            dataSourceMapping.setConnectionId(integer);
            ConnectionCacheUtils.deleteConnectionCache(dataSourceMapping);

            synchronized (ManagedDataSourceShutdownHandler.class) {
                IDataSourcePool dataSourcePool = DataSourcePool.getInstance();

                Map<HashMapKey, DataSource> pooledReferences = dataSourcePool.getPooledReferences();
                Set<Map.Entry<HashMapKey, DataSource>> entries = pooledReferences.entrySet();
                Map<HashMapKey, String> jsonMap = dataSourcePool.getJsonMap();
                HashMapKey hashMapKey = null;
                for (Map.Entry<HashMapKey, DataSource> entry : entries) {
                    HashMapKey key = entry.getKey();
                    String json = jsonMap.get(key);
                    JSONObject jsonObject = JSONObject.fromObject(json);

                    String id = jsonObject.getString("id");
                    if (id.equals("" + integer)) {
                        hashMapKey = key;
                        break;
                    }
                }


                if (hashMapKey!=null&&  pooledReferences.get(hashMapKey) != null) {
                    pooledReferences.remove(hashMapKey);


                    jsonMap.remove(hashMapKey);

                    close( pooledReferences.get(hashMapKey));

                }
            }
        }


        model.accumulate("message", "The requested DataSource(s) is/are shutdown successfully. The database cache(if any) entries are also cleared. ");
        return model.toString();
    }

    public static void close(DataSource dataSource) {
        if (dataSource == null) {
            return;
        }
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource source = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
            source.close(true);
        } else if (dataSource instanceof HikariDataSource) {
            HikariDataSource source = (HikariDataSource) dataSource;
            source.close();
        } else {
            throw new IllegalStateException("The DataSource is neither of type hikari nor tomcat.");
        }
    }

    private JSONArray getJsonArray(JSONObject formData) {
        JSONArray ids;
        if (formData.has("ids")) {
            try {
                ids = formData.getJSONArray("ids");
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            throw new IncompleteFormDataException("The datasource ids array is missing in the request.");
        }

        if (ids == null || ids.size() == 0) {
            throw new IllegalArgumentException("Parameter ids is null or empty");
        }
        return ids;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
