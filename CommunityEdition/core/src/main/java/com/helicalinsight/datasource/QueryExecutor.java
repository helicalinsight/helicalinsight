/**
 *    Copyright (C) 2013-2017 Helical IT Solutions (http://www.helicalinsight.com).
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
import com.helicalinsight.efw.validator.ResourceValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.HashMap;
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

    private final String settingPath;

    /**
     * Sets the instance variables
     *
     * @param data                  The data from the http request object
     * @param applicationProperties The singleton instance of the application
     */
    public QueryExecutor(String data, ApplicationProperties applicationProperties) {
        this.data = data;
        this.settingPath = applicationProperties.getSettingPath();
        this.applicationProperties = applicationProperties;
    }

    /**
     * <p>
     * This method is responsible to get the connection related details,
     * instantiate the DataSource class and get the final result in JSONObject
     * format.
     * <p/>
     * It reads the value of key efwd from setting.xml , if same name
     * extension is present in the directory then reads that file from resource
     * directory. If more than one file is present with that extension then it
     * will throw exception. Otherwise reads DataMap id and corresponding connection
     * details from the efwd file. The instantiated IDriver implementation will return the
     * json data in the form of JSONObject that will be sent to the browser for rendering.
     * </p>
     *
     * @return A json string, which is the query execution result
     */
    public JsonObject getResultSet() {
        JSONObject requestParameterJson = (JSONObject) JSONSerializer.toJSON(data);
        // EFWD directory name
        String directory = requestParameterJson.getString("dir");
        int mapId = requestParameterJson.getInt("map_id");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("dir", directory);
        parameters.put("map_id", "" + mapId);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        String solutionDirectory = applicationProperties.getSolutionDirectory();

        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        // Convert setting.xml into JSONObject
        JSONObject settings = processor.getJSONObject(settingPath, false);
        // Reading the DataSources tag from setting.xml
        JSONArray settingsDataSources = settings.getJSONArray("DataSources");
        // Get extension of efwd from setting.xml
        String extension = settings.getJSONObject("Extentions").getString("efwd");
        String efwdFile;
        String efwd = null;
        // EFWD file
        File folder = new File(solutionDirectory + File.separator + directory);
        File[] listOfFiles = folder.listFiles();
        Assert.notNull(listOfFiles, "List of files is null. Directory has no content.");

        int count = 0;
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    String fileName = listOfFile.getName();
                    String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
                    //Fixed ArrayIndexOutOfBoundsException
                    if ((tokens.length > 1) && (tokens[1].equalsIgnoreCase(extension))) {
                        efwd = fileName;
                        count++;
                    }
                }
            }
        } else {
            throw new EfwServiceException("The list of files is empty. Couldn't find any efwd.");
        }

        JsonObject jsonData;
        //Only one efwd file per folder. So count is one
        if (count == 1) {
            efwdFile = solutionDirectory + File.separator + directory + File.separator + efwd;
            JSONObject fileAsJson = processor.getJSONObject(efwdFile, false);

            ResourceValidator resourceValidator = new ResourceValidator(fileAsJson);
            boolean isValidEFWD = resourceValidator.validateEfwd();

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The map id from the http request is %s.", mapId));
            }

            int connectionId = 0;
            String type = "";
            JSONObject connectionDetails = null;
            JSONObject dataMapTagContent = null;
            if (isValidEFWD) {
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
                    throw new EfwdServiceException("The mapId " + mapId + " is not found in the " +
                            "respective efwd resource. Invalid request.");
                } else {
                    JSONArray dataSources = fileAsJson.getJSONArray("DataSources");
                    for (int counter = 0; counter < dataSources.size(); counter++) {
                        JSONObject connection = dataSources.getJSONObject(counter);
                        if (connectionId == connection.getInt("@id")) {
                            connectionDetails = connection;
                            type = connection.getString("@type");
                            break;
                        }
                    }
                }

                IDriver driverObject = null;
                int size = settingsDataSources.size();
                for (int counter = 0; counter < size; counter++) {
                    JSONObject settingsDataSource = settingsDataSources.getJSONObject(counter);
                    String dataSourceType = settingsDataSource.getString("@type");
                    if (type.equalsIgnoreCase(dataSourceType)) {
                        String clazz = settingsDataSource.getString("@class");
                        driverObject = (IDriver) FactoryMethodWrapper.getUntypedInstance(clazz);
                        break;
                    }
                }

                if (driverObject != null) {
                    //String query = driverObject.getQuery(requestParameterJson, dataMapTagContent);
                    jsonData = driverObject.getJSONData(requestParameterJson, connectionDetails, dataMapTagContent,
                            applicationProperties);
                } else {
                    logger.error("The IDriver object is null. Probably class not found or configured in setting" + "" +
                            ".xml for the DataMap type in setting.xml");
                    throw new ConfigurationException("Couldn't instantiate IDriver. Unable to execute datasource.");
                }
            } else {
                throw new ImproperDataMapConfigurationException("Duplicate DataMap id is found " + "the in Efwd file.");
            }
        } else {
            throw new DuplicateEfwdException("There are no or more than one efwd files in the directory %s. Only one " +
                    "" + "efwd file should be present in each directory.");
        }
        return jsonData;
    }
}