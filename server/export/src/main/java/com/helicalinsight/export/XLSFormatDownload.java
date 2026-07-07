package com.helicalinsight.export;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

import java.util.Date;
import java.util.Iterator;
/**
 * The `XLSFormatDownload` class implements the `IDownload` interface to handle the download of data in XLS format.
 */
@Component
@Scope("prototype")
@SuppressWarnings("unused")
public class XLSFormatDownload implements IDownload {
	/**
     * Generates an HSSFWorkbook with data in XLS format based on the provided JSON array and conversion options.
     *
     * @param obj              The JSON array containing the data to be exported.
     * @param conversionOptions Additional options for the XLS conversion (e.g., sheetName).
     * @return HSSFWorkbook containing the formatted data.
     */
    public HSSFWorkbook downloadFormat(Object obj, JsonObject conversionOptions) {
        JsonArray jsonData = (JsonArray) obj;
        String sheetName = GsonUtility.optString(conversionOptions, "sheetName");
        sheetName = (sheetName.length() == 0) ? "sheet 1" : sheetName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        createHeaderRow(jsonData, conversionOptions, sheet);
        createDataRows(jsonData, conversionOptions, sheet);
        return workbook;
    }
    /**
     * Creates the header row in the Excel sheet based on the keys of the first JSON object in the array.
     *
     * @param jsonData          The JSON array containing the data.
     * @param conversionOptions Additional options for the XLS conversion.
     * @param sheet             The HSSFSheet to which the header row is added.
     */
    public void createHeaderRow(JsonArray jsonData, JsonObject conversionOptions, HSSFSheet sheet) {
        long length = jsonData.size();
        Row row = sheet.createRow(0);
        if (length > 0) {
            Object firstObject = jsonData.get(0);
            if (firstObject instanceof JsonObject) {
                JsonObject firstData = (JsonObject) firstObject;
                Iterator<?> keys =  firstData.keySet().iterator();

                int cellNum = 0;

                while (keys.hasNext()) {
                    Cell cell = row.createCell(cellNum++);
                    cell.setCellValue("" + keys.next());
                }
            }
        }
    }
    /**
     * Creates data rows in the Excel sheet based on the JSON data provided.
     *
     * @param jsonData          The JSON array containing the data.
     * @param conversionOptions Additional options for the XLS conversion.
     * @param sheet             The HSSFSheet to which data rows are added.
     */
    private void createDataRows(JsonArray jsonData, JsonObject conversionOptions, HSSFSheet sheet) {
        for (int i = 0; i < jsonData.size(); i++) {
            Row row = sheet.createRow(i + 1);
            JsonObject retrievedObject = (JsonObject) jsonData.get(i);
            Iterator<?> keys = retrievedObject.keySet().iterator();
            int cellNum = 0;

            while (keys.hasNext()) {
                String key = (String) keys.next();
                Cell cell = row.createCell(cellNum++);
                Object object = retrievedObject.get(key);
                if (object instanceof Date) cell.setCellValue((Date) object);
                else if (object instanceof Boolean) cell.setCellValue((Boolean) object);
                else if (object instanceof String) cell.setCellValue((String) object);
                else if (object instanceof Double) cell.setCellValue((Double) object);
                else if (object instanceof Integer) cell.setCellValue((Integer) object);
                else cell.setCellValue(object.toString());
            }
        }
    }


}