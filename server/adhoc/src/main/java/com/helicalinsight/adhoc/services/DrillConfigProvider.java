package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.customauth.DataSourceEncrypt;
import com.helicalinsight.datasource.managed.jaxb.DrillConfig;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import java.io.File;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
public class DrillConfigProvider implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject jsonObject = JsonParser.parseString(jsonFormData).getAsJsonObject();
        return provideDrillConfig(jsonObject);
    }

    private String provideDrillConfig(JsonObject jsonObject) {
        if (!jsonObject.entrySet().isEmpty()) {
            if (jsonObject.has("enabled")) {
                String enabled = jsonObject.get("enabled").getAsString();
                File xml = new File(JsonUtils.getDrillConfigPath());
                DrillConfig drillConfig = JaxbUtils.unMarshal(DrillConfig.class, xml);
                if(drillConfig.getUrlConfig()!=null) {
                	String pwd=drillConfig.getUrlConfig().getPassword();
                	if(pwd!=null && !pwd.equals(""))
                		drillConfig.getUrlConfig().setPassword(DataSourceEncrypt.decrypt(pwd));
                }
                drillConfig.setEnabled(enabled);
                JaxbUtils.marshal(drillConfig, xml);


            }
        }
        return JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath()).toString();
    }

}
