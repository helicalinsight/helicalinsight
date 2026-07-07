package com.helicalinsight.export.handler;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

import com.helicalinsight.adhoc.utils.DashboardUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationSettings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.HIResourceEFWDD;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class handles the writing of Helical Insight EFWDD resources during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing EFWDD resources.
 * The write method is responsible for writing the EFWDD resource, its associated reports, and managing dependencies during the export process.
 *
 */
@Component("efwddWriterHandler")
public class EfwddWriterHandler extends AbstractResourceWriterHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(EfwddWriterHandler.class);

	@Autowired
	private FolderWriterHandler folderWriterHandlerHandler;

	@Autowired
	private HReportWriterHandler reportWriterHandler;
	
	/**
     * <p>
     * This method writes the EFWDD resource, its associated reports, and manages dependencies during the export process.
     * </p>
     *
     * @param resource 		HIResourceDTO object representing the EFWDD resource.
     * @param dir      		directory path where the resource is to be written.
     * @param manifest 		manifest object containing resource metadata.
     * @param options  		options for writing the resource.
     */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest, ResourceOptions options) {
		
		HIResource hResource = serviceDB.getResourceByIdIgnoreFilter(resource.getResourceId());
		HIResourceEFWDD efwddResource = hResource.getHiResourceEFWDD();
		ApplicationSettings applicationSettings = ApplicationContextAccessor.getBean(ApplicationSettings.class);
		JsonObject settingJson = applicationSettings.getSettingJson();
		Boolean autoSyncCutPasteDesigner = settingJson.get("autoSyncCutPasteDesigner").getAsBoolean();


		String state = efwddResource.getState();
		StringBuilder dependencies = new StringBuilder();
		List<HIResourceMapping> allPathR = pathService.findByParentId(hResource.getResourceId());

		if (autoSyncCutPasteDesigner && (allPathR!=null && allPathR.size()>0)) {


			List<String> correctPath = new ArrayList<>();
			for (HIResourceMapping hiResourceEfwddResource : allPathR) {
				HIResource rsource = serviceDB.getResourceByIdIgnoreFilter(hiResourceEfwddResource.getChildResource().getResourceId());
				if (rsource != null) {
					String reportPath = rsource.getResourceURL();
					correctPath.add(reportPath);
					HIResourceDTO reportDTO = null;
					boolean entered = false;
					if (!manifestUtils.pathExists(reportPath, manifest)) {
						entered = true;
						HIResource hreportResource = serviceDB.getResourceByUrl(reportPath, Deleted.FALSE);
						reportDTO = dtoMapper.map(hreportResource);

						if (reportDTO != null) {
							reportWriterHandler.write(reportDTO, dir, manifest, options);
						} else {
							logger.warn("Report file does not exists, please remove it from Dashboard");
						}
					}
					if (!entered || (entered && reportDTO != null)) {
						dependencies.append(reportPath + ",");
					}
				}
			}
			List<String> allPathd= DashboardUtils.getAllPathInState(state);

			if(correctPath.size()>0 && allPathd.size()==correctPath.size()) {
				state = DashboardUtils.replacePath(state, correctPath);
				efwddResource.setState(state);
			}
		}else{
			List<String> allPath= DashboardUtils.getAllPathInState(state);
			for (String reportPath : allPath) {
					HIResourceDTO reportDTO = null;
					boolean entered = false;
					if (!manifestUtils.pathExists(reportPath, manifest)) {
						entered = true;
						HIResource hreportResource = serviceDB.getResourceByUrl(reportPath, Deleted.FALSE);
						reportDTO = dtoMapper.map(hreportResource);

						if (reportDTO != null) {
							reportWriterHandler.write(reportDTO, dir, manifest, options);
						} else {
							logger.warn("Report file does not exists, please remove it from Dashboard");
						}
					}
					if (!entered || (entered && reportDTO != null)) {
						dependencies.append(reportPath + ",");
					}

			}

		}




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
		dependencies.append(depFolderUrl);
		efwddResource.setCreatedBy(hResource.getCreatedBy());
		dataWriter.write(this.addOwner(efwddResource,efwddResource.getCreatedBy()), dir, resource,"");
		manifestUtils.insertPath(resource.getPath(), manifest);
		manifestUtils.insertDependency(resource.getPath(), dependencies.toString(), manifest);
	}
}
