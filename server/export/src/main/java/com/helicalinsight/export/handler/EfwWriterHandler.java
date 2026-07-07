package com.helicalinsight.export.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class handles the writing of EFW resources during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing EFW resources.
 * The write method is responsible for writing the EFW resource, its associated content, and managing dependencies during the export process.
 */
@Component("efwWriterHandler")
public class EfwWriterHandler extends AbstractResourceWriterHandler {

	@Autowired
	private FolderWriterHandler folderWriterHandlerHandler;
	/**
     * <p>
     * This method writes the EFW resource, its associated content, and manages dependencies during the export process.
     * </p>
     *
     * @param resource 		HIResourceDTO object representing the EFW resource.
     * @param dir      		directory path where the resource is to be written.
     * @param manifest 		manifest object containing resource metadata.
     * @param options  		options for writing the resource.
     */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest, ResourceOptions options) {
		
		HIResource hResource = serviceDB.getResourceByIdIgnoreFilter(resource.getResourceId());
		HIResourceEFW efwResource = hResource.getHiResourceEFW();		
		String[] dirArr = StringUtils.split(hResource.getResourceURL(), "/");
		String depFolderUrl = StringUtils.join(dirArr, "/", 0, dirArr.length - 1);
		String depFolderUrlWithExtension = depFolderUrl+ResourceExtension.FOLDER;
		int contentId = efwResource.getEfwContentId();
		ResourceEfwContents content= serviceDB.getHIResourceEFWContents(contentId);
		if (!depFolderUrl.isEmpty()) {
			
			if (manifestUtils.pathExists(depFolderUrlWithExtension, manifest)) {
				HIResource resourceDir = serviceDB.getResourceByUrl(depFolderUrl,Deleted.FALSE);
				if (resourceDir != null) {
					HIResourceDTO folderDTO = dtoMapper.map(resourceDir);
					folderWriterHandlerHandler.write(folderDTO, dir, manifest, options);
				}
			}
		}
		dataWriter.write(this.addOwner(efwResource,efwResource.getCreatedBy()),dir, resource,"");
		dataWriter.write(content, dir, resource,ResourceSuffix.EFW_CONTENT);
		manifestUtils.insertPath(resource.getPath(), manifest);
		manifestUtils.insertDependency(resource.getPath(),depFolderUrl, manifest);
	}
}
