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

package com.helicalinsight.export.components;

import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * @author Somen
 *         Created  on 12/16/2016.
 */
public class ExcelImageBinder implements IComponent {
    private static final int COLUMN_WIDTH = 50;
    private static final int COLUMN_HEIGHT = 15;

    private void createXls(JSONArray formData, String excelFilePath) {

        try {

            createWorkBook(excelFilePath, formData);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void createWorkBook(String sheetName, JSONArray image) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet workbookSheet = workbook.createSheet("Sheet 1");
        int pictureIdx[] = new int[image.size()];
        int counter = 0;
        //FileInputStream obtains input bytes from the image file
        for (Object fileName : image) {
            JSONObject jsonObject = JSONObject.fromObject(fileName);
            InputStream inputStream = new FileInputStream(jsonObject.getString("index"));
            //Get the contents of an InputStream as a byte[].
            byte[] bytes = IOUtils.toByteArray(inputStream);
            //Adds a picture to the workbook
            pictureIdx[counter++] = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            inputStream.close();
        }
        //close the input stream

        //Returns an object that handles instantiating concrete classes
        CreationHelper helper = workbook.getCreationHelper();
        int index = 0;
        for (Object fileName : image) {
            JSONObject jsonObject = JSONObject.fromObject(fileName);
            //Creates the top-level drawing patriarch.
            Drawing drawing = workbookSheet.createDrawingPatriarch();

            //Create an anchor that is attached to the worksheet
            ClientAnchor anchor = helper.createClientAnchor();
            //set top-left corner for the image
            anchor.setCol1(jsonObject.getInt("left") / COLUMN_WIDTH);
            anchor.setRow1(jsonObject.getInt("top") / COLUMN_HEIGHT);


            //Creates a picture
            Picture pict = drawing.createPicture(anchor, pictureIdx[index++]);
            //Reset the image to the original size
            pict.resize();
            // pict.resize(0.5d);

        }


        //Write the Excel file
        FileOutputStream fileOut = null;
        File fileExcel = new File(sheetName);
        if (fileExcel.exists()) {
            fileExcel.delete();
        }
        fileOut = new FileOutputStream(sheetName);
        workbook.write(fileOut);
        fileOut.close();
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        String targetJson = formData.getString("targetJson");
        JSONObject excelData = JSONObject.fromObject(ExportUtils.getFileAsString(targetJson));
        String destinationFile = excelData.getString("destinationFile");
        JSONArray formArray = excelData.getJSONArray("report");
        createXls(formArray, destinationFile);
        JSONObject response = new JSONObject();
        response.put("message", "Successfully created object");
        return response.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
