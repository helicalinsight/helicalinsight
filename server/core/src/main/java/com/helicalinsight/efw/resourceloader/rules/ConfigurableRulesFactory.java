package com.helicalinsight.efw.resourceloader.rules;

import java.util.List;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;

/**
 * Created by author on 15-01-2015.
 *
 * @author Rajasekhar
 */
public final class ConfigurableRulesFactory {

    private final JsonObject fileAsJson;

    private final List<String> configurableRulesList;

    private String mode;

    public ConfigurableRulesFactory(JsonObject fileAsJson, List<String> configurableRulesList, String mode) {
        this.configurableRulesList = configurableRulesList;
        this.fileAsJson = fileAsJson;
        this.mode = mode;
    }

    public boolean apply() throws UnSupportedRuleImplementationException {
        IResourceSecurityRule securityRule;
        boolean stop = false;
        if ("and".equalsIgnoreCase(this.mode)) {
            stop = true;
        }
        boolean result = false;
        for (String ruleClass : this.configurableRulesList) {
            securityRule = FactoryMethodWrapper.getTypedInstance(ruleClass, IResourceSecurityRule.class);
            if (securityRule != null) {
                result = securityRule.validate(this.fileAsJson);
            }
            if (stop && !result) {
                return false;
            }
            if ("or".equalsIgnoreCase(this.mode)) {
                if (result) {
                    return result;
                }
            }
        }
        return result;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
