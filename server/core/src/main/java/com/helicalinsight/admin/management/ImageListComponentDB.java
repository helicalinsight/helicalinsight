package com.helicalinsight.admin.management;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;


public class ImageListComponentDB implements IComponent{
	
    private final HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
    private final ResourceEfwContentsService resourceEfwContent = ApplicationContextAccessor.getBean(ResourceEfwContentsService.class);

	@Override
	public String executeComponent(String formData) {
		
		JsonObject response = new JsonObject();
        JsonObject jsonFormData = GsonUtility.parseString(formData, JsonObject.class);
        String action = GsonUtility.optString(jsonFormData, "action");
        String dir =  GsonUtility.optString(jsonFormData, "dir");
        
        HIResource hiResource = getResource(dir);
        
        List<ResourceEfwContents> efwdFileContent=resourceEfwContent.fetchResourceEfwContentByResourceId(hiResource.getResourceId());
        switch (action) {
            case "list":
                List<String> collectedFilterList = efwdFileContent.stream().filter(
                		r -> JsonUtils.supportedImageExtensions.contains(r.getContentType().toLowerCase()))
                .map(r->r.getFileName()).collect(Collectors.toList());
                GsonUtility.accumulate(response, "dir", dir);
                GsonUtility.accumulate(response, "imageList", collectedFilterList);
                break;

            case "delete":
                String fileName = GsonUtility.optString(jsonFormData, "file");
                if(fileName==null || fileName.isEmpty() || !fileName.contains("."))
                	throw new FormValidationException("Please provide correct File Name");
                try {
                	resourceEfwContent.deleteResourceEwfContentByResourceId(fileName,hiResource.getResourceId());
                	GsonUtility.accumulate(response, "message", "File deleted successfully");
                }catch(Exception e) {
                	e.printStackTrace();
                }
                break;
            default:
                 response =  readImage(jsonFormData);
        }
        return response.toString();
	}
	
	
	
	private JsonObject readImage(JsonObject formData) {
		
		String dir = GsonUtility.optString(formData, "dir");
		String fileName = GsonUtility.optString(formData, "file");
		Boolean onlyPath = GsonUtility.optBoolean(formData, "fetchPath");
				
		
		JsonObject response = new JsonObject();
		JsonObject data = new JsonObject();
		
		if ( StringUtils.isBlank(fileName)) {
			String message = "Required properties file  not provided";
			throw new FormValidationException(message);
		}
		
		String resourceUrl = dir + "/" + fileName;
		
		HIResource resource =  getResource(resourceUrl);
		
		
		HIResourceImages imageResource = resource.getHiResourceImages();
		
		String fileType = imageResource.getContentType();
		
		String contentType =  ApplicationUtilities.getContentType(fileType);
		
		GsonUtility.accumulate(data, "title", resource.getTitle());
		GsonUtility.accumulate(data, "fileName", fileName);
		GsonUtility.accumulate(data, "location", dir);
		GsonUtility.accumulate(data, "resourceId", resource.getResourceId());
		
		if ( onlyPath ) {
			String imagePath = createImageAndReturnPath(imageResource,resource.getTitle());
			GsonUtility.accumulate(data, "imagePath", imagePath);
		}
		else {
			String base64Data = Base64.getEncoder().encodeToString(imageResource.getContent());
			String imageData = "data:"+contentType+";base64," + base64Data ;
			GsonUtility.accumulate(data, "imageData", imageData);
		}
		GsonUtility.accumulate(response, "data", data);
		
		return response;
	}
	
	
	private HIResource getResource(String url) {
		HIResource hiResource=serviceDB.getResourceByUrl(url);
        if(hiResource==null) {
        	throw new ResourceNotFoundException("Resource named with "+url+" not found");
        }
        return hiResource;
	}
	
	private String createImageAndReturnPath(HIResourceImages image,  String title) {
		String dir = UUID.randomUUID().toString();
		String imageName =  title + "." + image.getContentType();
		String tempLocation = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
		
		String directoryPath = tempLocation + File.separator + dir;
		
		FileUtils.createDirectory(new File(directoryPath));
	
		try (FileOutputStream fos = new FileOutputStream(directoryPath +File.separator + imageName)) {
			fos.write(image.getContent());
		} catch (IOException  ex) {
			throw new EfwServiceException(ex);
		}
		return "_TEMP_" + dir + "/" + imageName;
	}
	
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

}
