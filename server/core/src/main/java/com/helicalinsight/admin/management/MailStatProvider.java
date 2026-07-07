package com.helicalinsight.admin.management;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.SendPoolMail;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author somen
 *         Created on 11/08/2020
 */
public class MailStatProvider implements IComponent {


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        SendPoolMail sendPoolMail = ApplicationContextAccessor.getBean(SendPoolMail.class);

        JSONObject responseData;
        responseData = new JSONObject();
        Map<String, String> propertiesMap = sendPoolMail.getPropertiesMap();
        JSONObject props = JSONObject.fromObject(propertiesMap);
        props.discard("password");
        responseData.accumulate("configs", props);
        responseData.accumulate("totalMailSend",sendPoolMail.getMailSend());
        responseData.accumulate("totalMailFailed",sendPoolMail.getMailFailed());
        responseData.accumulate("durationStat",sendPoolMail.getDurationStat());
        return responseData.toString();
    }
}
