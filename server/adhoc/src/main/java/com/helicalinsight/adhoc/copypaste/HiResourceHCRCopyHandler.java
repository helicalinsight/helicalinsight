package com.helicalinsight.adhoc.copypaste;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.processor.DBProcessor;


/**
 * HiResourceHreportCopyHandler class extends {@link HiResourceCopyHandler}.
 * Handles the copying of HReport resources, ensuring uniqueness of names
 * and managing associated report data and security.
 */
@Component
public class HiResourceHCRCopyHandler extends HiResourceCopyHandler{


	@Autowired
	private HIResourceServiceDB hiResourceServiceDB;

	@Autowired
	private HiResourceCCPUtility hiResourceCCPUtility;


	@Autowired
	private EFWDConnectionService efwdConnectionService;

	@Autowired
	private HIResourceConstituentMappingService mappingService;
	/**
	 * Copies the resource, ensuring uniqueness and handling associated data.
	 */
	@Override
	public void copyResource() {

		if(getSource().getParentId()!=getDestinationResourceId().getResourceId()) {
			HIResource isSameResouceNameAlreadyExisted=hiResourceServiceDB.getResourceByUrl(getPrefix(),Boolean.FALSE);
			if(isSameResouceNameAlreadyExisted!=null && !isSameResouceNameAlreadyExisted.getDeleted()) {
				if(!getOnConflictSkip()) {
					String sourcePath=UUIDGenerator.getUuid();
					String prefixUrl=getDestinationResourceId().getResourceURL()+"/"+sourcePath+getSource().getResourceType().getExtension();
					HIResource hiResource=doCopy(prefixUrl,sourcePath);
					hiResourceCCPUtility.deleteOverridenResourceAndUpdateCopiedResource(hiResource, isSameResouceNameAlreadyExisted);
				}
			}
			else {
				if(isSameResouceNameAlreadyExisted!=null) {
					Format secondsFormat = new SimpleDateFormat("ss");
					String generatedSourcePath=DBProcessor.checkAndReplaceSpecialChars(getSourcePath()).trim() +
							"_"+ secondsFormat.format(new Date()).substring(0, 2);
					String generatedUrl=getPrefix().substring(0,getPrefix().length()-getSourcePath().length())+generatedSourcePath;
					doCopy(generatedUrl,generatedSourcePath);
				}
				else
					doCopy(getPrefix(),getSourcePath());
			}
		}
		else
			doCopy(getPrefix(),getSourcePath());
	}
	/**
	 * This method performs the actual copy operation.
	 * It prepares a new replica of the source resource with a given prefix and source path.
	 * It sets properties of the HIResourceHReport associated with the copied resource, such as report name, creation date, etc.
	 * It adds the new resource to the database, saves security information, and deletion of source.
	 *
	 * @param prefix          used to create new replica of {@code HIResource}
	 * @param sourcePath	  used to set the path of new replica/copy of {@code HIResource}
	 * @return {@code HIResource} object.
	 */
	private HIResource doCopy(String prefix, String sourcePath) {
		HIResource existingResource = getSource();
		HIResource hiResource=hiResourceCCPUtility.prepareNewReplica(
				existingResource, getDestinationResourceId(), prefix, sourcePath);
		HIResourceHCR hiResourceHReport=HiResourceCCPUtility.prepareEntity(existingResource.getHiResourceHCR(),HIResourceHCR.class);
		hiResourceHReport.setFileName(hiResource.getTitle());
		hiResourceHReport.setCreatedDate(new Date());
		hiResourceHReport.setLastUpdatedTime(new Date());
		hiResourceHReport.setCreatedBy(Integer.valueOf(AuthenticationUtils.getUserId()));
		hiResource.setHiResourceHCR(hiResourceHReport);
		hiResourceServiceDB.addHIResource(hiResource);
		hiResourceCCPUtility.saveSecurityInfoReplica(existingResource.getResourceId(), hiResource);
		if(existingResource.getDeleted())
			hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
		getDestinationResourceId().setLastUpdatedTime(new Date());
		hiResourceServiceDB.editHIResource(getDestinationResourceId());

		List<HIResourceMapping> existingIds = mappingService.findByParentId(existingResource.getResourceId());
		List<HIResourceMapping> newMapList = new ArrayList<>();
		for(HIResourceMapping maps: existingIds){
			HIResourceMapping newMap = new HIResourceMapping();
			newMap.setChildResource(maps.getChildResource());
			newMap.setParentResource(hiResource);
			newMapList.add(newMap);
		}
		mappingService.saveBatch(newMapList);

		String stateJson=hiResourceHReport.getState();
		String previewFormDataJson=hiResourceHReport.getPreviewFormData();
		List<String> allKeyValues = DashboardUtils.findKeyValue("temp_uuid", stateJson);
		List<String> previewValue = DashboardUtils.findKeyValue("temp_uuid", previewFormDataJson);
		allKeyValues.addAll(previewValue);
		Set<String> uniqueList = allKeyValues.stream().collect(Collectors.toSet());
		JsonArray filesList = new JsonArray();
		for (String efwString : uniqueList) {
			JsonObject fileAsJson=new JsonObject();
			if(efwString.replace("hi_hcr_db","").matches("\\d+")) {
				fileAsJson = HCRUtils.prepareConnectionJson(efwString);
			}
			fileAsJson.addProperty("fileName", efwString);
			filesList.add(fileAsJson);

		}

		JsonObject asJsonObject = JsonParser.parseString(previewFormDataJson).getAsJsonObject();
		List<HIHcrConnections> hiHcrConnections = efwdConnectionService.fetchAllHcrConnectionsByResourceId(hiResource.getResourceId());
		hiHcrConnections.forEach(hcrCon->{
			efwdConnectionService.deleteHiHcrConnection(hcrCon);
		});


		JsonObject stateJsonObject = JsonParser.parseString(stateJson).getAsJsonObject();
		JsonObject forEfwd = new JsonObject();
		forEfwd.add("prv",asJsonObject);
		forEfwd.add("stt",stateJsonObject);

		JsonObject newFd=setEfwd(forEfwd, hiResource, hiResourceHReport,filesList);
		hiResourceHReport.setPreviewFormData(newFd.get("prv").getAsJsonObject().toString());
		hiResourceHReport.setState(newFd.get("stt").getAsJsonObject().toString());

		hiResourceServiceDB.editHIResource(hiResource);



		return hiResource;
	}


	private JsonObject setEfwd(JsonObject formDataJson, HIResource hiResource,
							   HIResourceHCR hiResourceHCR,JsonArray efwdJsonObjects) {


		String formDataString = formDataJson.toString();
		for (JsonElement object : efwdJsonObjects) {

			JsonObject	efwdJsonObject = object.getAsJsonObject();



			if (efwdJsonObject != null) {
				addEfwdDetailsToHcr(efwdJsonObject, hiResource);
				String fileName= efwdJsonObject.get("fileName").getAsString();
				String conId= efwdJsonObject.get("dbConnectionId").getAsString();
				formDataString=formDataString.replace(fileName, conId+"hi_hcr_db");
			}
		}
		JsonObject newFd = JsonParser.parseString(formDataString).getAsJsonObject();
		return newFd;
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
}
