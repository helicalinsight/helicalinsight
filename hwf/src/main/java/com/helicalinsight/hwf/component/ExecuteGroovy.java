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

package com.helicalinsight.hwf.component;

import com.helicalinsight.hwf.core.IJobProcess;
import com.helicalinsight.hwf.core.api.ExecutionStatus;
import com.helicalinsight.hwf.util.ComponentUtils;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen 
 */
public class ExecuteGroovy implements IJobProcess {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteGroovy.class);
    private int executionState = 0;

    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject jobProcess) {
        logger.info("ExecuteGroovy jobProcess");

        String functionName = input.getString("functionName");
        String code = jobProcess.getString("code");
        JSONObject message = new JSONObject();
        executionState = ExecutionStatus.SUCCESS;
        return executeGroovy(code, functionName, input, message);
    }

    public JSONObject executeGroovy(String groovyCodeAsString, String methodName, JSONObject requestJsonObject,
                                    JSONObject errorMessage) {
        // Create GroovyClassLoader
        final GroovyClassLoader classLoader = new GroovyClassLoader();

        // Load string as Groovy script class.
        Class groovy = classLoader.parseClass(groovyCodeAsString);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (Exception ex) {
            logger.error("An exception occurred while executing custom groovy code. Skipping validation.", ex);
            return null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The input being sent to the groovy code is " + requestJsonObject);
        }

        JSONObject output = (JSONObject) groovyObj.invokeMethod(methodName, new Object[]{requestJsonObject,
                errorMessage});
        return output;
    }

    @Override
    public JSONObject executionStatus() {
        return ComponentUtils.setExecutionStatus(executionState);
    }
}
