package com.helicalinsight.export.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.utils.DashboardUtils;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class handles the writing of Helical Insight resources of type "cube" during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing cube resources.
 * The write method is responsible for writing the cube resource along with its associated metadata and dependencies.
 *
 */
@Component("hcrWriterHandler")
public class HCRWriterHandler  extends AbstractResourceWriterHandler {

	@Autowired
	private FolderWriterHandler folderWriterHandlerHandler;

	@Autowired
	private ImageWriterHandler imageWriterHandler;


	@Autowired
	private HIResourceConstituentMappingService mappingService;

	/**
	 * <p>
	 * This method writes the cube resource, its associated metadata, and manages dependencies during the export process.
	 * </p>
	 *
	 * @param resource 		HIResourceDTO object representing the cube resource.
	 * @param dir      		directory path where the resource is to be written.
	 * @param manifest 		manifest object containing resource metadata.
	 * @param options  		options for writing the resource.
	 */
	@Override
	public void write(HIResourceDTO report, String dir, Manifest manifest, ResourceOptions options) {
		HIResource hResource = serviceDB.getResourceByIdIgnoreFilter(report.getResourceId());
		HIResourceHCR hReport = hResource.getHiResourceHCR();
		String stateJson=hReport.getState();
		String previewFormDataJson=hReport.getPreviewFormData();
		List<String> allKeyValues = DashboardUtils.findKeyValue("temp_uuid", stateJson);
		List<String> previewValue = DashboardUtils.findKeyValue("temp_uuid", previewFormDataJson);
		allKeyValues.addAll(previewValue);
		Set<String> uniqueList = allKeyValues.stream().collect(Collectors.toSet());
		JsonArray filesList = new JsonArray();
		JsonArray efwdFileList = new JsonArray();
		List<Integer> globalIdList= new ArrayList<>();
		List<Integer> efwdList= new ArrayList<>();
		for (String efwString : uniqueList) {
			JsonObject fileAsJson=new JsonObject();
			if(efwString.replace("hi_hcr_db","").matches("\\d+")) {
				fileAsJson = HCRUtils.prepareConnectionJson(efwString);
			}
			fileAsJson.addProperty("fileName", efwString);
			JsonObject connectionJson=fileAsJson.get("DataSources").getAsJsonObject().get("Connection").getAsJsonObject();
			if(connectionJson.has("globalId")) {
				Integer globalId = connectionJson.get("globalId").getAsInt();
				globalIdList.add(globalId);
				filesList.add(fileAsJson);
			}else if(connectionJson.has("efwdId")) {
				Integer efwdId = connectionJson.get("efwdId").getAsInt();
				efwdList.add(efwdId);
				efwdFileList.add(fileAsJson);
			}


		}
		JsonObject asJsonObject = JsonParser.parseString(previewFormDataJson).getAsJsonObject();
		asJsonObject.add("connectionDbDetails",filesList);
		asJsonObject.add("connectionDbDetailsEfwd",efwdFileList);
		hReport.setPreviewFormData(asJsonObject.toString());

		hReport.setCreatedBy(hResource.getCreatedBy());


		String[] dirArr = StringUtils.split(hResource.getResourceURL(), "/");
		String depFolderUrl = StringUtils.join(dirArr, "/", 0, dirArr.length - 1);
		String depFolderUrlWithExtension = depFolderUrl+ResourceExtension.FOLDER;
		if (!manifestUtils.pathExists(depFolderUrlWithExtension, manifest)) {
			HIResource resourceDir = serviceDB.getResourceByUrl(depFolderUrlWithExtension,false);
			if (resourceDir != null) {
				HIResourceDTO folderDTO = dtoMapper.map(resourceDir);
				folderWriterHandlerHandler.write(folderDTO, dir, manifest, options);
			}
		}
		List<HIResourceMapping> byId = mappingService.findByParentId(report.getResourceId());
		String allDep=depFolderUrl+",";
		for(HIResourceMapping mp  :   byId){
			HIResource newIds = serviceDB.getResourceByIdIgnoreFilter(mp.getChildResource().getResourceId());
			String resourceURL = newIds.getResourceURL();
			if (!manifestUtils.pathExists(resourceURL, manifest)) {
				if (newIds != null) {
					HIResourceDTO folderDTO = dtoMapper.map(newIds);
					imageWriterHandler.write(folderDTO,dir,manifest,options);
				}

			}
			allDep=allDep+resourceURL+",";
		}
		allDep = allDep.substring(0,allDep.length()-1);
		manifestUtils.insertDependency(report.getPath(), allDep, manifest);


		dataWriter.write(this.addOwner(hReport,hReport.getCreatedBy()), dir, report,"");
		manifestUtils.insertPath(report.getPath(), manifest);
		share(report, manifest, options, dir);
		schedule(report, manifest, options, dir);
		if (options != null && options.getDataSource()) {
			if(globalIdList.size()>0) {
				DatasourceHandler dsHandler = DatasourceFactory.getHandler("global.jdbc");
				report.setGlobalIds(globalIdList);
				dsHandler.write(report, dir, manifest);
			}
			if(efwdList.size()>0) {
				DatasourceHandler dsHandler = DatasourceFactory.getHandler("folder");
				report.setEfwdIds(efwdList);
				dsHandler.write(report, dir, manifest);
			}
		}
	}

}
