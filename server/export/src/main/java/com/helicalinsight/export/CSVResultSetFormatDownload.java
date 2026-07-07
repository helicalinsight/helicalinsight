package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.ResultSetHelper;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * CSVResultSetFormatDownload class for handling CSV format download from ResultSet functionality.
 */
@Component
@Scope("prototype")
public class CSVResultSetFormatDownload implements IDownload {
    private List<String> hiddenList;
    private List<Integer> hiddenIndex = new ArrayList<>();
    /**
     * Appends quotes to the provided data for CSV formatting.
     *
     * @param data 		given data to be enclosed in quotes.
     * @return The data with quotes appended.
     */
    public static String appendQuotes(String data) {
        return "\"" + data + "\"";
    }
    /**
     * Generates CSV format data for download based on the provided ResultSet and conversion options.
     *
     * @param obj              		ResultSet data to be converted to CSV format.
     * @param conversionOptions 	The options for the CSV conversion.
     * @return A StringBuilder containing the CSV formatted data.
     */
    public StringBuilder downloadFormat(Object obj, JsonObject conversionOptions) {
        hiddenList = ResultSetHelper.getHiddenList(conversionOptions);
        ResultSet resultSet = (ResultSet) obj;
        String booleanValue = GsonUtility.optString(conversionOptions, "blockQuote");
        Boolean appendQuote = !"false".equals(booleanValue);
        StringBuilder firstRow = null;
        try {
            firstRow = headerRowAsCsv(resultSet, appendQuote);
            StringBuilder otherData = new StringBuilder();
            otherData.append(rowAsCsv(resultSet, appendQuote)).append("\n");
            firstRow.append("\n").append(otherData);
            appendWatermarkFooter(firstRow, conversionOptions, appendQuote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return firstRow;
    }
    /**
     * Generates the header row for the CSV format based on the provided ResultSet.
     *
     * @param resultSet    		The ResultSet to extract header information.
     * @param appendQuote 		boolean value indicating whether to append quotes to header values.
     * @return A StringBuilder containing the header row in CSV format.
     * @throws SQLException If a database access error occurs.
     */
    public  StringBuilder headerRowAsCsv(ResultSet resultSet, Boolean appendQuote) throws SQLException {
        StringBuilder headerRow = new StringBuilder();
        //Object firstObject = resultSet.getMetaData().getColumnLabel(1);
        ResultSetMetaData metaData = resultSet.getMetaData();
        long length = metaData.getColumnCount();
        for (int columnIndex = 0; columnIndex < length; columnIndex++) {

            String columnName = metaData.getColumnLabel(columnIndex + 1);
            if (hiddenList.contains(columnName)) {
                hiddenIndex.add(columnIndex + 1);
                continue;
            }
            if (appendQuote) {
                headerRow.append(appendQuotes(metaData.getColumnLabel(columnIndex + 1).toString())).append(",");
            } else {
                headerRow.append(metaData.getColumnLabel(columnIndex + 1)).append(",");
            }

        }
        if (headerRow.length() > 0) {
            headerRow.setLength(headerRow.length() - 1);
        }
        return headerRow;
    }
    /**
     * Generates a row in CSV format based on the provided ResultSet.
     *
     * @param resultSet    		ResultSet representing a row of data.
     * @param appendQuote 		Flag indicating whether to append quotes to cell values.
     * @return A StringBuilder containing a row in CSV format.
     * @throws SQLException If a database access error occurs.
     */
    private StringBuilder rowAsCsv(ResultSet resultSet, Boolean appendQuote) throws SQLException {
        StringBuilder rowData = new StringBuilder();
        int rowNumber = 1;
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 0; i < columnCount; i++) {
                if(hiddenIndex.contains(i+1)){
                    continue;
                }
                Object object = resultSet.getObject(i + 1);
                if(null==object){
                    rowData.append(ApplicationProperties.getInstance().getNullValue()).append(",");
                }else if (appendQuote) {
                    rowData.append(appendQuotes("" + object)).append(",");
                } else {
                    rowData.append(object).append(",");
                }
            }
            
            if (rowData.length() > 0) {
                rowData.setLength(rowData.length() - 1);
            }
            if(!resultSet.isLast()) {
            	rowData.append("\n");
            }
            
        }

        return rowData;
    }

    private void appendWatermarkFooter(StringBuilder csvContent, JsonObject conversionOptions, Boolean appendQuote) {
        if (!ExportWatermarkHelper.shouldApplyWatermark(conversionOptions)) {
            return;
        }
        String watermarkText = ExportWatermarkHelper.getWatermarkText();
        csvContent.append("\n\n");
        if (appendQuote) {
            csvContent.append(appendQuotes(watermarkText));
        } else {
            csvContent.append(watermarkText);
        }
    }
}