package com.helicalinsight.export.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class handles the writing of Helical Insight resources of type "cube" during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing cube resources.
 * The write method is responsible for writing the cube resource along with its associated metadata and dependencies.
 *
 */
@Component("cubeWriterHandler")
public class CubeWriterHandler  extends AbstractResourceWriterHandler {

	@Autowired
	private FolderWriterHandler folderWriterHandlerHandler;
	
	@Autowired
	private MetadataWriterHandler mdWriterHandler;
	
	
	@Autowired
	private HICubeDAOService cubeService;
	
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
	public void write(HIResourceDTO resource, String dir, Manifest manifest, ResourceOptions options) {
		String depPath = StringUtils.chop(StringUtils.substringBefore(resource.getPath(),resource.getName()));
		if (!manifestUtils.pathExists( depPath + ResourceExtension.FOLDER.getValue(),manifest)) {
			HIResource folderResource = serviceDB.getResourceByUrl(depPath);
			HIResourceDTO folder = dtoMapper.map(folderResource);
			folderWriterHandlerHandler.write(folder, dir, manifest, options);
		}
		if (!manifestUtils.pathExists(resource.getPath(), manifest)) {
			HIMetadataCube hiMetadataCube  = cubeService.findCubeByResourceId(resource.getResourceId());
			HIResourceDTO mdDTO = dtoMapper.map(hiMetadataCube.getHiResourceMetadata().getHiResource());
			mdWriterHandler.write(mdDTO, dir, manifest, options);
			manifestUtils.insertPath(resource.getPath(), manifest);
			dataWriter.write(this.addOwner(hiMetadataCube,null), dir, resource,"");
			String csv = hiMetadataCube.getHiResourceMetadata().getHiResource().getResourceURL().concat(",").concat(depPath);
			manifestUtils.insertDependency(resource.getPath(), csv, manifest);
		}
	}
	
}
