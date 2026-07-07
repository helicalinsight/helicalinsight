package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * Fetches cube metadata from a specified directory and file.
 * Implements the {@link IComponent} interface for execution and caching.
 * Author: Somen
 */
@SuppressWarnings("unused")
public class DatabaseCubeFetchHandler implements IComponent {
   /**
    * Checks whether this component is thread-safe to cache.
    * @return true if thread-safe to cache.
    */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Executes the component logic to fetch cube metadata based on the provided JSON form data. 
     * It then retrieves the cube resource from the HIResourceServiceDB, 
     * obtains the cube metadata using the HICubeDAOService, and returns the cube metadata 
     * as a JSON object with additional location and fileName properties.
     *
     * @param jsonFormData 			JSON data containing the form parameters required to fetch the cube metadata.
     *                              includes information about the directory and file of the cube.
     * @return a JSON string containing the cube metadata
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public String executeComponent(String jsonFormData) {

        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String dir = formJson.get("dir").getAsString();
        String file = formJson.get("file").getAsString();

        String cubeUrl = dir + "/" + file;
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResource cubeResource = serviceDB.getResourceByUrl(cubeUrl);
        HICubeDAOService hiCubeDAOService = ApplicationContextAccessor.getBean(HICubeDAOService.class);
        JsonObject cubeAsJsonObj = JsonParser.parseString(hiCubeDAOService.getCubeAsJsonObj(cubeResource.getResourceId()).toString()).getAsJsonObject();
        cubeAsJsonObj.addProperty("location",dir);
        cubeAsJsonObj.addProperty("fileName",file);
        return cubeAsJsonObj.toString();


}


}