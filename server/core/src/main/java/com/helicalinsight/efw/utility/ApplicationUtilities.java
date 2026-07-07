package com.helicalinsight.efw.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.admin.model.ApplicationCache;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.resourcesecurity.IResource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Consists of a set of Utility methods used throughout the code base
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.0
 */
public class ApplicationUtilities {
    private static String IP_ADDRESS = null;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUtilities.class);

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();
    public static List<String> lockPathForFileBrowser = new ArrayList<>();

    public static String schedulerPath() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> hashMap = propertiesFileReader.read("project.properties");
        return hashMap.get("schedulerPath");
    }

    public static void removeLockPathForFileBrowser(String resource) {
        if (JsonUtils.isFileBrowserCacheEnabled())
            lockPathForFileBrowser.remove(resource);
    }

    public static void addLockPathForFileBrowser(String resource) {
        if (JsonUtils.isFileBrowserCacheEnabled())
            lockPathForFileBrowser.add(resource);
    }

    /**
     * <p>
     * This method creates net.sf.Json.JSONArray using List of Maps
     * </p>
     *
     * @param listOfMaps a list of map objects
     * @return a <code>String</code> which is a JSONArray.
     */
    public static JsonArray getJSONArray(List<Map<String, String>> listOfMaps) {
        JsonArray jsonArray = new JsonArray();
        for (Map<String, String> map : listOfMaps) {
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                try {
                    jsonObject.addProperty(key, new Gson().toJson(value));
                } catch (JsonSyntaxException e) {
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

    public static void compressObject(Object actualObject, ApplicationCache cacheObj) {
        try {
            //  byte[] serialize = SerializationUtils.serialize(actualObject);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
            objectOut.writeObject(actualObject.toString());
            objectOut.close();
            byte[] bytes = baos.toByteArray();
            cacheObj.setValue(bytes);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            //ignore the exception
        }
    }

    public static Object unCompressObject(ApplicationCache applicationCache) {
        try {

            ByteArrayInputStream bais = new ByteArrayInputStream(applicationCache.getValue());
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            Object o = objectIn.readObject();
            //SerializationUtils.deserialize();
            objectIn.close();
            return o;
        } catch (IOException | ClassNotFoundException cfe) {
            cfe.printStackTrace();
        }
        return null;
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
       /* logger.error("path  :" + path);
        logger.error("filePath :" + filePath);
        logger.error("settingPath :" + settingsPath);*/
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

    public static String removeClass(@NotNull Class<?> aClass) {
        String removeClass = StringUtils.removeStart(aClass.toString(), "class ");
        String interfaceClass = StringUtils.removeStart(removeClass, "interface ");
        return StringUtils.removeStart(interfaceClass, "enum ");
    }

    @NotNull
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

    @NotNull
    public static File getTempEfwdFile(String directory) {
        File directoryOfEfwdFile = new File(directory);
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

    @NotNull
    public static File getEfwdFile(String directory, String file) {
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        String actualPath = solutionDirectory + File.separator + directory + File.separator + file;
        File directoryOfEfwdFile = new File(actualPath);
        if (directoryOfEfwdFile.exists()) {
            return directoryOfEfwdFile;
        } else {
            throw new EfwServiceException("There is no efwd file in the directory " + directory);
        }
    }

    public static File getEfwdFileFromTemp(String directory, String file) {
        String actualPath = directory + File.separator + file;
        File directoryOfEfwdFile = new File(actualPath);
        if (directoryOfEfwdFile.exists()) {
            return directoryOfEfwdFile;
        } else {
            throw new EfwServiceException("There is no efwd file in the directory " + directory);
        }
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

    public static String getIpAddress() {
    	JSONObject noSql = JsonUtils.getNoSqlFile();
    	String masterUrl = noSql.getString("masterUrl");
    	if("auto".equalsIgnoreCase(masterUrl)) {
	        if (IP_ADDRESS == null) {
	            try (final DatagramSocket socket = new DatagramSocket()) {
	                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
	
	                IP_ADDRESS = socket.getLocalAddress().getHostAddress();
	            } catch (Exception ignore) {            /*return hostAddress;*/
	
	            }
	        }
    	}
    	else {
    		IP_ADDRESS=masterUrl;
    	}
        return IP_ADDRESS;

    }

    public static boolean isPortAvailable(String port) {
        Integer portInInteger = Integer.valueOf(port);
        String host=getIpAddress();
        return isPortAvailable(portInInteger,host);
    }
    public static boolean isPortAvailable(String port, String host) {
        Integer portInInteger = Integer.valueOf(port);
        return isPortAvailable(portInInteger,host);
    }

    public static boolean isPortAvailable(int port, String host) {
        Socket socket = null;
        try {

            socket = new Socket(host, port);
            return false;
        } catch (IOException ioException) {
            logger.info("There is no daemon service  running on  port number: " + port);
            return true;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException exception) {
                    logger.error("Exception occurred while closing the socket {}", exception);
                }
            }
        }
    }

    public static Map<String, String> getSecurityRulesMap() {
        PropertiesFileReader reportPropertiesReader = new PropertiesFileReader();
        Map<String, String> map = reportPropertiesReader.read("Admin", "security.properties");
        if (map == null) {
            throw new ConfigurationException("The properties file defaults" + "" +
                    ".properties is not found in the Admin directory.");
        }
        return map;
    }
    
    
    public static String getContentType(String extension) {
   	 return switch (extension.toLowerCase()) {
        case "svg"   -> "image/svg+xml";
        case "jpg", "jpeg" -> "image/jpeg";
        case "png"   -> "image/png";
        case "gif"   -> "image/gif";
        case "bmp"  -> "image/bmp";
        case "webp" -> "image/webp";
        case "ico"  -> "image/x-icon";
        case "tiff", "tif" -> "image/tiff";
        default      -> "application/octet-stream";
    };
   }
}