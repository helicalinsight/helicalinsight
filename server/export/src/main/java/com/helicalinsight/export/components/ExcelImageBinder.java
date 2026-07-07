package com.helicalinsight.export.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.export.ExportUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * An implementation of the IComponent interface for creating Excel files with images.
 * It takes a JSON input specifying the target file, image details, and destination file,
 * and generates an Excel file with the provided images.
 *
 * @author Somen
 * Created on 12/16/2016.
 */
public class ExcelImageBinder implements IComponent {
    private static final int COLUMN_WIDTH = 50;
    private static final int COLUMN_HEIGHT = 15;
    /**
     * Creates an Excel workbook with images based on the provided JSON input.
     *
     * @param formData        JSON array containing image details.
     * @param excelFilePath   Destination file path for the Excel workbook.
     */
    private void createXls(JsonArray formData, String excelFilePath) {

        try {

            createWorkBook(excelFilePath, formData);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /**
     * Creates a workbook and adds images to it based on the provided JSON array.
     *
     * @param sheetName 	Destination file path for the Excel workbook.
     * @param image     	JSON array containing image details.
     * @throws IOException If an I/O error occurs.
     */
    private void createWorkBook(String sheetName, JsonArray image) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet workbookSheet = workbook.createSheet("Sheet 1");
        int pictureIdx[] = new int[image.size()];
        int counter = 0;
        //FileInputStream obtains input bytes from the image file
        for (JsonElement fileName : image) {
            JsonObject jsonObject = fileName.getAsJsonObject();
            InputStream inputStream = new FileInputStream(jsonObject.get("index").getAsString());
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
        for (JsonElement fileName : image) {
            JsonObject jsonObject = fileName.getAsJsonObject();
            //Creates the top-level drawing patriarch.
            Drawing drawing = workbookSheet.createDrawingPatriarch();

            //Create an anchor that is attached to the worksheet
            ClientAnchor anchor = helper.createClientAnchor();
            //set top-left corner for the image
            anchor.setCol1(jsonObject.get("left").getAsInt() / COLUMN_WIDTH);
            anchor.setRow1(jsonObject.get("top").getAsInt() / COLUMN_HEIGHT);


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
    /**
     * This method Executes the ExcelImageBinder component with the provided JSON input.
     *
     * @param jsonFormData 			JSON input specifying the target file, image details, and destination file.
     * @return JSON response with a success message.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String targetJson = formData.get("targetJson").getAsString();
        JsonObject excelData = JsonParser.parseString(ExportUtils.getFileAsString(targetJson)).getAsJsonObject();
        String destinationFile = excelData.get("destinationFile").getAsString();
        JsonArray formArray = excelData.getAsJsonArray("report");
        createXls(formArray, destinationFile);
        JsonObject response = new JsonObject();
        response.addProperty("message", "Successfully created object");
        return response.toString();
    }
    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, otherwise {@code false}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
