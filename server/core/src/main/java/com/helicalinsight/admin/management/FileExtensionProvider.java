package com.helicalinsight.admin.management;

import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author somen
 *         Created on 14/10/2015.
 */
public class FileExtensionProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        List<String> allExtension = JsonUtils.getAllVisibleExtension();
        JSONObject responseData;
        responseData = new JSONObject();
        responseData.accumulate("extensions", JSONArray.fromObject(allExtension));
        return responseData.toString();
    }
}
