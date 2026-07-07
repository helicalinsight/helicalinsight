package com.helicalinsight.resourcedb.processor;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import com.helicalinsight.efw.resourceloader.rules.RulesUtils;
import com.helicalinsight.resourcedb.processor.model.Security;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Deprecated
public final class ResourceSecurityRuleDB implements IResourceSecurityRule {

    private static final Logger logger = LoggerFactory.getLogger(ResourceSecurityRuleDB.class);



    private ResourceSecurityRuleDB() {
    }


    @NotNull
    public static IResourceSecurityRule getInstance() {
        return ResourceSecurityRuleHolder.INSTANCE;
    }


    @Override
    public boolean validate(JsonObject fileAsJson) {
        return false;
    }

    @Override
    public boolean validateMap(Map<String,Object> resourceMap) {
        try {
            //TODO need to change this hardcoded value
            HIResource hiResource=(HIResource) resourceMap.get("folder");
            Boolean isVisible = (Boolean) resourceMap.get("visible");
            if(hiResource!=null){
                if (!isVisible) {
                    return false;
                }
                boolean containsSecurity = resourceMap.containsKey("security");
                if(containsSecurity){
                    Security security = (Security) resourceMap.get("security");
                    return RulesUtilsDB.validateUser(AuthenticationUtils.getUserDetails(), security);
                }
            }
            else{
                //TODO need to check this
                String security = "[]";
                return RulesUtils.validateUser(AuthenticationUtils.getUserDetails(), security);
            }


        } catch (JSONException ex) {
            logger.error("The resource is malformed. Setting.xml is configured to apply rules. " +
                    "But the resource does not support " + "the rules.", ex);
        }
        return false;
    }



    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }


    private static class ResourceSecurityRuleHolder {
        private static final IResourceSecurityRule INSTANCE = new ResourceSecurityRuleDB();
    }
}
