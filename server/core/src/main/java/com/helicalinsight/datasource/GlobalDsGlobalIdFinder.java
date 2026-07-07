package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.helicalinsight.datasource.managed.IGlobalXmlReader;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.GlobalDSReaderUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * GlobalDsGlobalIdFinder Implements of the {@link IGlobalXmlReader} interface .
 * This class provides functionality to retrieve a JSON representation of global data source configurations by
 * their unique IDs. It leverages the {@link GlobalDSReaderUtility} to fetch the required data source configuration.
 * @see IGlobalXmlReader
 * @see GlobalDSReaderUtility
 */
@Component
public class GlobalDsGlobalIdFinder  implements IGlobalXmlReader {
	/**
	 * getDataSourceJson(Integer connectionId)
     * returns a JSON representation of a global data source configuration based on its unique identifier (ID).
     *
     * @param connectionId 				ID of the global data source to retrieve.
     * @return A JSON string representing the global data source configuration, or {@code null} if not found.
     */
    @Override
    public String getDataSourceJson(Integer connectionId) {

        GlobalDSReaderUtility bean = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
        Map<String, Object> connectionMap = bean.addDataSourcesId(DataSourceSecurityUtility.EXECUTE, connectionId);
        if(connectionMap!=null) {
            Gson gsonObj = new Gson();
            return gsonObj.toJson(connectionMap);
        }
        return null;

    }
}
