package com.helicalinsight.admin.management;

import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import com.helicalinsight.datasource.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author somen
 *         Created by helical021 on 10/6/2015.
 */
@SuppressWarnings("unused")
public class TempDirectoryCleanHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);
        String action = formJson.get("action").getAsString();
        int noOfDays = GsonUtility.optInt(formJson,"noOfDays");

        if (noOfDays == 0) {
            noOfDays = 1;
        }

        switch (action) {
            case "list":
                return retrieveTempFiles(noOfDays);
            case "delete":
                JsonArray files = formJson.get("files").getAsJsonArray();
                List<String> stringList = new ArrayList<>();

                // Iterate through the JsonArray and add each string to the List
                for (JsonElement element : files) {
                    stringList.add(element.getAsString());
                }

                return purgeSelectedFiles(stringList);
            case "deleteAll":
                return purgeAllFiles(noOfDays);
            default:
                throw new EfwServiceException("This action is not found");
        }
    }

    public String retrieveTempFiles(int noOfDays) {
        DateTime dateTime = DateTime.now();
        DateTime previousDate = dateTime.minusDays(noOfDays);
        long yesterdayMillis = previousDate.getMillis();
        JsonArray tempFileArray = getTempFileArray(0,yesterdayMillis,null);
        JsonObject response = new JsonObject();
        response.add("tempFileArray", tempFileArray);
        return response.toString();
    }

    public String purgeSelectedFiles(List<String> deleteArray) {
        if (deleteArray == null || deleteArray.isEmpty()) {
            throw new FormValidationException("There is no file to  delete");
        }
        getTempFileArray(1,0,deleteArray);
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("message", "Resource(s) deleted successfully");
        return responseJson.toString();
    }

    public String purgeAllFiles(int noOfDays) {
        DateTime dateTime = DateTime.now();
        DateTime previousDate = dateTime.minusDays(noOfDays);
        long yesterdayMillis = previousDate.getMillis();
        getTempFileArray(2,yesterdayMillis,null);
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("message", "Resource(s) deleted successfully");
        return responseJson.toString();
    }

    public JsonArray getTempFileArray(int flag,long yesterdayMillis,List<String> deleteArray) {

        JsonArray tempFileArray = new JsonArray();
        File tempDirectory = TempDirectoryCleaner.getTempDirectory();
        Path start = Paths.get(tempDirectory.getPath());
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            stream
                    .map(String::valueOf)
                    .sorted().forEach(t->{
                    	File file=new File(t);;
                    	if(flag==0) {
                    		if (file.lastModified() <= yesterdayMillis) {

                                JsonObject tempFile = new JsonObject();
                                tempFile.addProperty("fileName", file.getName());
                                tempFile.addProperty("fileSize", file.length());
                                tempFile.addProperty("lastModified", file.lastModified());
                                tempFileArray.add(tempFile);
                            }
                    	}
                    	else if(flag==1) {
                    		if (deleteArray !=null && deleteArray.contains(file.getName())) {
                                if(file.exists())
                                	file.delete();
                            }
                    	}
                    	else {
                    		if (file.lastModified() <= yesterdayMillis) {
                                file.delete();
                            }
                    	}
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFileArray;
    }
}
