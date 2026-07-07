
package com.helicalinsight.resourcedb.processor;


import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceloader.rules.IResourceSecurityRule;
import java.util.List;
import java.util.Map;


public final class ConfigurableRulesFactoryDB {

    private Map<String,Object> resourceMap;

    private final List<String> configurableRulesList;

    private String mode;

    public ConfigurableRulesFactoryDB(Map<String,Object> resourceMap, List<String> configurableRulesList, String mode) {
        this.configurableRulesList = configurableRulesList;
        this.resourceMap = resourceMap;
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
                result = securityRule.validateMap(this.resourceMap);
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


