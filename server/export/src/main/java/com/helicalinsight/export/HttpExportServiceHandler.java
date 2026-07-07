//PhantomExportService.java
package com.helicalinsight.export;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * One of the extended class from PhantomExportService abstract class
 * responsible for requesting phantom daemon service with url through HttpClient
 * calls
 *
 * @author Rajasekhar M
 */
public class HttpExportServiceHandler extends PhantomExportService {
    private static Logger logger = LoggerFactory.getLogger(HttpExportServiceHandler.class);

   
    private String printSettingFileName(JsonObject settings) {
        JsonObject printSettings = new Gson().fromJson(settings,JsonObject.class);
        String jsonFileName = PhantomJS.extractSettingJsonFileName(printSettings);
        if (jsonFileName == null) {
            jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + "defaultTemplate"
                    + ExportUtils.JSON_EXTENSION;
        }

        return jsonFileName;
    }
    /**
     * Handles the export process by making HTTP requests to the Phantom daemon service.
     *
     * @param settings JsonObject containing export settings. such as domain, base url, format(e.g., PDF, PNG).     
     * @return List of strings containing the result of the export process.
     */
    @Override
	public List<String> handlePhantomjs(JsonObject settings) {
    	 putFormat(settings);

         settings.addProperty("printOptions", printSettingFileName(settings));
         settings.addProperty("reportDirectory", ExportUtils.getReportDirectory());
         logger.debug("HttpExportServiceHandler handlePhantomJS() method");

         logger.debug("domain" + settings.get("domain").getAsString());
         logger.debug("format" + settings.get("format").getAsString());
         logger.debug("url" + settings.get("reportSourceUri").getAsString());
         logger.debug("report name" + settings.get("destinationFile").getAsString());

         // Create an instance of HttpClient.
         CloseableHttpClient httpClient = HttpClients.createDefault();


         URI uri = null;
         URIBuilder uriBuilder = new URIBuilder();
         uriBuilder.setScheme("http").setHost("localhost").setPort(getPhantomPort()).setPath("/");
         //uriBuilder.setParameter("formData", settings.toString());
         // uriBuilder.setPath(settings.getString("reportSourceUri"));

         // uriBuilder.setFragment(settings.getString("destinationFile"));
         try {
             uri = uriBuilder.build();
         } catch (URISyntaxException uriSyntaxException) {
             logger.info("requesting phantom daemon service with Malformed uri " + uriSyntaxException.getMessage());
         }
         HttpPost postMethod = new HttpPost(uri);
         StringEntity requestEntity = new StringEntity(settings.toString(), ContentType.APPLICATION_JSON);
         postMethod.setEntity(requestEntity);
         HttpResponse response = null;
         try {
             response = httpClient.execute(postMethod);
         } catch (IOException ioException) {
             logger.info("Exception raised while getting uri for phantom" + ioException.getStackTrace());
         } catch (Exception exception) {
             logger.info("Exception raised while getting uri for phantom" + exception.getStackTrace());
         }

         return handleResponse(response);

	}
    /**
     * Method handles the HTTP response received from the Phantom daemon service.
     *
     * @param response 		HTTP response from the Phantom service.
     * @return List of strings containing the result of the response handling.
     */
    private List<String> handleResponse(HttpResponse response) {
        List<String> list = new ArrayList<>();
        isResponseNull(response);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            handleResult(response, list);
            //Added blank content to mock empty error stream
            list.add("");
            return list;
        } else {
            list.add("");
            handleResult(response, list);
            return list;
        }


    }
    /**
     * Method responsible for handling the result obtained from the HTTP response.
     *
     * @param response 	    HTTP response from the Phantom service.
     * @param list          List to store the result of the response handling.
     */
    private void handleResult(HttpResponse response, List<String> list) {
        HttpEntity responseEntity = response.getEntity();
        try {
            if (responseEntity != null) {
                list.add(EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            throw new ExportException("Cannot parse the response");
        }
    }
    /**
     * Checks if the HTTP response is {@code null} and throws an {@code ExportException}
     * @param response The HTTP response from the Phantom service.
     */
    private void isResponseNull(HttpResponse response) {
        if (response == null) {
            throw new ExportException("The response is null. Possibly the server is not running");
        }
    }
    /**
     * Modifies the format property in the settings JsonObject for compatibility.
     * @param settings JsonObject containing export settings.
     */  
    private void putFormat(JsonObject settings) {
        if (settings.has("format")) {
            String format = "";
            for (Object formatItem : settings.getAsJsonArray("format")) {
                format += "," + formatItem;
            }
            format = format.substring(1);

            settings.addProperty("format", format);
        }
    }
    /**
     * Retrieves the PhantomJS port from the settings.
     * @return The PhantomJS port as an Integer.
     */
    public static Integer getPhantomPort() {
        return GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export").get("phantomPort").getAsInt();
    }

	

}
