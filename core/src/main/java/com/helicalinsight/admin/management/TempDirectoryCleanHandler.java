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

package com.helicalinsight.admin.management;

import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Somen 
 */
@SuppressWarnings("unused")
public class TempDirectoryCleanHandler implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String action = formJson.optString("action");
        int noOfDays = formJson.optInt("noOfDays");

        if (noOfDays == 0) {
            noOfDays = 1;
        }

        switch (action) {
            case "list":
                return retrieveTempFiles(noOfDays);
            case "delete":
                JSONArray files = formJson.getJSONArray("files");
                return purgeSelectedFiles(files);
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
        JSONArray tempFileArray = new JSONArray();
        int index = 0;
        for (File file : getTempFileArray()) {
            if (file.lastModified() <= yesterdayMillis) {
                JSONObject tempFile = new JSONObject();
                tempFile.accumulate("fileName", file.getName());
                tempFile.accumulate("fileSize", file.length());
                tempFile.accumulate("lastModified", file.lastModified());
                tempFileArray.add(index++, tempFile);
            }
        }
        JSONObject response = new JSONObject();
        response.put("tempFileArray", tempFileArray);
        return response.toString();
    }

    public String purgeSelectedFiles(JSONArray deleteArray) {
        if (deleteArray == null || deleteArray.isEmpty()) {
            throw new FormValidationException("There is no file to  delete");
        }
        for (File file : getTempFileArray()) {
            if (deleteArray.contains(file.getName())) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put("message", "Resource(s) deleted successfully");
        return responseJson.toString();
    }

    public String purgeAllFiles(int noOfDays) {
        DateTime dateTime = DateTime.now();
        DateTime previousDate = dateTime.minusDays(noOfDays);
        long yesterdayMillis = previousDate.getMillis();
        for (File file : getTempFileArray()) {
            if (file.lastModified() <= yesterdayMillis) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
        JSONObject responseJson = new JSONObject();
        responseJson.put("message", "Resource(s) deleted successfully");
        return responseJson.toString();
    }

    public ArrayList<File> getTempFileArray() {
        ArrayList<File> tempFileArray = new ArrayList<>();
        File tempDirectory = TempDirectoryCleaner.getTempDirectory();
        File[] files = tempDirectory.listFiles();
        if (files != null) {
            Collections.addAll(tempFileArray, files);
        }
        return tempFileArray;
    }
}
