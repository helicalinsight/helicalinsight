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

import com.helicalinsight.efw.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class can be used to read a property file. The file can be in the
 * solution directory or in the web application class-path
 *
 * @author Rajasekhar
 */
public class PropertiesFileReader {
    private final static Logger logger = LoggerFactory.getLogger(PropertiesFileReader.class);


    /**
     * The singleton class of the application
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Assigns the applicationProperties with the singleton reference
     */
    public PropertiesFileReader() {
        this.applicationProperties = ApplicationProperties.getInstance();
    }

    /**
     * This method is used to read a property file with in the EFW solution
     * directory.
     *
     * @param directory A directory with in EFW/System directory
     * @param fileName  Name of the properties file
     * @return A <code>Map<String, String></code> with properties as keys
     */
    public Map<String, String> read(String directory, String fileName) {
        String path = applicationProperties.getSystemDirectory();
        String propertiesFile = path + File.separator + directory + File.separator + fileName;

        logger.debug("The location of properties file: " + propertiesFile);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertiesFile);
            return getPropertiesMap(inputStream);
        } catch (FileNotFoundException e) {
            logger.error(fileName + " is not present", e);
            //handle error
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignore) {
            }
        }
        return null;
    }

    /**
     * Returns a map of Map<String, String> of the properties file
     *
     * @param inputStream The Input Stream of the properties file
     * @return Returns a map of <code>Map<String, String></code> of the
     * properties file
     */
    private Map<String, String> getPropertiesMap(InputStream inputStream) {
        Map<String, String> propertiesMap = new HashMap<>();
        Properties properties = new Properties();
        try {
            if (inputStream != null) {
                properties.load(inputStream);
                Set<Object> keySet = properties.keySet();

                for (Object aKeySet : keySet) {
                    String key = (String) aKeySet;
                    String value = properties.getProperty(key);
                    propertiesMap.put(key, value);
                }
            } else {
                logger.error("InputStream is null!");
            }
        } catch (FileNotFoundException ex) {
            logger.error("properties file is not present", ex);
        } catch (IOException e) {
            logger.error("IOException occurred", e);
            //handle error
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignore) {
                logger.debug("Exception ", ignore);
            }
        }

        Assert.notNull(propertiesMap, "Property file map is null!!");
        return propertiesMap;
    }

    /**
     * This method is used to read a property file with in the web application
     * class-path
     *
     * @param propertiesFile propertyFile in the class path
     * @return A <code>Map<String, String></code> with properties as keys
     */

    public Map<String, String> read(String propertiesFile) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFile);
        return getPropertiesMap(inputStream);
    }
}
