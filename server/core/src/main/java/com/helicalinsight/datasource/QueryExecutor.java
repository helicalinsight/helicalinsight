package com.helicalinsight.datasource;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.DuplicateEfwdException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.validator.ResourceValidator;



/**
 * QueryExecutor
 * This class is responsible to get the dataSource related details and execute
 * query and get result.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @author Avi
 * @since 1.0
 */
public class QueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

    protected final ApplicationProperties applicationProperties;

    protected final String data;

    protected JsonObject settings;

    // private int int1;

    /**
     * QueryExecutor
     * Sets the instance variables
     *
     * @param data                  The data from the http request object
     * @param applicationProperties The singleton instance of the application
     */
    public QueryExecutor(String data, ApplicationProperties applicationProperties) {
        this.data = data;
        this.applicationProperties = applicationProperties;
        this.settings = JsonUtils.newGetSettingsJson();
    }

    /**
     * <p>
     * This method is responsible to get the connection related details,
     * instantiate the DataSource class and get the final result in JSONObject
     * format.
     * <p/>
     * It reads the value of key efwd from setting.xml , if same name extension
     * is present in the directory then reads that file from resource directory.
     * If more than one file is present with that extension then it will throw
     * exception. Otherwise reads DataMap id and corresponding connection
     * details from the efwd file. The instantiated IDriver implementation will
     * return the json data in the form of JSONObject that will be sent to the
     * browser for rendering.
     * </p>
     *
     * @return A json string, which is the query execution result
     */

	/**
	 * newReadEFWD(String directory, String file, String solutionDirectory)
	 * Reads an EFWD  file in JSON format, given the directory, file name, and solution directory.
	 * 
	 * @param directory              directory where the EFWD file is located.
	 * @param file                   name of the EFWD file to read. If null or empty, the first EFWD file in the directory is read.
	 * @param solutionDirectory      base directory of the solution.
	 * @return A JSON object representing the content of the EFWD file.
	 * @throws DuplicateEfwdException If there are multiple EFWD files in the directory and no specific file is provided.
	 * @throws EfwServiceException   If the specified EFWD file does not exist.
	 */
	public JsonObject newReadEFWD(String directory, String file, String solutionDirectory) {

        List<String> efwd = findEFWDFiles(directory, solutionDirectory, settings);

        // Only one efwd file per folder. So count is one
        if (efwd.size() > 1 && (file == null || file.isEmpty())) {
            throw new DuplicateEfwdException("There are more than one efwd files in the directory :" + directory + ". Please " + ""
                    + "provide efwd file name.");
        }
        logger.debug(efwd + "<-efwd   file->" + file);
        String efwdFile;

        if (file != null && !file.isEmpty()) {
            if (efwd.contains(file)) {
                efwdFile = solutionDirectory + File.separator + directory + File.separator + file;
                logger.debug(efwdFile + " efwdfile");
            } else {
                throw new EfwServiceException("The file doesnot exist");
            }
        } else {
            efwdFile = solutionDirectory + File.separator + directory + File.separator + efwd.get(0);

        }
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject fileAsJson = processor.getJsonObject(efwdFile, false);
        fileAsJson.addProperty("_efwdFileName_", efwdFile);
        return fileAsJson;
    }
	/**
	 * getResultSet()
	 * Retrieves a JSON object containing the result set of a data query based on the provided parameters.
	 * The method reads request parameters, validates them, and fetches data from the configured data source.
	 *
	 * @return A JSON object representing the result set of the data query.
	 * @throws ImproperDataMapConfigurationException If a duplicate DataMap id is found in the Efwd file.
	 * @throws ConfigurationException                If there are configuration issues with the data source or driver.
	 */
    public JsonObject getResultSet() {
        JsonObject requestParameterJson = new Gson().fromJson(data, JsonObject.class);
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
        Integer connectionId = dataMapTagContent.get("@connection").getAsInt();

        JsonObject connectionDetails = getDatasourceList(fileAsJson, connectionId);

        String type = connectionDetails.get("@type").getAsString();
        IDriver driverObject = getIDriverImpl(type);

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
     * getIDriverImpl(String type)
     * @param type 				type of the datasource for which an IDriver implementation is needed.
     * @return An implementation of the IDriver interface corresponding to the specified datasource type.
     */
    protected IDriver getIDriverImpl(String type) {
        IDriver driverObject = null;
        JsonArray settingsDataSources = settings.getAsJsonObject("DataSources").getAsJsonArray("DataSource");

        for (int counter = 0; counter < settingsDataSources.size(); counter++) {
            JsonObject settingsDataSource = settingsDataSources.get(counter).getAsJsonObject();
            String dataSourceType = settingsDataSource.get("type").getAsString();
            if (type.equalsIgnoreCase(dataSourceType)) {
                String clazz = settingsDataSource.get("class").getAsString();
                driverObject = (IDriver) FactoryMethodWrapper.getUntypedInstance(clazz);
                break;
            }
        }
        return driverObject;
    }
    /**
     * getDatasourceList(JsonObject fileAsJson, Integer connectionId)
     * Returns the details of a datasource from the provided EFWD file JSON object based on the connection ID.
     *
     * @param fileAsJson   		JSON object representing the EFWD file.
     * @param connectionId 		ID of the connection for which datasource details are to be retrieved.
     * @return The JSON object containing the details of the datasource corresponding to the given connection ID.
     */
    protected JsonObject getDatasourceList(JsonObject fileAsJson, Integer connectionId) {
        JsonObject connectionDetails = null;

        JsonObject dataSources = fileAsJson.getAsJsonObject("DataSources");
        
            JsonObject connection = dataSources.getAsJsonObject("Connection");
            if (connectionId == connection.get("id").getAsInt()) {
                connectionDetails = connection;
            }
      
        return connectionDetails;
    }
    /**
     * findEFWDFiles(String directory, String solutionDirectory, JsonObject settings)
     * Finds EFWD (Efwd) files within a specified directory.
     * @param directory                    directory where EFWD files are to be searched.
     * @param solutionDirectory            base directory 
     * @param settings					   settings containing file extensions information.
     * @return A list of EFWD file names found within the specified directory.
     * @throws EfwServiceException If there was an issue with the EFWD files or if the list of files is empty.
     */
    private List<String> findEFWDFiles(String directory, String solutionDirectory, JsonObject settings) {
        String extension = settings.getAsJsonObject("Extentions").get("efwd").getAsString();
        List<String> efwd = new ArrayList<>();
        // EFWD file
        File folder = new File(solutionDirectory + File.separator + directory);
        logger.debug("directory  :" + solutionDirectory + File.separator + directory);
        File[] listOfFiles = folder.listFiles();
        Assert.notNull(listOfFiles, "List of files is null. Directory has no content.");

        if (listOfFiles == null) {
            throw new EfwServiceException("The list of files is empty. Couldn't find any efwd.");
        }
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {

                String fileName = listOfFile.getName();

                String fileExtension = FilenameUtils.getExtension(fileName);
                logger.debug(" filename :" + fileName + "  extension" + fileExtension);

                if ((fileExtension.equalsIgnoreCase(extension))) {
                    efwd.add(fileName);
                    logger.debug("adding the files to the list");
                }
            }
        }
        return efwd;
    }
    /**
     * validateRequestParameter(String directory, int mapId)
     * Validates the request parameters to ensure that 'directory' and 'mapId' are not null or empty.
     * @param directory 	 directory 
     * @param mapId     	 mapId 
     * @throws IllegalArgumentException If 'directory' or 'mapId' is null or empty.
     */
    protected void validateRequestParameter(String directory, int mapId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", directory);
        parameters.put("map_id", "" + mapId);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }

	
    /**
     * getDataMapTagContent(JsonObject fileAsJson, int mapId)
     * Retrieves the content of a specific DataMap tag identified by its mapId from an EFWD configuration JSON.
     *
     * @param fileAsJson 		 EFWD configuration JSON object.
     * @param mapId      	     identifier of the DataMap tag to retrieve.
     * @return A JsonObject containing the content of the specified DataMap tag.
     * @throws EfwdServiceException If the specified mapId is not found in the EFWD resource.
     */
    protected JsonObject getDataMapTagContent(JsonObject fileAsJson, int mapId) {

        
    	JsonObject dataMapTagContent = null;
        int connectionId = 0;
        JsonElement jsonElement = fileAsJson.getAsJsonObject("DataMaps").get("DataMap");
        if(jsonElement.isJsonObject()) {
        	JsonObject dataMap = jsonElement.getAsJsonObject();
        	if (mapId == dataMap.get("id").getAsInt()) {
                dataMapTagContent = dataMap;
                connectionId = dataMap.get("connection").getAsInt();
            }
        }
        if(jsonElement.isJsonArray()) {
        	JsonArray dataMapsArray = fileAsJson.getAsJsonObject("DataMaps").getAsJsonArray("DataMap");
            for (int counter = 0; counter < dataMapsArray.size(); counter++) {
                JsonObject dataMapTag = dataMapsArray.get(counter).getAsJsonObject();
                if (mapId == dataMapTag.get("id").getAsInt()) {
                    dataMapTagContent = dataMapTag;
                    connectionId = dataMapTag.get("connection").getAsInt();
                    break;
                }
            }	
        }
        
        if (connectionId == 0) {
            throw new EfwdServiceException(
                    "The mapId " + mapId + " is not found in the " + "respective efwd resource. Invalid request.");
        }
        return dataMapTagContent;
    }
    /**
     * getResultSet(Connection connection, String solutionDirectory)
     * Returns a JSON result set using a specified IDataMap implementation and connection.
     *
     * @param connection             database connection to use for the query.
     * @param solutionDirectory      directory of the solution.
     * @return A JsonObject containing the result set.
     * @throws JSONException     If there is an issue with JSON parsing.
     * @throws EfwdServiceException If there is an issue with the EFWD configuration.
     * @throws SQLException      If there is an issue with the database query.
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
     * getDataMapObj(String efwdDataMapType, IDataMap dataMap)
     * This method will read the DataMaps tag from the settings.xml then reads
     * each DataMap tag and returns the matching class Name which is the
     * implementation of the IDataMap.
     *
     * @param efwdDataMapType     data map type  
     * @param dataMap             instance of IDataMap
     * @return instance of an IDataMap implementation that matches the specified data map type.
	 * @throws EfwdServiceException If the specified data map type is not configured in the settings.xml file.
     */
    protected IDataMap getDataMapObj(String efwdDataMapType, IDataMap dataMap) {
        JsonArray settingsDataMapArray = settings.getAsJsonObject("DataMaps").getAsJsonArray("DataMap");

        for (int counter = 0; counter < settingsDataMapArray.size(); counter++) {
            JsonObject settingsDataMap = settingsDataMapArray.get(counter).getAsJsonObject();
            String dataMapType = settingsDataMap.get("type").getAsString();
            if (efwdDataMapType.equalsIgnoreCase(dataMapType)) {
                String clazz = settingsDataMap.get("class").getAsString();
                dataMap = (IDataMap) FactoryMethodWrapper.getUntypedInstance(clazz);
                break;
            }
        }
        if (dataMap == null) {
            throw new EfwdServiceException("The datamap is not configured");
        }
        return dataMap;
    }
}