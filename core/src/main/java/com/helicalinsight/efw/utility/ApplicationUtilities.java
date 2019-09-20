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
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.resourcesecurity.IResource;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Consists of a set of Utility methods used throughout the code base
 *
 * @author Rajasekhar
 */
public class ApplicationUtilities {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUtilities.class);

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    public static String schedulerPath() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> hashMap = propertiesFileReader.read("project.properties");
        return hashMap.get("schedulerPath");
    }

    /**
     * <p>
     * This method creates net.sf.Json.JSONArray using List of Maps
     * </p>
     *
     * @param listOfMaps a list of map objects
     * @return a <code>String</code> which is a JSONArray.
     */
    public static JSONArray getJSONArray(List<Map<String, String>> listOfMaps) {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, String> map : listOfMaps) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                try {
                    jsonObject.put(key, value);
                } catch (JSONException e) {
                    logger.error("JSONException", e);
                }
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * <p>
     * Checks whether the class in question exists in the class path.
     * </p>
     *
     * @param className a <code>String</code> which specifies class name.
     * @return true if given String is class else return false.
     */
    public static boolean isClass(String className) {
        try {
            FactoryMethodWrapper.getClass(className);
            return true;
        } catch (ClassNotFoundException ex) {
            logger.error("ClassNotFoundException occurred", ex);
            return false;
        }
    }

    /**
     * <p>
     * Creates a <code>File</code> with specified data
     * </p>
     *
     * @param location a <code>File</code> which specifies file location.
     * @param data     a <code>String</code> which specifies data which has to be
     *                 written in file.
     * @return true if data is successfully written into file otherwise false
     */
    public static boolean createAFile(File location, String data) {
        String encoding = getEncoding();
        try {
            FileUtils.createFile(location);
        } catch (IOException ex) {
            logger.debug("IOException", ex);
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(location);
            if (data != null) {
                fileOutputStream.write(data.getBytes(encoding));
            }
            fileOutputStream.flush();
        } catch (IOException ex) {
            logger.error("IOException occurred", ex);
            // In case of any anomaly, return false
            return false;
        } finally {
            closeResource(fileOutputStream);
        }
        return true;
    }

//    /**
//     * <p>
//     * This method is responsible to create xml file using
//     * <code>TransformerFactory</code> if it does not exists. If xml file exists
//     * then it overwrites the existing file.
//     * </p>
//     *
//     * @param file      a <code>File</code> object which specifies file name.
//     * @param xmlSource a <code>String</code> which specifies xml data.
//     * @return true if xml file is completely written otherwise false
//     */
//    private static boolean stringToDom(File file, String xmlSource) {
//        // Parse the given input
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new InputSource(new StringReader(xmlSource)));
//
//            // Write the parsed document to an xml file
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer;
//            transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.ENCODING, ApplicationUtilities.getEncoding
// ());
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
//
//            DOMSource source = new DOMSource(document);
//            FileUtils.createFile(file);
//            StreamResult result = new StreamResult(file.toURI().getPath());
//            logger.debug("The path of the file is " + file.toURI().getPath());
//            transformer.transform(source, result);
//        } catch (TransformerConfigurationException e) {
//            logger.error("TransformerConfigurationException", e);
//            return false;
//        } catch (ParserConfigurationException e) {
//            logger.error("ParserConfigurationException", e);
//            return false;
//        } catch (SAXException e) {
//            logger.error("SAXException", e);
//            return false;
//        } catch (IOException e) {
//            logger.error("IOException", e);
//            return false;
//        } catch (TransformerException e) {
//            logger.error("TransformerException", e);
//            return false;
//        }
//        return true;
//    }

    /**
     * Gets encoding value from message.properties
     *
     * @return a <code>String</code> which specify encoding value from
     * message.properties
     */
    public static String getEncoding() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> propertiesMap = propertiesFileReader.read("message.properties");
        return propertiesMap.get("encoding");
    }

    /**
     * Closes a closeable resource
     *
     * @param resource a {@link Closeable} interface
     */
    public static void closeResource(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException exception) {
                logger.error("IOException " + exception);
            }
        }
    }

    /**
     * Returns true if the sequence consists of pattern
     *
     * @param sequence The url of the reportSource
     * @param regEx    The regular expression
     */

    public static boolean foundPattern(String regEx, String sequence) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(sequence);
        return matcher.find();
    }

    /**
     * A utility method for casting a collection of objects
     *
     * @param clazz      a class which is in string format
     * @param collection The collection of objects of type clazz
     * @param <T>        A type of type T
     * @return Casted list of type T
     */
    public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> collection) {
        List<T> list = new ArrayList<>(collection.size());
        for (Object object : collection) {
            list.add(clazz.cast(object));
        }
        return list;
    }

//    /**
//     * Writes an xml file with the specified topLevelKeyName
//     *
//     * @param xmlFile         a <code>File</code> which specify file name.
//     * @param jsonObject      a <code>JSONObject</code> which specify which has to be
//     *                        written in xml.
//     * @param topLevelKeyName The root key name of the xml
//     * @return true if successfully created
//     */
//    public static boolean writeJsonAsXml(File xmlFile, JSONObject jsonObject,
//                                         String topLevelKeyName) {
//        XMLSerializer serializer = new XMLSerializer();
//        JSON json = JSONSerializer.toJSON(jsonObject);
//        serializer.setTypeHintsCompatibility(true);
//        serializer.setObjectName(topLevelKeyName);
//        serializer.setTypeHintsEnabled(false);
//        String xml = serializer.write(json);
//        return stringToDom(xmlFile, xml);
//    }

    /**
     * <p>
     * Utility method which checks whether an Object is empty or not.
     * </p>
     *
     * @param value a <code>Object</code>
     * @return true if Object is empty else return false.
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String) {
            return ((String) value).trim().length() == 0;
        } else if (value instanceof Object[]) {
            return ((Object[]) value).length == 0;
        } else if (value instanceof Collection<?>) {
            return ((Collection<?>) value).size() == 0;
        } else if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).size() == 0;
        } else {
            return value.toString() == null || value.toString().trim().length() == 0;
        }
    }

    /**
     * Returns the relative location of the resource in the EFW solution
     * directory
     *
     * @param path The absolute path of the resource being passed
     * @return Returns the relative location of the resource in the EFW
     * directory
     */
    public static String getRelativeSolutionPath(String path) {
        String filePath = new File(path).getAbsolutePath();
        String settingsPath = new File(APPLICATION_PROPERTIES.getSolutionDirectory()).getAbsolutePath();
        if (filePath.startsWith(settingsPath) && !filePath.equalsIgnoreCase(settingsPath)) {
            return filePath.substring(settingsPath.length() + 1);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IResource> Class<T> resourceJaxBType(String clazz) {
        Class<T> resourceType;
        Class<?> baseType = IResource.class;
        try {
            resourceType = (Class<T>) FactoryMethodWrapper.getClass(clazz);
            if (!baseType.isAssignableFrom(resourceType)) {
                throw new IllegalArgumentException("Class " + clazz + " is not of type " + baseType);
            }
        } catch (ClassNotFoundException ex) {
            logger.error(clazz + " is not found in the classpath");
            throw new RuntimeException(ex);
        }
        return resourceType;
    }

    public static String removeClass(Class<?> aClass) {
        return StringUtils.removeStart(aClass.toString(), "class ");
    }


    public static File getEfwdFile(String directory) {
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        String actualPath = solutionDirectory + File.separator + directory;
        File directoryOfEfwdFile = new File(actualPath);
        String efwdExtension = JsonUtils.getEfwdExtension();
        File[] list = directoryOfEfwdFile.listFiles();
        if (list != null) {
            for (File file : list) {
                if (file.getName().endsWith(("." + efwdExtension))) {
                    return file;
                }
            }
        }
        throw new EfwServiceException("There is no efwd file in the directory " + directory);
    }

    public static Map<String, String> getDefaultsMap() {
        PropertiesFileReader reportPropertiesReader = new PropertiesFileReader();
        Map<String, String> map = reportPropertiesReader.read("Admin", "defaults.properties");
        if (map == null) {
            throw new ConfigurationException("The properties file defaults" + "" +
                    ".properties is not found in the Admin directory.");
        }
        return map;
    }
}