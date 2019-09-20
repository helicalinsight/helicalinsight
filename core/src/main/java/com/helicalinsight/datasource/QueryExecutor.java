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

package com.helicalinsight.datasource;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible to get the dataSource related details and execute
 * query and get result.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @author Avi
 */
public class QueryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

    private final ApplicationProperties applicationProperties;

    private final String data;

    private JSONObject settings;

    /**
     * Sets the instance variables
     *
     * @param data                  The data from the http request object
     * @param applicationProperties The singleton instance of the application
     */
    public QueryExecutor(String data, ApplicationProperties applicationProperties) {
        this.data = data;
        this.applicationProperties = applicationProperties;
        this.settings = JsonUtils.getSettingsJson();
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
    public JSONObject readEFWD(String directory, String file, String solutionDirectory) {

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
        JSONObject fileAsJson = processor.getJSONObject(efwdFile, false);
        fileAsJson.put("_efwdFileName_", efwdFile);
        return fileAsJson;
    }

    public JsonObject getResultSet() {
        JSONObject requestParameterJson = (JSONObject) JSONSerializer.toJSON(data);
        // EFWD directory name
        String directory = requestParameterJson.getString("dir");
        int mapId = requestParameterJson.getInt("map_id");
        String file = null;
        JSONObject efwdJson = requestParameterJson.optJSONObject("efwd");
        if (efwdJson != null && !efwdJson.isEmpty()) {
            file = efwdJson.optString("file");
            String dir = efwdJson.optString("dir");
            if (dir != null && !dir.isEmpty()) {
                directory = dir;
            }
        }

        validateRequestParameter(directory, mapId);
        String solutionDirectory = applicationProperties.getSolutionDirectory();
        JSONObject fileAsJson = readEFWD(directory, file, solutionDirectory);

        ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
        boolean isValidEFWD = resourceValidator.validateEfwd();
        if (!isValidEFWD) {
            throw new ImproperDataMapConfigurationException("Duplicate DataMap id is found " + "the in Efwd file.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The map id from the http request is %s.", mapId));
        }

        JSONObject dataMapTagContent = getDataMapTagContent(fileAsJson, mapId);
        Integer connectionId = dataMapTagContent.getInt("@connection");

        JSONObject connectionDetails = getDatasourceList(fileAsJson, connectionId);

        String type = connectionDetails.getString("@type");
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

    private IDriver getIDriverImpl(String type) {
        IDriver driverObject = null;
        JSONArray settingsDataSources = settings.getJSONArray("DataSources");

        for (int counter = 0; counter < settingsDataSources.size(); counter++) {
            JSONObject settingsDataSource = settingsDataSources.getJSONObject(counter);
            String dataSourceType = settingsDataSource.getString("@type");
            if (type.equalsIgnoreCase(dataSourceType)) {
                String clazz = settingsDataSource.getString("@class");
                driverObject = (IDriver) FactoryMethodWrapper.getUntypedInstance(clazz);
                break;
            }
        }
        return driverObject;
    }

    private JSONObject getDatasourceList(JSONObject fileAsJson, Integer connectionId) {
        JSONObject connectionDetails = null;

        JSONArray dataSources = fileAsJson.getJSONArray("DataSources");
        for (int counter = 0; counter < dataSources.size(); counter++) {
            JSONObject connection = dataSources.getJSONObject(counter);
            if (connectionId == connection.getInt("@id")) {
                connectionDetails = connection;

                break;
            }
        }
        return connectionDetails;
    }

    private List<String> findEFWDFiles(String directory, String solutionDirectory, JSONObject settings) {
        String extension = settings.getJSONObject("Extentions").getString("efwd");
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
                logger.debug(" filenale :" + fileName + "  extension" + fileExtension);

                if ((fileExtension.equalsIgnoreCase(extension))) {
                    efwd.add(fileName);
                    logger.debug("adding the files to the list");
                }
            }
        }
        return efwd;
    }

    private void validateRequestParameter(String directory, int mapId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", directory);
        parameters.put("map_id", "" + mapId);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }


    private JSONObject getDataMapTagContent(JSONObject fileAsJson, int mapId) {

        JSONObject dataMapTagContent = null;

        int connectionId = 0;
        JSONArray dataMapsArray = fileAsJson.getJSONArray("DataMaps");
        for (int counter = 0; counter < dataMapsArray.size(); counter++) {
            JSONObject dataMapTag = dataMapsArray.getJSONObject(counter);
            if (mapId == dataMapTag.getInt("@id")) {
                dataMapTagContent = (JSONObject) dataMapsArray.get(counter);
                connectionId = dataMapTag.getInt("@connection");
                break;
            }
        }

        if (connectionId == 0) {
            throw new EfwdServiceException(
                    "The mapId " + mapId + " is not found in the " + "respective efwd resource. Invalid request.");
        }
        return dataMapTagContent;
    }

}