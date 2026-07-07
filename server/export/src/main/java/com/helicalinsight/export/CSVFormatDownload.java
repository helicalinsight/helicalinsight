package com.helicalinsight.export;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import java.util.Iterator;

/**
 * CSVFormatDownload class for handling CSV format download functionality.
 */
@Component
@Scope("prototype")
public class CSVFormatDownload implements IDownload {

	/**
     * Generates CSV format data for download, based on the provided JSON data and conversion options.
     *
     * @param obj              	provides JSON data to be converted to CSV format.
     * @param conversionOptions options for the CSV conversion.
     * @return A StringBuilder containing the CSV formatted data.
     */
    public StringBuilder downloadFormat(Object obj, JsonObject conversionOptions) {
        JsonArray jsonData = (JsonArray) obj;
        String booleanValue = GsonUtility.optString(conversionOptions, "blockQuote");
        Boolean appendQuote = !"false".equals(booleanValue);
        StringBuilder firstRow = headerRowAsCsv(jsonData, appendQuote);
        StringBuilder otherData = new StringBuilder();
        for (int i = 0; i < jsonData.size(); i++) {
            otherData.append(rowAsCsv(jsonData.get(i).getAsJsonObject(), appendQuote)).append("\n");
        }
        firstRow.append("\n").append(otherData);
        return firstRow;
    }
    /**
     * Generates the header row(contains the names or labels for each column) for the CSV format based .
     *
     * @param jsonData    			JSON data to extract header information.
     * @param appendQuote Flag 		indicating whether to append quotes to header values.
     * @return A StringBuilder containing the header row in CSV format.
     */
    public static StringBuilder headerRowAsCsv(JsonArray jsonData, Boolean appendQuote) {
        StringBuilder headerRow = new StringBuilder();
        long length = jsonData.size();

        if (length > 0) {
            Object firstObject = jsonData.get(0);
            if (firstObject instanceof JsonObject) {

                JsonObject firstData = (JsonObject) firstObject;
                Iterator<?> keys =  firstData.keySet().iterator();

                while (keys.hasNext()) {
                    if (appendQuote) {
                        headerRow.append(appendQuotes((String) keys.next())).append(",");
                    } else {
                        headerRow.append(keys.next()).append(",");
                    }
                }
            }
        }
        if (headerRow.length() > 0) {
            headerRow.setLength(headerRow.length() - 1);
        }

        return headerRow;
    }
    /**
     * Generates a row in CSV format based on the provided JSON object.
     *
     * @param jObject     		JSON object representing a row of data.
     * @param appendQuote Flag  indicating whether to append quotes to cell values.
     * @return A StringBuilder containing a row in CSV format.
     */
    private StringBuilder rowAsCsv(JsonObject jObject, Boolean appendQuote) {

        StringBuilder rowData = new StringBuilder();
        Iterator<?> keys =  jObject.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object retrievedObject = jObject.get(key);
            if (appendQuote) {
                rowData.append(appendQuotes("" + retrievedObject)).append(",");
            } else {
                rowData.append(retrievedObject).append(",");
            }
        }

        if (rowData.length() > 0) {
            rowData.setLength(rowData.length() - 1);
        }
        return rowData;
    }
    /**
     * Appends quotes to the provided data for CSV formatting.
     *
     * @param data The data to be enclosed in quotes.
     * @return The data with quotes appended.
     */
    public static String appendQuotes(String data) {
        return "\"" + data + "\"";
    }
}