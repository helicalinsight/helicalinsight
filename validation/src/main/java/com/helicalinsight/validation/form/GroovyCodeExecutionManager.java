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

package com.helicalinsight.validation.form;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by user on 2/24/2016.
 *
 * @author Rajasekhar
 */
public class GroovyCodeExecutionManager {

    private static final Logger logger = LoggerFactory.getLogger(GroovyCodeExecutionManager.class);
    private String groovyCodeAsString;
    private String methodName;
    private JSONObject requestJsonObject;
    private JSONObject errorMessage;

    public GroovyCodeExecutionManager(String groovyCodeAsString, String methodName, JSONObject requestJsonObject,
                                      JSONObject errorMessage) {
        this.groovyCodeAsString = groovyCodeAsString;
        this.methodName = methodName;
        this.requestJsonObject = requestJsonObject;
        this.errorMessage = errorMessage;
    }

    public boolean executeGroovy() {
        // Create GroovyClassLoader
        final GroovyClassLoader classLoader = new GroovyClassLoader();

        // Load string as Groovy script class.
        Class groovy = classLoader.parseClass(this.groovyCodeAsString);
        GroovyObject groovyObj;
        try {
            groovyObj = (GroovyObject) groovy.newInstance();
        } catch (Exception ex) {
            logger.error("An exception occurred while executing custom groovy code. Skipping validation.", ex);
            return true;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The input being sent to the groovy code is " + this.requestJsonObject);
        }

        JSONObject output = (JSONObject) groovyObj.invokeMethod(this.methodName, new Object[]{this.requestJsonObject,
                this.errorMessage});
        if (!output.isEmpty()) {
            this.errorMessage.accumulate("result", output);
            return false;
        }
        return true;
    }
}
