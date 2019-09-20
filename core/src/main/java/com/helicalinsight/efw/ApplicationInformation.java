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

package com.helicalinsight.efw;

import com.helicalinsight.efw.utility.PropertiesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The product information controller service utilizes the data provided from
 * this particular object
 *
 * @author Rajasekhar
 */
public class ApplicationInformation {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInformation.class);
    /**
     * The singleton instance of ApplicationInformation
     */
    private volatile static ApplicationInformation applicationInformation;
    /**
     * The product type of the application
     */
    private final String productType;
    /**
     * Varying integer with two levels major, minor
     */
    private final String version;
    /**
     * Helical Insight
     */
    private final String productName;
    /**
     * Usually related to the svn revision number along with the date in indian
     * format
     */
    private final String build;

    /**
     * Singleton class. So a private constructor. Reads the properties file
     * message.properties to get the build and version details
     */
    private ApplicationInformation() {
        super();
        this.productName = "Helical Insight CE";
        this.productType = "Business Intelligence Framework";
        PropertiesFileReader propertyFileReader = new PropertiesFileReader();
        Map<String, String> messagesMap = propertyFileReader.read("message.properties");
        this.version = messagesMap.get("version");
        this.build = messagesMap.get("build");
    }

    /**
     * Returns an instance of the same class
     *
     * @return An instance of the same class
     */
    public static ApplicationInformation getInstance() {
        if (applicationInformation == null) {
            synchronized (ApplicationInformation.class) {
                if (applicationInformation == null) {
                    applicationInformation = new ApplicationInformation();
                }
            }
        }
        logger.debug("Returning ApplicationInformation Singleton");
        return applicationInformation;
    }

    /**
     * Simple getter for productType
     *
     * @return The productType
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Simple getter for version
     *
     * @return The version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Simple getter for productName
     *
     * @return productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Simple getter for build
     *
     * @return The build information
     */
    public String getBuild() {
        return build;
    }
}