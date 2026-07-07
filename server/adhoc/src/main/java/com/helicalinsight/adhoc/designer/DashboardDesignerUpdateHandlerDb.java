package com.helicalinsight.adhoc.designer;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.adhoc.utils.DashboardUtils;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.Nullable;
import java.util.*;

/**
 * @author Somen
 */
public class DashboardDesignerUpdateHandlerDb implements IComponent {

    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        return null;
    }

    @Nullable
    @Override
    public JsonObject componentLogic(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();

        if (formJson.get("uuid").isJsonNull()) {
            return null;
        }

        String uuid = formJson.get("uuid").getAsString();




        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceConstituentMappingService pathService = ApplicationContextAccessor.getBean(HIResourceConstituentMappingService.class);

        String state = formJson.get("state").toString();

        List<Integer> idList = new ArrayList<Integer>();
        ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
        JsonObject settingJson = applicationSettings.getSettingJson();
        Boolean autoSyncCutPasteDesigner = settingJson.get("autoSyncCutPasteDesigner").getAsBoolean();

        if (autoSyncCutPasteDesigner) {
            List<String> pathList = DashboardUtils.getAllPathInState(state);
            idList = DashboardUtils.getIdFromDb(pathList, serviceDB);
        }
        String dir = formJson.get("dir").getAsString();
        String htmlString = formJson.get("htmlString").getAsString();
        String description = formJson.get("description") != null ? formJson.get("description").getAsString() : null;
        String icon = formJson.get("icon") != null ? formJson.get("icon").getAsString() : null;
        String style = formJson.get("style") != null ? formJson.get("style").getAsString() : null;


        // Update fileName, state and description if available

        String ddExtension = JsonUtils.getDesignerExtension();


        if (uuid.endsWith(ddExtension)) {
            uuid = uuid.substring(0, uuid.lastIndexOf('.'));
        }
        String designerFileName = uuid + "." + ddExtension;
        HIResource designerResource = serviceDB.getResourceByUrl(dir + "/" + designerFileName);
        Integer dasboardId = designerResource.getResourceId();

        if (idList != null && idList.size() >= 0) {
            pathService.deleteChildrenByParentId(dasboardId);
            
            List<HIResourceMapping> list = new ArrayList<>();
            
            for (Integer id : idList) {
                HIResourceMapping resourceEfwddResource = new HIResourceMapping();
                resourceEfwddResource.setParentResource(designerResource);
                
                HIResource hreportResource = null;
                
                try {
                	hreportResource = serviceDB.getHIResourceById(id);
                }
                catch (EfwServiceException resourceNotFoundException) {
                	continue;
				}
                
                resourceEfwddResource.setChildResource(hreportResource);
                list.add(resourceEfwddResource);
            }
            pathService.saveBatch(list);

        }

        if (designerResource == null) {
            throw new IllegalArgumentException("Aborting operation. There is no designer " + "resource with the " +
                    "specified name.");
        }

        HIResourceEFWDD dashboardDesigner = designerResource.getHiResourceEFWDD();

        if (description != null) {
            dashboardDesigner.setDescription(description);
        }

        // Edit and Save the designer
        dashboardDesigner.setState(state);
        Date lastUpdatedTime = new Date();
        designerResource.setLastUpdatedTime(lastUpdatedTime);
        dashboardDesigner.setLastUpdatedTime(lastUpdatedTime);
        serviceDB.editHIResource(designerResource);


        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
        FileInfo designerFileInfo = bean.prepareFileInfo(dir, designerFileName);
//        FileInfo efwFileInfo= bean.prepareFileInfo(dir,efwFileName);
        JsonObject responseJson;
        responseJson = new JsonObject();
        GsonUtility.accumulate(responseJson,"uuid", uuid);
        GsonUtility.accumulate(responseJson,"message", "Design is edited successfully");
        JsonArray array=new JsonArray();
        JsonObject designerFileInfoObject = JsonParser.parseString(new Gson().toJson(designerFileInfo)).getAsJsonObject();

        array.add(designerFileInfoObject);
        responseJson.add("data", array);
//        responseJson.accumulate("data", efwFileInfo);
        return responseJson;
    }
}
