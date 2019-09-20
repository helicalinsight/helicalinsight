/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.export;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;

@Component
@Scope("prototype")
@SuppressWarnings("unused")
public class XLSFormatDownload implements IDownload {

    public HSSFWorkbook downloadFormat(JSONArray jsonData, JSONObject conversionOptions) {
        String sheetName = conversionOptions.optString("sheetName");
        sheetName = (sheetName.length() == 0) ? "sheet 1" : sheetName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        createHeaderRow(jsonData, conversionOptions, sheet);
        createDataRows(jsonData, conversionOptions, sheet);
        return workbook;
    }

    public void createHeaderRow(JSONArray jsonData, JSONObject conversionOptions, HSSFSheet sheet) {
        long length = jsonData.size();
        Row row = sheet.createRow(0);
        if (length > 0) {
            Object firstObject = jsonData.get(0);
            if (firstObject instanceof JSONObject) {
                JSONObject firstData = (JSONObject) firstObject;
                Iterator<?> keys = firstData.keys();

                int cellNum = 0;

                while (keys.hasNext()) {
                    Cell cell = row.createCell(cellNum++);
                    cell.setCellValue("" + keys.next());
                }
            }
        }
    }

    private void createDataRows(JSONArray jsonData, JSONObject conversionOptions, HSSFSheet sheet) {
        for (int i = 0; i < jsonData.size(); i++) {
            Row row = sheet.createRow(i + 1);
            JSONObject retrievedObject = (JSONObject) jsonData.get(i);
            Iterator<?> keys = retrievedObject.keys();
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







