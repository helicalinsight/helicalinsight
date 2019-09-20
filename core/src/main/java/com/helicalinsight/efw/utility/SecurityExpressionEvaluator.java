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

package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.Profile;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.ApplicationProperties;
import net.sf.json.JSONObject;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Somen
 *         Created on 9/10/2015.
 */
@Component("securityExprEvaluatar")
public final class SecurityExpressionEvaluator {
    private static final String prefix = "\\$\\{\\s*";
    private static final String suffix = "\\s*\\}";

    private static final Logger logger = LoggerFactory.getLogger(SecurityExpressionEvaluator.class);
    private static final Pattern unusedExpression = Pattern.compile("\\$\\{([^\\}]*)\\}");
    private static StandardEvaluationContext context;

    public static boolean evaluateExpression(String expression) {
        try {
            logger.info("expression " + expression);
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(context, Boolean.class);
        } catch (Exception exception) {
            logger.error("Exception occurred", exception);
            return false;
        }
    }


    public static JSONObject validateCondition(String expression) {
        JSONObject response = new JSONObject();
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            exp.getValue(context, Boolean.class);
            response.put("condition", "Test Success");
            response.put("result", true);
        } catch (Exception exception) {
            response.put("result", false);
            String message = exception.getMessage();
            response.put("errorMessage", message == null ? " Unknown. Please check the logs" : message);
        }
        return response;
    }

    @SuppressWarnings("unused")
    //Method is used by using reflection. In Spring Expression Language(SpEL)
    public static boolean check(String multiValue, String argument) {
        if (multiValue != null && argument != null) {
            multiValue = multiValue.replaceAll("'", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s",
                    "");

        } else {
            return false;
        }
        List<String> multiArrayList = Arrays.asList(multiValue.split(","));
        List<String> argumentList = Arrays.asList(argument.replaceAll("'", "").replaceAll("\\s", "").split(","));
        for (String argumentValue : argumentList) {
            if (multiArrayList.contains(argumentValue)) {
                return true;
            }
        }

        return false;
    }

    public static String replaceExpression(String expression) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Principal currentUser = (Principal) auth.getPrincipal();
        User loggedInUser = currentUser.getLoggedInUser();
        expression = replaceUser(expression, loggedInUser);
        expression = replaceOrganization(expression, loggedInUser);
        expression = replaceProfile(expression, loggedInUser);
        expression = replaceRole(expression);
        expression = expression.replaceAll("check\\(", "#check(");
        logger.info("expression returned is " + expression);
        if (expression.contains("${")) {
            expression = handleUnusedExpression(expression);
        }

        logger.info("expression is " + expression);
        return expression;
    }

    private static String handleUnusedExpression(String expression) {
        Matcher matcher = unusedExpression.matcher(expression);
        StringBuffer stringBuffer = new StringBuffer(expression.length());
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(""));
        }
        matcher.appendTail(stringBuffer);
        expression = stringBuffer.toString();
        return expression;
    }

    private static String replaceRole(String expression) {
        List<String> roles = AuthenticationUtils.getUserRoles();
        List<String> rolesIds = AuthenticationUtils.getUserRolesIds();
        Map<String, String> roleMap = new HashMap<>();

        for (int count = 0; count < rolesIds.size(); count++) {
            roleMap.put("role[" + count + "].id", rolesIds.get(count));
            roleMap.put("role[" + count + "].name", appendQuote(roles.get(count)));
        }
        expression = resolveProfileAndRole(roleMap, expression);
        expression = replaceText(expression, "role", "\\.id", arrayListToCSV(rolesIds, false), false);
        boolean append = roles.size() > 1;
        expression = replaceText(expression, "role", "\\.name", arrayListToCSV(roles, true), append);
        expression = replaceText(expression, "role", "", arrayListToCSV(roles, true), append);

        return expression;
    }

    private static String replaceProfile(String expression, User loggedInUser) {
        List<Profile> profileList = loggedInUser.getProfile();
        List<String> profileName = new ArrayList<>();
        List<String> profileId = new ArrayList<>();
        List<String> profileValue = new ArrayList<>();
        Map<String, String> profileMap = new HashMap<>();
        int count = 0;
        for (Profile profile : profileList) {
            profileMap.put("profile[" + count + "].id", String.valueOf(profile.getId()));
            profileMap.put("profile[" + count + "].name", appendQuote(profile.getProfile_name()));
            profileMap.put("profile['" + profile.getProfile_name() + "']", appendQuote(profile.getProfile_value()
                    .replaceAll(",", appendQuote(","))));
            profileMap.put("profile[" + count + "].value", appendQuote(profile.getProfile_value().replaceAll(",",
                    appendQuote(","))));
            count++;
            profileId.add(String.valueOf(profile.getId()));
            profileName.add(profile.getProfile_name());
            profileValue.add(profile.getProfile_value());
        }
        logger.info(profileMap.toString());
        boolean append = profileName.size() > 1;
        expression = resolveProfileAndRole(profileMap, expression);
        expression = replaceText(expression, "profile", "\\.name", arrayListToCSV(profileName, true), append);
        expression = replaceText(expression, "profile", "\\.id", arrayListToCSV(profileId, false), false);
        expression = replaceText(expression, "profile", "\\.value", arrayListToCSV(profileValue, true), append);

        expression = replaceText(expression, "profile", "", arrayListToCSV(profileName, true), append);

        //expression = replaceText(expression,"profile\\[\\d\\]",arrayListToCSV(profileValue.get()))
        //java.beans.Expression

        return expression;
    }


    private static String resolveProfileAndRole(Map profileMap, String expression) {
        StrSubstitutor sub = new StrSubstitutor(profileMap);
        return sub.replace(expression);
    }

    private static String replaceOrganization(String expression, User loggedInUser) {
            String nullValue = ApplicationProperties.getInstance().getNullValue();
            expression = replaceText(expression, "org", "\\.id", nullValue, false);
            expression = replaceText(expression, "org", "\\.name", nullValue, true);
            expression = replaceText(expression, "org", "", nullValue, true);

        return expression;
    }

    private static String replaceUser(String expression, User loggedInUser) {
        expression = replaceText(expression, "user", "\\.id", String.valueOf(loggedInUser.getId()), false);
        expression = replaceText(expression, "user", "\\.email", loggedInUser.getEmailAddress(), true);
        expression = replaceText(expression, "user", "\\.enabled", ("" + loggedInUser.isEnabled()), true);
        expression = replaceText(expression, "user", "\\.name", loggedInUser.getUsername(), true);
        expression = replaceText(expression, "user", "\\.isExternalUser",
                ("" + loggedInUser.getIsExternallyAuthenticated()), true);
        expression = replaceText(expression, "user", "", loggedInUser.getUsername(), true);
        return expression;
    }

    private static String replaceText(String expression, String context, String subContext, String replaceChar,
                                      boolean append) {
        if (append && !replaceChar.contains(",")) {
            replaceChar = appendQuote(replaceChar);

        }
        return expression.replaceAll(prefix + context + suffix + subContext, replaceChar);
    }

    private static String arrayListToCSV(List<String> arrayList, boolean append) {
        String result = "";
        for (String item : arrayList) {
            item = item.replaceAll(",", appendQuote(","));
            if (append) {
                result += appendQuote(item) + ",";
            } else {
                result += item + ",";
            }
        }

        return "".equals(result) ? "" : result.substring(0, result.length() - 1);

    }


    private static String appendQuote(String expression) {
        return GroovyUtils.singleQuotes(expression);
    }

    @PostConstruct
    public void init() {
        context = new StandardEvaluationContext();
        try {
            context.registerFunction("check", SecurityExpressionEvaluator.class.getDeclaredMethod("check", String
                    .class, String.class));
        } catch (NoSuchMethodException ex) {
            logger.error("Method check does not exists", ex);
        }
    }
}
