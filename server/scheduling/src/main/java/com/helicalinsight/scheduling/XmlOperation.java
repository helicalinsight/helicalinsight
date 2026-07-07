package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controller.EFWController;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TemplateReader;

import net.sf.json.JSONArray;
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
 * XmlOperation
 * This class is responsible for doing XML related operation.
 * this class is no longer used.
 * xml logic will shifted to annotation
 * @author Prashansa
 */
@Deprecated
@SuppressWarnings("unused")
public class XmlOperation {
    private static final Logger logger = LoggerFactory.getLogger(XmlOperation.class);

    /**
     * convertXmlStringIntoJSONArray(String path
     * <p>
     * This method is responsible to convert xml into JSONArray.;
     * </p>
     *
     * @param path a <code>String</code> specify the location of scheduling.xml
     * @return JSONArray
     */
    public JsonArray convertXmlStringIntoJSONArray(String path) {
        JsonArray jsonArray = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            String xml = IOUtils.toString(inputStream, ApplicationUtilities.getEncoding());
            jsonArray = new Gson().fromJson(xml, JsonArray.class);
            logger.debug("jsonArray2:  " + jsonArray);
        } catch (JsonSyntaxException ex) {
            logger.error("JsonSyntaxException occurred", ex);
        } catch (IOException ex) {
            logger.error("An IOException occurred", ex);
        } finally {
            ApplicationUtilities.closeResource(inputStream);
        }
        return jsonArray;
    }
    /**
     * deleteJobFromXml(JsonObject jsonobject, String id)
     * this method is responsible for deleting job from scheduling.xml
     * @param jsonobject    to provide schedule related data
     * @param id            schedule id to delete
     * @return jsonObject
     */
    public JsonObject deleteJobFromXml(JsonObject jsonobject, String id) {
        JsonArray jsonArray;
        jsonArray = jsonobject.getAsJsonArray("Schedules");
        for (int jsonArrayCount = 0; jsonArrayCount < jsonArray.size(); jsonArrayCount++) {
            if (jsonArray.get(jsonArrayCount).getAsJsonObject().get("@id").getAsString().equals(id)) {
                jsonobject.getAsJsonArray("Schedules").remove(jsonArrayCount);

            }
        }
        logger.debug("JSON object after deleted ID" + id + ": " + jsonobject);
        return jsonobject;
    }
    /**
     * createNewFileAndAddJob(JsonObject newData, String path)
     * @param newData           object contains URLs for report 
     * @param path				to create new file
     * @return message "FileCreated" on successful creation.
     */
    public String createNewFileAndAddJob(JsonObject newData, String path) {
        JsonArray jsonArray = new JsonArray();
        logger.debug("Check ReportURL Contains or not:" + newData.has("ReportURL"));
        if (newData.has("ReportURL")) {
            newData.addProperty("@id", 1);
            jsonArray.add(newData);
            String data = convertJsonToString(jsonArray);
            logger.debug("DATA" + data);
            writeStringIntoFile(data, path);
        } else {
            logger.error("ReportURL Not found");
        }
        return "FileCreated";
    }
    /**
     * convertJsonToString(JsonArray jsonArray)
     * @param jsonArray       consists of report urls
     * @return array into string format.
     */
    public String convertJsonToString(JsonArray jsonArray) {
    	return jsonArray.toString();
    }
    /**
     * @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link XmlOperation#convertJsonToString(JsonArray jsonArray)} instead.</p>
     * @param JsonArray jsonArray
     * @return String
     * 
     */
    @Deprecated
    public String convertJsonToXml(JSONArray jsonArray) {
        XMLSerializer serializer = new XMLSerializer();
        serializer.setTypeHintsEnabled(false);
        serializer.setForceTopLevelObject(true);
        serializer.setElementName("Schedule");
        serializer.setRootName("Schedules");
       
        return serializer.write(jsonArray);
    }
    /**
     * writeStringIntoFile(String data, String path)
     * @param data           urls for report
     * @param path			 to create file
     * @return message "ok" on successful file creation
     */
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
    /**
     * addNewJobInExistingXML(JsonObject newData, String path)
     * it creates file for adding new job/schedule.
     * @param newData          object consists of url for report and schedule data.
     * @param path			   to create new file , if exists alredy get schedule data from scheduling.xml
     * @return schedule/job id if file not exists, otherwise you get logger. 
     */
    public String addNewJobInExistingXML(JsonObject newData, String path) {
        File file = new File(path);
        JsonObject jsonObject;
        String writeData;
        int id = 0;
        if (file.exists()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            jsonObject = processor.getJsonObject(path, true);
            boolean validXml = validateXml(jsonObject);
            id = searchMaxIdInXml(jsonObject);
            if (validXml) {
                if (newData.has("ReportURL")) {
                    if (jsonObject.get("Schedules") instanceof JsonArray) {
                        newData.addProperty("@id", id + 1);

                        jsonObject.add("Schedules", newData);

                        writeData = convertJsonToString(jsonObject.getAsJsonArray("Schedules"));
                    } else {
                        newData.addProperty("@id", id + 1);

                        jsonObject.getAsJsonObject("Schedules").add("Schedule", newData);
                        writeData = convertJsonToString(jsonObject.getAsJsonObject("Schedules").getAsJsonArray("Schedule"));
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
    /**
     * validateXml(JsonObject jsonObject)
     * it simple checks the schedule object inside jsonObject.
     * @param jsonObject      object to check schedule data is present or not
     * @return {@code true} if schedule data is present , {@code false} otherwise
     */
    public boolean validateXml(JsonObject jsonObject) {
        boolean tagAvailable;
        tagAvailable = jsonObject.has("Schedules");
        return tagAvailable;
    }
    /**
     * searchMaxIdInXml(JsonObject jsonobject)
     * Returns maximum id number of schedules
     * @param jsonobject        consists of schedule data
     * @return id in integer otherwise returns 0 if jsonObject is {@code null}
     */
    public int searchMaxIdInXml(JsonObject jsonobject) {
        JsonArray jsonArray;
        ArrayList<Integer> arrayList = new ArrayList<>();
        int maxValue = 0;
        boolean validJSon = validateXml(jsonobject);
        if (jsonobject != null) {
            if (validJSon) {
                if (jsonobject.get("Schedules") instanceof JsonArray) {

                    jsonArray = jsonobject.getAsJsonArray("Schedules");

                    for (int jsonArrayCount = 0; jsonArrayCount < jsonArray.size(); jsonArrayCount++) {

                        String ids = jsonArray.get(jsonArrayCount).getAsJsonObject().get("@id").getAsString();
                        int id = Integer.parseInt(ids);
                        arrayList.add(id);
                    }
                } else if (jsonobject.getAsJsonObject("Schedules").get("Schedule") instanceof com.google.gson.JsonObject) {

                    arrayList.add(jsonobject.getAsJsonObject("Schedules").getAsJsonObject("Schedule").get("@id").getAsInt());
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
    /**
     * modifyJobByIdWriteInFile(JsonObject newData, String path)
     * It changes the job id in previously created file.
     * @param newData		 object to provide id	
     * @param path			 path of file to modify job id
     * @return message "ok" .
     */
    public String modifyJobByIdWriteInFile(JsonObject newData, String path) {
        File file = new File(path);
        JsonObject jsonObject;
        JsonArray jsonArray;
        TemplateReader readFile = new TemplateReader(file);
        String xmlData = readFile.readTemplate();
        String writeData;

        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        if (convertStringToJsonElement(xmlData).isJsonArray()) {
            jsonObject = processor.getJsonObject(path, true);
            jsonArray = jsonObject.getAsJsonArray("Schedules");
            boolean checkId = searchId(jsonObject, newData.get("@id").getAsString());
            if (checkId) {
                for (int count = 0; count < jsonArray.size(); count++) {
                    if (jsonArray.get(count).getAsJsonObject().get("@id").getAsString().equals(newData.get("@id").getAsString())) {
                        jsonObject = modifyDataById(jsonObject, newData, count);
                    }
                }
                writeData = convertJsonToString(jsonObject.getAsJsonArray("Schedules"));
                writeStringIntoFile(writeData, path);
            } else {
                logger.error("Job Which you are trying to modify is not available in XML");
            }
        } else {
            jsonObject = processor.getJsonObject(path, true);
            boolean checkId = searchId(jsonObject, newData.get("@id").getAsString());
            if (checkId) {
                jsonObject = modifyJSONObjectById(jsonObject, newData);
                writeData = convertJsonToString(jsonObject.getAsJsonObject("Schedules"));
                writeStringIntoFile(writeData, path);
            } else {
                logger.error("Job Which you are trying to modify is not available in XML");
            }
        }
        return "ok";
    }
    /**
     * convertStringToJsonElement(String xmlData)
     * @param xmlData        Data in string format
     * @return data in the form JsonElement
     */
    public JsonElement convertStringToJsonElement(String xmlData) {
    	JsonElement json;
        json = new Gson().fromJson(xmlData,JsonElement.class);
        return json;
    }

    /**
     * searchId(JsonObject json, String id) 
     * Search id in given JsonObject
     *
     * @param json a <code>JSONObject</code>
     * @param id   a <code>String</code> specify id
     * @return boolean value ,if id exist in JSONObject then return {@code true} else
     * return {@code false}
     */
    public boolean searchId(JsonObject json, String id) {
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
    /**
     * modifyDataById(JsonObject existingData, JsonObject modifiedData, int index)
     * @param existingData        existing schedule data
     * @param modifiedData        new schedule data
     * @param index               to fetch json array from using index
     * @return jsonobject with modified data.
     */
    public JsonObject modifyDataById(JsonObject existingData, JsonObject modifiedData, int index) {
        List<String> key = JsonUtils.listKeys(modifiedData);
        boolean searchId = searchId(existingData, modifiedData.get("@id").getAsString());
        if (searchId) {
            for (String aKey : key) {
                existingData.getAsJsonArray("Schedules").get(index).getAsJsonObject().add(aKey, modifiedData.get(aKey));
            }
        } else {
            logger.error("Job which you are trying to modify is not exist in XML");
        }
        return existingData;
    }
    /**
     * modifyJSONObjectById(JsonObject existingData, JsonObject modifiedData)
     * @param existingData			existing schedule data
     * @param modifiedData			new schedule data
     * @return jsonobject with modified data.
     */
    public JsonObject modifyJSONObjectById(JsonObject existingData, JsonObject modifiedData) {
        List<String> key = JsonUtils.listKeys(modifiedData);
        for (String aKey : key) {
            existingData.getAsJsonObject("Schedules").getAsJsonObject("Schedule").add(aKey, modifiedData.get(aKey));
        }
        return existingData;

    }
    /**
     * convertJsonToString(JsonObject jsonObject)
     * @param jsonObject    object 
     * @return jsonObject in string format
     */
    public String convertJsonToString(JsonObject jsonObject) {
    	return jsonObject.toString();
    }
    /**
     * convertJsonToString 
     * @deprecated
     * This method is no longer acceptable 
	 * <p> Use {@link XmlOperation#convertJsonToString(JsonObject formData)} instead.</p>
     * @param jsonObject
     * @return String
     */
    @Deprecated
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
    /**
     * validateXmlForJSONArray(JsonObject jsonObject)
     * this method checks object consists of report url or not.
     * @param jsonObject  provides schedule data
     * @return {@code true} if object contains url array, otherwise returns {@code false}
     */
    public boolean validateXmlForJSONArray(JsonObject jsonObject) {
        boolean tagAvailable;
        tagAvailable = jsonObject.has("Schedules");

        if (tagAvailable) {
            JsonArray jsonArray;
            jsonArray = jsonObject.getAsJsonArray("Schedules");
            for (int arrayCount = 0; arrayCount < jsonArray.size(); arrayCount++) {
                tagAvailable = jsonArray.get(arrayCount).getAsJsonObject().getAsJsonObject("SchedulingJob").has("ReportURL");
            }
        } else {
            tagAvailable = false;
        }
        return tagAvailable;
    }
    /**
     * writeIntoFileUsingRandomAccess(String data, String path)
     * @param data          data to be written into the file
     * @param path			path of the file where the data will be written.
     */
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
    /**
     * getParticularObject(String path, String id)
     * @param path           path for scheduling.xml
     * @param id			 schedule id
     * @return jsonObject containing  schedule object
     */
    public JsonObject getParticularObject(String path, String id) {
        JsonObject jsonObject;
        JsonObject another = new JsonObject();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        jsonObject = processor.getJsonObject(path, true);
        for (int count = 0; count < jsonObject.getAsJsonArray("Schedules").size(); count++) {
            if (jsonObject.getAsJsonArray("Schedules").get(count).getAsJsonObject().get("@id").getAsString().equals(id)) {
                another = jsonObject.getAsJsonArray("Schedules").get(count).getAsJsonObject();
                return another;
            }
        }
        return another;
    }

    /**
     * getIdFromJson(String path)
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
        JsonObject jsonObject;
        jsonObject = processor.getJsonObject(path, true);

        File file = new File(path);
        JsonArray jsonArray;
        List<String> listOfId = new ArrayList<>();
        if (file.exists()) {
            jsonArray = jsonObject.getAsJsonArray("Schedules");
            for (int arrayCount = 0; arrayCount < jsonArray.size(); arrayCount++) {
                listOfId.add(jsonArray.get(arrayCount).getAsJsonObject().get("@id").getAsString());
            }
        }
        return listOfId;
    }

    /**
     * getUserNameFromJson(String path)
     * Get the List Of user from scheduling.xml
     * @param path a <code>String</code> specify path of scheduling.xml
     * @return {@code List} Of user.
     */
    public List<String> getUserNameFromJson(String path) {
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JsonObject jsonObject = processor.getJsonObject(path, true);

        File file = new File(path);
        JsonArray jsonArray;
        List<String> listOfUserName = new ArrayList<>();
        if (file.exists()) {
            jsonArray = jsonObject.getAsJsonArray("Schedules");
            for (int arrayCount = 0; arrayCount < jsonArray.size(); arrayCount++) {
                final JsonObject object = jsonArray.get(arrayCount).getAsJsonObject();
                if (!object.get("@id").getAsString().equals("0")) {
                    listOfUserName.add(AuthenticationUtils.getUserId(object));
                }
            }
        }
        return listOfUserName;
    }
}
