package com.helicalinsight.efw.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.customauth.DataSourceEncrypt;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.resourceloader.BaseLoader;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by author on 09-01-2015.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class JsonUtils {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static String[] extensionArray = {"png", "jpeg", "gif", "bmp", "jpg", "tiff"};
    public static List<String> supportedImageExtensions = Arrays.asList(extensionArray);
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final Map<String,String> dsTypeMap=new HashMap<>();
    public static final String JSON_EXTENSION = ".json";
    public static final String HTML_EXTENSION = ".html";
    public static final String XML_EXTENSION = ".xml";
    public static final String RTF_EXTENSION = ".rtf";
    public static final String DOCX_EXTENSION = ".docx";
    public static final String ODT_EXTENSION = ".odt";
    public static final String ODS_EXTENSION = ".ods";
    public static final String PPTX_EXTENSION = ".pptx";
    public static final String TXT_EXTENSION = ".txt";
    public static final String XLS_EXTENSION = ".xls";
    public static final String CSV_EXTENSION = ".csv";
    public static final String XLSX_EXTENSION = ".xlsx";
    public static final String PDF_EXTENSION = ".pdf";
    public static final String JPEG_EXTENSION = ".jpeg";
    public static final String JPG_EXTENSION = ".jpg";
    public static final String PNG_EXTENSION = ".png";
    public static final String JS_EXTENSION = ".js";


    /**
     * The extension of efwd key
     *
     * @return efwd extension from setting.xml
     */
    public static String getEfwdExtension() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.getJSONObject("Extentions").getString("efwd");
    }

    public static String getEfwvfExtension() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.getJSONObject("Extentions").getString("efwvf");
    }

    public static String getHcrPrintExtension() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.getJSONObject("Extentions").getJSONObject("hcrPrint").getString("#text");
    }

    public static Boolean isFileBrowserCacheEnabled() {
        JSONObject settingsJson = getSettingsJson();
        boolean flag = false;
        if (settingsJson.has("enableFileBrowserCache"))
            return settingsJson.getBoolean("enableFileBrowserCache");
        else
            return flag;
    }

    public static String getDesignerExtendedExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension = "efwdx";
        try {
            JSONObject efwdxObject = settingsJson.getJSONObject("Extentions").optJSONObject("efwdx");
            if (efwdxObject != null && !efwdxObject.isEmpty()) {
                extension = efwdxObject.getString("#text");
            }
        } catch (Exception e) {
            throw new ConfigurationException("The key  dashboard designer extended is not " +
                    "configured in application " + "properly.", e);
        }
        return extension;
    }

    /**
     * The json of System/Admin/setting.xml object
     *
     * @return The json of settings.xml
     */
    /**
     * getSettingsJson
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JsonUtils#newGetSettingsJson()} instead.</p>
     * @return JsonObject 
     */
	@Deprecated
    public static JSONObject getSettingsJson() {
        final String settingPath = ApplicationProperties.getInstance().getSettingPath();
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(settingPath, false);
    }	
	
	
	public static JSONObject getNoSqlFile() {
		IProcessor processor = ResourceProcessorFactory.getIProcessor();
		String noSqlFilePath = noSqlConfigPath();
		return processor.getJSONObject(noSqlFilePath, false);
	}


	/**
	 * newGetSettingsJson  using gson
	 * @return JsonObject
	 */
    public static JsonObject newGetSettingsJson() {
        final String settingPath = ApplicationProperties.getInstance().getSettingPath();
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(settingPath, false);
    }
    
    public static File defaultFunctionsFile() {
        return new File(ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "defaultFunctions.properties");
    }

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

    public static String getDesignerExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getJSONObject("efwdd").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key efw dashboard designer is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
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

    public static String getHWFExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getString("hwf");
        } catch (Exception e) {
            throw new ConfigurationException("The key hwf  is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
    }

    public static String getHWFDExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getString("hwfd");
        } catch (Exception e) {
            throw new ConfigurationException("The key hwfd is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
    }

    /**
     * The extension of report key
     *
     * @return report extension from setting.xml
     */
    public static String getReportExtension() {
        JSONObject settingsJson = getSettingsJson();
        String report;
        try {
            report = settingsJson.getJSONObject("Extentions").getJSONObject("report").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key report is not configured in application " + "settings.", e);
        }
        return report;
    }
	
	public static String getInstantReportExtension() {
        JSONObject settingsJson = getSettingsJson();
        String report;
        try {
            report = settingsJson.getJSONObject("Extentions").getJSONObject("instant").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key report is not configured in application " + "settings.", e);
        }
        return report;
    }
    public static String getAiModelExtension() {
        JSONObject settingsJson = getSettingsJson();
        String report;
        try {
            report = settingsJson.getJSONObject("Extentions").getJSONObject("model").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key report is not configured in application " + "settings.", e);
        }
        return report;
    }


    /**
     * The extension of report key
     *
     * @return report extension from setting.xml
     */
    public static String getHrReportExtension() {
        JSONObject settingsJson = getSettingsJson();
        String report;
        try {
            report = settingsJson.getJSONObject("Extentions").getJSONObject("hr").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key report is not configured in application " + "settings.", e);
        }
        return report;
    }

    /**
     * The json of any resource
     *
     * @return The json of the file usually that is configured to be visible in
     * setting.xml
     * @deprecated
     * This method is no longer acceptable 
	 * <p> Use {@link JsonUtils#newGetAsJson(JsonObject formData)} instead.</p>
	 * @param resource
     */
    public static JSONObject getAsJson(File resource) {
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(resource.toString(), false);
    }
    /**using gson
     * newGetAsJson(File resource)
     * @param resource
     * @return
     */
    public static JsonObject newGetAsJson(File resource) {
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(resource.toString(), false);
    }
    
    public static JSONObject getAsJson(File resource, boolean flag) {
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(resource.toString(), flag);
    }

    /**
     * ,
     * The json of global connections xml file
     *
     * @return The json of the global connections xml file
     * @deprecated
     * This method is no longer acceptable
     * <p> use {@link JsonUtils#newGetGlobalConnectionsJson()} instead   </p>
     */
    @Deprecated
    public static JSONObject getGlobalConnectionsJson() {
        final String globalConnectionsPath = getGlobalConnectionsPath();
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(globalConnectionsPath, false);
    }

    /**
     * using gson
     * newGetGlobalConnectionsJson()
     * @return
     */
    public static JsonObject newGetGlobalConnectionsJson() {
        final String globalConnectionsPath = getGlobalConnectionsPath();
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(globalConnectionsPath, false);
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
        if (driverClass.startsWith(getHiMiddleWareName())) {
            return "drill";
        }
        final String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
        String functionsPath = systemDirectory + File.separator + "Admin";

        File file = new File(functionsPath + File.separator + "sqlFunctionsXmlMapping.properties");

        return ConfigurationFileReader.getMapFromPropertiesFile(file).get(driverClass);
    }

    public static String getHiMiddleWareName() {
        JSONObject settingsJson = getSettingsJson();
        if (settingsJson.has("hiMiddleWareDriverName")) {
            return settingsJson.optString("hiMiddleWareDriverName");
        } else {
            return "helicalinsight";
        }
    }

    public static String getCacheDirectory() {
        return ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Cache";
    }

    @NotNull
    public static String getHCRPrintLocation() {
        String cacheDirectory = getCacheDirectory();
        String hcrPrintPath = cacheDirectory + File.separator + "Hcr";
        FileUtils.createDirectory(new File(hcrPrintPath));
        return hcrPrintPath;
    }

    @NotNull
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
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(xmlPathInAdmin, false);
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
     * @since 02/04/2015
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
    //TODO need to remove JSONObject
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
    /**
     * newHttpRequestToFormData using gson
     * @param HttpServletRequest httpRequest 
     * @return JsonObject
     * 
     */
    @SuppressWarnings("unchecked")
    public static JsonObject newHttpRequestToFormData(HttpServletRequest httpRequest) {
        JsonObject formData = null;

        String requestFormData = httpRequest.getParameter("formData");
        if (requestFormData != null) {
            if (Base64.isBase64(requestFormData)) {
                Base64 base64 = new Base64();
                requestFormData = new String(base64.decode(requestFormData));
            }
            formData = new Gson().fromJson(requestFormData,JsonObject.class);
        }
        if (formData == null) {
            formData = new JsonObject();
        }

        Enumeration<String> parameterNames = httpRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!"formData".equals(paramName)) {
                String[] pramValues = httpRequest.getParameterValues(paramName);
                if (pramValues.length == 1) {
                    formData.addProperty(paramName, pramValues[0]);
                } else {
                    formData.add(paramName, new Gson().fromJson(new Gson().toJson(pramValues), JsonArray.class));
                }

            }
        }
        return formData;
    }
    /**
     * httpRequestToFormData
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JsonUtils#newHttpRequestToFormData(HttpServletRequest httpRequest)} instead.</p>
     *
     * @param HttpServletRequest httpRequest 
     * @return JsonObject 
     */
	@Deprecated
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
    
   
	/**
	 * newHttpRequestWithServiceAndFormData using gson
	 * @param httpRequest
	 * @return JsonObject
	 */
    @SuppressWarnings("unchecked")
    public static JsonObject newHttpRequestWithServiceAndFormData(HttpServletRequest httpRequest) {
        JsonObject serviceParameterJson = new JsonObject();
        JsonObject resultJson = new JsonObject();
        String requestFormData = httpRequest.getParameter("formData");
        if (requestFormData != null) {
            resultJson.add("formData", new Gson().fromJson(requestFormData,JsonObject.class));
        }

        Enumeration<String> parameterNames = httpRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (!"formData".equals(paramName)) {
                String[] pramValues = httpRequest.getParameterValues(paramName);
                if (pramValues.length == 1) {
                    serviceParameterJson.addProperty(paramName, pramValues[0]);
                } else {
                    serviceParameterJson.add(paramName, new Gson().fromJson(new Gson().toJson(pramValues), JsonArray.class));
                }

            }
        }
        resultJson.add("serviceJson", serviceParameterJson);
        return resultJson;
    }
    /**
     * httpRequestWithServiceAndFormData
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JsonUtils#newHttpRequestWithServiceAndFormData(HttpServletRequest httpRequest)} instead.</p>
     *
     * @param HttpServletRequest httpRequest 
     * @return JsonObject 
     */
    @Deprecated
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
    /**
     * getKeys
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JsonUtils#getKeys(JsonObject jsonObject)} instead.</p>
     *
     * @param JsonObject jsonObject 
     * @return List<String>
     */
    @Deprecated
    public static List<String> getKeys(JSONObject jsonObject) {
    	@SuppressWarnings("rawtypes") Iterator keys = jsonObject.keys();
        List<String> totalKeys = new ArrayList<>();
        while (keys.hasNext()) {
            totalKeys.add((String) keys.next());
        }
        return totalKeys;
    }
    
    /**
     * getKeys using gson
     * @param JsonObject jsonObject
     * @return List<String>
     */
    public static List<String> getKeys(JsonObject jsonObject) {
    	
    	if ( jsonObject == null ) return Collections.emptyList();

    	Set<String> keySet = jsonObject.keySet();
        List<String> totalKeys = new ArrayList<>();
        for (String key : keySet) {
            totalKeys.add(key);
        }
        return totalKeys;
    }
    public static String defaultDialect() {
        JSONObject adhocSqlSettings = getAdhocSqlSettings();
        if (!adhocSqlSettings.has("defaultJdbcDialect")) {
            return "org.hibernate.dialect.PostgreSQLDialect";
        }
        return adhocSqlSettings.getString("defaultJdbcDialect");
    }

    /**
     * The json of System/Admin/setting.xml object
     *
     * @return The json of settings.xml
     */
    /**
     * @deprecated
     * use newGetAdhocSqlSettings() method
     * @return
     */
    public static JSONObject getAdhocSqlSettings() {
        final String settingPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "adhocSqlSettings.xml";
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(settingPath, false);
    }
    /**
     * using gson
     * @return
     */
    public static JsonObject newGetAdhocSqlSettings() {
        final String settingPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "adhocSqlSettings.xml";
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(settingPath, false);
    }
    public static String defaultDriverClassName() {
        JSONObject adhocSqlSettings = getAdhocSqlSettings();
        if (!adhocSqlSettings.has("defaultDriverClassName")) {
            return "org.postgresql.Driver";
        }
        return adhocSqlSettings.getString("defaultDriverClassName");
    }

    @Nullable
    public static String extractDriverName(String json) {
        JSONObject fromJson = JSONObject.fromObject(json);
        String driverName = null;
        if (fromJson.has("driver")) driverName = fromJson.getString("driver");
        if (fromJson.has("driverName")) driverName = fromJson.getString("driverName");
        if (fromJson.has("driverClassName")) driverName = fromJson.getString("driverClassName");

        if (driverName == null) {
            throw new MalformedJsonException("The json is malformed as there is no " + "driverName or " +
                    "driverClassName parameter");
        }
        return DriverClassCompat.normalize(driverName);
    }


    /**
     * listKeys
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JsonUtils#listKeys(JsonObject jsonObject)} instead.</p>
     *
     * @param JsonObject jsonObject 
     * @return List<String>
     */
    @Deprecated
    public static List<String> listKeys(JSONObject jsonObject) {
        Iterator<?> keys = jsonObject.keys();
        List<String> listOfKeys = new ArrayList<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            listOfKeys.add(key);
        }
        return listOfKeys;
    }
    /**
     * listKeys using gson
     * @param JsonObject jsonObject
     * @return List<String>
     */
    public static List<String> listKeys(JsonObject jsonObject) {
        Iterator<?> keys =  jsonObject.keySet().iterator();
        List<String> listOfKeys = new ArrayList<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            listOfKeys.add(key);
        }
        return listOfKeys;
    }

    public static String getDrillConfigPath() {
        return applicationProperties.getSystemDirectory() + File.separator
                + "Admin" + File.separator + "drillConfig.xml";
    }

    public static String getThreadPoolConfigPath() {
        return applicationProperties.getSystemDirectory() + File.separator
                + "Admin" + File.separator + "threadPoolConfig.xml";
    }

    public static String noSqlConfigPath() {
        return applicationProperties.getSystemDirectory() + File.separator
                + "Admin" + File.separator + "noSql.xml";
    }
    
    /**
     * Deprecated this method in favor of  {@link getNewThreadPoolConfig }
     * @return
     */
    @Deprecated
    public static JSONObject getThreadPoolConfig() {
        return getXmlAsJson(getThreadPoolConfigPath()).getJSONObject("threadPoolConfig");
    }
    
    public static JsonObject getNewThreadPoolConfig() {
    	return newGetXmlAsJson(getThreadPoolConfigPath()).getAsJsonObject();
    }
    
    public static int getThreadPoolTaskTimeOut() {
    	return getNewThreadPoolConfig().get("taskTimeout").getAsInt();
    }
    
    /**
     * getXmlAsJson(String path)
     * @deprecated
     * This method no longer acceptable
     * <p> use  {@link JsonUtils#newGetXmlAsJson(String path)} instead.
     * @param path
     * @return
     */
    @Deprecated
    public static JSONObject getXmlAsJson(String path) {
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJSONObject(path, true);
    }
    /**
     * using gson
     * @param path
     * @return
     */
    public static JsonObject newGetXmlAsJson(String path) {
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(path, true);
    }
    //encryptedParameters
    public static List<String> getEncryptedParametersList(String parameter) {
        String encryptedParameters = getSettingsJson().optString(parameter);
        String arr[] = encryptedParameters.trim().split("\\s*,\\s*");
        return Arrays.asList(arr);
    }
    /**
     * changing to gson
     * @param formDataJsonObject
     * @param driverName
     * @return
     */
    public static JsonObject prepareJdbcUrlForMiddleWare(JsonObject formDataJsonObject, String driverName) {
        //name, type, directory, driverName, url, userName, password,databaseDialect
        String hiMiddleWareName = JsonUtils.getHiMiddleWareName();
        if (driverName.startsWith(hiMiddleWareName) && !driverName.contains("mongo")) {
            String jdbcUrl = formDataJsonObject.get("jdbcUrl").getAsString();
            String suffix = FilenameUtils.getExtension(jdbcUrl);
            String prefix = FilenameUtils.getBaseName(jdbcUrl);
            if ( StringUtils.isBlank(suffix) ) {
            	throw new EfwdServiceException("Extension should not be blank.");
            }
            //replacing all the dots from uploaded file name.(excluding extension)
            prefix = prefix.replaceAll("\\.", "_");
            jdbcUrl = prefix + "." + suffix;
            formDataJsonObject.addProperty("jdbcUrl", jdbcUrl);
        }
        return formDataJsonObject;
    }

    public static String removeLastSlash(String url) {
        if (url.endsWith("\\")) {
            return url.substring(0, url.lastIndexOf("\\"));
        } else {
            return url;
        }
    }

    /**
     * This method is used to replace all the @ from the json.
     *
     * @param data
     * @return
     */
    public static JSONArray removeAtFromKey(JSONArray data) {
        JSONArray newArray = new JSONArray();
        for (int index = 0; index < data.size(); index++) {
            JSONObject eachJsonObject = data.getJSONObject(index);
            JSONObject newJson = new JSONObject();
            for (Object eachK : eachJsonObject.keySet()) {
                String eachKey = (String) eachK;
                String eachValue = eachJsonObject.getString(eachKey);
                eachKey = eachKey.replaceFirst("@", "");
                eachKey = "class".equals(eachKey) ? "clazz" : eachKey;
                newJson.put(eachKey, eachValue);
            }
            newArray.add(newJson);
        }
        return newArray;
    }

    public static String getHCRExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getJSONObject("hcr").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key hcr  is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
    }

    public static String getCubeExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getJSONObject("cube").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key cube  is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
    }


    public static String getImageExtension() {
        JSONObject settingsJson = getSettingsJson();
        String extension;
        try {
            extension = settingsJson.getJSONObject("Extentions").getJSONObject("image").getString("#text");
        } catch (Exception e) {
            throw new ConfigurationException("The key cube  is not " +
                    "configured in application " + "settings.", e);
        }
        return extension;
    }

    public static JsonArray getHCRBandContent() {
        JsonObject settingsJson = newGetSettingsJson();
        JsonArray componentArray;
        try {
            componentArray = settingsJson.getAsJsonObject("HCRBandContent").getAsJsonArray("contentClass");
        } catch (Exception e) {
            throw new ConfigurationException("The key HCRBandContent  is not " +
                    "configured in application " + "settings.", e);
        }
        return componentArray;
    }

    public static String getHCRDefaultGeneratorType() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.optString("HCRDefaultGeneratorType", "bean-datasource");
    }
    /**
     * getScheduleStorageType()
     *  @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link JsonUtils#newGetScheduleStorageType()} instead.</p>
     * @return string
     */
    public static String getScheduleStorageType() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.optString("ScheduleStorageType", "XML");
    }
    /**
     * newGetScheduleStorageType() using gson
     * @return
     */
    public static String newGetScheduleStorageType() {
        JsonObject settingsJson = newGetSettingsJson();
        return optStringValue(settingsJson,"ScheduleStorageType", "XML");
    }
    private static String optStringValue(JsonObject jsonObject, String key, String defaultValue) {
		if (jsonObject != null) {
			if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
				return jsonObject.get(key).getAsString();
			}
		}
		return defaultValue;
	}

	/**
	 * isScheduleStorageTypeIsDatabase
	 * @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link JsonUtils#newIsScheduleStorageTypeIsDatabase()} instead.</p>
	 * @return Boolean true or false 
	 */
	@Deprecated
    public static Boolean isScheduleStorageTypeIsDatabase() {
        return "DATABASE".equals(getScheduleStorageType());
    }
	/**
	 * newIsScheduleStorageTypeIsDatabase using gson
	 * @return true or false.
	 */
	public static Boolean newIsScheduleStorageTypeIsDatabase() {
        return "DATABASE".equals(newGetScheduleStorageType());
    }
	
	
    public static Boolean isScheduleMigrationIsEnabled() {
        JSONObject settingsJson = getSettingsJson();
        return isScheduleStorageTypeIsDatabase() && settingsJson.optBoolean("MigrateFromXMLSchedulingToDatabase", false);
    }

    public static Boolean isSchedulingXMLExists() {
        Map<String, String> hashMap;
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        hashMap = propertiesFileReader.read("project.properties");
        String SchedulerPath = hashMap.get("schedulerPath");
        File file = new File(SchedulerPath);
        return file.exists();
    }

    public static String getHCRGeneratorType() {
        JSONObject settingsJson = getSettingsJson();
        String defaultGeneratorType = settingsJson.optString("HCRDefaultGeneratorType", "bean-datasource");
        JSONArray generatorArray;
        String generatorString = "";
        try {
            generatorArray = settingsJson.getJSONArray("HCRGeneratorTypes");
            for (int index = 0; index < generatorArray.size(); index++) {
                JSONObject eachElement = generatorArray.getJSONObject(index);
                String eachElementName = eachElement.getString("@name");
                if (eachElementName.equals(defaultGeneratorType)) {
                    generatorString = eachElement.getString("@bean");
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException("The key HCRGeneratorTypes  is not " +
                    "configured in application " + "settings.", e);
        }
        return generatorString;
    }

    public static JsonArray getHCRConfigurations() {
        JsonObject settingsJson = newGetSettingsJson();
        JsonArray hcrConfigurations;
        try {
            hcrConfigurations = settingsJson.getAsJsonObject("hcrConfigurations").getAsJsonArray("properties");
        } catch (Exception e) {
            throw new ConfigurationException("The key hcrConfigurations  is not " +
                    "configured in application " + "settings.", e);
        }
        return hcrConfigurations;
    }

    public static String getAllTypesFromSetting(JSONObject formJson) {
        JSONObject settingsJson = getSettingsJson();
        JSONArray dataSources = removeAtFromKey(settingsJson.getJSONArray("DataSources"));
        JSONArray visualizationTypes = removeAtFromKey(settingsJson.getJSONArray("visualizationTypes"));
        JSONArray sqlTypes = removeAtFromKey(settingsJson.getJSONArray("sqlTypes"));
        JSONArray parameterTypes = removeAtFromKey(settingsJson.getJSONArray("parameterTypes"));
        JSONObject response = new JSONObject();

        if (formJson == null || formJson.isEmpty()) {
            response.put("sqlTypes", sqlTypes);
            response.put("vizTypes", visualizationTypes);
            response.put("connTypes", dataSources);
            response.put("parameterTypes", parameterTypes);
            return response.toString();
        } else if (formJson.has("actions")) {
            JSONArray actions = formJson.getJSONArray("actions");
            if (actions == null || actions.isEmpty()) {
                response.put("sqlTypes", sqlTypes);
                response.put("vizTypes", visualizationTypes);
                response.put("connTypes", dataSources);
                response.put("parameterTypes", parameterTypes);
                return response.toString();
            }
            for (int index = 0; index < actions.size(); index++) {
                String eachRequestedAction = actions.getString(index);
                if ("sqlTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("sqlTypes", sqlTypes);
                else if ("vizTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("vizTypes", visualizationTypes);
                else if ("connTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("connTypes", dataSources);
                else if ("parameterTypes".equalsIgnoreCase(eachRequestedAction))
                    response.put("parameterTypes", parameterTypes);
                else
                    response.put("message", "invalid action :" + eachRequestedAction);
            }
        } else {
            response.put("sqlTypes", sqlTypes);
            response.put("vizTypes", visualizationTypes);
            response.put("connTypes", dataSources);
            response.put("parameterTypes", parameterTypes);
            return response.toString();
        }
        return response.toString();
    }

    public static JsonObject getSqlWhiteListKeyWords() {
        final String settingPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "whitelist-keywords.xml";
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(settingPath, false);
    }

    public static JsonObject getEscapeStrings() {
        final String settingPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator +
                "Admin" + File.separator + "quotes.xml";
        final IProcessor processor = ResourceProcessorFactory.getIProcessor();
        return processor.getJsonObject(settingPath, false);
    }

    private static volatile List<String[]> drillConfigXmlReplaceRules;

    /**
     * Applies {@code drillConfigXmlReplaceRules} CDATA from setting.xml ({@code findText} / {@code replaceText} JSON arrays).
     */
    public static String applyDrillConfigXmlValueForAttribute(Object rawValue) {
        if (rawValue == null || net.sf.json.util.JSONUtils.isNull(rawValue)) {
            return "";
        }
        if (rawValue instanceof net.sf.json.JSONArray || rawValue instanceof net.sf.json.JSONObject) {
            return "";
        }
        String value = String.valueOf(rawValue);
        if ("null".equalsIgnoreCase(value)) {
            return "";
        }
        List<String[]> rules = getDrillConfigXmlReplaceRules();
        for (String[] rule : rules) {
            value = value.replace(rule[0], rule[1]);
        }
        return value;
    }

    private static List<String[]> getDrillConfigXmlReplaceRules() {
        List<String[]> local = drillConfigXmlReplaceRules;
        if (local != null) {
            return local;
        }
        synchronized (JsonUtils.class) {
            if (drillConfigXmlReplaceRules == null) {
                drillConfigXmlReplaceRules = loadDrillConfigXmlReplaceRules();
            }
            return drillConfigXmlReplaceRules;
        }
    }

    private static List<String[]> loadDrillConfigXmlReplaceRules() {
        JSONObject settingsJson = getSettingsJson();
        String rulesCdata = settingsJson != null ? settingsJson.optString("drillConfigXmlReplaceRules", "") : "";
        if (rulesCdata == null || rulesCdata.trim().isEmpty()) {
            return Collections.emptyList();
        }
        JSONObject rulesJson;
        try {
            rulesJson = JSONObject.fromObject(rulesCdata.trim());
        } catch (JSONException ex) {
            logger.warn("drillConfigXmlReplaceRules CDATA is not valid JSON", ex);
            return Collections.emptyList();
        }
        JSONArray findText = rulesJson.optJSONArray("findText");
        JSONArray replaceText = rulesJson.optJSONArray("replaceText");
        if (findText == null || replaceText == null) {
            logger.warn("drillConfigXmlReplaceRules must contain findText and replaceText arrays");
            return Collections.emptyList();
        }
        int pairCount = Math.min(findText.size(), replaceText.size());
        if (findText.size() != replaceText.size()) {
            logger.warn("drillConfigXmlReplaceRules: findText size ({}) != replaceText size ({})",
                    findText.size(), replaceText.size());
        }
        List<String[]> rules = new ArrayList<>(pairCount);
        for (int i = 0; i < pairCount; i++) {
            rules.add(new String[]{findText.getString(i), replaceText.getString(i)});
        }
        return rules;
    }

    public static String getDSStorageType() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.optString("DSStorageType", "XML");
    }


    public static Boolean isDSTypeStorageDatabase() {
        return "DATABASE".equalsIgnoreCase(getDSStorageType());
    }

    public static Boolean isDSMigrationIsEnabled() {
        JSONObject settingsJson = getSettingsJson();
        return isDSTypeStorageDatabase() && settingsJson.optBoolean("MigrateFromGlobalXMLDatasourceToDatabase");
    }


    public static Boolean isForceUpdateEnabled() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.optBoolean("ForceMigrationDatasource");
    }
    /**
     * loadOrder()
     * @deprecated
     * This method is no longer acceptable 
     * <p> Use {@link JsonUtils#loadOrder()} instead.</p>
     * @return JsonArray
     */
    @Deprecated
    public static JSONArray loadOrder() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.optJSONArray("loadOrder");
    }
    /**
     * newLoadOrder() using gson
     * @return jsonArray from setting.xml
     */
    public static JsonArray newLoadOrder() {
        JsonObject settingsJson = newGetSettingsJson();
        return GsonUtility.optJsonArray(settingsJson.getAsJsonObject("loadOrder"),"bean");
    }

    public static String getDSType(String dsTypeClassName) {
        JSONObject settingsJson = getSettingsJson();
        JSONArray dsType = settingsJson.optJSONArray("dsType");
        for (int index = 0; index < dsType.size(); index++) {
            JSONObject jsonObject = dsType.getJSONObject(index);
            String className = jsonObject.optString("@name");
            if (StringUtils.isNotEmpty(className) && className.equalsIgnoreCase(dsTypeClassName)) {
                String dsTypeValue = jsonObject.getString("#text");
                return dsTypeValue;
            }
        }
        throw new ConfigurationException("Invalid DsType Configuration,Please re-check the settings.xml file");
    }


    public static String getDSTypeClass(String dsTypeValue) {
        JSONObject settingsJson = getSettingsJson();
        JSONArray dsType = settingsJson.optJSONArray("dsType");
        for (int index = 0; index < dsType.size(); index++) {
            JSONObject jsonObject = dsType.getJSONObject(index);
            String value = jsonObject.optString("#text");
            if (StringUtils.isNotEmpty(value) && value.equalsIgnoreCase(dsTypeValue)) {
                return jsonObject.getString("@name");
            }

        }
        throw new ConfigurationException("Invalid DsType Configuration,Please re-check the settings.xml file");
    }

    public static JsonObject getDSTypeClassJson(String dsTypeValue) {
        JsonObject settingsJson = newGetSettingsJson();
        JsonArray dsType = settingsJson.getAsJsonObject("dsType").getAsJsonArray("class");
        for (int index = 0; index < dsType.size(); index++) {
            JsonObject jsonObject = dsType.get(index).getAsJsonObject();
            String value = GsonUtility.optString(jsonObject, "");
            if (StringUtils.isNotEmpty(value) && value.equalsIgnoreCase(dsTypeValue)) {
                return jsonObject;
            }

        }
        throw new ConfigurationException("Invalid DsType Configuration,Please re-check the settings.xml file");
    }


    public static String getFileBrowserStorageType() {
        JSONObject settingsJson = getSettingsJson();
        return settingsJson.optString("FileBrowserStorageType", "DATABASE");
    }


    public static Boolean isFileBrowserStorageDatabase() {
        return "DATABASE".equalsIgnoreCase(getFileBrowserStorageType());
    }
    
    /**
     * using gson
     * decryptPasswordFromDrillConfigObj(JsonObject drillConfig)
     * @param drillConfig
     * @return
     */
    public static JsonObject decryptPasswordFromDrillConfigObj(JsonObject drillConfig) {
        String password1=drillConfig.getAsJsonObject("fileSystemConfiguration")
        	.getAsJsonObject("sftp").get("password").getAsString();
        String password2=drillConfig.getAsJsonObject("urlConfig").get("password").getAsString();
        if(password1!=null && !password1.equals("") || 
        		password2!=null && !password2.equals("")) {
			if (password1 != null && !password1.equals("")) {
				drillConfig.getAsJsonObject("fileSystemConfiguration").getAsJsonObject("sftp")
						.addProperty("password", DataSourceEncrypt.decrypt(password1));
			}
			if (password2 != null && !password2.equals("")) {
				drillConfig.getAsJsonObject("urlConfig").addProperty("password", DataSourceEncrypt.decrypt(password2));
			}
			return drillConfig;
        }
        return drillConfig;
    }
	public static String getMapProperties() {
		Properties props = new Properties();
		Path filePath = Path.of(applicationProperties.getSystemDirectory(), "Admin", "map.properties");
		JsonObject response = new JsonObject();
		try {
			FileSystemResource propertyFile = new FileSystemResource(filePath.toString());
			props.load(propertyFile.getInputStream());
			 for (String key : props.stringPropertyNames()) {
		            String[] parts = key.split("\\.");
		            JsonObject currentObj = response;
		            for (int i = 0; i < parts.length - 1; i++) {
		                if (!currentObj.has(parts[i])) {
		                    currentObj.add(parts[i], new JsonObject());
		                }
		                currentObj = currentObj.getAsJsonObject(parts[i]);
		            }
		            currentObj.addProperty(parts[parts.length - 1], props.getProperty(key));
		        }
		} catch (Exception e) {
			throw new EfwServiceException(e.getMessage());
		}

		return response.toString();
	}
	

	public static JSONObject getFromDataJson(String formData){
        JsonObject jsonElement=JsonParser.parseString(formData).getAsJsonObject();
        jsonElement.remove("state");
        jsonElement.remove("htmlString");

        String s = jsonElement.toString();
        JSONObject formDatasf= JSONObject.fromObject(s);
        return formDatasf;
    }
	
    public static JSONObject decryptPasswordFromDrillConfigObj(JSONObject drillConfig) {
        String password1=drillConfig.getJSONObject("drill").getJSONObject("fileSystemConfiguration")
        	.getJSONObject("sftp").getString("password");
        String password2=drillConfig.getJSONObject("drill").getJSONObject("urlConfig").getString("password");
        if (password1 != null && !password1.equals("")) {
			drillConfig.getJSONObject("drill").getJSONObject("fileSystemConfiguration").getJSONObject("sftp")
					.put("password", DataSourceEncrypt.decrypt(password1));
		}
		if (password2 != null && !password2.equals("")) {
			drillConfig.getJSONObject("drill").getJSONObject("urlConfig").put("password", DataSourceEncrypt.decrypt(password2));	
		}
        return drillConfig.getJSONObject("drill");
    }
	
	public static String replaceSpecialCharacter(String input) {
		
		if(input.contains("&amp;")) {
			input =  input.replace("&amp;", "&");
		}
		else if (input.contains("&")) {
			input =  input.replace("&", "&amp;");
		}
		
		if(input.contains("<")) {
			input =  input.replace("<","&lt;");
		}
		else if(input.contains("&lt;")) {
			input =  input.replace("&lt;", "<");
		}
		return input;
	}

	 public static String getFileAsString(String settingFilePath) {
	        try {
	            return new String(Files.readAllBytes(Paths.get(settingFilePath)), ControllerUtils.defaultCharSet());
	        } catch (IOException ioe) {
	            throw new RuntimeException("There was a problem in the operation" + " " + ioe.getMessage());
	        }
	    }
	 
	 public static String getDSTypeClassName(String dsTypeValue) {
			try {
				if (dsTypeMap.isEmpty()) {
					JsonObject settingsJson = newGetSettingsJson();
					JsonArray dsType = settingsJson.getAsJsonArray("dsType");
					for (int index = 0; index < dsType.size(); index++) {
						JsonObject jsonObject = dsType.get(index).getAsJsonObject();
						String value = GsonUtility.optString(jsonObject,"#text");
						if (value!=null && StringUtils.isNotEmpty(value)) {
							dsTypeMap.put(value, jsonObject.get("@name").getAsString());
						}
					}
				}
				return dsTypeMap.get(dsTypeValue);
			} catch (Exception ex) {
				throw new ConfigurationException("Invalid DsType Configuration,Please re-check the settings.xml file");
			}
	    }

    public static String getQueryMapping(String q) {
        return new PropertiesFileReader().read("Admin", "driverDefaultQuery.properties").get(q);

    }


    public static JsonObject safeGetJsonObject(Object value) {
        if (value == null) return new JsonObject();

        String jsonStr = String.valueOf(value);
        try {
            // Handle double-wrapped JSON
            if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                jsonStr = new Gson().fromJson(jsonStr, String.class);
            }
            return JsonParser.parseString(jsonStr).getAsJsonObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON content", e);
        }
    }
    
    public static int getResponseEventBatchSize() {
        JsonObject settingsJson = newGetSettingsJson();
        return GsonUtility.optIntValue(settingsJson, "eventBatchSize", 100);
    }
    
    public static String getSseUpdateBatchStrategy() {
    	JsonObject settingsJson = newGetSettingsJson();
    	JsonObject sseUpdate =  GsonUtility.optJsonObject(settingsJson, "sseUpdate");
    	return GsonUtility.optStringValue(sseUpdate, "strategy", "default");
    }
    
    public static int getSseUpdateBufferTime() {
    	JsonObject settingsJson = newGetSettingsJson();
    	JsonObject sseUpdate =  GsonUtility.optJsonObject(settingsJson, "sseUpdate");
    	return GsonUtility.optIntValue(sseUpdate, "bufferTimeMillis", 50);
    }
    
    public static boolean waitUntilFirstPageLoad() {
        JsonObject settingsJson = newGetSettingsJson();
        return GsonUtility.optBooleanValue(settingsJson, "waitUntilFirstPageLoad", false);
    }
    



}