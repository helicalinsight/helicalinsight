package com.helicalinsight.datasource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import  com.helicalinsight.efw.utility.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.helicalinsight.efw.utility.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.callback.CallBack;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.DuplicateEfwdException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.efw.validator.ResourceValidator;

/**
 * EnhancedQueryExecutor class extends {@link QueryExecutor}
 * This class is responsible to get the dataSource related details and execute
 * query and get result.
 * Created by author on 11/18/2019.
 * @author Rajesh
 */
public class EnhancedQueryExecutor extends QueryExecutor {
    public static final String DATAMAPTAGCONTENT = "DATAMAPTAGCONTENT";
    public static final String CONNECTION_PART = "CONNECTION_PART";
    public static final String QUERY = "QUERY";
    public static final String RESULT = "RESULT";
    public static final String RESULTSET = "RESULTSET";
    public static final String STREAM = "STREAM";
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);
    private JsonObject settings;
    private final HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
    private EFWDConnectionService efwdService=ApplicationContextAccessor.getBean(EFWDConnectionService.class);

    public EnhancedQueryExecutor(String data, ApplicationProperties applicationProperties) {
        super(data, applicationProperties);
        this.settings = JsonUtils.newGetSettingsJson();

    }
    /**
     * getDataMapTagContent
     * @return data map tag in jsonObject format.
     */
    public JsonObject getDataMapTagContent() {
        return (JsonObject) getResultOrQuery(DATAMAPTAGCONTENT);
    }

    /**
     * getConnectionPart() using gson
     * @return connection details in jsonObject form.
     */
    public JsonObject getConnectionPart() {
        return (JsonObject) getResultOrQuery(CONNECTION_PART);
    }
    /**
     * getQuery()
     * @return  sql Query in string format.
     */
    public String getQuery() {
        return (String) getResultOrQuery(QUERY);
    }
    /**
     * getQueryFromTemp()
     * @return sql query for Temp dataSource
     */
    public String getQueryFromTemp() {
        return (String) getResultOrQueryFromTemp(QUERY, null);
    }
    
    public void streamResultFromTemp(CallBack<ResultSet> callBack) {
    	getResultOrQueryFromTemp(STREAM, callBack);
    }
    /**
     * getUnProcessedQueryFromTemp()
     * @return sql query.
     */
    public String getUnProcessedQueryFromTemp() {
        return (String) getResultOrQueryFromTemp(QUERY, null);
    }



    /**
     * getConnectionPartFromTemp() using gson
     * @return JsonObject of connection details of dataSource
     */
    public JsonObject getConnectionPartFromTemp() {
        return (JsonObject) getResultOrQueryFromTemp(CONNECTION_PART, null);
    }
    /**
     * getDataMapTagContentFromTemp()
     * @return data map tag content in jsonObject format
     */
    public JsonObject getDataMapTagContentFromTemp() {
        return (JsonObject) getResultOrQueryFromTemp(DATAMAPTAGCONTENT, null);
    }
    /**
     * getResultSet()
     * @return query result.
     */
    public JsonObject getResultSet() {
        return (JsonObject) getResultOrQuery(RESULT);
    }
    /**
     * getResultSetData()
     * @return {@code java.sql.ResultSet}
     */
    public ResultSet getResultSetData() {
        return (ResultSet) getResultOrQuery(RESULTSET);
    }
    /**
     * getResultOrQuery(String option)
     * Returns a result or query based on the specified option.
     * @param option        option to determine the type of data to get like, {"RESULT","QUERY","RESULTSET"}
     * @return the result based on the specified option.
     */
    public Object getResultOrQuery(String option) {
        JsonObject requestParameterJson = new Gson().fromJson(data,JsonObject.class);
        String directory = requestParameterJson.get("dir").getAsString();
        int mapId = requestParameterJson.get("map_id").getAsInt();
        String file = null;
        JsonObject efwdJson = GsonUtility.optJsonObject(requestParameterJson, "efwd");
        if (efwdJson != null && !efwdJson.entrySet().isEmpty()) {
            file = GsonUtility.optString(efwdJson, "file");
            String dir = GsonUtility.optString(efwdJson, "dir");
            if (dir != null && !dir.isEmpty()) {
                directory = dir;
            }
        }

        validateRequestParameter(directory, mapId);

        /*String solutionDirectory = applicationProperties.getSolutionDirectory();
        JSONObject fileAsJson = readEFWD(directory, file, solutionDirectory);

        ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
        boolean isValidEFWD = resourceValidator.validateEfwd();
        if (!isValidEFWD) {
            throw new ImproperDataMapConfigurationException("Duplicate DataMap id is found " + "the in Efwd file.");
        }*/

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The map id from the http request is %s.", mapId));
        }
        JsonObject dataMapTagContent;
        String efwd;
        if(requestParameterJson.has("efwd")) {
            efwd=requestParameterJson.getAsJsonObject("efwd").toString();
            dataMapTagContent= getDataMaps(efwd,mapId);
        }else {
            HIResource hiResource = serviceDB.getResourceByUrl(directory+"/"+requestParameterJson.get("uuid").getAsString());
            List<HIHcrConnections> hcrConnections=efwdService.fetchAllHcrConnectionsByResourceId(hiResource.getResourceId());
            efwd=com.helicalinsight.datasource.managed.JsonUtils.prepareEfwJsonByHcr(hcrConnections).toString();
            dataMapTagContent= getDataMaps(efwd,mapId);
        }
        Integer connectionId = dataMapTagContent.get("connection").getAsInt();
        JsonObject connectionDetails = getDataSource(connectionId,efwd);
        String type = connectionDetails.get("type").getAsString();
        IDriver driverObject = getIDriverImpl(type);

        Object genericReturn = null;
        switch (option) {
            case RESULT:
                genericReturn = getJsonFromDriver(requestParameterJson, dataMapTagContent, connectionDetails, driverObject);
                break;
            case QUERY:
                genericReturn = getQueryFromDriver(requestParameterJson, dataMapTagContent, driverObject);
                break;
            case CONNECTION_PART:
                genericReturn = connectionDetails;
                break;
            case DATAMAPTAGCONTENT:
                genericReturn = dataMapTagContent;
                break;
            case RESULTSET:
                genericReturn = getResultSetFromDriver(requestParameterJson, dataMapTagContent, connectionDetails, driverObject);
                break;
        }
        return genericReturn;
    }
    /**
     * getDataSource(Integer connectionId, String efwdDetails)
     * @param connectionId              id in int form
     * @param efwdDetails               EFWD details containing connection information.
     * @return  connection details in jsonObject format
     */
    private JsonObject getDataSource(Integer connectionId, String efwdDetails) {
        JsonObject connectionDetails = null;
        JsonObject dataObject =  new Gson().fromJson(efwdDetails, JsonObject.class);
        if(dataObject.has("efwd"))
            dataObject= dataObject.get("efwd").getAsJsonObject();
        dataObject=dataObject.getAsJsonObject("dataSources");
        JsonArray dataSources = GsonUtility.optJsonArray(dataObject, "connections");
        for (int counter = 0; counter < dataSources.size(); counter++) {
            JsonObject connection = dataSources.get(counter).getAsJsonObject().getAsJsonObject("connection");
            //JsonObject connection = dataSources.get(counter).getAsJsonObject();
            if (connectionId == connection.get("id").getAsInt()) {
                connectionDetails = connection;
                break;
            }
        }
        return connectionDetails;
    }
    /**
     * getDataMaps(String efwdDetails,int mapId)
     * This method help to get data map tag content in jsonObject format.
     * @param efwdDetails           EFWD details containing dataMap information
     * @param mapId                 map id
     * @return data map content in json object form.
     */
    private JsonObject getDataMaps(String efwdDetails,int mapId) {

        JsonObject dataMapTagContent = null;
        int connectionId = 0;
        JsonObject dataObject =   new Gson().fromJson(efwdDetails,JsonObject.class);
        if(dataObject.has("efwd"))
            dataObject= dataObject.get("efwd").getAsJsonObject();
        JsonArray dataMapsArray = GsonUtility.optJsonArray(dataObject, "dataMaps");
        for (int counter = 0; counter < dataMapsArray.size(); counter++) {
            JsonObject dataMapTag = dataMapsArray.get(counter).getAsJsonObject().getAsJsonObject("dataMap");
            //JsonObject dataMapTag = dataMapsArray.get(counter).getAsJsonObject();
            if (mapId == dataMapTag.get("id").getAsInt()) {
                dataMapTagContent = dataMapTag;
                connectionId = dataMapTag.get("connection").getAsInt();
                break;
            }
        }

        if (connectionId == 0) {
            throw new EfwdServiceException(
                    "The mapId " + mapId + " is not found in the " + "respective efwd resource. Invalid request.");
        }
        return dataMapTagContent;
    }
    /**
     * getResultSetFromDriver(JSONObject requestParameterJson, JSONObject dataMapTagContent,
     *  JSONObject connectionDetails, IDriver driverObject)
     * @param requestParameterJson			parameter json containing request details.
     * @param dataMapTagContent			    data map content
     * @param connectionDetails				connection details
     * @param driverObject					driver instance to get ResultSet
     * @return  {@code java.sql.ResultSet} in object form .
     * @throws ConfigurationException if there is an issue with the driver configuration.
     */
    private Object getResultSetFromDriver(JsonObject requestParameterJson, JsonObject dataMapTagContent, JsonObject connectionDetails, IDriver driverObject) {
        if (driverObject != null) {
            return driverObject.getResultSetData(requestParameterJson, connectionDetails, dataMapTagContent,
                    applicationProperties);

        } else {
            logger.error("The IDriver object is null. Probably class not found or configured in setting" + ""
                    + ".xml for the DataMap type in setting.xml");
            throw new ConfigurationException("Couldn't instantiate IDriver. Unable to execute datasource.");
        }
    }
    
    private void streamResultSetFromDriver(JsonObject requestParameterJson, JsonObject dataMapTagContent, JsonObject connectionDetails, IDriver driverObject, CallBack<ResultSet> callBack) {
        if (driverObject != null) {
             driverObject.streamResultSetData(requestParameterJson, connectionDetails, dataMapTagContent,
                    applicationProperties, callBack);

        } else {
            logger.error("The IDriver object is null. Probably class not found or configured in setting" + ""
                    + ".xml for the DataMap type in setting.xml");
            throw new ConfigurationException("Couldn't instantiate IDriver. Unable to execute datasource.");
        }
    }
    
    
    /**
     * getJsonFromDriver(JSONObject requestParameterJson, JSONObject dataMapTagContent,
     *  JSONObject connectionDetails, IDriver driverObject)
     * @param requestParameterJson			parameter json containing request details.
     * @param dataMapTagContent			    data map content
     * @param connectionDetails				connection details
     * @param driverObject					Driver instance used to fetch json data.
     * @return  {@code java.sql.ResultSet} in object form .
     * @throws ConfigurationException if there is an issue with the driver configuration.
     */
    private JsonObject getJsonFromDriver(JsonObject requestParameterJson, JsonObject dataMapTagContent, JsonObject connectionDetails, IDriver driverObject) {
        if (driverObject != null) {
            return driverObject.getJSONData(requestParameterJson, connectionDetails, dataMapTagContent,
                    applicationProperties);

        } else {
            logger.error("The IDriver object is null. Probably class not found or configured in setting" + ""
                    + ".xml for the DataMap type in setting.xml");
            throw new ConfigurationException("Couldn't instantiate IDriver. Unable to execute datasource.");
        }
    }
    /**
     * getQueryFromDriver(JSONObject requestParameterJson, JSONObject dataMapTagContent, IDriver driverObject)
     * @param requestParameterJson			parameter json containing request details.
     * @param dataMapTagContent				data map content
     * @param driverObject					Driver instance to generate query
     * @return sql query .
     */
    private String getQueryFromDriver(JsonObject requestParameterJson, JsonObject dataMapTagContent, IDriver driverObject) {
        if (driverObject != null) {

            return driverObject.getQuery(dataMapTagContent, requestParameterJson);

        } else {
            logger.error("The IDriver object is null. Probably class not found or configured in setting" + ""
                    + ".xml for the DataMap type in setting.xml");
            throw new ConfigurationException("Couldn't instantiate IDriver. Unable to execute datasource.");
        }
    }
    /**
     * getResultSet(Connection connection, String solutionDirectory)
     * @param connection				 Database connection to execute the query.
     * @param solutionDirectory          directory of efwd file
     * @return ResultSet in jsonObject format.
     */
    public JsonObject getResultSet(Connection connection, String solutionDirectory) {

        JsonObject requestParameterJson = new Gson().fromJson(data,JsonObject.class);

        int mapId = requestParameterJson.get("map_id").getAsInt();
        String directory = requestParameterJson.get("dir").getAsString();
        String file = requestParameterJson.get("file").getAsString();
        JsonObject fileAsJson = newReadEFWD(directory, file, solutionDirectory);
        JsonObject dataMapTagContent = getDataMapTagContent(fileAsJson, mapId);
        String type = dataMapTagContent.get("type").getAsString();

        IDataMap dataMap = null;
        dataMap = getDataMapObj(type, dataMap);
        return dataMap.getResultSet(dataMapTagContent, requestParameterJson, connection);

    }
    /**
     * getResultSetFromTemp()
     * @return Database result in jsonObject format.
     */
    public JsonObject getResultSetFromTemp() {
        return (JsonObject) getResultOrQueryFromTemp(RESULT, null);
    }
    /**
     * getResultSetDataFromTemp()
     * @return Result in the form {@code java.sql.ResultSet}
     */
    public ResultSet getResultSetDataFromTemp() {
        return (ResultSet) getResultOrQueryFromTemp(RESULTSET, null);
    }
    /**
     * getResultOrQueryFromTemp(String option)
     * Returns a result, query, or result set based on the specified option from a temporary data source.
     * @param option        option to determine the type of data to get like, {"RESULT","QUERY","RESULTSET"}
     * @return result, query, or result set based on the specified option.
     */
    private Object getResultOrQueryFromTemp(String option, CallBack<ResultSet> callBack) {
        JsonObject requestParameterJson = new Gson().fromJson(data, JsonObject.class);
        // EFWD directory name
        String tempPath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
        String directory = GsonUtility.optStringValue(requestParameterJson, "dir", tempPath);
        int mapId = requestParameterJson.get("map_id").getAsInt();
        String file = null;
        JsonObject efwdJson = GsonUtility.optJsonObject(requestParameterJson, "efwd");
        if (efwdJson != null && !efwdJson.entrySet().isEmpty()) {
            file = GsonUtility.optString(efwdJson, "file");
            String dir =  GsonUtility.optString(efwdJson, "dir");
            if (dir != null && !dir.isEmpty()) {
                directory = dir;
            }
        }
        validateRequestParameter(directory, mapId);
        JsonObject fileAsJson = newReadTempEFWD(directory, file);

        ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
        boolean isValidEFWD = resourceValidator.newValidateEfwd();
        if (!isValidEFWD) {
            throw new ImproperDataMapConfigurationException("Duplicate DataMap id is found " + "the in Efwd file.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The map id from the http request is %s.", mapId));
        }
        JsonObject dataMapTagContent = getDataMapTagContent(fileAsJson, mapId);
        Integer connectionId = dataMapTagContent.get("connection").getAsInt();

        JsonObject connectionDetails = getDatasourceList(fileAsJson, connectionId);
        if (connectionDetails == null) {
            throw new EfwServiceException("There is no data-source present in with provided connectionId :" + connectionId);
        }
        String type = connectionDetails.get("type").getAsString();


        String fileJson=fileAsJson.toString();
        if(fileJson.contains("efwdId")) {
            JsonObject connectionJson = fileAsJson.get("DataSources").getAsJsonObject().get("Connection").getAsJsonObject();
            String ids = connectionJson.get("efwdId").getAsString();
            String types = connectionJson.get("type").getAsString();
            // ObjectNode datas = EfwdDatasourceUtils.getContent(ids, types);
            ObjectNode datas = EfwdDatasourceUtils.getEfwdConnection(ids,types);
            if(option.equals(RESULT) || option.equals(RESULTSET)) {
                datas=EfwdDatasourceUtils.getContent(ids,types);
            }
            JsonNode data=null;
            if(datas.has("data")){
                String jsonData= datas.get("data").toString();
                ObjectMapper objectMapper = new ObjectMapper();

                // Convert JSON string to JsonNode
                try {
                    data = objectMapper.readTree(jsonData);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            }else{
                data=datas;
            }
            if(data.has("driverName") && data.get("driverName")!=null) {
                connectionDetails.addProperty("Driver", data.get("driverName").asText());
            }
            if(data.has("jdbcUrl") && data.get("jdbcUrl")!=null) {

                connectionDetails.addProperty("Url",data.get("jdbcUrl").asText());
            }
            if(data.has("username")&& data.get("username")!=null) {

                connectionDetails.addProperty("User",data.get("username").asText());
            }
            if(data.has("userName")&& data.get("userName")!=null) {

                connectionDetails.addProperty("User",data.get("userName").asText());
            }

            if(data.has("password") && data.get("password")!=null) {

                connectionDetails.addProperty("Pass",data.get("password").asText());
            }
            if(data.has("condition") && data.get("condition")!=null) {
                connectionDetails.addProperty("Condition",data.get("condition").asText());
            }


        }

        IDriver driverObject = getIDriverImpl(type);
        Object genericReturn = null;
        switch (option) {
            case RESULT:
                genericReturn = getJsonFromDriver(requestParameterJson, dataMapTagContent, connectionDetails, driverObject);
                break;
            case QUERY:
                genericReturn = getQueryFromDriver(requestParameterJson, dataMapTagContent, driverObject);
                break;
            case CONNECTION_PART:
                genericReturn = connectionDetails;
                break;
            case DATAMAPTAGCONTENT:
                genericReturn = dataMapTagContent;
                break;
            case RESULTSET:
                genericReturn = getResultSetFromDriver(requestParameterJson, dataMapTagContent, connectionDetails, driverObject);
                break;
            case STREAM:
            	streamResultSetFromDriver(requestParameterJson, dataMapTagContent, connectionDetails, driverObject, callBack);
            	break;
        }
        return genericReturn;
    }
    /**
     * getUnprocessedQueryFromTemp(String option)
     * Returns an unprocessed query from a temporary data source based on the specified option.
     *
     * @param option                 not used in this method.
     * @return the unprocessed query as a String.
     */
    private String getUnprocessedQueryFromTemp(String option) {
        JsonObject requestParameterJson = new Gson().fromJson(data,JsonObject.class);
        // EFWD directory name
        String directory = requestParameterJson.get("dir").getAsString();
        int mapId = requestParameterJson.get("map_id").getAsInt();
        String file = null;
        JsonObject efwdJson = GsonUtility.optJsonObject(requestParameterJson, "efwd");
        if (efwdJson != null && !efwdJson.entrySet().isEmpty()) {
            file = GsonUtility.optString(efwdJson, "file");
            String dir = GsonUtility.optString(efwdJson,"dir");
            if (dir != null && !dir.isEmpty()) {
                directory = dir;
            }
        }
        validateRequestParameter(directory, mapId);
        JsonObject fileAsJson = newReadTempEFWD(directory, file);

        ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
        boolean isValidEFWD = resourceValidator.validateEfwd();
        if (!isValidEFWD) {
            throw new ImproperDataMapConfigurationException("Duplicate DataMap id is found " + "the in Efwd file.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The map id from the http request is %s.", mapId));
        }
        String query = null;
        JsonObject dataMapTagContent = getDataMapTagContent(fileAsJson, mapId);
        if (dataMapTagContent.has("Query"))
            query = dataMapTagContent.get("Query").getAsString();
        return query;
    }
    /**
     * newReadTempEFWD(String directory, String file)  using gson
     * Reads the contents of a temporary EFWD file
     * @param directory                 directory of efwd file
     * @param file                     name of the EFWD file.
     * @return JsonObject containing the EFWD file contents.
     */
    public JsonObject newReadTempEFWD(String directory, String file) {
        if (file.replace("hi_hcr_db.efwd","").matches("\\d+")) {
            file=file.replace("hi_hcr_db.efwd","");
            JsonObject fileJson = HCRUtils.prepareConnectionJson(file);
            String efwdFile =  directory + File.separator + file;
            fileJson.addProperty("_efwdFileName_", efwdFile);
            return fileJson;
        }
        List<String> efwd = findEFWDFilesInTemp(directory, settings);

        // Only one efwd file per folder. So count is one
        if (efwd.size() > 1 && (file == null || file.isEmpty())) {
            throw new DuplicateEfwdException("There are more than one efwd files in the directory :" + directory + ". Please " + ""
                    + "provide efwd file name.");
        }
        logger.debug(efwd + "<-efwd   file->" + file);
        String efwdFile;

        if (file != null && !file.isEmpty()) {
            if (efwd.contains(file)) {
                efwdFile = directory + File.separator + file;
                logger.debug(efwdFile + " efwdfile");
            } else {
                throw new EfwServiceException("The file doesnot exist");
            }
        } else {
            efwdFile = directory + File.separator + efwd.get(0);

        }
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject fileAsJson = processor.getJsonObject(efwdFile, false);
        fileAsJson.addProperty("_efwdFileName_", efwdFile);
        return fileAsJson;
    }
    /**
     * getUnProcessedQuery()
     * Returns an unprocessed query based on the specified parameters in the request JSON.
     *
     * @return The unprocessed query as a String, or {@code null} if not found.
     * @throws ImproperDataMapConfigurationException If the EFWD file contains duplicate DataMap IDs.
     */
    public String getUnProcessedQuery() {
        JsonObject requestParameterJson = new Gson().fromJson(data,JsonObject.class);
        String directory = requestParameterJson.get("dir").getAsString();
        int mapId = requestParameterJson.get("map_id").getAsInt();
        String file = null;
        JsonObject efwdJson = GsonUtility.optJsonObject(requestParameterJson, "efwd");
        if (efwdJson != null && !efwdJson.entrySet().isEmpty()) {
            file =  GsonUtility.optString(efwdJson,"file");
            String dir =  GsonUtility.optString(efwdJson,"dir");
            if (dir != null && !dir.isEmpty()) {
                directory = dir;
            }
        }

        validateRequestParameter(directory, mapId);
        String solutionDirectory = applicationProperties.getSolutionDirectory();
        JsonObject fileAsJson = newReadEFWD(directory, file, solutionDirectory);

        ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
        boolean isValidEFWD = resourceValidator.validateEfwd();
        if (!isValidEFWD) {
            throw new ImproperDataMapConfigurationException("Duplicate DataMap id is found " + "the in Efwd file.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The map id from the http request is %s.", mapId));
        }

        JsonObject dataMapTagContent = getDataMapTagContent(fileAsJson, mapId);
        String query = null;
        if (dataMapTagContent.has("Query"))
            query = dataMapTagContent.get("Query").getAsString();
        return query;
    }
    /**
     * findEFWDFilesInTemp(String directory, JSONObject settings)
     * Searches for EFWD  files with the specified extension in the given directory.
     *
     * @param directory 		directory to search for EFWD files.
     * @param settings  		jsonObject containing file extension and efwd details.
     * @return A list of EFWD file names found in the directory.
     * @throws EfwServiceException If there are issues accessing or listing files in the directory.
     */
    private List<String> findEFWDFilesInTemp(String directory, JsonObject settings) {
        String extension = settings.getAsJsonObject("Extentions").get("efwd").getAsString();
        List<String> efwd = new ArrayList<>();
        // EFWD file
        File folder = new File(directory);
        logger.debug("directory  :" + directory);
        File[] listOfFiles = folder.listFiles();
        Assert.notNull(listOfFiles, "List of files is null. Directory has no content.");

        if (listOfFiles == null) {
            throw new EfwServiceException("The list of files is empty. Couldn't find any efwd.");
        }
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {

                String fileName = listOfFile.getName();

                String fileExtension = FilenameUtils.getExtension(fileName);
                logger.debug(" filenale :" + fileName + "  extension" + fileExtension);

                if ((fileExtension.equalsIgnoreCase(extension))) {
                    efwd.add(fileName);
                    logger.debug("adding the files to the list");
                }
            }
        }
        return efwd;
    }
}
