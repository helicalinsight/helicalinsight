package com.helicalinsight.scheduling.utils;

import com.google.gson.*;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.GroovyUtils;
import com.helicalinsight.resourcedb.Deleted;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateReplacer2 {

    public static String replaceEmailComponents(String jsonString, String bodyOrSubject, Boolean last,String baseURL) {
        try {



            baseURL = baseURL.replace("hi.html", "");
            if (baseURL.endsWith("/")) {
                baseURL = baseURL.substring(0, baseURL.length() - 1);
            }

             bodyOrSubject = bodyOrSubject.replace("${baseUrl}", baseURL);
             bodyOrSubject = bodyOrSubject.replace("${baseURL}", baseURL);


            JSONObject jsonObject = JSONObject.fromObject(jsonString);

            HIResourceServiceDB serviceDb = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);

            String reportDir = jsonObject.optString("reportDir");
            String reportFileName = jsonObject.optString("reportNameWithExtension");
            if (StringUtils.isNotBlank(reportDir) && StringUtils.isNotBlank(reportFileName)) {
                HIResource resource = serviceDb.getResourceByUrl(reportDir + "/" + reportFileName, Deleted.FALSE);
                String title = resource.getTitle();
                jsonObject.put("reportPath", resource.getResourcePath());
                jsonObject.put("reportUrl", resource.getResourceURL());

                bodyOrSubject = bodyOrSubject.replace("${reportTitle}", title);
                bodyOrSubject = bodyOrSubject.replace("${reportName}", title);


                String deepURL = baseURL + "/#/report-viewer?dir=" + reportDir + "&file=" + reportFileName + "&mode=open";
                String deepUrl = baseURL + "/#/report-viewer?dir=" + reportDir + "&file=" + reportFileName + "&mode=open";
                bodyOrSubject = bodyOrSubject.replace("${deepURL}", deepURL);
                bodyOrSubject = bodyOrSubject.replace("${deepUrl}", deepUrl);
            }

            bodyOrSubject = replaceDatePlaceholders(bodyOrSubject);
            bodyOrSubject = replacePlaceholders(bodyOrSubject, jsonObject.toString());
            if (last) {
                UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
               String userId =jsonObject.optString("userIdSchedule");
               if(userId!=null && !userId.isEmpty()) {
                   User userObject = userService.findUser(Integer.parseInt(userId));
                   bodyOrSubject = GroovyUtils.replaceExpressionUser(bodyOrSubject,userObject);
               }else {
                   bodyOrSubject = GroovyUtils.replaceExpression(bodyOrSubject);
               }
            }
            return bodyOrSubject;
        } catch (Exception e) {
            e.printStackTrace();
            return bodyOrSubject;
        }
    }



    public static String replacePlaceholders(String template, String jsonString) {
        // Parse JSON string into a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        // Iterate through the keys in the JSON object and replace placeholders
        for (String key : jsonObject.keySet()) {
            String placeholder = "${" + key + "}";
            JsonElement element = jsonObject.get(key);

            // Handle arrays, objects, and primitives
            String replacement = null;
            if (element.isJsonArray()) {
                // Convert JsonArray to a comma-separated string
                JsonArray array = element.getAsJsonArray();
                replacement = arrayToString(array);
            } else if (element.isJsonPrimitive()) {
                // Use primitive as a string
                replacement = element.getAsString();
            } else if (element.isJsonObject()) {
                // Convert JsonObject to its string representation (or handle as needed)
                replacement = element.toString();
            }

            // Replace the placeholder in the template
            if (replacement != null) {
                template = template.replace(placeholder, replacement);
            }
        }

        return template;
    }


    private static String arrayToString(JsonArray array) {
        StringBuilder result = new StringBuilder();
        for (JsonElement elem : array) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(elem.getAsString());
        }
        return result.toString();
    }




    public static String replaceDatePlaceholders(String template) {
        // Regex to match date placeholders like ${date:yyyy-MM-dd}
        Pattern pattern = Pattern.compile("\\$\\{date(:([^}]+))?}");
        Matcher matcher = pattern.matcher(template);

        StringBuffer result = new StringBuffer();
        Date currentDate = new Date(); // Current date

        while (matcher.find()) {
            String format = matcher.group(2); // Extract format, e.g., "yyyy-MM-dd"
            String replacement;

            if (format != null) {
                // If a format is specified, use it
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                    replacement = dateFormat.format(currentDate);
                } catch (IllegalArgumentException e) {
                    replacement = "INVALID_FORMAT";
                }
            } else {
                SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
                replacement = defaultFormat.format(currentDate);
            }
            matcher.appendReplacement(result, replacement);
        }

        matcher.appendTail(result); // Append the rest of the template
        return result.toString();
    }


}






