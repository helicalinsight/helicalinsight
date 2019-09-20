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

package com.helicalinsight.scheduling;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controller.EFWController;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TemplateReader;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for doing XML related operation.
 *
 * @author Prashansa
 */
@SuppressWarnings("unused")
public class XmlOperation {
    private static final Logger logger = LoggerFactory.getLogger(XmlOperation.class);

    /**
     * <p>
     * This method is responsible to convert xml into JSONArray.
     * </p>
     *
     * @param path a <code>String</code> specify the location of scheduling.xml
     * @return JSONArray
     */
    public JSONArray convertXmlStringIntoJSONArray(String path) {
        JSONArray jsonArray = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            String xml = IOUtils.toString(inputStream, ApplicationUtilities.getEncoding());
            XMLSerializer xmlSerializer = new XMLSerializer();
            xmlSerializer.setForceTopLevelObject(false);
            xmlSerializer.setTypeHintsCompatibility(false);
            xmlSerializer.setTypeHintsEnabled(false);
            jsonArray = (JSONArray) xmlSerializer.read(xml);
            logger.debug("jsonArray2:  " + jsonArray);
        } catch (JSONException ex) {
            logger.error("JSONException occurred", ex);
        } catch (IOException ex) {
            logger.error("An IOException occurred", ex);
        } finally {
            ApplicationUtilities.closeResource(inputStream);
        }
        return jsonArray;
    }

    public JSONObject deleteJobFromXml(JSONObject jsonobject, String id) {
        JSONArray jsonArray;
        jsonArray = jsonobject.getJSONArray("Schedules");
        for (int jsonArrayCount = 0; jsonArrayCount < jsonArray.size(); jsonArrayCount++) {
            if (jsonArray.getJSONObject(jsonArrayCount).getString("@id").equals(id)) {
                jsonobject.getJSONArray("Schedules").remove(jsonArrayCount);

            }
        }
        logger.debug("JSON object after deleted ID" + id + ": " + jsonobject);
        return jsonobject;
    }

    public String createNewFileAndAddJob(JSONObject newData, String path) {
        JSONArray jsonArray = new JSONArray();
        logger.debug("Check ReportURL Contains or not:" + newData.containsKey("ReportURL"));
        if (newData.containsKey("ReportURL")) {
            newData.accumulate("@id", 1);
            jsonArray.add(0, newData);
            String data = convertJsonToXml(jsonArray);
            logger.debug("DATA" + data);
            writeStringIntoFile(data, path);
        } else {
            logger.error("ReportURL Not found");
        }
        return "FileCreated";
    }

    public String convertJsonToXml(JSONArray jsonArray) {
        XMLSerializer serializer = new XMLSerializer();
        serializer.setTypeHintsEnabled(false);
        serializer.setForceTopLevelObject(true);
        serializer.setElementName("Schedule");
        serializer.setRootName("Schedules");
        return serializer.write(jsonArray);
    }

    public String writeStringIntoFile(String data, String path) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(path);
            fop = new FileOutputStream(file);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = data.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException ex) {
            logger.error("Exception stack trace is ", ex);
        } finally {
            try {
                if (fop != null) {
                    fop.flush();
                    fop.close();
                    System.gc();
                }
            } catch (IOException ex) {
                logger.error("Exception stack trace is ", ex);
            }
        }
        return "ok";
    }

    public String addNewJobInExistingXML(JSONObject newData, String path) {
        File file = new File(path);
        JSONObject jsonObject;
        String writeData;
        int id = 0;
        if (file.exists()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            jsonObject = processor.getJSONObject(path, true);
            boolean validXml = validateXml(jsonObject);
            id = searchMaxIdInXml(jsonObject);
            if (validXml) {
                if (newData.containsKey("ReportURL")) {
                    if (jsonObject.get("Schedules") instanceof JSONArray) {
                        newData.accumulate("@id", id + 1);

                        jsonObject.accumulate("Schedules", newData);

                        writeData = convertJsonToXml(jsonObject.getJSONArray("Schedules"));
                    } else {
                        newData.accumulate("@id", id + 1);

                        jsonObject.getJSONObject("Schedules").accumulate("Schedule", newData);
                        writeData = convertJsonToXml(jsonObject.getJSONObject("Schedules").getJSONArray("Schedule"));
                    }
                    writeStringIntoFile(writeData, path);
                } else {
                    logger.error("ReportURL Not found");
                }
            } else {
                logger.error("XML is not valid");
            }
        } else {
            logger.error("XML file you are trying to modify is not available");
        }
        return String.valueOf(id + 1);
    }

    public boolean validateXml(JSONObject jsonObject) {
        boolean tagAvailable;
        tagAvailable = jsonObject.has("Schedules");
        return tagAvailable;
    }

    public int searchMaxIdInXml(JSONObject jsonobject) {
        JSONArray jsonArray;
        ArrayList<Integer> arrayList = new ArrayList<>();
        int maxValue = 0;
        boolean validJSon = validateXml(jsonobject);
        if (jsonobject != null) {
            if (validJSon) {
                if (jsonobject.get("Schedules") instanceof JSONArray) {

                    jsonArray = jsonobject.getJSONArray("Schedules");

                    for (int jsonArrayCount = 0; jsonArrayCount < jsonArray.size(); jsonArrayCount++) {

                        String ids = jsonArray.getJSONObject(jsonArrayCount).getString("@id");
                        int id = Integer.parseInt(ids);
                        arrayList.add(id);
                    }
                } else if (jsonobject.getJSONObject("Schedules").get("Schedule") instanceof net.sf.json.JSONObject) {

                    arrayList.add(jsonobject.getJSONObject("Schedules").getJSONObject("Schedule").getInt("@id"));
                }
                maxValue = Collections.max(arrayList);
            } else {
                logger.error("Not a valid XML");
            }
        } else {
            logger.error("Json Object is null");
        }
        logger.debug("Max ID: " + maxValue);
        return maxValue;
    }

    public String modifyJobByIdWriteInFile(JSONObject newData, String path) {
        File file = new File(path);
        JSONObject jsonObject;
        JSONArray jsonArray;
        TemplateReader readFile = new TemplateReader(file);
        String xmlData = readFile.readTemplate();
        String writeData;

        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        if (convertXmlStringToJSon(xmlData).isArray()) {
            jsonObject = processor.getJSONObject(path, true);
            jsonArray = jsonObject.getJSONArray("Schedules");
            boolean checkId = searchId(jsonObject, newData.getString("@id"));
            if (checkId) {
                for (int count = 0; count < jsonArray.size(); count++) {
                    if (jsonArray.getJSONObject(count).getString("@id").equals(newData.getString("@id"))) {
                        jsonObject = modifyDataById(jsonObject, newData, count);
                    }
                }
                writeData = convertJsonToXml(jsonObject.getJSONArray("Schedules"));
                writeStringIntoFile(writeData, path);
            } else {
                logger.error("Job Which you are trying to modify is not available in XML");
            }
        } else {
            jsonObject = processor.getJSONObject(path, true);
            boolean checkId = searchId(jsonObject, newData.getString("@id"));
            if (checkId) {
                jsonObject = modifyJSONObjectById(jsonObject, newData);
                writeData = convertJsonToXml(jsonObject.getJSONObject("Schedules"));
                writeStringIntoFile(writeData, path);
            } else {
                logger.error("Job Which you are trying to modify is not available in XML");
            }
        }
        return "ok";
    }

    public JSON convertXmlStringToJSon(String xmlData) {
        JSON json;
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setForceTopLevelObject(false);
        json = xmlSerializer.read(xmlData);
        return json;
    }

    /**
     * Search id in given JSONObject
     *
     * @param json a <code>JSONObject</code>
     * @param id   a <code>String</code> specify id
     * @return boolean value ,if id exist in JSONObject then return true else
     * return false
     */
    public boolean searchId(JSONObject json, String id) {
        boolean result = false;
        StringBuilder sb = new StringBuilder();
        sb.append("\"@id\"");
        sb.append(":");
        sb.append("\"");
        sb.append(id);
        sb.append("\"");
        logger.debug("sb: " + sb);
        if (json.toString().contains(sb.toString().trim())) {
            result = true;
        }
        return result;
    }

    public JSONObject modifyDataById(JSONObject existingData, JSONObject modifiedData, int index) {
        List<String> key = JsonUtils.listKeys(modifiedData);
        boolean searchId = searchId(existingData, modifiedData.getString("@id"));
        if (searchId) {
            for (String aKey : key) {
                existingData.getJSONArray("Schedules").getJSONObject(index).put(aKey, modifiedData.get(aKey));
            }
        } else {
            logger.error("Job which you are trying to modify is not exist in XML");
        }
        return existingData;
    }

    public JSONObject modifyJSONObjectById(JSONObject existingData, JSONObject modifiedData) {
        List<String> key = JsonUtils.listKeys(modifiedData);
        for (String aKey : key) {
            existingData.getJSONObject("Schedules").getJSONObject("Schedule").put(aKey, modifiedData.get(aKey));
        }
        return existingData;

    }

    public String convertJsonToXml(JSONObject jsonObject) {
        XMLSerializer serializer = new XMLSerializer();
        serializer.setTypeHintsEnabled(false);
        serializer.setForceTopLevelObject(true);
        serializer.setElementName("Schedule");
        serializer.setRootName("Schedules");
        String result = serializer.write(jsonObject);
        logger.debug("result" + result);
        return result;
    }

    public boolean validateXmlForJSONArray(JSONObject jsonObject) {
        boolean tagAvailable;
        tagAvailable = jsonObject.has("Schedules");

        if (tagAvailable) {
            JSONArray jsonArray;
            jsonArray = jsonObject.getJSONArray("Schedules");
            for (int arrayCount = 0; arrayCount < jsonArray.size(); arrayCount++) {
                tagAvailable = jsonArray.getJSONObject(arrayCount).getJSONObject("SchedulingJob").has("ReportURL");
            }
        } else {
            tagAvailable = false;
        }
        return tagAvailable;
    }

    public void writeIntoFileUsingRandomAccess(String data, String path) {
        RandomAccessFile file;
        File file1 = new File(path);
        try {
            file = new RandomAccessFile(file1, "rw");
            file.write(data.getBytes());
            file.close();
            Runtime.getRuntime().gc();
        } catch (IOException ex) {
            logger.error("Exception stack trace is ", ex);
        }
    }

    public JSONObject getParticularObject(String path, String id) {
        JSONObject jsonObject;
        JSONObject another = new JSONObject();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        jsonObject = processor.getJSONObject(path, true);
        for (int count = 0; count < jsonObject.getJSONArray("Schedules").size(); count++) {
            if (jsonObject.getJSONArray("Schedules").getJSONObject(count).getString("@id").equals(id)) {
                another = jsonObject.getJSONArray("Schedules").getJSONObject(count);
                return another;
            }
        }
        return another;
    }

    /**
     * <p>
     * Get list of id from xml
     * </p>
     *
     * @param path a <code>String</code> specify path of xml
     * @return List of id
     * @see EFWController
     */
    public List<String> getIdFromJson(String path) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonObject;
        jsonObject = processor.getJSONObject(path, true);

        File file = new File(path);
        JSONArray jsonArray;
        List<String> listOfId = new ArrayList<>();
        if (file.exists()) {
            jsonArray = jsonObject.getJSONArray("Schedules");
            for (int arrayCount = 0; arrayCount < jsonArray.size(); arrayCount++) {
                listOfId.add(jsonArray.getJSONObject(arrayCount).getString("@id"));
            }
        }
        return listOfId;
    }

    /**
     * Get the List Of user from scheduling.xml
     *
     * @param path a <code>String</code> specify path of scheduling.xml
     */
    public List<String> getUserNameFromJson(String path) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonObject = processor.getJSONObject(path, true);

        File file = new File(path);
        JSONArray jsonArray;
        List<String> listOfUserName = new ArrayList<>();
        if (file.exists()) {
            jsonArray = jsonObject.getJSONArray("Schedules");
            for (int arrayCount = 0; arrayCount < jsonArray.size(); arrayCount++) {
                final JSONObject object = jsonArray.getJSONObject(arrayCount);
                if (!object.getString("@id").equals("0")) {
                   listOfUserName.add(AuthenticationUtils.getUserId(object));
                }
            }
        }
        return listOfUserName;
    }
}
