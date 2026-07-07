package com.helicalinsight.admin.management;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.EfwdReaderUtility;
import com.helicalinsight.efw.components.GlobalDSReaderUtility;
import com.helicalinsight.efw.components.GlobalXmlReaderUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Somen
 *         Created  on 17/4/2017.
 */
@SuppressWarnings("unused")
public class DataSourceCountProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        String efwExtension = JsonUtils.getEfwExtension();
        JSONObject responseJson = new JSONObject();
        responseJson.put("dataSourceCount", getDataSourceCount());
        return responseJson.toString();
    }


    private int getDataSourceCount() {
        List dataSources = new ArrayList<>();
        JsonArray extensions = new JsonArray();
        String efwdExtension = JsonUtils.getEfwdExtension();
        extensions.add(efwdExtension);
        EfwdReaderUtility efwdReaderUtility = new EfwdReaderUtility(extensions);
        //dataSources = efwdReaderUtility.getAllEfwdConnections("sql.jdbc");
        Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
        if(!dsTypeStorageDatabase){
            GlobalXmlReaderUtility globalXmlReaderUtility = ApplicationContextAccessor.getBean
                    (GlobalXmlReaderUtility.class);
            globalXmlReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.EXECUTE);
        }else{
            GlobalDSReaderUtility globalDSReaderUtility = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
            globalDSReaderUtility.addDataSources(dataSources, DataSourceSecurityUtility.EXECUTE,null,null);
        }
        List<ObjectNode> plainConnections = new ArrayList<>();
        efwdReaderUtility.addDataSources(plainConnections, "all",DataSourceSecurityUtility.EXECUTE); 
        return dataSources.size() + plainConnections.size();
    }


}
