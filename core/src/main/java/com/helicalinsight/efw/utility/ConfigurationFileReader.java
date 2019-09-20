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

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Rajasekhar
 */
public class ConfigurationFileReader {


    public static Map<String, String> mapFromClasspathPropertiesFile(String classPathPropertiesFile) {
        InputStream inputStream = ConfigurationFileReader.class.getClassLoader().getResourceAsStream
                (classPathPropertiesFile);
        return getPropertiesMap(inputStream);
    }


    private static Map<String, String> getPropertiesMap(InputStream inputStream) {
        Map<String, String> propertiesMap = new HashMap<>();
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            Set<Object> keySet = properties.keySet();
            for (Object aKeySet : keySet) {
                String key = (String) aKeySet;
                String value = properties.getProperty(key);
                propertiesMap.put(key, value);
            }
        } catch (FileNotFoundException ex) {
            throw new ConfigurationException("Property file not found in the class path", ex);
        } catch (IOException ex) {
            throw new RuntimeIOException("Property file could not be read", ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignore) {
            }
        }
        return propertiesMap;
    }

    public static Map<String, String> getMapFromPropertiesFile(File configurationFile) {
        try {
            InputStream inputStream = new FileInputStream(configurationFile);
            return getPropertiesMap(inputStream);
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(configurationFile + " is not present in the directory", e);
        }
    }

// --Commented out by Inspection START (02-05-2015 12:14):
//    public static String getPropertyValue(String classPathPropertyFileName, String property) {
//        if (property == null || classPathPropertyFileName == null) {
//            return null;
//        }
//        return getMapFromClasspathPropertiesFile(classPathPropertyFileName).get(property);
//    }
// --Commented out by Inspection STOP (02-05-2015 12:14)

// --Commented out by Inspection START (10/8/2015 1:20 PM):
//    public static Properties getPropertiesFromClasspathFile(String classPathPropertiesFileName) {
//        InputStream inputStream = ConfigurationFileReader.class.getClassLoader().getResourceAsStream
//                (classPathPropertiesFileName);
//        return getProperties(inputStream);
//    }
// --Commented out by Inspection STOP (10/8/2015 1:20 PM)

    public static Properties getPropertiesFromFile(File configurationFile) {
        try {
            InputStream inputStream = new FileInputStream(configurationFile);
            return getProperties(inputStream);
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(configurationFile + " is not present in the directory", e);
        }
    }

    private static Properties getProperties(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            return properties;
        } catch (FileNotFoundException ex) {
            throw new ConfigurationException("Property file not found in the class path", ex);
        } catch (IOException e) {
            throw new RuntimeIOException("Property file could not be read", e);
        }
    }
}