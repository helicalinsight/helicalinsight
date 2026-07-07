package com.helicalinsight.efw.utility;

import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.customauth.DataSourceEncrypt;
import com.helicalinsight.efw.ApplicationProperties;

import net.sf.json.JSONObject;

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
 * @since 1.1
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
            Map<String, String> map=getPropertiesMap(inputStream);
            if(map.containsKey("password") && !map.get("password").equals(""))
            	map.put("password", DataSourceEncrypt.decrypt(map.get("password")));
            return map;
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
    
    
    
    public Map<String,String> read(String dir, String fileName, String prefix)  {
    	 String path = applicationProperties.getSystemDirectory();
    	 Map<String, String> propertiesMap = new HashMap<>();
         Properties properties = new Properties();
         String propertiesFile = path + File.separator + dir + File.separator + fileName;
         try(InputStream inputStream = new FileInputStream(propertiesFile)) {
        	    properties.load(inputStream);
                Set<Object> keySet = properties.keySet();
                for (Object aKeySet : keySet) {
                    String key = (String) aKeySet;
                    if ( key.startsWith(prefix)) {
                    	String value = properties.getProperty(key);
                    	propertiesMap.put(key, value);
                    }
                }
         } catch ( IOException  e ) {
             logger.error(fileName + " is not present", e);
         } 
         return propertiesMap;
    }
}
