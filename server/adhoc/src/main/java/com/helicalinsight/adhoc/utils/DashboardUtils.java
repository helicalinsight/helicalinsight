package com.helicalinsight.adhoc.utils;

import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;

import com.helicalinsight.admin.service.HIResourceServiceDB;
import net.sf.json.JSONArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import com.helicalinsight.admin.model.HIResource;
import org.slf4j.LoggerFactory;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


@SuppressWarnings("unused")
public class DashboardUtils{

    private static final Logger logger = LoggerFactory.getLogger(DashboardUtils.class);

    public static List<String> getAllPathInState(String state){
        return findKeyValue("path",state);
    }
    public static List<Integer> getIdFromDb(List<String> pathList,HIResourceServiceDB serviceDB){
        List<Integer> returnList = new ArrayList<Integer>();
        for(String path : pathList){
            HIResource rsource = serviceDB.getResourceByUrl(path,false);
            if (rsource != null) {
                Integer id = rsource.getResourceId();
                returnList.add(id);
            } else {
                returnList.add(null);
            }

        }
        return returnList;
    }



    public static List<String> findKeyValue(String key, String jsonString) {
        List<String> pathValues = new ArrayList<>();

        String regex = "\""+key+"\"\\s*:\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);
        while (matcher.find()) {
            pathValues.add(matcher.group(1)); // Extract the value of the "path" key
        }

        return pathValues;
    }

    public static List<String> getJsonStringFromLargeJson(String key, String largeJson) {
        // Escape the key for regex
        String escapedKey = Pattern.quote(key);

        // Regex pattern to match all occurrences of the specified key
        String pattern = "\"" + escapedKey + "\"\\s*:\\s*(\\{.*?\\})";

        // Create a Pattern object
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(largeJson);

        // List to hold all JSON strings associated with the key
        List<String> jsonStringList = new ArrayList<>();

        while (matcher.find()) {
            // Extract the JSON object as a string
            String jsonString = matcher.group(1);
            jsonStringList.add(jsonString);
        }

        return jsonStringList;
    }
    public static List<String> getJsonArrayStringFromLargeJson(String key, String largeJson) {
        // Escape the key for regex
        String escapedKey = Pattern.quote(key);

        // Regex pattern to match all occurrences of the specified key
        String pattern = "\"" + escapedKey + "\"\\s*:\\s*(\\[.*?\\])";

        // Create a Pattern object
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(largeJson);

        // List to hold all JSON strings associated with the key
        List<String> jsonStringList = new ArrayList<>();

        while (matcher.find()) {
            // Extract the JSON object as a string
            String jsonString = matcher.group(1);
            jsonStringList.add(jsonString);
        }

        return jsonStringList;
    }

    public static String replacePath(String jsonString, List<String> newPaths) {
        return replaceKeyValues("path",jsonString,newPaths);
    }
    public static String replaceKeyValues(String key,String jsonString, List<String> newPaths) {
        String regex =  "\""+key+"\"\\s*:\\s*\"([^\"]+)\"";


        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);

        int pathIndex = 0;

        while (matcher.find()) {
            String newPath = newPaths.get(pathIndex);

            jsonString=jsonString.replace(matcher.group(1),newPath!=null?newPath:matcher.group(1));
            pathIndex++;
        }

        return jsonString;
    }




}
