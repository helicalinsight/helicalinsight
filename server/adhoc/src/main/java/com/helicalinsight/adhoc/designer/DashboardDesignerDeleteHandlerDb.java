package com.helicalinsight.adhoc.designer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements {@link IComponent} interface and is responsible for deleting a designer database.
 * 
 * Created by author on 16-07-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class DashboardDesignerDeleteHandlerDb implements IComponent {

    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    /**
     * This method is used to delete a designer database.
     * @param jsonFormData   FormData providing parameters like directory and file.
     * @return A response message indicating successful deletion.
     * @throws IncompleteFormDataException if the directory or file parameters are missing
     */
    @Nullable
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();

        String directory = GsonUtility.optString(formData,"dir");
        String fileName = GsonUtility.optString(formData,"file");
        if (StringUtils.isBlank(directory) || StringUtils.isBlank(fileName)) {
            throw new IncompleteFormDataException("The request parameters dir and/or file are " + "missing");
        }

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);


        HIResource resourceByUrl = serviceDB.getResourceByUrl(directory + "/" + fileName);

        if (resourceByUrl == null) {
            throw new IllegalArgumentException("The designer file " + " doesn't exists. Aborting " +
                    "operation");
        }
        serviceDB.deleteHIResource(resourceByUrl.getResourceId(),null,null);


        JsonObject response;
        response = new JsonObject();


        GsonUtility.accumulate(response,"message", "The requested file is deleted successfully");
        return response.toString();
    }
    /**
     * Indicates whether the method is thread-safe for caching purposes.
     * @return {@code true}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
