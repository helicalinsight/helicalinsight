package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * EfwdServiceUtility
 * This Utility class help to get Efwd object.
 * Created by author on 17-Jan-15.
 * @author Rajasekhar
 */
@Component
class EfwdServiceUtility {
	/**
	 * getEfwd(String connectionDetails)
	 * Using connection details parameter it returns Efwd object.
	 * @param connectionDetails     Driver connection details    
	 * @return Efwd object .
	 * @throws EfwdServiceException  if any required data is not present
	 */
    @NotNull
    public Efwd getEfwd(String connectionDetails) {
        int globalId;
        String serviceType;
        try {
            JsonObject connection = new Gson().fromJson(connectionDetails,JsonObject.class);
            globalId = Integer.valueOf(connection.get("globalId").getAsString());
            serviceType = connection.get("type").getAsString();
        } catch (JsonSyntaxException ex) {
            throw new EfwdServiceException("The json from the efwd file is malformed.", ex);
        } catch (NumberFormatException ex) {
            throw new EfwdServiceException("The global id from the efwd file is not a number.");
        }
        return new Efwd(globalId, serviceType);
    }
    /**
     * getEfwd2(String connectionDetails)
     * @param connectionDetails         to get the globalId and service type
     * @return Efwd object.
     * @throws EfwdServiceException  if any required data is not present
     */
    public Efwd getEfwd2(String connectionDetails) {
        int globalId;
        String serviceType;
        try {
            JsonObject connection = new Gson().fromJson(connectionDetails,JsonObject.class);
            if(connection.has("connDetails")) {
            	globalId =Integer.parseInt( connection.getAsJsonObject("connDetails").get("globalId").getAsString());
            }else {
            	globalId = Integer.valueOf(connection.get("globalId").getAsString());
            }
            serviceType = connection.get("type").getAsString();
        } catch (JsonSyntaxException ex) {
            throw new EfwdServiceException("The json from the efwd file is malformed.", ex);
        } catch (NumberFormatException ex) {
            throw new EfwdServiceException("The global id from the efwd file is not a number.");
        }
        return new Efwd(globalId, serviceType);
    }
}
