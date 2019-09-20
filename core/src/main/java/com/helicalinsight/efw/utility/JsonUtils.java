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
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by author on 09-01-2015.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @author Rajesh
 */
@SuppressWarnings("unused")
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * The extension of efwd key
     *
     * @return efwd extension from setting.xml
     */
    public static String getEfwdExtension() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.getJSONObject("Extentions").getString("efwd");
    }

    /**
     * The extension of efwvf key
     *
     * @return efwvf extension from setting.xml
     */
    public static String getEfwvfExtension() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.getJSONObject("Extentions").getString("efwvf");
    }

    /**
     * The extension of efwce key
     *
     * @return efwce extension from setting.xml
     */
    public static String getEFWCEExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getJSONObject("efwce").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key efwce  is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
    }

    /**
     * The json of System/Admin/setting.xml object
     *
     * @return The json of settings.xml
     */
    public static JSONObject getSettingsJson() {
        final String settingPath = ApplicationProperties.getInstance().getSettingPath();
        logger.info("The path of the file is " + settingPath);
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(settingPath, false);
    }

    public static File defaultFunctionsFile() {
        return new File(ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "defaultFunctions.properties");
    }


    public static String getEfwExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getJSONObject("efw").getString("#text");
        } catch (Exception ex) {
            throw new ConfigurationException("The key efw is not configured in application " + "settings.", ex);
        }
        return extension;
    }

    /**
     * This method is used to get the extension for the file which need to be
     * create
     *
     * @return String
     */
    public static String getEfwResultExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension = null;
        try {
            JSONObject extensionsJSONObject = settingsJson.getJSONObject("Extentions").getJSONObject("efwresult");
            if (extensionsJSONObject == null) {
                throw new ApplicationException();
            }
            extension = extensionsJSONObject.getString("#text");
        } catch (JSONException ex) {
            logger.error("Please provide attribute visible to be true in the Extensions tag for " + "efwresult", ex);
        } catch (ApplicationException e) {
            logger.error("The object extensionsJSONObject is null.");
        }
        return extension;
    }

    /**
     * The extension of metadata key
     *
     * @return metadata extension from setting.xml
     */
    public static String getMetadataExtension() {
        JSONObject settingsJson = getSettingsJson();
        String metadata;
        try {
            metadata = settingsJson.getJSONObject("Extentions").getJSONObject("metadata").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key metadata is not configured in application " + "settings.", e);
        }
        return metadata;
    }


    /**
     * The json of any resource
     *
     * @return The json of the file usually that is configured to be visible in
     * setting.xml
     */
    public static JSONObject getAsJson(File resource) {
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(resource.toString(), false);
    }

    /**
     * The json of global connections xml file
     *
     * @return The json of the global connections xml file
     */
    public static JSONObject getGlobalConnectionsJson() {
        final String globalConnectionsPath = getGlobalConnectionsPath();
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(globalConnectionsPath, false);
    }

    /**
     * The path of global connections xml file will be returned
     *
     * @return The path of the global connections xml file
     */
    public static String getGlobalConnectionsPath() {
        final String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
        JSONObject settings = getSettingsJson();
        final String name = getImportableXmlName("globalConnections", settings);
        if (name == null) {
            throw new ConfigurationException("The import xml element of type globalConnections " +
                    "is" + " not " + "configured in setting.xml");
        }
        return systemDirectory + File.separator + "Admin" + File.separator + name;
    }

    private static String getImportableXmlName(String importableXml, JSONObject settings) {
        String name = null;
        try {
            JSONArray imports = settings.getJSONArray("import");
            for (Object xml : imports) {
                JSONObject eachXml = (JSONObject) xml;
                if (importableXml.equals(eachXml.getString("@type"))) {
                    name = eachXml.getString("@name");
                    break;
                }
            }
        } catch (Exception ex) {
            throw new ConfigurationException("The import xml element is not configured in " + "setting.xml", ex);
        }
        return name;
    }

    public static String functionsReference(String driverClass) {
        final String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
        String functionsPath = systemDirectory + File.separator + "Admin";

        File file = new File(functionsPath + File.separator + "sqlFunctionsXmlMapping.properties");

        return ConfigurationFileReader.getMapFromPropertiesFile(file).get(driverClass);
    }


    public static String sqlFunctionsLocation() {
        final String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
        return systemDirectory + File.separator + "Admin" + File.separator + "SqlFunctions";
    }

    /**
     * The json of System/Admin/services.xml object
     *
     * @param importXml The importable xml file from setting.xml
     * @return The json of services.xml
     */
    public static JSONObject getJsonOfImportableXml(String importXml) {
        final JSONObject importedXmlJson;
        final JSONObject settings = getSettingsJson();
        final String name = getImportableXmlName(importXml, settings);

        if (name == null) {
            throw new ConfigurationException(String.format("The import xml element of type %s is " +
                    "" + "not " + "configured in setting.xml", importXml));
        }

        String xmlPathInAdmin = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" +
                File.separator + name;
        importedXmlJson = ResourceProcessorFactory.getIProcessor().getJSONObject(xmlPathInAdmin, false);
        return importedXmlJson;
    }

    /**
     * Gives the Json of the imported xml
     *
     * @param name The name of the file in Admin directory
     * @return A json object
     */
    public static JSONObject getImportedXmlJson(String name) {
        String completePath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" +
                File.separator + name;
        if (!new File(completePath).exists()) {
            throw new ConfigurationException("The file " + name + " is configured but missing in " +
                    "" + "Admin directory");
        }
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(completePath, false);
    }

    /**
     * @param jsonArrayObject A json array
     * @return List<String> List of strings
     */
    public static List<String> JSONArrayToArrayList(JSONArray jsonArrayObject) {
        List<String> list = new ArrayList<>();
        for (Object anObject : jsonArrayObject) {
            String obj = (String) anObject;
            list.add(obj);
        }
        return list;
    }

    /**
     * The extension of efwscript key
     *
     * @return efwScript extension from setting.xml
     */
    public static String getScriptExtension() {
        JSONObject settingsJson = getSettingsJson();
        String script;
        try {
            script = settingsJson.getJSONObject("Extentions").getJSONObject("efwScript").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key script is not configured in application " + "settings.", e);
        }
        return script;

    }

    /**
     * <p>
     * Obtains the efwsr file extension from the setting.xml file
     * </p>
     *
     * @return A string that is the key value of efwsr in setting.xml
     */
    public static String getEFWSRExtension() {
        ApplicationProperties properties = ApplicationProperties.getInstance();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject json = processor.getJSONObject(properties.getSettingPath(), false);

        String extension = null;
        try {
            JSONObject extensionsJSONObject = json.getJSONObject("Extentions").getJSONObject("efwsr");
            if (extensionsJSONObject == null) {
                throw new ApplicationException();
            }
            extension = extensionsJSONObject.getString("#text");
        } catch (JSONException ex) {
            logger.error("Please provide attribute visible to be true in the Extensions tag for " + "efwsr", ex);
        } catch (ApplicationException e) {
            logger.error("The object extensionsJSONObject is null.");
        }
        return extension;
    }

    /**
     * Gets favourite file extension from setting.xml
     *
     * @return a <code>String</code> which specifies favourite file extension.
     */
    public static String getEfwfavExtension() {
        JSONObject jsonObject = getSettingsJson();
        try {
            return jsonObject.getJSONObject("Extentions").getJSONObject("efwfav").getString("#text");
        } catch (JSONException ex) {
            logger.error("JSONException", ex);
        }
        return null;
    }

    /**
     * <p>
     * Gets efwFolder key value from setting.xml. If it is not present in
     * setting.xml, it will throw exception else return value of efwFolder node.
     * </p>
     *
     * @return a <code>String</code> which specifies value of efwFolder node in
     * setting.xml
     */
    public static String getFolderFileExtension() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject json = processor.getJSONObject(applicationProperties.getSettingPath(), false);

        String extension = null;
        try {
            JSONObject extensionsJSONObject = json.getJSONObject("Extentions").getJSONObject("folder").getJSONObject
                    ("efwFolder");
            if (extensionsJSONObject == null) {
                throw new ImproperXMLConfigurationException("Setting.xml configuration is " + "incorrect");
            }
            extension = extensionsJSONObject.getString("#text");
        } catch (JSONException ex) {
            logger.error("Exception while retrieving efwFolder key's value", ex);
        } catch (ImproperXMLConfigurationException e) {
            logger.error("ImproperXMLConfigurationException", e);
        }
        return extension;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject httpRequestToFormData(HttpServletRequest httpRequest) {
        JSONObject formData = null;

        String requestFormData = httpRequest.getParameter("formData");
        if (requestFormData != null) {
            if (Base64.isBase64(requestFormData)) {
                Base64 base64 = new Base64();
                requestFormData = new String(base64.decode(requestFormData));
            }
            formData = JSONObject.fromObject(requestFormData);
        }
        if (formData == null) {
            formData = new JSONObject();
        }

        Enumeration<String> parameterNames = httpRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!"formData".equals(paramName)) {
                String[] pramValues = httpRequest.getParameterValues(paramName);
                if (pramValues.length == 1) {
                    formData.accumulate(paramName, pramValues[0]);
                } else {
                    formData.accumulate(paramName, pramValues);
                }

            }
        }
        return formData;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject httpRequestWithServiceAndFormData(HttpServletRequest httpRequest) {
        JSONObject serviceParameterJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        String requestFormData = httpRequest.getParameter("formData");
        if (requestFormData != null) {
            resultJson.accumulate("formData", JSONObject.fromObject(requestFormData));
        }

        Enumeration<String> parameterNames = httpRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!"formData".equals(paramName)) {
                String[] pramValues = httpRequest.getParameterValues(paramName);
                if (pramValues.length == 1) {
                    serviceParameterJson.accumulate(paramName, pramValues[0]);
                } else {
                    serviceParameterJson.accumulate(paramName, pramValues);
                }

            }
        }
        resultJson.accumulate("serviceJson", serviceParameterJson);
        return resultJson;
    }

    public static List<String> getAllVisibleExtension() {

        BaseLoader baseLoader = new BaseLoader(ApplicationProperties.getInstance());
        List<String> extensionList = new ArrayList<>();
        JSONObject settingJson = getSettingsJson();
        JSONObject extensionsJSONObject = settingJson.getJSONObject("Extentions");
        List<String> keyList = getKeys(extensionsJSONObject);
        for (String key : keyList) {
            Object keyObject = extensionsJSONObject.get(key);
            if (keyObject instanceof JSONObject) {
                JSONObject extensionObjectKey = (JSONObject) keyObject;
                try {
                    String visible = extensionObjectKey.getString("@visible");
                    if ("true".equalsIgnoreCase(visible)) {
                        String text = extensionObjectKey.optString("#text");
                        if (!"".equals(text)) {
                            extensionList.add(text);
                        }
                    }
                } catch (Exception ignore) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("The key " + key + " is set not to be visible in the " +
                                "repository.");
                    }
                }

            }
        }
        return extensionList;
    }

    /**
     * Returns the keys of the given json object
     *
     * @param jsonObject An instance of JSONObject
     * @return List of keys
     */
    public static List<String> getKeys(JSONObject jsonObject) {
        @SuppressWarnings("rawtypes") Iterator keys = jsonObject.keys();
        List<String> totalKeys = new ArrayList<>();
        while (keys.hasNext()) {
            totalKeys.add((String) keys.next());
        }
        return totalKeys;
    }

    public static String defaultDriverClassName() {
        JSONObject settingsJson = getSettingsJson();
        String defaultClass = settingsJson.getString("defaultDriverClassName");
        if (defaultClass == null) {
            return "org.postgresql.Driver";
        }
        return defaultClass;
    }

    /**
     * Lists the keys of <code>JSONObject</code>
     */
    public static List<String> listKeys(JSONObject jsonobject) {
        Iterator<?> keys = jsonobject.keys();
        List<String> listOfKeys = new ArrayList<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            listOfKeys.add(key);
        }
        return listOfKeys;
    }

    //encryptedParameters
    public static List<String> getEncryptedParametersList(String parameter) {
        String encryptedParameters = getSettingsJson().optString(parameter);
        String arr[] = encryptedParameters.trim().split("\\s*,\\s*");
        return Arrays.asList(arr);
    }
}