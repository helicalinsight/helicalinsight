package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.RuntimeIOException;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Rajasekhar
 */
public class ConfigurationFileReader {

    @NotNull
    public static Map<String, String> mapFromClasspathPropertiesFile(String classPathPropertiesFile) {
        ClassLoader classLoader = ConfigurationFileReader.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(classPathPropertiesFile);
        return getPropertiesMap(inputStream);
    }

    public static Map<String, String> getProjectPropertiesFile() {
        return mapFromClasspathPropertiesFile("project.properties");
    }

    public static JSONObject getConfigurationAsJSON(String path) {
        ClassLoader classLoader = ConfigurationFileReader.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        String filePath = null;
        if (resource != null) {
            try {
                filePath = new File(resource.toURI()).getAbsolutePath();
            } catch (URISyntaxException e) {
                throw new ConfigurationException("xml file not found in the class path", e);
            }
        }
        return JsonUtils.getXmlAsJson(filePath);
    }


    @NotNull
    private static Map<String, String> getPropertiesMap(@NotNull InputStream inputStream) {
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