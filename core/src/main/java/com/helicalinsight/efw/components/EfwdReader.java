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

package com.helicalinsight.efw.components;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
public class EfwdReader implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(EfwdReader.class);

    private static boolean check(String id, String type, JSONObject connection) {
        String theId = connection.getString("@id");
        String theType = connection.getString("@type");
        return id.equals(theId) && type.equals(theType);
    }

    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        String type;
        String id;
        String directory;

        try {
            type = formDataJson.getString("type");
            id = formDataJson.getString("id");
            directory = formDataJson.getString("dir");
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ExceptionUtils.getRootCauseMessage(ex));
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("type", type);
        parameters.put("dir", directory);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        File efwdFile = ApplicationUtilities.getEfwdFile(directory);
        JSONObject fileAsJson = getJsonOfEfwd(efwdFile);

        JSONObject efwdConnection = getEfwdConnection(fileAsJson, id, type);
        return efwdConnection.toString();
    }

    private JSONObject getJsonOfEfwd(File efwdFile) {
        JSONObject fileAsJson;
        try {
            fileAsJson = JsonUtils.getAsJson(efwdFile);
        } catch (Exception ex) {
            log(ex);
            //Problem due to the newly created efwds with only DataSources
            fileAsJson = ResourceProcessorFactory.getIProcessor().getJSONObject(efwdFile.toString(), true);
        }
        return fileAsJson;
    }

    private JSONObject getEfwdConnection(JSONObject fileAsJson, String id, String type) {
        EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);
        try {
            JSONArray dataSources = fileAsJson.getJSONArray("DataSources");
            for (Object object : dataSources) {
                JSONObject connection = JSONObject.fromObject(object);
                if (check(id, type, connection)) {
                    return handler.readDS(id, type, connection);
                }
            }
        } catch (Exception ex) {
            log(ex);
            //The exception is due to the newly created efwds with only DataSources
            //So, EFWD is an array of another array with actual connections
            JSONArray efwd = fileAsJson.getJSONArray("EFWD");
            //Get the inner array at index 0. The info regarding DataSources is missing.
            JSONArray innerArray = efwd.getJSONArray(0);
            for (Object object : innerArray) {
                JSONObject connection = JSONObject.fromObject(object);
                if (check(id, type, connection)) {
                    return handler.readDS(id, type, connection);
                }
            }
        }
        throw new EfwServiceException(String.format("Invalid request. There is no connection with the given id " +
                "%s, and type %s", id, type));
    }

    private void log(Exception ex) {
        if (logger.isDebugEnabled()) {
            logger.debug("There was an exception. The exception is " + ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
