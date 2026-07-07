package com.helicalinsight.adhoc.designer;

import com.google.gson.JsonElement;
import  com.helicalinsight.adhoc.utils.DashboardUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.utility.JaxbUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Somen
 */
@SuppressWarnings("unused")
public class DashboardDesignerFetchHandlerDb implements IComponent {

    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public JsonObject componentLogic(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
        JsonObject settingJson = applicationSettings.getSettingJson();
        Boolean autoSyncCutPasteDesigner = settingJson.get("autoSyncCutPasteDesigner").getAsBoolean();


        JsonObject responseJson = new JsonObject();

        String file = formJson.getString("file");

        if (file == null) {
            return null;
        }

        String dir = formJson.getString("dir");

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceConstituentMappingService pathService = ApplicationContextAccessor.getBean(HIResourceConstituentMappingService.class);
        HIResource designerResource = serviceDB.getResourceByUrl(dir + "/" + file);

        if (null==designerResource) {
            throw new IllegalArgumentException("Aborting operation. There is no designer " + "resource with the " +
                    "specified name.");
        }


        HIResourceEFWDD designerDD = designerResource.getHiResourceEFWDD();
        if(null!=designerDD){
            String state = designerDD.getState();
            List<HIResourceMapping> allPath = pathService.findByParentId(designerResource.getResourceId());
            if(autoSyncCutPasteDesigner) {
                List<String> pathList = DashboardUtils.getAllPathInState(state);
                List<String> newPathList = new ArrayList<>();
                Integer count=0;
                JSONArray jsonArray = new JSONArray();
                for (HIResourceMapping hiResourceEfwddResource : allPath) {
                    HIResource rsource = serviceDB.getResourceByIdIgnoreFilter(hiResourceEfwddResource.getChildResource().getResourceId());
                    JSONObject jsonObject = new JSONObject();
                    String path=null;
                    String directory="";
                    String fileName="";
                    if(rsource!=null){
                        path= rsource.getResourceURL();
                        Path pathd = Paths.get(path);

                        // Extract the directory
                        directory = pathd.getParent().toString();

                        // Extract the file name
                        fileName = pathd.getFileName().toString();
                    }
                    newPathList.add(path);

                    jsonObject.put("order", count++);
                    jsonObject.put("path", path);
                    jsonObject.put("dir", directory);
                    jsonObject.put("file", fileName);
                    jsonObject.put("resourceId", hiResourceEfwddResource.getChildResource().getResourceId());

                    // Add the JsonObject to the JsonArray
                    jsonArray.add(jsonObject);
                }

                //state = DashboardUtils.replacePath(state, newPathList);
                responseJson.add("report_paths", JsonParser.parseString(jsonArray.toString()));
            }
            JsonElement value = JsonParser.parseString(state);
            responseJson.add("state", value);

            responseJson.addProperty("reportName", designerDD.getFileName());

        }

        return responseJson;

    }

    @Override
    public String executeComponent(String jsonFormData) {
        return null;
    }
}
