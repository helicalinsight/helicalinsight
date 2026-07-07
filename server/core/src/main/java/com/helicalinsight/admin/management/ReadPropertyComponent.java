package com.helicalinsight.admin.management;

import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * @author Rajesh.
 *         Created on 11/13/2018.
 */
public class ReadPropertyComponent implements IComponent {
    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String filePath = formJson.getString("filePath");
        String fileName = formJson.getString("fileName");


        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> releaseNoteMap = propertiesFileReader.read(filePath, fileName);
        if (releaseNoteMap == null) {
            throw new OperationFailedException("The given file may not be present. Please check the path/filename.");
        }
        JSONObject releaseNoteJSON = JSONObject.fromObject(releaseNoteMap);
        JSONObject releaseNote = new JSONObject();
        releaseNote.put("releaseNote", releaseNoteJSON);
        return releaseNote.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
