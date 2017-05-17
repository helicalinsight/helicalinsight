/**
 *    Copyright (C) 2013-2017 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
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

import com.helicalinsight.efw.ApplicationProperties;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JdbcUrlFormatUtility {

    public JSONObject getJsonOfDrivers() {
        Map<String, String> propertiesMap;
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();

        String driverPath = ApplicationProperties.getInstance().getDriverPath();
        List<String> availableDrivers = DriverJarsReader.getAvailableJdbcDrivers(driverPath);

        propertiesMap = propertiesFileReader.read("Admin", "databaseDrivers.properties");
        JSONObject finalJsonObject;
        finalJsonObject = new JSONObject();

        for (String driver : availableDrivers) {
            if (propertiesMap.containsKey(driver)) {
                JSONObject eachDriverInfo = new JSONObject();
                String urlAndPortString = propertiesMap.get(driver);
                String[] urlAndPortArray = urlAndPortString.split(",");

                int arrayLength = urlAndPortArray.length;

                if (arrayLength >= 1) {
                    String defaultUrl = urlAndPortArray[0];
                    eachDriverInfo.accumulate("url", defaultUrl);
                } else {
                    throw new RuntimeException("The properties file databaseDrivers.properties in is not configured "
                            + "properly.");
                }

                eachDriverInfo.accumulate("driver", driver);

                JSONObject parameters = new JSONObject();
                if (arrayLength >= 2) {
                    parameters.accumulate("port", urlAndPortArray[1]);
                }
                parameters.accumulate("hostName", "localhost");
                parameters.accumulate("database", "database");

                eachDriverInfo.accumulate("parameters", parameters);

                finalJsonObject.accumulate("drivers", eachDriverInfo);
            } else {
                JSONObject notConfiguredDriver = new JSONObject();
                notConfiguredDriver.accumulate("driver", driver);
                finalJsonObject.accumulate("drivers", notConfiguredDriver);
            }
        }
        return finalJsonObject;
    }
}
