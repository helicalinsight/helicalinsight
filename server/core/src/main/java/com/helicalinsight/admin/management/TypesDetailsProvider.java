package com.helicalinsight.admin.management;

import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Rajesh
 *         Created by helical019 on 4/23/2019.
 */
public class TypesDetailsProvider implements IComponent {
    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        return JsonUtils.getAllTypesFromSetting(formJson);
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
