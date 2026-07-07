package com.helicalinsight.admin.management;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.resourcecache.IResourceManager;
import com.helicalinsight.efw.resourcecache.ResourceManager;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Author on 13/05/2015
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class ResourceDeleteHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String resourcePath = formJson.getString("resourcePath");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("resourcePath", resourcePath);
        JSONObject jsonObject = new JSONObject();
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        IResourceManager resourceManager = ResourceManager.getInstance();
        if (resourceManager.deleteResource(resourcePath + ", " + false)) {
            jsonObject.put("message", "Resource deleted successfully.");

        } else {
            jsonObject.put("message", "Resource cannot be deleted. It may not exists");
        }
        return jsonObject.toString();
    }
}