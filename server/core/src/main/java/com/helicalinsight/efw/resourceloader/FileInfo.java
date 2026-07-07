package com.helicalinsight.efw.resourceloader;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * @author Somen
 *         Created by helical021 on 12/29/2020.
 */
public class FileInfo {
    public JSONArray getFileInfo(String dir, String file) {
        String hiRepository = ApplicationProperties.getInstance().getSolutionDirectory();
        String path = dir + "/" + file;
        String pathname = hiRepository + "/" + path;
        File fileOb = new File(pathname);
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        JSONObject jsonFromXml = processor.getJSONObject(pathname, false);
        JSONObject fileInfo = new JSONObject();
        String title = jsonFromXml.optString("reportName");
        fileInfo.put("path", path);
        String extension = FileUtils.getExtension(pathname);
        if (extension.equals(JsonUtils.getEfwExtension())) {
            title = jsonFromXml.optString("title");
        } else if (extension.equals(JsonUtils.getHCRExtension())) {
            title = jsonFromXml.optString("fileName");
        }
        fileInfo.put("title", title);
        fileInfo.put("extension", extension);
        fileInfo.put("lastModified", fileOb.lastModified());

        JSONArray array = new JSONArray();
        array.add(fileInfo);
        return array;

    }
}
