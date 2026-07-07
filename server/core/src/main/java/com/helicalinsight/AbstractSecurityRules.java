package com.helicalinsight;

import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Helical on 3/9/2021.
 */
@Deprecated
public abstract class AbstractSecurityRules implements IReportSecurityRules {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSecurityRules.class);

    public static String getAdhocSecurityRulesClass(String securityLevel) throws
            ImproperXMLConfigurationException {
        String resourceSecurityClass="";
        try {
            final JSONObject settings = JsonUtils.getSettingsJson();
            JSONArray rulesArray = settings.getJSONObject("AdhocSecurityRules").getJSONArray("rules");
            for(int i=0;i<rulesArray.size();i++){
                String level=rulesArray.getJSONObject(i).getString("@level");
                if(level.equalsIgnoreCase(securityLevel)){
                    resourceSecurityClass=rulesArray.getJSONObject(i).getString("@class");
                }
            }

        } catch (Exception e) {
            throw new ImproperXMLConfigurationException("Setting.xml configuration is incorrect. " +
                    "" + "Provide AdhocSecurityRule class");
        }
        return resourceSecurityClass;
    }
}
