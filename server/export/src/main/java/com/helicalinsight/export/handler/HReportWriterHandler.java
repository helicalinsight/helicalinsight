package com.helicalinsight.export.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class handles the writing of Helical Insight HReport resources during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing HReport resources.
 * The write method is responsible for writing the HReport resource, managing dependencies, and handling metadata export.
 *
 */
@Component("hrWriterHandler")
public class HReportWriterHandler extends AbstractResourceWriterHandler {

	@Autowired
	private MetadataWriterHandler mdWriterHandler;

	@Autowired
	private FolderWriterHandler folderWriterHandlerHandler;
	/**
     * <p>
     * This method writes the HReport resource, manages dependencies, and handles metadata export during the export process.
     * </p>
     *
     * @param report   		HIResourceDTO object representing the HReport resource.
     * @param dir      		directory path where the resource is to be written.
     * @param manifest 		manifest object containing resource metadata.
     * @param options  		options for writing the resource.
     */
	@Override
	public void write(HIResourceDTO report, String dir, Manifest manifest, ResourceOptions options) {
		HIResource hResource = serviceDB.getResourceByIdIgnoreFilter(report.getResourceId());
		HIResourceHReport hReport = hResource.getHiResourceHReport();
		Integer mdResourceId = hReport.getHiResourceMetadata();
		HIResource mdata = serviceDB.getResourceByIdIgnoreFilter(mdResourceId);
		if (mdata == null) {
			throw new ResourceExportException("Metadata Not found for the HReport : " + report.getName());
		}
		String[] dirArr = StringUtils.split(hResource.getResourceURL(), "/");
		String depFolderUrl = StringUtils.join(dirArr, "/", 0, dirArr.length-1);
		if (!depFolderUrl.isEmpty()) {
			String depFolderUrlWithExtension = depFolderUrl+ResourceExtension.FOLDER.getValue();
			if (!manifestUtils.pathExists(depFolderUrlWithExtension, manifest)) {
				HIResource resourceDir = serviceDB.getResourceByUrl(depFolderUrl,Deleted.FALSE);
				HIResourceDTO folderDTO = dtoMapper.map(resourceDir);
				if(folderDTO != null ) {
					folderWriterHandlerHandler.write(folderDTO, dir, manifest, options);
				}
			}
		}
		HIResourceDTO mdDTO = dtoMapper.map(mdata);
		if (mdDTO != null && !manifestUtils.pathExists(mdDTO.getPath(), manifest)) {
			mdWriterHandler.write(mdDTO, dir, manifest, options);
		}
		String csv = mdata.getResourceURL().concat(",").concat(depFolderUrl);
		hReport.setCreatedBy(hResource.getCreatedBy());
		dataWriter.write(this.addOwner(hReport,hReport.getCreatedBy()), dir, report,"");
		manifestUtils.insertPath(report.getPath(), manifest);
		manifestUtils.insertDependency(report.getPath(), csv, manifest);
		share(report, manifest, options, dir);
		schedule(report, manifest, options, dir);
	}

}
