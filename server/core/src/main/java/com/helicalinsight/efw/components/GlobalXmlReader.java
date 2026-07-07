package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 05-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GlobalXmlReader implements IComponent {

    @Override
    public String executeComponent(String formData) {
        JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();
        String type = formDataJson.get("type").getAsString();
        String id = formDataJson.get("id").getAsString();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("type", type);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        JsonObject theDataSource = findDataSource(id, type);

        if (theDataSource == null) {
            throw new EfwServiceException(String.format("The given data source with id %s and " +
                    "type %s could " + "not be found", id, type));
        }

        @SuppressWarnings("rawtypes") Iterator iterator = theDataSource.keySet().iterator();
        JsonObject dataSourceJson = new JsonObject();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object obj = theDataSource.get((String) key);
            Boolean isEmptyArray = false;
            if (obj instanceof JsonArray) {
                isEmptyArray = true;
            }
            if ("username".equals(key)) {
                dataSourceJson.addProperty("userName", isEmptyArray ? "" : String.valueOf(obj));
                continue;
            } else if ("driverClassName".equals(key)) {
                dataSourceJson.addProperty("driverName", isEmptyArray ? "" : String.valueOf(obj));
                continue;
            } else if ("url".equals(key)) {
                dataSourceJson.addProperty("jdbcUrl", isEmptyArray ? "" : String.valueOf(obj));
                continue;
            } else if ("password".equals(key)) {
                String decryptedPWD = CipherUtils.decrypt((String) (isEmptyArray ? "" : obj));
                dataSourceJson.addProperty("password", decryptedPWD);
            } else {
                dataSourceJson.add((String)key, (JsonElement)obj);
            }
        }
        return dataSourceJson.toString();
    }

    @Nullable
    private JsonObject findDataSource(@NotNull String id, @NotNull String type) {
        JsonObject globalJson = JsonUtils.newGetGlobalConnectionsJson();
        List<String> keys = JsonUtils.getKeys(globalJson);

        for (String key : keys) {
            Object theKey = globalJson.get(key);
            if (theKey instanceof JsonArray) {
                JsonArray jsonArray = globalJson.getAsJsonArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    JsonObject aDataSource = jsonArray.get(counter).getAsJsonObject();
                    if (check(id, type, aDataSource)) {
                        return aDataSource;
                    }
                }
            } else if (theKey instanceof JsonObject) {
                JsonObject aDataSource = globalJson.getAsJsonObject(key);
                if (check(id, type, aDataSource)) {
                    return aDataSource;
                }
            }
        }
        return null;
    }

    private boolean check(@NotNull String id, @NotNull String type, @NotNull JsonObject aDataSource) {
        String theId = aDataSource.get("@id").getAsString();
        // String theType = aDataSource.getString("@type");
        return id.equals(theId); //&& type.equals(theType);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
