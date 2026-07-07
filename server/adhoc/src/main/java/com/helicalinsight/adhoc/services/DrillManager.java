package com.helicalinsight.adhoc.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
public class DrillManager {
    private static final Logger logger = LoggerFactory.getLogger(DrillManager.class);


    public static String manageDrill(JsonObject formJSON) {

        JsonObject responseJSON = JsonUtils.newGetXmlAsJson(JsonUtils.getDrillConfigPath());
        JsonObject drillJson=JsonUtils.decryptPasswordFromDrillConfigObj(responseJSON);
        String endPointManager = drillJson.getAsJsonObject("endPoints").get("endPointManager").getAsString();
        DrillEndPointManager untypedInstance = (DrillEndPointManager) FactoryMethodWrapper
                .getUntypedInstance(endPointManager);
        JsonObject endPointJSONObjects = drillJson.getAsJsonObject("endPoints");
        JsonObject endPointJSON = untypedInstance.provideRequiredEndpoints(endPointJSONObjects, formJSON.get("type").getAsString());

        String result = null;
        String resourceUrl = AdhocServiceUtils.prepareUrlForDrill(drillJson);

        try {
            if (AdhocServiceUtils.testURL(resourceUrl)) {
                String appendedURL = resourceUrl + endPointJSON.get("endpoint").getAsString();
                result = connectResource(appendedURL, endPointJSON, drillJson, formJSON);
                return result;
            }

        } catch (IOException e1) {
            logger.error("Error creating HTTP connection ", e1);
        }
        return "Error creating HTTP connection";

    }

    private static String connectResource(String resourceUrl, JsonObject requestJson, JsonObject drillJson,
    		JsonObject formJSON) {
        HttpURLConnection connection = null;
        String result = null;

        JsonObject requestQueryJSON = new JsonObject();
        requestQueryJSON.addProperty("queryType", formJSON.get("queryType").getAsString().toUpperCase());
        requestQueryJSON.addProperty("query", formJSON.get("query").getAsString());
        String httpMethod = GsonUtility.optString(requestJson,"method");

        try {
            connection = (HttpURLConnection) new URL(resourceUrl).openConnection();

            connection.setDoOutput(httpMethod.equals("POST") ? true : false);
            if (drillJson.has("username") && (!drillJson.get("username").getAsString().isEmpty())) {
                connection.addRequestProperty("User-Name", drillJson.get("username").getAsString());
            }
            if (drillJson.has("password") && (!drillJson.get("password").getAsString().isEmpty())) {
                connection.addRequestProperty("password", drillJson.get("password").getAsString());
            }
            if (requestJson.has("output")) {
                connection.setRequestProperty("Content-Type", requestJson.get("output").getAsString());
            }
            // Adding the queryJson to the HttpUrlConnection.
            if (formJSON.has("query") && (!formJSON.get("query").getAsString().isEmpty())) {
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(requestQueryJSON.toString().getBytes(StandardCharsets.UTF_8.name()));
                } catch (IOException e) {
                    logger.debug("Exception occoured while doing write operation on HttpUrlConnection");
                    return result = "Exception occoured while doing write operation on HttpUrlConnection";
                }
            }
            // Getting the Output from HttpUrlConnection.
            try (InputStream response = connection.getInputStream()) {
                result = IOUtils.toString(response, StandardCharsets.UTF_8);

            } catch (IOException e) {
                logger.debug("Exception occoured while getting InputStream from HttpUrlConnection");
                return result = "Exception occoured while getting InputStream from HttpUrlConnection";
            }

        } catch (MalformedURLException e) {
            logger.debug("Provided URL is MALFORMED Please check once");
            return result = "Provided URL is MALFORMED Please check once";
        } catch (IOException e) {
            logger.debug("Exception occoured while doing some I/O Operations");
            return result = "Exception occoured while doing some I/O Operations";
        } finally {
            logger.debug("Disconnection the HttpUrlConnection");
            connection.disconnect();
        }

        return result;
    }

}
