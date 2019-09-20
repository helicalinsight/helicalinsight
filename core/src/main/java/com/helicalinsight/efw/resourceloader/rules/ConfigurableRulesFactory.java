/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.resourceloader.rules;

import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by author on 15-01-2015.
 *
 * @author Rajasekhar
 */
public final class ConfigurableRulesFactory {

    private final JSONObject fileAsJson;

    private final List<String> configurableRulesList;

    private String mode;

    public ConfigurableRulesFactory(JSONObject fileAsJson, List<String> configurableRulesList, String mode) {
        this.configurableRulesList = configurableRulesList;
        this.fileAsJson = fileAsJson;
        this.mode = mode;
    }

    public boolean apply() throws UnSupportedRuleImplementationException {
        IResourceSecurityRule securityRule;
        boolean stop = false;
        if ("and".equals(this.mode)) {
            stop = true;
        }
        for (String ruleClass : this.configurableRulesList) {
            securityRule = FactoryMethodWrapper.getTypedInstance(ruleClass, IResourceSecurityRule.class);
            boolean result = false;
            if (securityRule != null) {
                result = securityRule.validate(this.fileAsJson);
            }
            if (stop && !result) {
                return false;
            }
        }
        return true;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
