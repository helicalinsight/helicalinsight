package com.helicalinsight.admin.management;

import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

/**
 * Created by Author on 13/05/2015
 *
 * @author Somen
 */
public class ResourceSizeInformationHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject responseJson;
        responseJson = new JSONObject();
        IResourceManager resourceManager = ResourceManager.getInstance();
        responseJson.accumulate("size", resourceManager.getSize());
        return responseJson.toString();
    }
}