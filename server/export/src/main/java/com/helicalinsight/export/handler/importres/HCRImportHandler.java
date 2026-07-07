package com.helicalinsight.export.handler.importres;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.services.AbstractHCRConnectionSave;
import com.helicalinsight.adhoc.services.HCREfwConnectionSaveHandler;
import com.helicalinsight.adhoc.services.HCRGlobalConnectionSaveHandler;
import com.helicalinsight.adhoc.utils.DashboardUtils;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component("hcrImportHandler")
@Scope("prototype")
public class HCRImportHandler extends AbstractResourceImportHandler {

    @Autowired
    private EFWDConnectionService efwdConnectionService;

    @Autowired
    private HIResourceConstituentMappingService mappingService;

    /**
     * Imports an hcrReport resource.
     *
     * @param resourceUrl URL of the hcrReport resource to import.
     * @return The imported {@link HIResource} representing the hcrReport resource.
     */
    @Override
    public HIResource importResource(String resourceUrl) {

        String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
        String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
        parentUrl = StringUtils.chop(parentUrl);
        String onConflict = context.getRequest().getOnConflict();
        HIResourceHCR report = fileReader.read(context, resourceUrl, HIResourceHCR.class);
        HIResource resource = serviceDb.getResourceByUrl(resourceUrl, Deleted.FALSE);
        String previewFormDataJson = report.getPreviewFormData();
        String diagramString = report.getDiagram();

        //--------------------handle image-----------------------
        Map<Integer, String> newOldImageIds = context.getNewOldImageIds();
        List<Integer> requiredImageId = new ArrayList<>();
        if (previewFormDataJson.contains("imageResourceId")) {

            List<String> jsonStringFromLargeJson = DashboardUtils.getJsonArrayStringFromLargeJson("image", previewFormDataJson);
            List<String> replaceImageId = new ArrayList<>();

            Map<String,String> oldPathNewPath = new HashMap<>();
            for (String imageArray : jsonStringFromLargeJson) {
                JsonElement element = JsonParser.parseString(imageArray);
                if (element.isJsonArray()) {
                    JsonArray jsonArray = element.getAsJsonArray();
                    for (JsonElement elem : jsonArray) {
                        JsonObject elemnt = elem.getAsJsonObject();
                        if (elemnt.has("imageResourceId")) {
                            Integer imageResourceId = elemnt.get("imageResourceId").getAsInt();

                            String idUrl = newOldImageIds.get(imageResourceId);
                            
                            if (StringUtils.isBlank(idUrl)) continue;
                            
                            String[] idSplit = idUrl.split(":");
                            String urlOfImage = idSplit[1];
                            if (urlOfImage != null) {
                                Path path = Paths.get(urlOfImage);
                                Path parentDir = path.getParent();
                                Path fileImage = path.getFileName();
                                String dir = elemnt.get("dir").getAsString();
                                String file = elemnt.get("file").getAsString();
                                oldPathNewPath.put(dir+"/"+file,urlOfImage);
                                elemnt.addProperty("dir", parentDir.toString());
                                elemnt.addProperty("file", fileImage.toString());
                                Integer rqImage = Integer.valueOf(idSplit[0]);
                                requiredImageId.add(rqImage);
                                elemnt.addProperty("imageResourceId", rqImage);

                            }
                        }

                    }
                    replaceImageId.add(element.toString());
                }
            }

            int counter = 0;
            for (String imageArray : jsonStringFromLargeJson) {
                previewFormDataJson = previewFormDataJson.replace(imageArray, replaceImageId.get(counter));
                counter++;
            }


            for(String key:oldPathNewPath.keySet()){
                diagramString = diagramString.replace(key,oldPathNewPath.get(key));
            }


        }

        //handle image


        JsonObject asJsonObject = JsonParser.parseString(previewFormDataJson).getAsJsonObject();
        JsonArray connectionArray = null;
        JsonArray connectionArrayEfwd = null;
        if (asJsonObject.has("connectionDbDetails")) {
            connectionArray = asJsonObject.get("connectionDbDetails").getAsJsonArray();
        }
        if (asJsonObject.has("connectionDbDetailsEfwd")) {
            connectionArrayEfwd = asJsonObject.get("connectionDbDetailsEfwd").getAsJsonArray();
            connectionArray.addAll(connectionArrayEfwd);
        }
        asJsonObject.remove("connectionDbDetails");
        asJsonObject.remove("connectionDbDetailsEfwd");

        previewFormDataJson = asJsonObject.toString();
        report.setPreviewFormData(previewFormDataJson);
        Boolean isSkip = false;
        if (null != resource) {
            if (onConflict.equalsIgnoreCase("update") && context.recover(resource)) {

                HIResourceHCR dbReport = resource.getHiResourceHCR();
                dbReport.setFileName(report.getFileName());
                dbReport.setState(report.getState());

                dbReport.setDiagram(diagramString);
                dbReport.setPreviewFormData(report.getPreviewFormData());

                Date date = context.getDate();
                dbReport.setLastUpdatedTime(date);
                resource.setCreatedBy(report.getCreatedBy());
                resource.setLastUpdatedTime(context.getDate());
                Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy() :
                        Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
                if (report.getCreatedBy() == null) {
                    resource.setCreatedBy(null);
                    dbReport.setCreatedBy(null);
                } else {
                    resource.setCreatedBy(importedBy);
                    dbReport.setCreatedBy(importedBy);
                }

                resource.setTitle(report.getFileName());
                resource.setHiResourceHCR(dbReport);
                serviceDb.editHIResource(resource);
                context.appendUpdate(resource.getResourceURL());
                report = dbReport;
            } else {
                isSkip = true;
                context.appendSkip(resource.getResourceURL());
            }
        } else {
            resource = createNewReport(report, fileName, parentUrl, resourceUrl);
            context.appendInsert(resource.getResourceURL());
        }


        if (!isSkip) {
            List<String> oldMappingNewMapping = new ArrayList<>();
            List<String> oldMappingNewMappingEfwd = new ArrayList<>();
            boolean dsExists = manifestUtils.compareOptions(context.getRequest().getOptions(), context.getManifest(), "datasource");
            if (dsExists) {
                String dsFileName = manifestUtils.getDatasource(context.removeDestination(resourceUrl), context.getManifest());
                if (null != dsFileName) {
                    DatasourceHandler dsHandler = DatasourceFactory.getHandler("global.jdbc");
                    dsHandler.setContext(context);
                    oldMappingNewMapping = dsHandler.importResourceHCR(dsFileName, onConflict);
                }
            }
            Integer hcrId = resource.getResourceId();
            
           ResourceType resourceType =  resourceTypeService.getResourceTypeByTypeAndExtension("image","." + JsonUtils.getImageExtension());
            
            mappingService.deleteChildrenByParentIdAndType(hcrId, resourceType.getResourceTypeId());
            List<HIResourceMapping> mappingList = new ArrayList<HIResourceMapping>();
            for (Integer imgId : requiredImageId) {
                HIResourceMapping mapping = new HIResourceMapping();
                mapping.setParentResource(resource);
                HIResource imageResource = null; 
                
                try {
                 	imageResource = serviceDb.getHIResourceById(imgId);
                }
                catch (EfwServiceException resourceNotFoundException) {
                	// image resource not found.. skipping the image
                	continue;
				}
                mapping.setChildResource(imageResource);
                mappingList.add(mapping);
            }

            mappingService.saveBatch(mappingList);

            //for efwd
            String dsFileNameEfwd = manifestUtils.getDatasource(context.removeDestination(resourceUrl) + "efwd", context.getManifest());
            if (null != dsFileNameEfwd) {
                DatasourceHandler dsHandler = DatasourceFactory.getHandler("folder");
                dsHandler.setContext(context);
                oldMappingNewMappingEfwd = dsHandler.importResourceHCR(dsFileNameEfwd, onConflict);
            }

            shareHandler.setContext(context);
            shareHandler.importResource(resource, context.getRequest(), context.getManifest());
            if (Boolean.TRUE.equals(context.getRequest().getOptions().getSchedules())) {
                ResourceIOHandler scheduleHandler = (ResourceIOHandler) ApplicationContextAccessor.getBean("scheduleIOHandler");
                scheduleHandler.setContext(context);
                scheduleHandler.importResource(resource, resourceUrl, onConflict);
            }

            List<HIHcrConnections> hiHcrConnections = efwdConnectionService.fetchAllHcrConnectionsByResourceId(resource.getResourceId());
            hiHcrConnections.forEach(hcrCon -> {
                efwdConnectionService.deleteHiHcrConnection(hcrCon);
            });
            String stateJson = report.getState();
            List<String> allConn = DashboardUtils.getJsonStringFromLargeJson("connectionDetails", stateJson);

            Map<Integer, Integer> globalMapping = new HashMap<>();
            for (String ss : oldMappingNewMapping) {
                String kv[] = ss.split(":");
                Integer key = Integer.valueOf(kv[0]);
                Integer value = Integer.valueOf(kv[1]);
                globalMapping.put(key, value);
            }
            Map<Integer, Integer> efwdMapping = new HashMap<>();
            for (String ss : oldMappingNewMappingEfwd) {
                String kv[] = ss.split(":");
                Integer key = Integer.valueOf(kv[0]);
                Integer value = Integer.valueOf(kv[1]);
                efwdMapping.put(key, value);
            }
            List<String> newString = new ArrayList<>();
            for (String s : allConn) {
                for (Integer key : globalMapping.keySet()) {
                    if (s.contains("global.jdbc") && s.contains("" + key)) {
                        newString.add(s.replace("id\":\"" + key, "id\":\"" + globalMapping.get(key)));
                    }
                }
                for (Integer key : efwdMapping.keySet()) {
                    if (!s.contains("global.jdbc") && s.contains("" + key)) {
                        newString.add(s.replace("id\":\"" + key, "id\":\"" + efwdMapping.get(key)));
                    }
                }
            }
            if (allConn.size() == newString.size()) {
                for (int i = 0; i < allConn.size(); i++) {
                    String ss = allConn.get(i);
                    String sn = newString.get(i);
                    stateJson = stateJson.replace(ss, sn);
                }
            }
            JsonObject stateJsonObject = JsonParser.parseString(stateJson).getAsJsonObject();
            JsonObject forEfwd = new JsonObject();
            forEfwd.add("prv", asJsonObject);
            forEfwd.add("stt", stateJsonObject);

            JsonObject newFd = setEfwd(forEfwd, resource, report, connectionArray, globalMapping, efwdMapping);
            report.setPreviewFormData(newFd.get("prv").getAsJsonObject().toString());
            report.setState(newFd.get("stt").getAsJsonObject().toString());
            resource.setHiResourceHCR(report);
            serviceDb.editHIResource(resource);
        }
        return resource;
    }

    private JsonObject setEfwd(JsonObject formDataJson, HIResource hiResource,
                               HIResourceHCR hiResourceHCR, JsonArray efwdJsonObjects,
                               Map<Integer, Integer> globalMapping, Map<Integer, Integer> efwdMapping) {

        String formDataString = formDataJson.toString();
        for (JsonElement object : efwdJsonObjects) {

            JsonObject efwdJsonObject = object.getAsJsonObject();


            if (efwdJsonObject != null) {
                addEfwdDetailsToHcr(efwdJsonObject, hiResource, globalMapping, efwdMapping);
                String fileName = efwdJsonObject.get("fileName").getAsString();
                String conId = efwdJsonObject.get("dbConnectionId").getAsString();
                formDataString = formDataString.replace(fileName, conId + "hi_hcr_db");
            }
        }
        JsonObject newFd = JsonParser.parseString(formDataString).getAsJsonObject();
        return newFd;
    }


    public HIResource createNewReport(HIResourceHCR report, String fileName, String parentUrl, String resourceUrl) {
        Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? report.getCreatedBy() : null;
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        HIResource resource = ResourceUtils.newHIResource(JsonUtils.getHCRExtension(), context.getDate(), ownerId, resourceUrl, fileName, report.getFileName(), null, report.getCreatedBy() == null);
        HIResource parent = context.getResourceUrlMap().get(parentUrl + "." + JsonUtils.getFolderFileExtension());
        if (null != parent) resource.setParentId(parent.getResourceId());
        report.setCreatedBy(resource.getCreatedBy());
        report.setLastUpdatedTime(context.getDate());
        report.setCreatedDate(context.getDate());
        resource.setHiResourceHCR(report);
        serviceDb.addHIResource(resource);
        return resource;
    }



    private void addEfwdDetailsToHcr(JsonObject efwdJsonObject, HIResource hiResource,
                                     Map<Integer, Integer> globalMapping, Map<Integer, Integer> efwdMapping) {

        AbstractHCRConnectionSave abstractHCRConnectionSave = null;
        JsonObject dataSources = efwdJsonObject.getAsJsonObject("DataSources");
        JsonObject obj = dataSources.getAsJsonObject("Connection");
        if (obj.has("globalId")) {
            Integer old = obj.get("globalId").getAsInt();
            Integer newId = globalMapping.get(old);
            obj.remove("globalId");
            obj.addProperty("globalId", newId);
        }
        if (obj.has("efwdId")) {
            Integer old = obj.get("efwdId").getAsInt();
            Integer newId = efwdMapping.get(old);
            obj.remove("efwdId");
            obj.addProperty("efwdId", newId);
        }
        String type = obj.get("type").getAsString();
        if (type.equals("sql.jdbc") || type.equals("sql.jdbc.groovy") || type.equals("sql.jdbc.groovy.managed")) {
            abstractHCRConnectionSave = ApplicationContextAccessor.getBean(HCREfwConnectionSaveHandler.class);
        } else {
            abstractHCRConnectionSave = ApplicationContextAccessor.getBean(HCRGlobalConnectionSaveHandler.class);
        }
        HIHcrConnections hiHcrConnections = abstractHCRConnectionSave.addHcrConnection(type, hiResource);
        abstractHCRConnectionSave.saveConnection(obj);
        efwdJsonObject.addProperty("dbConnectionId", "" + hiHcrConnections.getId());

        JsonObject dataMapArray = efwdJsonObject.get("DataMaps").getAsJsonObject().get("DataMap").getAsJsonObject();
        if (abstractHCRConnectionSave != null) {
            abstractHCRConnectionSave.addQueryAndParams(dataMapArray);
        }
    }

}
