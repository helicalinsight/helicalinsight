package com.helicalinsight.efw.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

/**
 * Created by user on 2/24/2016.
 *
 * @author Rajasekhar
 */
public class GroovyCodeExecutionManager {

    private static final Logger logger = LoggerFactory.getLogger(GroovyCodeExecutionManager.class);
    private String groovyCodeAsString;
    private String methodName;
    private JsonObject requestJsonObject;
    private JsonObject errorMessage;

    public GroovyCodeExecutionManager(String groovyCodeAsString, String methodName, JsonObject requestJsonObject,
                                      JsonObject errorMessage) {
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

        JsonObject output = (JsonObject) groovyObj.invokeMethod(this.methodName, new Object[]{this.requestJsonObject,
                this.errorMessage});
        if (!output.entrySet().isEmpty()) {
            GsonUtility.accumulate(this.errorMessage,"result", output);
            return false;
        }
        return true;
    }
}
