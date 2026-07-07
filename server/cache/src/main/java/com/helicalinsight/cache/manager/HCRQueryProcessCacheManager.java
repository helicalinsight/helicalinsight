package com.helicalinsight.cache.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 12/10/2019.
 * responsible for managing cache for hcr which extends
 * <p>{@link CacheManager}</p>
 * The {@code @Component} annotation is used to mark a class as a Spring bean/component.
 * {@code @Scope("prototype")} new instance of the component will be created each time.
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class HCRQueryProcessCacheManager extends CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(HCRQueryProcessCacheManager.class);
    private static ApplicationProperties applicationProperties;
    private static Map<String, String> settingsDataSourcesMap = new HashMap<>();
    private static JsonObject settingFileAsJson;
    private static String solutionDirectory;
    private JsonObject requestParameterJson;
    private JsonObject efwdFileAsJson;
    private Integer mapId;
    private String efwdFile;
    private EnhancedQueryExecutor queryExecutor;
    private JsonArray result;
    private JsonObject cacheManagerFormData;


    public JsonArray getResult() {
        return result;
    }

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

    @Override
    public String getRequestData() {
        return null;
    }

    @Override
    public void setRequestData(String data) {
    	JsonObject formData = new Gson().fromJson(data,JsonObject.class);
        cacheManagerFormData = formData;
        requestParameterJson = formData.getAsJsonObject("connectionDetails");
    }

    private void setSettingsDataSourcesMap() {
    	JsonArray settingsDataSources = settingFileAsJson.getAsJsonObject("DataSources").getAsJsonArray("DataSource");
        for (int dataSource = 0; dataSource < settingsDataSources.size(); dataSource++) {
            JsonObject settingsDataSource = settingsDataSources.get(dataSource).getAsJsonObject();
            String dataSourceType = settingsDataSource.get("type").getAsString();
            String clazz = settingsDataSource.get("class").getAsString();
            settingsDataSourcesMap.put(dataSourceType, clazz);
        }

    }

    @Override
    public String getConnectionFilePath() {
        if (!requestParameterJson.entrySet().isEmpty()) {
            if (requestParameterJson.has("temp_uuid")) {
                String dir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
                String file = requestParameterJson.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension();
                //requestParameterJson.put("dir", dir);
                JsonObject efwd = new JsonObject();
                efwd.addProperty("file", file);
                requestParameterJson.add("efwd", efwd);
                queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(), applicationProperties);
                efwdFileAsJson = queryExecutor.newReadTempEFWD(dir, file);
            } else {
            	JsonObject efwdJson = GsonUtility.optJsonObject(this.requestParameterJson,"efwd");
                String outerDir = this.requestParameterJson.get("dir").getAsString();
                String file = null;
                if (efwdJson != null) {
                	file = GsonUtility.optString(efwdJson,"file");
                    String dir = GsonUtility.optString(efwdJson,"dir");
                    if (dir != null && !dir.isEmpty()) {
                        outerDir = dir;
                    }
                }
                queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(), applicationProperties);
                efwdFileAsJson = queryExecutor.newReadEFWD(outerDir, file, solutionDirectory);
            }

            efwdFile = efwdFileAsJson.get("_efwdFileName_").getAsString();
           // ResourceValidator resourceValidator = new ResourceValidator(efwdFileAsJson);
            //boolean isValidEFWD = resourceValidator.newValidateEfwd();
            //if (isValidEFWD) {
                int length = (solutionDirectory + File.separator).length();
                return efwdFile.substring(length);
            //} else {
              //  return null;
            //}
        }
        return "EMPTY";
    }
    /**
	 * getConnectionId()
	 * 
	 * this method is responsible for getting connection id from datasource/efwdFileAsJson(JsonObject)
	 * for that requestParameterJson should not be empty
	 * 
	 *{ @return id in long value}{@code -404l} if requestParameterJson is empty
	 */
    @Override
    public Long getConnectionId() {
        logger.info("RequestParameterJson is " + requestParameterJson);
        this.mapId = -404;
        if (!requestParameterJson.entrySet().isEmpty()) {
            int mapId = requestParameterJson.get("map_id").getAsInt();
            int connectionId = 0;
            if (efwdFile != null) {
                JsonElement dataMaps = this.efwdFileAsJson.get("DataMaps");
                JsonArray dataMapsArray= new JsonArray();
                if(dataMaps instanceof JsonObject){
                    dataMapsArray.add(((JsonObject)dataMaps).getAsJsonObject("DataMap"));
                }
                if(dataMaps instanceof  JsonArray){
                    dataMapsArray=this.efwdFileAsJson.getAsJsonArray("DataMaps");
                }

                for (int counter = 0; counter < dataMapsArray.size(); counter++) {
                	JsonObject dataMapTag = dataMapsArray.get(counter).getAsJsonObject();
                    if (mapId == Integer.valueOf(dataMapTag.get("id").getAsString())) {
                        this.mapId = mapId;
                        // dataMapTagContent = (JSONObject) dataMapsArray.get(counter);
                        connectionId = Integer.valueOf(dataMapTag.get("connection").getAsString());
                        break;
                    }
                }

            }

            return withAuthentication(connectionId);
        }
        return -404l;
    }
    /**
	 * withAuthentication(int connectionId)
	 * 
	 * it authenticates connection details, efwd file and metadata file
	 *
	 * @param connectionId  connection id from datasource
	 * @return connectionId in long value.
	 */
    public Long withAuthentication(int connectionId) {
    	JsonObject formData = new JsonObject();
        formData.addProperty("id", connectionId);
        formData.addProperty("dir", getDirectory());
        formData.addProperty("access", DataSourceSecurityUtility.EXECUTE);
        if (requestParameterJson.has("efwd"))
            formData.add("efwd", requestParameterJson.getAsJsonObject("efwd"));
        if (requestParameterJson.has("temp_uuid")) {
            JsonObject efwd = new JsonObject();
            efwd.addProperty("file", requestParameterJson.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension());
            formData.add("efwd", efwd);
            DataSourceSecurityUtility.isDataSourceAuthenticatedFromTemp(JSONObject.fromObject(formData.toString()));
        } else
            DataSourceSecurityUtility.isDataSourceAuthenticated(formData);
        return (long) connectionId;
    }
    /**
     * getMapId()
     * @return  returns the mapId from efwd connection
     */
    @Override
    public Integer getMapId() {
        return this.mapId;
    }
    /**
	 * getConnectionType(Long connectionId)
	 * this method used to get connection type from datasource
	 * @param connectionId    used to check null or 0
	 * {@return connection type in string format}{@code "JUST_TO_IGNORE"} if connectionId null or zero,efwdFileAsJson is null
	 */
    @Override
    public String getConnectionType(Long connectionId) {
        String type = "JUST_TO_IGNORE";
        if (connectionId != null && connectionId != 0 && efwdFileAsJson != null) {
        	JsonArray dataSources =null;
            JsonElement elemDs = efwdFileAsJson.get("DataSources");
            if(elemDs instanceof  JsonArray){
                dataSources=efwdFileAsJson.getAsJsonArray("DataSources");
            }else {
                dataSources = new JsonArray();
                dataSources.add(efwdFileAsJson.getAsJsonObject("DataSources").getAsJsonObject("Connection"));
            }

            for (int counter = 0; counter < dataSources.size(); counter++) {
                JsonObject connection = dataSources.get(counter).getAsJsonObject();
                if (connectionId == connection.get("id").getAsInt()) {
                    //  connectionDetails = connection;
                    type = connection.get("type").getAsString();
                    break;
                }
            }
        }
        return type;
    }
    /**
	 * getQuery(String connectionType)
	 *
	 * this method is responsible to generates query from hcr dataSource
	 * 
	 * @param connectionType  hcr type
	 * {@return query for report generating in string format}{@code "JUST_TO_IGNORE"} if requestParameterJson is empty.
	 */
    @Override
    public String getQuery(String connectionType) {
        String query = "JUST_TO_IGNORE";
        if (!requestParameterJson.entrySet().isEmpty()) {
            if (requestParameterJson.has("temp_uuid")) {
                query = queryExecutor.getQueryFromTemp();
            } else {
                query = queryExecutor.getQuery();
            }
        }
        return query;
        //return SplitterUtils.prepareServiceId(requestParameterJson.toString() + cacheManagerFormData + query);
    }
    /**
	 * getDataFromDatabase(String query)
	 * 
	 * this method is responsible for providing Result from hcr data.
	 * 
	 * @param query    query from hcr dataSource
	 * {@return  returns query result in JsonObject format}{@code "{}"} if requestParameterJson object is empty.
	 */
    @Override
    public JsonObject getDataFromDatabase(String query) {
        JsonObject jsonObject = new JsonObject();
        if (!requestParameterJson.entrySet().isEmpty()) {
            if (requestParameterJson.has("temp_uuid")) {
                jsonObject = queryExecutor.getResultSetFromTemp();
            } else {
                jsonObject = queryExecutor.getResultSet();
            }
        }
        return jsonObject;
    }
    /**
     * getDirectory()
     * @return  it returns hcr directory in string format from requestParameterJson(JsonObject)
     */
    @Override
    public String getDirectory() {
        return GsonUtility.optStringValue(this.requestParameterJson,"dir", null);
    }
    /**
	 * serveCachedContent(HttpServletRequest request, HttpServletResponse response, Object rawObject)
	 * this method tells whether the ResultSet(rawObject) content is assigned or not.
	 * @param request   HttpServletRequest object
	 * @param response  HttpServletResponse response object.
	 * @param rawObject query result in Object format, set this value to result(jsonArray).
	 * {@return True if ResultSet is not null} {@code false} if ResultSet is null.
	 */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response, Object rawObject) {
        JsonObject object = (JsonObject) rawObject;
        boolean flag = false;
        if (object != null && object.size() > 0) {
            this.result = object.getAsJsonArray("data");
            flag = true;
        }
        return flag;
    }

}
