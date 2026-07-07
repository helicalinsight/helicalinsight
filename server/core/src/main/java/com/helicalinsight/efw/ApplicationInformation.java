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
 * @since 1.1
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
        this.productName = "Helical Insight";
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
