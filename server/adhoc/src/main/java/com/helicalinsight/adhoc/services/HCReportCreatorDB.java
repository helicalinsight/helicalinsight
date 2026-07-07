package com.helicalinsight.adhoc.services;

import com.helicalinsight.adhoc.utils.*;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.datasource.HCRUtils;
import static com.helicalinsight.resourcedb.processor.DBProcessor.checkAndReplaceSpecialChars;

import com.helicalinsight.efw.resourceprocessor.IProcessor;

import java.util.*;
import java.util.regex.Pattern;

import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.utils.ResourceInfoUtility;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efwce.EFWCEUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.*;

public class HCReportCreatorDB implements IComponent {
	private static final Logger logger = LoggerFactory.getLogger(HCReportCreatorDB.class);
	
    private final HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
    private final ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);
    private final EFWDConnectionService efwdConnectionService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
    private final HIResourceConstituentMappingService mappingService = ApplicationContextAccessor.getBean(HIResourceConstituentMappingService.class);
    
    public String executeComponent(String jsonFormData) {
        HIResource hiResource;
        HIResourceHCR hiResourceHCR;
        Date createdDate = new Date();
        JsonObject formDataJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonObject result = new JsonObject();
        String dir = GsonUtility.optStringValue(formDataJson, "dir", null);
        String path, title, fileName;
        if (dir != null && formDataJson.has("state")) {
            title = formDataJson.get("name").getAsString();
            fileName = checkAndReplaceSpecialChars(title);
            String uuid = GsonUtility.optStringValue(formDataJson, "uuid", null);
            if (uuid == null || uuid.isEmpty())
                uuid = fileName + '.' + JsonUtils.getHCRExtension();
            HIResource parentHiResource = serviceDB.getResourceByUrl(dir);
            if (parentHiResource == null) {
                throw new ResourceNotFoundException("Folder Not existed");
            }
            path = dir + '/' + uuid;
            hiResource = serviceDB.getResourceByUrl(path);
            String createdBy = SecurityUtils.securityObject().getCreatedBy();
            if (hiResource == null || formDataJson.has("saveUUID")) {
                hiResource = new HIResource();
                hiResource.setCreatedBy(Integer.valueOf(createdBy));
                hiResource.setCreated_date(createdDate);
                hiResourceHCR = new HIResourceHCR();
                hiResourceHCR.setCreatedDate(createdDate);
                hiResourceHCR.setCreatedBy(Integer.valueOf(createdBy));
            } else {
                hiResourceHCR = hiResource.getHiResourceHCR();
            }
            hiResource.setFolder(false);
            hiResource.setMigrated(false);
            hiResource.setVisible(true);
            hiResource.setTitle(title);
            hiResource.setLastUpdatedTime(createdDate);
            hiResource.setResourceType(resourceTypeServiceDB
                    .getResourceTypeByTypeAndExtension(JsonUtils.getHCRExtension(), '.' + JsonUtils.getHCRExtension()));
            setHcr(path, hiResourceHCR, title, formDataJson, createdDate);
            if (formDataJson.has("previewFormData")) {
                setPreviewData(hiResourceHCR, dir, formDataJson);
            }
            hiResource.setHiResourceHCR(hiResourceHCR);
            hiResource.setParentId(parentHiResource.getResourceId());
            hiResource.setResourceURL(path);
            int dotIndex = uuid.lastIndexOf(".");

            // If a dot is found, return the substring before it; otherwise, return the full name
            String fileNameWithoutExtension = (dotIndex == -1) ? uuid : uuid.substring(0, dotIndex);

            hiResource.setResourcePath(fileNameWithoutExtension);
            if (hiResource.getResourceId() == null)
                serviceDB.addHIResource(hiResource);
            JsonObject newFd=formDataJson;
            try {
                newFd=setEfwd(formDataJson, hiResource, hiResourceHCR);
                setHcr(path, hiResourceHCR, title, newFd, createdDate);
                if (newFd.has("previewFormData")) {
                    setPreviewData(hiResourceHCR, dir, newFd);
                }
                serviceDB.editHIResource(hiResource);
            } catch (Exception ex) {
                logger.error("There was an exception for saving the content ", ex);
                ex.printStackTrace();
            }
            result.addProperty("uuid", uuid);
            result.addProperty("name", title);
            result.addProperty("location", dir);
            ResourceInfoUtility bean = ApplicationContextAccessor.getBean(ResourceInfoUtility.class);
            String[] pathSplit = hiResource.getResourceURL().split(Pattern.quote("/"));
            FileInfo fileInfo = bean.prepareFileInfo(dir, pathSplit[pathSplit.length - 1]);
            JsonArray data = new JsonArray();
            JsonObject asJsonObject = JsonParser.parseString(new Gson().toJson(fileInfo)).getAsJsonObject();
            data.add(asJsonObject);
            String previewFormData = newFd.get("previewFormData").toString();
            List<String> jsonStringFromLargeJson = DashboardUtils.getJsonArrayStringFromLargeJson("image", previewFormData);
            List<String> replaceImageId= new ArrayList<>();
            Integer hcrId = hiResource.getResourceId();
            
            List<HIResourceMapping> imageMappingList = new ArrayList<>();
            
            ResourceType resourceType = resourceTypeServiceDB.getResourceTypeByTypeAndExtension("image", "." + JsonUtils.getImageExtension());
            
            
            mappingService.deleteChildrenByParentIdAndType(hcrId, resourceType.getResourceTypeId());
            
            for(String imageArray: jsonStringFromLargeJson){
                JsonElement element = JsonParser.parseString(imageArray);
                if (element.isJsonArray()) {
                    JsonArray jsonArray = element.getAsJsonArray();
                    for (JsonElement elem : jsonArray) {
                        JsonObject elemnt = elem.getAsJsonObject();
                        String file = elemnt.get("file").getAsString();
                        String imgDir = elemnt.get("dir").getAsString();
                        if(StringUtils.isNotBlank(file)&&StringUtils.isNotBlank(dir)) {
                            HIResource resourceByUrl = serviceDB.getResourceByUrl(imgDir + "/" + file);
                            if(resourceByUrl != null) {
                            	HIResourceMapping mapping = new HIResourceMapping();
                            	mapping.setParentResource(hiResource);
                            	mapping.setChildResource(resourceByUrl);
                            	imageMappingList.add(mapping);
                            }
                           
                        }
                            replaceImageId.add(elemnt.toString());

                    }

                }
            }
            
            
            imageMappingList.addAll(addSubreportMapping(hiResource, previewFormData));
            
            mappingService.saveBatch(imageMappingList);
            
            result.add("data", data);
            result.add("state", newFd.get("state"));
            result.add("diagram", newFd.get("diagram"));
            result.add("previewFormData", newFd.get("previewFormData"));
            result.addProperty("message", "Report saved successfully");
        } else {
            String uuid2 = "_temp_" + UUID.randomUUID().toString();
            ;
            dir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
            if (EFWCEUtils.saveTempEfwd(GsonUtility.optJsonObject(formDataJson, "efwd"), dir, uuid2, JsonUtils.getEfwdExtension())) {
                result.addProperty("temp_uuid", uuid2);
            } else {
                result.addProperty("message", "Could not able to save the file in temp.");
            }
        }
        return result.toString();
    }

    
    private List<HIResourceMapping>  addSubreportMapping(HIResource parentResource, String previewFormData) {
    	
    	List<HIResourceMapping> subReportMappingList = new ArrayList<HIResourceMapping>();
    	
        if (!previewFormData.contains("subReport"))  {
        	return subReportMappingList;
        }
    	
    	int hcrId = parentResource.getResourceId();
    	
        JsonArray subReportObjects = HCRUtils.extractAllObjectsFromBands(previewFormData,"subReport");

        if (subReportObjects.isEmpty()) return subReportMappingList;
		
        ResourceType resourceType = resourceTypeServiceDB.getResourceTypeByTypeAndExtension("hcr", "."+JsonUtils.getHCRExtension());
        
		mappingService.deleteChildrenByParentIdAndType(hcrId, resourceType.getResourceTypeId());
		
		for (JsonElement subreportElement : subReportObjects) {
			JsonObject subreportJson = subreportElement.getAsJsonObject();
			Integer resourceId = subreportJson.get("subReportExpression").getAsInt();
			
			HIResource subReportResource = null;
			
			try {
				subReportResource = serviceDB.getHIResourceById(resourceId);
			}
			catch (EfwServiceException resourceNotFoundException) {
				// ignore the missing resources
				logger.info("resource with id {} not present, skipping the resource", resourceId);
				continue;
			}
			
			HIResourceMapping mapping = new HIResourceMapping();
			mapping.setParentResource(parentResource);
			mapping.setChildResource(subReportResource);
			subReportMappingList.add(mapping);
		}
		return subReportMappingList;
	}
    

    private JsonObject setEfwd(JsonObject formDataJson, HIResource hiResource,
                         HIResourceHCR hiResourceHCR) {
        List<JsonObject> efwdJsonObjects = convertTempUUidToJson(formDataJson);
        List<HIHcrConnections> hiHcrConnections = efwdConnectionService.fetchAllHcrConnectionsByResourceId(hiResource.getResourceId());
        hiHcrConnections.forEach(hcrCon->{
            efwdConnectionService.deleteHiHcrConnection(hcrCon);
        });
        String formDataString = formDataJson.toString();
        for (JsonObject efwdJsonObject : efwdJsonObjects) {
            if (efwdJsonObject != null) {
                addEfwdDetailsToHcr(efwdJsonObject, hiResource);
                String fileName= efwdJsonObject.get("fileName").getAsString();
                String conId= efwdJsonObject.get("dbConnectionId").getAsString();
                    formDataString = formDataString.replace(fileName, conId + "hi_hcr_db");
            }
        }
        JsonObject newFd = JsonParser.parseString(formDataString).getAsJsonObject();
        return newFd;
    }

    private void setPreviewData(HIResourceHCR hiResourceHCR, String dir, JsonObject formDataJson) {
        JsonObject formData = JsonParser.parseString(formDataJson.get("previewFormData").toString()).getAsJsonObject();
        //formData=checkForDefaultValues(formData);
        JsonObject connectionDetails;
        if (formData.has("connectionDetails")) {
            connectionDetails = formData.getAsJsonObject("connectionDetails");
            formData.remove("connectionDetails");
            if (connectionDetails.has("efwd"))
                connectionDetails.remove("efwd");
            connectionDetails.add("efwd", formDataJson.getAsJsonObject("efwd"));
            formData.add("connectionDetails", connectionDetails);
        }
        JsonObject saveDetails = new JsonObject();
        saveDetails.addProperty("dir", dir);
        formData.add("saveDetails", saveDetails);
        hiResourceHCR.setPreviewFormData(formData.toString());
    }



    private List<JsonObject> convertTempUUidToJson(JsonObject formDataInc) {
        List<String> allKeyValues = DashboardUtils.findKeyValue("temp_uuid", formDataInc.toString());
        Set<String> uniqueList = allKeyValues.stream().collect(Collectors.toSet());
        List<JsonObject> filesList = new ArrayList<>();
        for (String efwString : uniqueList) {
            JsonObject fileAsJson=new JsonObject();
            if(efwString.replace("hi_hcr_db","").matches("\\d+")){
                fileAsJson = HCRUtils.prepareConnectionJson(efwString);
            }else {
                IProcessor processor = ResourceProcessorFactory.getIProcessor();
                String tempDirectory = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
                String efwdFile = tempDirectory + "/" + efwString + ".efwd";
                fileAsJson = processor.getJsonObject(efwdFile, false);
                }
                 fileAsJson.addProperty("fileName", efwString);
                filesList.add(fileAsJson);

            }
        return filesList;
    }








    private void setHcr(String directory, HIResourceHCR hiResourceHCR, String fileName, JsonObject formDataJson, Date createdDate) {
        hiResourceHCR.setFileName(fileName);
        hiResourceHCR.setState(formDataJson.get("state").toString());
        hiResourceHCR.setLastUpdatedTime(createdDate);
        hiResourceHCR.setDiagram(formDataJson.get("diagram").toString());

    }

    private void addEfwdDetailsToHcr(JsonObject efwdJsonObject, HIResource hiResource) {

        AbstractHCRConnectionSave abstractHCRConnectionSave = null;
        JsonObject dataSources = efwdJsonObject.getAsJsonObject("DataSources");
        JsonObject obj = dataSources.getAsJsonObject("Connection");
            String type = obj.get("type").getAsString();
            if (type.equals("sql.jdbc") || type.equals("sql.jdbc.groovy") || type.equals("sql.jdbc.groovy.managed")) {
                abstractHCRConnectionSave = ApplicationContextAccessor.getBean(HCREfwConnectionSaveHandler.class);
            } else {
                abstractHCRConnectionSave = ApplicationContextAccessor.getBean(HCRGlobalConnectionSaveHandler.class);
            }
        HIHcrConnections hiHcrConnections= abstractHCRConnectionSave.addHcrConnection(type, hiResource);
            abstractHCRConnectionSave.saveConnection(obj);
            efwdJsonObject.addProperty("dbConnectionId",""+hiHcrConnections.getId());

        JsonObject dataMapArray = efwdJsonObject.get("DataMaps").getAsJsonObject().get("DataMap").getAsJsonObject();
        if ( abstractHCRConnectionSave != null) {
            abstractHCRConnectionSave.addQueryAndParams(dataMapArray);
        }
    }


    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
