package com.helicalinsight.cache.manager;

import com.google.gson.Gson;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.cache.service.CacheService;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.rowset.RowSetProvider;

/**
 * Created by author on 5/5/2020.
 * responsible for managing cache for hcr which extends <p>{@link CacheManager}</p>
 * The {@code @Component} annotation is used to mark a class as a Spring bean/component.
 * {@code @Scope("prototype")} new instance of the component will be created each time.
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class HCRQueryProcessCacheManagerForResultSet extends CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(HCRQueryProcessCacheManagerForResultSet.class);
    private static ApplicationProperties applicationProperties;
    private static Map<String, String> settingsDataSourcesMap = new HashMap<>();
    private static JsonObject settingFileAsJson;
    private static String solutionDirectory;
    private JsonObject requestParameterJson;
    private JsonObject efwdFileAsJson;
    private Integer mapId;
    private String efwdFile;
    private EnhancedQueryExecutor queryExecutor;
    private ResultSet result;
    private JsonObject cacheFormData;
    private JsonObject targetFile;
    /**
     * getResult()
     * @return it returns the ResultSet from hcr datasource
     */
    public ResultSet getResult() {
        return result;
    }
    /**
	 * init()
	 * Initializes the content.
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
	 * setRequestData(String data)
	 *
	 *  setRequestData(String data)
	 * @param data 	  its formData string provides connection details of hcr datasource
	 */
    @Override
    public void setRequestData(String data) {
    	JsonObject formData = new Gson().fromJson(data,JsonObject.class);
        requestParameterJson = formData.getAsJsonObject("connectionDetails");
        this.cacheFormData = formData;
		if (formData.has("targetFile")) {
	        targetFile=formData.getAsJsonObject("targetFile");
			requestParameterJson.addProperty("dir", targetFile.get("dir").getAsString());
			requestParameterJson.addProperty("uuid", targetFile.get("file").getAsString());
		}
    }

    @Override
    public String getRequestData() {
        return null;
    }

    /**
	 * setSettingsDataSourcesMap()
	 * 
	 * This method stores xml data in memory.
	 * 
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
	 * getConnectionFilePath()
	 *
	 * responsible to get the hcr dataSource related connection details and execute
     * query.
	 *
	 * {@return A json string, which is the query execution result}{@code EMPTY} if requestParameterJson is empty.
	 */
    @Override
    public String getConnectionFilePath() {
        if (!requestParameterJson.entrySet().isEmpty()) {
        	String filePath;
            if (requestParameterJson.has("temp_uuid")) {
                String dir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
                String file = requestParameterJson.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension();
                //requestParameterJson.put("dir", dir);
                JsonObject efwd = new JsonObject();
                efwd.addProperty("file", file);
                requestParameterJson.add("efwd", efwd);
                queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(),applicationProperties);
                filePath=dir+"/"+file;
                efwdFile = filePath;
                efwdFileAsJson = queryExecutor.newReadTempEFWD(dir, file);
                
            } else {
            	JsonObject efwdJson = GsonUtility.optJsonObject(this.requestParameterJson,"efwd");
                if(efwdJson==null) return "EMPTY";
                String outerDir = this.requestParameterJson.get("dir").getAsString();
                String file = null;
                if (efwdJson != null) {
                	file = GsonUtility.optString(efwdJson,"file");
                    String dir = GsonUtility.optString(efwdJson,"dir");
                    if (dir != null && !dir.isEmpty()) {
                        outerDir = dir;
                    }
                }
                queryExecutor = new EnhancedQueryExecutor(requestParameterJson.toString(),applicationProperties);
                filePath=targetFile.get("dir").getAsString()+"/"+targetFile.get("file").getAsString();
                efwdFileAsJson = queryExecutor.newReadEFWD(outerDir, file, solutionDirectory);
            }

            efwdFile = efwdFileAsJson.get("_efwdFileName_").getAsString();
            /*ResourceValidator resourceValidator = new ResourceValidator(efwdFileAsJson);
            boolean isValidEFWD = resourceValidator.validateEfwd();
            if (isValidEFWD) {
                int length = (solutionDirectory + File.separator).length();
                return efwdFile.substring(length);
            } else {
                return null;
            }*/
        	return filePath;
        }
        return "EMPTY";
    }
    /**
	 * getConnectionId()
	 * 
	 * this method is responsible for getting connection id from efwdFileAsJson(JsonObject)/datasource
	 * for that requestParameterJson should not be empty
	 * 
	 *{ @return id in long value}{@code -404l} if requestParameterJson is empty
	 */
    @Override
    public Long getConnectionId() {
        logger.info("RequestParameterJson is " + requestParameterJson);
        this.mapId = -404;
        if (!requestParameterJson.entrySet().isEmpty() && requestParameterJson.has("map_id")) {
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
                    if (mapId == dataMapTag.get("id").getAsInt()) {
                        this.mapId = mapId;
                        // dataMapTagContent = (JSONObject) dataMapsArray.get(counter);
                        connectionId = dataMapTag.get("connection").getAsInt();
                        break;
                    }
                }

            }

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
        return -404l;
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
	 * {@return type in string format}{@code "JUST_TO_IGNORE"} if connectionId null or zero and efwdFileAsJson is null
	 */
    @Override
    public String getConnectionType(Long connectionId) {
        String type = "JUST_TO_IGNORE";
        if (connectionId != null && connectionId != 0 && efwdFileAsJson != null) {
        	JsonObject datasource = efwdFileAsJson.getAsJsonObject("DataSources");
        	JsonObject connection = datasource.getAsJsonObject("Connection");
                if (connectionId == connection.get("id").getAsInt()) {
                    //  connectionDetails = connection;
                    type =   connection.get("type").getAsString();
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
	 *{@return query in string format}{@code "JUST_TO_IGNORE"} if requestParameterJson is empty.
	 */
    @Override
    public String getQuery(String connectionType) {
        String query = "JUST_TO_IGNORE";
        if (!requestParameterJson.entrySet().isEmpty() && queryExecutor!=null) {
            if (requestParameterJson.has("temp_uuid")) {
                query = queryExecutor.getQueryFromTemp();
            } else {
                query = queryExecutor.getQuery();
            }
        }
        return query;
        // return SplitterUtils.prepareServiceId(requestParameterJson.toString() + cacheFormData + query);
    }
    /**
	 * getDataFromDatabase(String query)
	 * 
	 * this method is responsible for providing ResultSet from hcr data.
	 * 
	 * @param query    query from hcr dataSource
	 *{@return  returns jsonObject of query result}{@code "{}"} if requestParameterJson object is empty.
	 */
    @Override
    public ResultSet getDataFromDatabase(String query) {
        ResultSet resultSet = null;
        if (!requestParameterJson.entrySet().isEmpty() && queryExecutor!=null) {
            if (requestParameterJson.has("temp_uuid")) {
                resultSet = queryExecutor.getResultSetDataFromTemp();
            } else {
                resultSet = queryExecutor.getResultSetData();
            }
        }
        // FIXME : This is a temporary fix, we must fix the cache mechanism for sub datasets.
        try {
			return resultSet == null ? RowSetProvider.newFactory().createCachedRowSet() : resultSet;
		} catch (SQLException e) {
			throw new EfwServiceException(e.getMessage());
		}
    }
    
    @Override
    public void streamDataFromDatabase(String query, CallBack<ResultSet> callBack) {
        if (!requestParameterJson.entrySet().isEmpty() && queryExecutor!=null) {
            if (requestParameterJson.has("temp_uuid")) {
                queryExecutor.streamResultFromTemp(callBack);
            } 
        }
        else {
        	// TODO:  Temporary fix
        	try {
				callBack.process(RowSetProvider.newFactory().createCachedRowSet());
			} catch (SQLException e) {
				throw new EfwServiceException(e.getMessage());
			}
        }
    }
    
    
    /**
     * getDirectory()
     * @return  it returns directory in string format from requestParameterJson(JsonObject)
     */
    @Override
    public String getDirectory() {

        if(this.requestParameterJson.has("temp_uuid")) {

            String connectionId = this.requestParameterJson.get("temp_uuid").getAsString();
            if(connectionId.contains("hi_hcr_db")) {
                connectionId = connectionId.replace(".efwd", "").replace("hi_hcr_db", "");

                EFWDConnectionService efwdConnectionService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
                HIHcrConnections item = efwdConnectionService.fetchHIHcrConnectionsById(Integer.valueOf(connectionId));
                if (item != null) {
                    String resourceUrl = item.getHiResourceHcr().getResourceURL();
                    String resourcePath = item.getHiResourceHcr().getResourcePath() + ".hcr";
                    String dir = resourceUrl.replace(resourcePath, "");
                    return dir;
                }
            }
        }
    	return GsonUtility.optStringValue(this.requestParameterJson,"dir", null);
    }
    /**
	 * serveCachedContent(HttpServletRequest request, HttpServletResponse response, Object rawObject)
	 * this method tells whether the ResultSet(rawObject) content is assigned or not.
	 * @param request   HttpServletRequest object
	 * @param response  HttpServletResponse object.
	 * @param rawObject ResultSet value in Object format.
	 * {@return True if ResultSet is not null} {@code false} if ResultSet is null.
	 */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response, Object rawObject) {
        ResultSet object = (ResultSet) rawObject;
        boolean flag = false;
        try {
            if (object != null) {
                this.result = object;
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
	 * saveToDisk(Cache requestCache, String directory, Object jsonData)
	 * ResultSet data generated by hcr would be stored in disk. 
	 * The data is saved in a temporary file and the cache is updated with the file path,
	 * file timestamp, expiry time, and file size. 
	 * @param requestCache cache object to store data. metadata information about hcr
	 * @param directory    optional. defaulted to TEMP_DIRECTORY
	 * @param jsonData     ResultSet value in Json Format
	 */
    @Override
    public void saveToDisk(Cache requestCache, String directory, Object jsonData) {
        CacheService cacheService = ApplicationContextAccessor.getBean(CacheService.class);
        String cacheUUID = UUID.randomUUID().toString();
        String s= this.cacheFormData.has("dir")?this.cacheFormData.get("dir").getAsString():null;
        if (directory == null && s==null) {
            directory = "TEMP_DIRECTORY";
        }
        if(s!=null){
            directory=s;
        }
        final String finalDirectory = directory;
        boolean saved = true;
        try {
            String cacheFilePath =  requestCache.getCacheFilePath() != null ? requestCache.getCacheFilePath() :  finalDirectory + File.separator + cacheUUID + "." + CacheUtils.getCacheExtension();
            String cacheDirectory = CacheUtils.getCacheDirectory();
            String cacheFileSavePath;
            requestCache.setCacheFilePath(cacheFilePath);
            if (logger.isInfoEnabled()) {
                logger.info("File created with the file name " + cacheFilePath);
            }
            ResultSet resultSet = (ResultSet) jsonData;

            cacheFileSavePath = cacheDirectory + File.separator + cacheFilePath;
            CacheUtils.saveFileToDisk(resultSet, cacheFileSavePath);

            File file = new File(cacheFileSavePath);
            requestCache.setCacheFileTimeStamp(new Date(file.lastModified()));
            requestCache.setCacheExpiryTime(new Date(CacheUtils.getActualCacheExpireDuration()));
            requestCache.setCacheFileSize(file.length());
            cacheService.editCache(requestCache);
        } catch (Exception ex) {
            saved = false;
            logger.error("Exception occurred ", ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (!saved) {
                cacheService.deleteCache(requestCache.getCacheId());
            }
        }
    }
    /**
     * readFileContent(String filePath) 
     * responsible for query result data to store in ResultSet.
     * @param filepath     query result data
     * @return			   ResultSet
     * @throws IOException
     */
    @Override
    public ResultSet readFileContent(String filePath) throws IOException {
        ResultSet resultSet = null;
        InputStream buffer = new BufferedInputStream(new FileInputStream(filePath));
        try (ObjectInput input = new ObjectInputStream(buffer)) {
            resultSet = (ResultSet) input.readObject();
        } catch (ClassNotFoundException ex) {
            logger.error("The class not found ", ex);
        } finally {
            buffer.close();
        }
        return resultSet;
    }
    
    
}
