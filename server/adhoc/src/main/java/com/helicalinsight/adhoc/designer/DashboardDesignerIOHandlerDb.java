package com.helicalinsight.adhoc.designer;
import  com.helicalinsight.adhoc.utils.DashboardUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.Format;
import java.text.SimpleDateFormat;

import static com.helicalinsight.resourcedb.ResourceConstants.ResourceNameSize;

/**
 * @author Somen
 */
public class DashboardDesignerIOHandlerDb implements IComponent {


    private static final Logger logger = LoggerFactory.getLogger(DashboardDesignerIOHandlerDb.class);

    @NotNull
    private Integer saveHtml(String dir, String htmlString, String uuid, HIResourceServiceDB serviceDB) {

        HIResource resourceByUrl = serviceDB.getResourceByUrl(dir);
        ResourceEfwContents efwContents = new ResourceEfwContents();
        efwContents.setContent(htmlString.getBytes());
        efwContents.setResourceId(resourceByUrl.getResourceId());
        efwContents.setFileName(uuid + ".html");
        efwContents.setContentType("html");
        Integer idOfHtml = serviceDB.addHIResourceEFWContents(efwContents);
        return idOfHtml;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        return null;
    }

    @Override
    public JsonObject componentLogic(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIResourceConstituentMappingService pathService = ApplicationContextAccessor.getBean(HIResourceConstituentMappingService.class);
        ResourceTypeServiceDB resourceTypeService = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);
        if (formJson.has("uuid")) {
            String uuid = formJson.get("uuid").getAsString();
            if (StringUtils.isBlank(uuid)) {
                throw new IllegalArgumentException("The parameter uuid is null or empty");
            }

            DashboardDesignerUpdateHandlerDb editComponent = new DashboardDesignerUpdateHandlerDb();
            return editComponent.componentLogic(jsonFormData);
        }

        String state = formJson.get("state").toString();
        List<Integer> idList = new ArrayList<Integer>();
        ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
        JsonObject settingJson = applicationSettings.getSettingJson();
        Boolean autoSyncCutPasteDesigner = settingJson.get("autoSyncCutPasteDesigner").getAsBoolean();

        if(autoSyncCutPasteDesigner) {
            List<String> pathList = DashboardUtils.getAllPathInState(state);
            idList = DashboardUtils.getIdFromDb(pathList, serviceDB);
        }
        String name = formJson.get("fileName").getAsString();

        if(name.length()<3 ){
            throw  new EfwServiceException("At least 3 characters has to be provided for report name");
        }



        String dir = formJson.get("dir").getAsString();
        String htmlString = formJson.get("htmlString").getAsString();
        String description = formJson.get("description")!=null?formJson.get("description").getAsString():null;
        String icon = formJson.get("icon")!=null?formJson.get("icon").getAsString():null;
        String style = formJson.get("style")!=null?formJson.get("style").getAsString():null;
        String uuid = UUID.randomUUID().toString();

        Integer htmlId = saveHtml(dir, htmlString, uuid, serviceDB);

        String resourcePath=DBProcessor.checkAndReplaceSpecialChars(name).trim();
        String resourceURL=dir + "/"+DBProcessor.checkAndReplaceSpecialChars(name).trim()+"."+JsonUtils.getEfwExtension();
        Integer efwId=null;

        ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);

        resourcePath=DBProcessor.checkAndReplaceSpecialChars(name).trim();
        resourceURL=dir+"/"+DBProcessor.checkAndReplaceSpecialChars(name).trim()+"."+JsonUtils.getDesignerExtension();

        HIResourceEFWDD designer = designerObject(state, description, efwId,name);
        HIResource designerResource = ResourceUtils.newHIResource(dir, false, name, resourcePath, resourceURL,JsonUtils.getDesignerExtension());
        designerResource.setHiResourceEFWDD(designer);
        try{
            Integer efwddId = serviceDB.addHIResource(designerResource);

            if(idList!=null && idList.size()>0) {
                
            	pathService.deleteChildrenByParentId(efwddId);
            	
            	ResourceType resourceType =  resourceTypeService.getResourceTypeByTypeAndExtension("hr", "." + JsonUtils.getHrReportExtension());
            	
                pathService.deleteChildrenByParentIdAndType(efwddId, resourceType.getResourceTypeId());
               
                List<HIResourceMapping> mappingList = new ArrayList<>();
                
                for (Integer id : idList) {
                    HIResourceMapping resourceEfwddResource = new HIResourceMapping();
                    resourceEfwddResource.setParentResource(designerResource);
                    HIResource hReportResource = null;
                    try {
                    	hReportResource =  serviceDB.getHIResourceById(id);
                    }
                    catch (EfwServiceException resourceNotFoundException) {
                    	continue;
					}
                    resourceEfwddResource.setChildResource(hReportResource);
                    mappingList.add(resourceEfwddResource);
                }
                pathService.saveBatch(mappingList);

            }

            resourcePath = designerResource.getResourcePath();
        }catch (Exception e){
            try{
                Format secondsFormat = new SimpleDateFormat("ss");
                resourceURL = dir + "/" + DBProcessor.checkAndReplaceSpecialChars(name).trim() + "_" + secondsFormat.format(new Date()).substring(0, 2)+"."+JsonUtils.getDesignerExtension();
                resourcePath = DBProcessor.checkAndReplaceSpecialChars(name).trim() + "_" + secondsFormat.format(new Date()).substring(0, 2);
                designerResource.setResourceURL(resourceURL);
                designerResource.setResourcePath(resourcePath);
                Integer efwddId = serviceDB.addHIResource(designerResource);
            }catch (Exception innerException){
                logger.error(innerException.getMessage());
                e.printStackTrace();
            }
        }

        JsonObject responseJson = new JsonObject();
        GsonUtility.accumulate(responseJson,"uuid", resourcePath);
        GsonUtility.accumulate(responseJson,"message", "Design is saved successfully");

        FileInfo designerFileInfo = bean.prepareFileInfo(dir,resourcePath+"."+JsonUtils.getDesignerExtension());
        JsonArray array=new JsonArray();
        JsonObject designerFileInfoObject = JsonParser.parseString(new Gson().toJson(designerFileInfo)).getAsJsonObject();
        array.add(designerFileInfoObject);
        responseJson.add("data", array);
        //responseJson.accumulate("data", efwFileInfo);

        return responseJson;
    }

    private HIResourceEFWDD designerObject(String state, String description, Integer efwLocation,String name) {

        HIResourceEFWDD designer = new HIResourceEFWDD();
        designer.setDescription(description == null ? "Efw Dashboard Designer" : description);
//        if(null!=efwLocation){
//            designer.setEfw(efwLocation);
//        }
        designer.setState(state);
        designer.setFileName(name);
        designer.setCreatedDate(new Date());
        if(StringUtils.isNotEmpty(AuthenticationUtils.getUserId())){
            designer.setCreatedBy(Integer.parseInt(AuthenticationUtils.getUserId()));
        }
        return designer;
    }


    private HIResourceEFW efwObject(String icon, String style, String name, String description,
                                    Integer htmlId) {

        HIResourceEFW efw = new HIResourceEFW();
        efw.setTitle(name);
        efw.setAuthor(AuthenticationUtils.getUserName());
        efw.setDescription(description == null ? "Efw File" : description);
        efw.setIcon(icon);
        efw.setEfwContentId(htmlId);
        efw.setVisible(true);
        efw.setStyle(style);
        if(StringUtils.isNotEmpty(AuthenticationUtils.getUserId())){
            efw.setCreatedBy(Integer.parseInt(AuthenticationUtils.getUserId()));
        }
        efw.setCreatedDate(new Date());
        return efw;

    }
}
