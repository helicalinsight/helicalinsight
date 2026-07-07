package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.management.DriverListLoaderUtility;
import com.helicalinsight.efw.ApplicationProperties;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JdbcUrlFormatUtility {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    public static JSONObject getJsonOfDrivers() {
        JSONObject finalJsonObject = new JSONObject();
        JSONObject settingJson = JsonUtils.getSettingsJson();
        List<String> fatList = new ArrayList<>();
        String regexString = getRegexString(settingJson);
        getFatListForRegex(fatList, regexString);
        getExcludeRegexFromFatList(settingJson, fatList);


        Map<String, String> propertiesMap = getDatabaseDriversProperty();


        List<JSONObject> driverInfoList = new ArrayList<JSONObject>();

        for (String driver : fatList) {
            if (propertiesMap.containsKey(driver)) {
                gotDriverMatched(propertiesMap, driverInfoList, driver);

            } else {

                JSONObject notConfiguredDriver = new JSONObject();
                notConfiguredDriver.accumulate("available", "true");

                notConfiguredDriver.accumulate("driver", driver);
                driverInfoList.add(notConfiguredDriver);
            }

        }
        finalJsonObject.accumulate("drivers", driverInfoList);
        return finalJsonObject;
    }

    public static Map<String, String> getDatabaseDriversProperty() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> allProps = propertiesFileReader.read("Admin", "databaseDrivers.properties");
        Map<String, String> filtered =
                allProps.entrySet()
                        .stream()
                        .filter(e -> !e.getKey().startsWith("type"))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));
        return filtered;
    }

    public static void gotDriverMatched(Map<String, String> propertiesMap, List<JSONObject> driverInfoList, String driver) {
        JSONObject eachDriverInfo = new JSONObject();
        String urlAndPortString = propertiesMap.get(driver);
        String[] urlAndPortArray = urlAndPortString.split(",");

        int arrayLength = urlAndPortArray.length;

        if (arrayLength >= 1) {
            String defaultUrl = urlAndPortArray[0];
            eachDriverInfo.accumulate("url", defaultUrl);
        }
        eachDriverInfo.accumulate("driver", driver);

        JSONObject parameters = new JSONObject();
        if (arrayLength >= 2) {
            parameters.accumulate("port", urlAndPortArray[1]);
        }
        if (urlAndPortString.contains("{{hostName}}")) {
            parameters.accumulate("hostName", "localhost");
        }
        if (urlAndPortString.contains("{{database}}")) {
            parameters.accumulate("database", "database");
        }
        eachDriverInfo.accumulate("available", "true");

        eachDriverInfo.accumulate("parameters", parameters);
        driverInfoList.add(eachDriverInfo);
    }

    public static String getRegexString(JSONObject settingJson) {
        if (settingJson.has("driverLoadRegexPatterns")
                && !(settingJson.optString("driverLoadRegexPatterns").isEmpty())) {
            return settingJson.optString("driverLoadRegexPatterns");

        } else {
            return ".*[Dd]{1}river";

        }
    }

    public static void getExcludeRegexFromFatList(JSONObject settingJson, List<String> fatList) {
        if (settingJson.has("exculdeRegexFromLoadedClass")
                && !(settingJson.optString("exculdeRegexFromLoadedClass").isEmpty())) {
            String excludeRegex = settingJson.optString("exculdeRegexFromLoadedClass");
            if (excludeRegex != null && !("[]".equals(excludeRegex) && !excludeRegex.isEmpty())) {
                String array[] = excludeRegex.split(",");
                ListIterator<String> iterator = fatList.listIterator();

                removeFromList(array, iterator);

            }
        }
    }

    public static void removeFromList(String[] array, ListIterator<String> iterator) {
        while (iterator.hasNext()) {
            String clazz = iterator.next();
            for (int i = 0; i < array.length; i++) {
                if (clazz.matches(array[i])) {
                    iterator.remove();
                }
            }
        }
    }

    public static void getFatListForRegex(List<String> fatList, String regex) {
        if (regex != null && !("".equals(regex)))
        	fatList.addAll(DriverListLoaderUtility.getRegexClasses(Arrays.asList(regex.split(","))));
    }

    //This method is used in dataSourceList.groovy file
    public static Map<String, String> getDialectInformation() {
        final String systemDirectory = applicationProperties.getSystemDirectory();
        String functionsPath = systemDirectory + File.separator + "Admin";

        File file = new File(functionsPath + File.separator + "sqlFunctionsXmlMapping.properties");
        return ConfigurationFileReader.getMapFromPropertiesFile(file);

    }

    public static String getEnabledTypesForDrill() {
        JSONObject drillConfigJson = JsonUtils.getXmlAsJson(JsonUtils.getDrillConfigPath());
        JSONObject drillJson=JsonUtils.decryptPasswordFromDrillConfigObj(drillConfigJson);
        String enabledTypes = null;
        if (drillJson.has("enabled") && "true".equalsIgnoreCase(drillJson.getString("enabled"))) {
            enabledTypes = drillJson.optString("enabledTypes");
        }
        return enabledTypes;
    }
}
