package com.helicalinsight.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.vf.ChartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for converting chart data in to json array and
 * iterate the JSONArray and return the string for download CSV file of chart
 * data
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

public class CSVUtility {

    private static final Logger logger = LoggerFactory.getLogger(CSVUtility.class);

    /**
     * The singleton instance which holds the application settings. Only one
     * instance per application
     */
    private final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    /**
     * This method is used to convert chart data to json array, iterate the
     * json array and return the data
     *
     * @param data chart data
     * @return String
     */
    public String getCSVData(String data) {
        JsonObject jsonObject;
        try {
            ChartService chartService = new ChartService(data, this.applicationProperties);
            jsonObject = chartService.getData();
            String jsonString = jsonObject.get("data").getAsString();
            JsonObject jsonObj = new JsonObject();
            JsonArray dataNJsonArray = JsonParser.parseString(jsonString).getAsJsonArray();

            for (JsonElement object : dataNJsonArray) {
                jsonObj = object.getAsJsonObject();
            }

            Iterator<?> keys = jsonObj.keySet().iterator();
            List<String> listOfKeys = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                listOfKeys.add(key);
            }

            String header = "";
            String stringOfHeader;
            for (String hValue : listOfKeys) {
                stringOfHeader = '"' + hValue + '"';
                header = header + stringOfHeader + ",";
            }

            String result;
            result = header.substring(0, header.length() - 1).trim() + "\n";
            for (int j = 0; j < dataNJsonArray.size(); j++) {
                String stringOfValues = "";
                logger.debug("Size of Json Array" + dataNJsonArray.size());
                for (String listOfKey : listOfKeys) {
                    String value = dataNJsonArray.get(j).getAsJsonObject().get(listOfKey).getAsString();
                    String valuesInQuotes = '"' + value + '"';
                    stringOfValues = stringOfValues + valuesInQuotes + ",";
                    logger.debug("Values == " + stringOfValues);
                }
                result = result + stringOfValues.substring(0, stringOfValues.length() - 1).trim() + "\n";
            }
            return result;
        } catch (JsonIOException e) {
            logger.error("JSONException ", e);
            //handle error
        } catch (ApplicationException e) {
            logger.error("ApplicationException occurred. " + e);
            //handle error
        }
        return null;
    }
}
