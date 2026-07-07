package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonObject;

/**
 * @author Somen
 *         Created on 8/28/2016.
 */
@SuppressWarnings("UnusedDeclaration")
@Deprecated
public class GroovyUsersSession extends com.helicalinsight.efw.utility.GroovyUsersSession {

    //This is common file to synchronize the community and enterprise version.
    // In future please use the core version
    public static boolean evaluateCondition(String condition) {
        return com.helicalinsight.efw.utility.GroovyUsersSession.evaluateCondition(condition);
    }

    public static String getValue(String expression) {
        return com.helicalinsight.efw.utility.GroovyUsersSession.getValue(expression);
    }

    public static JsonObject validateCondition(String expression) {
        return com.helicalinsight.efw.utility.GroovyUsersSession.validateCondition(expression);
    }
}