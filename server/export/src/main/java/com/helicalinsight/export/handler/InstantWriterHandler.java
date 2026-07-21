package com.helicalinsight.export.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;

/**
 * Handles the writing of Instant BI report resources during the export process.
 * Instant reports depend on an AI Model referenced inside the state JSON at {@code subject.model}.
 */
@Component("instantWriterHandler")
public class InstantWriterHandler extends AbstractResourceWriterHandler {

	@Autowired
	private ModelWriterHandler modelWriterHandler;

	@Autowired
	private FolderWriterHandler folderWriterHandlerHandler;

	@Override
	public void write(HIResourceDTO report, String dir, Manifest manifest, ResourceOptions options) {
		HIResource hResource = serviceDB.getResourceByIdIgnoreFilter(report.getResourceId());
		HIResourceInstantReport instantReport = hResource.getHiResourceInstantReport();


		HIResource modelResource = serviceDB.getResourceByIdIgnoreFilter(instantReport.getHiResourceModel());
		if (modelResource == null) {
			throw new ResourceExportException("Model resource not found for the Instant Report : "+hResource.getResourceURL());
		}

		String[] dirArr = StringUtils.split(hResource.getResourceURL(), "/");
		String depFolderUrl = StringUtils.join(dirArr, "/", 0, dirArr.length - 1);
		if (!depFolderUrl.isEmpty()) {
			String depFolderUrlWithExtension = depFolderUrl + ResourceExtension.FOLDER.getValue();
			if (!manifestUtils.pathExists(depFolderUrlWithExtension, manifest)) {
				HIResource resourceDir = serviceDB.getResourceByUrl(depFolderUrl, Deleted.FALSE);
				HIResourceDTO folderDTO = dtoMapper.map(resourceDir);
				if (folderDTO != null) {
					folderWriterHandlerHandler.write(folderDTO, dir, manifest, options);
				}
			}
		}

		HIResourceDTO modelDTO = dtoMapper.map(modelResource);
		if (modelDTO != null && !manifestUtils.pathExists(modelDTO.getPath(), manifest)) {
			modelWriterHandler.write(modelDTO, dir, manifest, options);
		}
		String modelUrl = modelResource.getResourceURL();

		String csv = modelUrl.concat(",").concat(depFolderUrl);
		instantReport.setCreatedBy(hResource.getCreatedBy());
		dataWriter.write(this.addOwner(instantReport, instantReport.getCreatedBy()), dir, report, "");
		manifestUtils.insertPath(report.getPath(), manifest);
		manifestUtils.insertDependency(report.getPath(), csv, manifest);
		share(report, manifest, options, dir);
		schedule(report, manifest, options, dir);
	}
}
