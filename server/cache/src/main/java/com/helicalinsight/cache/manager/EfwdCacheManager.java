package com.helicalinsight.cache.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.QueryExecutor;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.GroovyUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.efw.validator.ResourceValidator;


import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Somen
 * responsible for managing cache for Efwd file which extends <p>{@link CacheManager}</p>
 * The {@code @Component} annotation is used to mark a class as a Spring bean/component.
 * {@code @Scope("prototype")} new instance of the component will be created each time.
 * Created by Somen on 5/30/2015.
 */
@Component
@Scope("prototype")
public class EfwdCacheManager extends CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(EfwdCacheManager.class);
    private static ApplicationProperties applicationProperties;
    private static Map<String, String> settingsDataSourcesMap = new HashMap<>();
    private static JsonObject settingFileAsJson;
    private static String solutionDirectory;
    private JsonObject dataMapTagContent;
    private JsonObject requestParameterJson;
    private JsonObject connectionDetails;
    private JsonObject efwdFileAsJson;
    private Integer mapId;
    private IDriver driverObject;
    private String efwdFile;

	/**
	 * serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object fileContent)
	 *
	 * @param request    request object provides modified cache object
	 * @param response   response sets the content type and encoding value from message.properties
	 * @param rawContent query execution result of hcr in object format
	 * {@return true} if the content was served successfully.
	 */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object fileContent) {
        response.setContentType(ControllerUtils.defaultContentType());
        response.setCharacterEncoding(ApplicationUtilities.getEncoding());
        PrintWriter out = null;
        logger.info("Serving cache file for executeDatasource......");
        try {
            out = response.getWriter();
            JsonObject jsonContent = (JsonObject) fileContent;
            Object lastModifiedTime = request.getAttribute("lastModifiedCache");
            if (lastModifiedTime != null)
                jsonContent.addProperty("lastModified", (Long) lastModifiedTime);
            out.print(jsonContent);
        } catch (IOException ioe) {
            logger.error("Exception occurred ", ioe);
        } finally {
            ApplicationUtilities.closeResource(out);
        }
        return true;
    }

	/**
	 * init()
	 * This method initializes the content. 
	 */
    @PostConstruct
    public void init() {
        logger.debug("initializing content");
        applicationProperties = ApplicationProperties.getInstance();
        String settingPath = applicationProperties.getSettingPath();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        settingFileAsJson = processor.getJsonObject(settingPath, false);
        solutionDirectory = applicationProperties.getSolutionDirectory();
        this.setSettingsDataSourcesMap();
    }

	/**
	 * setSettingsDataSourcesMap()
	 * This method stores xml data in memory.
	 */
    private void setSettingsDataSourcesMap() {
        JsonArray settingsDataSources = settingFileAsJson.getAsJsonObject("DataSources").getAsJsonArray("DataSource");
        for (int dataSource = 0; dataSource < settingsDataSources.size(); dataSource++) {
            JsonObject settingsDataSource = settingsDataSources.get(dataSource).getAsJsonObject();
            String dataSourceType = settingsDataSource.get("type").getAsString();
            String clazz = settingsDataSource.get("class").getAsString();
            settingsDataSourcesMap.put(dataSourceType, clazz);
        }
    }


	/**
	 * setRequestData(String data)
	 * @param data 		 this is efwd file json data , provided in string format
	 */
    public void setRequestData(String data) {
        this.requestParameterJson = new Gson().fromJson(data,JsonObject.class);
    }

	/**
	 * getConnectionFilePath()
	 * this method is responsible for providing efwd file path details
	 * {@return efwd connection file}{@code null} if not valid file
	 */
    public String getConnectionFilePath() {
        String outerDir = this.requestParameterJson.get("dir").getAsString();
        if ("System/Temp".equalsIgnoreCase(outerDir)) {
            String tempDir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
            JsonObject efwdJson = GsonUtility.optJsonObject(this.requestParameterJson,"efwd");
            String file = null;
            if (efwdJson != null)
                file = GsonUtility.optString(efwdJson,"file");

            EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(), applicationProperties);
            efwdFileAsJson = queryExecutor.newReadTempEFWD(tempDir, file);
        } else {
            QueryExecutor dataSource = new QueryExecutor(this.requestParameterJson.toString(), applicationProperties);
            JsonObject efwdJson = GsonUtility.optJsonObject(this.requestParameterJson,"efwd");
            String file = null;
            if (efwdJson != null) {
                file = GsonUtility.optString(efwdJson,"file");
                String dir = GsonUtility.optString(efwdJson,"dir");
                if (dir != null && !dir.isEmpty()) {
                    outerDir = dir;
                }
            }
            efwdFileAsJson = dataSource.newReadEFWD(outerDir, file, solutionDirectory);
        }
        efwdFile = efwdFileAsJson.get("_efwdFileName_").getAsString();
       // ResourceValidator resourceValidator = new ResourceValidator(efwdFileAsJson);
       // boolean isValidEFWD = resourceValidator.validateEfwd();
       // if (isValidEFWD) {
            int length = (solutionDirectory + File.separator).length();
            return efwdFile.substring(length);
        //} else {
          //  return null;
        //}

    }
    
    
	/**
	 * getDirectory()
	 * @return  it returns directory in string format from requestParameterJson(JsonObject)
	 */
	public String getDirectory() {
        return this.requestParameterJson.get("dir").getAsString();
    }

	/**
	 * getConnectionId()
	 * this method is responsible for getting connection id from efwdFile
	 * for that efwdFile should not be null
	 * 
	 * @return connection id in long format.
	 */
    public Long getConnectionId() {
        logger.info("RequestParameterJson is " + requestParameterJson);
        String outerDir = this.requestParameterJson.get("dir").getAsString();
        JsonObject innerEfwdJson = GsonUtility.optJsonObject(requestParameterJson,"efwd");
        int mapId = requestParameterJson.get("map_id").getAsInt();
        int connectionId = 0;
        if (efwdFile != null) {
            JsonArray dataMapsArray = this.efwdFileAsJson.getAsJsonArray("DataMaps");
            for (int counter = 0; counter < dataMapsArray.size(); counter++) {
                JsonObject dataMapTag = dataMapsArray.get(counter).getAsJsonObject();
                if (mapId == dataMapTag.get("@id").getAsInt()) {
                    this.mapId = mapId;
                    dataMapTagContent = (JsonObject) dataMapsArray.get(counter);
                    connectionId = dataMapTag.get("@connection").getAsInt();
                    break;
                }
            }

        }
        JsonObject formData = new JsonObject();
        formData.addProperty("id", connectionId);
        formData.addProperty("dir", getDirectory());
        formData.addProperty("access", DataSourceSecurityUtility.EXECUTE);
        if (innerEfwdJson != null && innerEfwdJson.has("file")) {
            JsonObject efwd = new JsonObject();
            efwd.addProperty("file", innerEfwdJson.get("file").getAsString());
            formData.add("efwd", efwd);
        }
        if ("System/Temp".equalsIgnoreCase(outerDir)) {
            JsonObject newFormData = new Gson().fromJson(requestParameterJson,JsonObject.class);
            newFormData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
            newFormData.addProperty("access", DataSourceSecurityUtility.EXECUTE);

            DataSourceSecurityUtility.isDataSourceAuthenticatedFromTemp(JSONObject.fromObject(newFormData.toString()));
        } else
            DataSourceSecurityUtility.isDataSourceAuthenticated(formData);
        return (long) connectionId;
    }

	/**
	 * getMapId()
	 * @return  returns the mapId from efwd connection
     */
    public Integer getMapId() {
        return this.mapId;
    }

	/**
	 * getConnectionType(Long connectionId)
	 * this method used to get connection type from efwdFile
	 * @param connectionId    used to check 0 or not
	 * {@return efwd file connection type}{@code null} if connectionId is zero
	 */
    public String getConnectionType(Long connectionId) {
        String type = null;
        if (connectionId != 0) {
            JsonArray dataSources = efwdFileAsJson.getAsJsonArray("DataSources");
            for (int counter = 0; counter < dataSources.size(); counter++) {
                JsonObject connection = dataSources.get(counter).getAsJsonObject();
                if (connectionId == connection.get("@id").getAsInt()) {
                    connectionDetails = connection;
                    type = connection.get("@type").getAsString();
                    break;
                }
            }
        }
        return type;
    }

	/**
	 * getQuery(String connectionType)
	 *
	 * @param connectionType 	it provides type of class from efwd file
	 * {@return query for datasource}{@code null} if driverObject is null
	 */
    public String getQuery(String connectionType) {
        driverObject = this.getInstance(connectionType);
        if (driverObject != null) {
            String query = driverObject.getQuery(dataMapTagContent, requestParameterJson);
            String typeFromConnectionDetails = connectionDetails.get("@type").getAsString();
            if (GlobalJdbcTypeUtils.isTypeGroovy(typeFromConnectionDetails)) {
                JsonObject condition = GroovyUtils.executeGroovy(connectionDetails.get("Condition").getAsString(), "evalCondition", JsonObject.class);
                query += "##__"+condition+"__##";

            }
            return query;
        }
        return null;
    }

	/**
	 * getInstance(String connectionType)
	 * this method is used to get the class name for the connection type.
	 * @param connectionType 	it provides type of class
	 * @return IDriver			returns object created out of the class instance using reflection API
	 */
    private IDriver getInstance(String connectionType) {
        IDriver driverObject = null;
        String clazz = settingsDataSourcesMap.get(connectionType);
        if (clazz != null) {
            driverObject = (IDriver) FactoryMethodWrapper.getUntypedInstance(clazz);
        }
        return driverObject;

    }

	/**
	 * getDataFromDatabase(String query)
	 * method return the jsonObject of the result of the database query
	 * @param query 			query for hcr dataSource
	 * @return JsonObject	    
	 */
    public JsonObject getDataFromDatabase(String query) {
        requestParameterJson.addProperty("query", query);//accumulate
        return driverObject.getJSONData(requestParameterJson, connectionDetails, dataMapTagContent,
                applicationProperties);

    }

	
    @Override
    public String getRequestData() {
        return null;
    }

}
