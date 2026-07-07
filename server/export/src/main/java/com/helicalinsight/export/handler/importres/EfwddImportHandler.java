package com.helicalinsight.export.handler.importres;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.utils.DashboardUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;





import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationSettings;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;


import java.util.*;

/**
 * Handles the import of EFWDD (Embedded Folders, Widgets, Dashboards, and Documents) resources.
 *
 * This class is responsible for importing EFWDD resources from a specified file. It reads the EFWDD resource from the
 * file, determines the import mode (update or skip), and processes the import accordingly.
 *
 * The EFWDD resource includes information about the file, its metadata, and other related properties. The import
 * process involves updating or skipping the existing resource and creating or updating its associated HIResourceEFWDD
 * instance.
 *
 * The EFWDD import handler is designed to be a prototype-scoped component, allowing for multiple instances to handle
 * concurrent imports independently.
 */
@Component("efwddImportHandler")
@Scope("prototype")
public class EfwddImportHandler extends AbstractResourceImportHandler {
	/**
     * Imports an EFWDD resource.
     *
     * @param resourceUrl 		URL of the EFWDD resource to import.
     * @return The imported HIResource representing the EFWDD resource.
     */
	@Override
	public HIResource importResource(String resourceUrl) {
		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		ImportRequest request = context.getRequest();
		Manifest manifest = context.getManifest();
		String conflictMode = request.getOnConflict();
		HIResourceEFWDD efwddResource = fileReader.read(context,resourceUrl,HIResourceEFWDD.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl,Deleted.FALSE);
		if (null != resource) {
			if ("update".equalsIgnoreCase(conflictMode) && context.recover(resource)) {
				updateEfwddResource(efwddResource, resource);
				context.appendUpdate(resource.getResourceURL());
			}
			else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewEfwddResource(efwddResource, parentUrl, resourceUrl,efwddResource.getFileName());
			context.appendInsert(resource.getResourceURL());
		}
		shareHandler.setContext(context);
		shareHandler.importResource(resource, request, manifest);
		ResourceIOHandler scheduleHandler = (ResourceIOHandler) ApplicationContextAccessor.getBean("scheduleIOHandler");
		scheduleHandler.setContext(context);
		scheduleHandler.importResource(resource, resourceUrl, conflictMode);
		return resource;
	}
	/**
     * Updates an existing EFWDD resource.
     *
     * @param efwdd    		EFWDD instance to update from (Ex: time, owner, filename).
     * @param resource 		HIResource object sets the data .
     */
	public void updateEfwddResource(HIResourceEFWDD efwdd, HIResource resource) {
		Date date = context.getDate();
		efwdd.setLastUpdatedTime(date);
		resource.setLastUpdatedTime(date);
		resource.setCreatedBy(efwdd.getCreatedBy());
		Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy() : 
			Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
		if(efwdd.getCreatedBy() == null ) {
			resource.setCreatedBy(null);
		}
		else {
			resource.setCreatedBy(importedBy);
			efwdd.setCreatedBy(importedBy);
		}
		Map<String, Object>  s = updateState(efwdd.getState());
		updateReportPaths(s,efwdd, resource);
		resource.setTitle(efwdd.getFileName());
		resource.setHiResourceEFWDD(efwdd);
		serviceDb.editHIResource(resource);
	}
	 /**
     * Creates a new EFWDD resource.
     *
     * @param efwdd        	EFWDD instance to create from.
     * @param parentUrl    	URL of the parent resource.
     * @param resourceUrl  	URL of the EFWDD resource.
     * @param title         The title of the EFWDD resource.
     * @return The newly created HIResource representing the EFWDD resource.
     */
	public HIResource createNewEfwddResource(HIResourceEFWDD efwdd, String parentUrl, String resourceUrl,String title) {
		String path=FilenameUtils.removeExtension(resourceUrl);
		String pathArray[]=path.split("/");
		String actualPath=pathArray[pathArray.length - 1];
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? efwdd.getCreatedBy() : null;
		HIResource parent = context.getResourceUrlMap().get(parentUrl+"."+JsonUtils.getFolderFileExtension());
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getDesignerExtension(), context.getDate(),ownerId, 
                resourceUrl, actualPath, title, parent.getResourceId(), efwdd.getCreatedBy() == null);
		
        Map<String, Object> s = updateState(efwdd.getState());
        efwdd.setState((String) s.get("state"));
		efwdd.setLastUpdatedTime(context.getDate());
		efwdd.setCreatedDate(context.getDate());
		efwdd.setCreatedBy(resource.getCreatedBy());
		resource.setHiResourceEFWDD(efwdd);
        serviceDb.addHIResource(resource);
        updateReportPaths(s, efwdd, resource);
		return resource;
	}

	/**
	 * Updates the state information within the EFWDD resource.
	 *
	 * @param state 		The current state information.
	 * @return The updated state information.
	 */
    private Map<String, Object> updateState(String state) {
        List<Integer> idList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
		JsonObject settingJson = applicationSettings.getSettingJson();
		Boolean autoSyncCutPasteDesigner = settingJson.get("autoSyncCutPasteDesigner").getAsBoolean();


		if(!context.destinationExists()) {
            map.put("state", state);
            if (autoSyncCutPasteDesigner) {
                List<String> pathList = DashboardUtils.getAllPathInState(state);
                idList = DashboardUtils.getIdFromDb(pathList, serviceDb);
		}
            map.put("idList", idList);
            return map;
        }
        List<String> pathList = DashboardUtils.getAllPathInState(state);
        List<String> newPath = new ArrayList<>();
        for (String path : pathList) {
				HIResource resource =  serviceDb.getResourceByUrl(context.addDestination(path),Deleted.FALSE);
				if(resource != null ) {
                newPath.add(resource.getResourceURL());
                idList.add(resource.getResourceId());

            } else {
                idList.add(null);
                newPath.add(null);
				}
			}
        String newState = DashboardUtils.replacePath(state, newPath);
        if(autoSyncCutPasteDesigner) {
            map.put("idList", idList);
		}
        map.put("state", newState);
        return map;
	}
    
	private void updateReportPaths(Map<String, Object> s , HIResourceEFWDD efwdd, HIResource resource) {
		
		pathService.deleteChildrenByParentId(resource.getResourceId());
		
		@SuppressWarnings("unchecked")
		List<Integer> idList = (List<Integer>) s.get("idList");
		if (idList != null && idList.size() > 0) {
			List<HIResourceMapping> mappingList = new ArrayList<>();
			for (Integer id : idList) {
				HIResourceMapping resourceEfwddResource = new HIResourceMapping();
				resourceEfwddResource.setParentResource(resource);

				HIResource hReportResource = null;

				try {
					hReportResource = serviceDb.getHIResourceById(id);
				} catch (EfwServiceException resourceNotFoundException) {
					continue;
				}

				resourceEfwddResource.setChildResource(hReportResource);
				mappingList.add(resourceEfwddResource);
			}
			pathService.saveBatch(mappingList);
		}
	}
}
