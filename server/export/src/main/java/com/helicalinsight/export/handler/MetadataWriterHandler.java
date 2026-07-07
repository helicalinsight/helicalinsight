package com.helicalinsight.export.handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

import com.helicalinsight.admin.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class handles the writing of metadata resources during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing metadata resources.
 * The write method is responsible for writing metadata, managing dependencies, and handling datasource export.
 *
 */
@Component("metadataWriterHandler")
public class MetadataWriterHandler extends AbstractResourceWriterHandler {
	private static final Logger logger = LoggerFactory.getLogger(MetadataWriterHandler.class);
	@Autowired
	FolderWriterHandler folderWriterHandlerHandler;

	/**
     * <p>
     * This method writes metadata, manages dependencies, and handles datasource export during the export process.
     * </p>
     *
     * @param resource The HIResourceDTO object representing the metadata resource.
     * @param dir      The directory path where the resource is to be written.
     * @param manifest The manifest object containing resource metadata.
     * @param options  The options for writing the resource.
     */
	@Override
	public void write(HIResourceDTO resource, String dir, Manifest manifest, ResourceOptions options) {
		String depPath = StringUtils.chop(StringUtils.substringBefore(resource.getPath(),resource.getName()));
		if (!manifestUtils.pathExists( depPath + ResourceExtension.FOLDER.getValue(),manifest)) {
			HIResource folderResource = serviceDB.getResourceByUrl(depPath,false);
			HIResourceDTO folder = dtoMapper.map(folderResource);
			if(folder != null ) {
				folderWriterHandlerHandler.write(folder, dir, manifest, options);
			}
		}
		if (!manifestUtils.pathExists(resource.getPath(), manifest)) {
			HIResourceMetadataDTO metadata = mdServiceDB.giveHIResourceMetadataByResId(resource.getResourceId());
			HIResource hiResource = serviceDB.getResourceByIdIgnoreFilter(resource.getResourceId());
			HIResourceDTO hiResourceDTO =  dtoMapper.map(hiResource);
			metadata.setHiResource(hiResourceDTO);
			manifestUtils.insertPath(resource.getPath(), manifest);
			dataWriter.write(this.addOwner(metadata,metadata.getCreatedBy()), dir, resource,"");
			manifestUtils.insertDependency(resource.getPath(), depPath, manifest);
			share(resource, manifest, options, dir);
			if (options != null && options.getDataSource()) {
				DatasourceHandler dsHandler = DatasourceFactory.getHandler(metadata.getConnectionType());
				dsHandler.write(resource, dir, manifest);
			}
		}
	}
	
	/*
	 * TODO : Temporary Solution
	 * Hibernate fetching duplicate entries for OneToMany relationships .. check this in Hibernate v5 or v6
	 *
	 */
	/**
	private final void removeDuplicates(HIResourceMetadataDTO metadata) {
		 List<HIMetadataConnections> connections =  metadata.getHiMetadataConnections().stream().distinct().collect(Collectors.toList());
		 metadata.setMetadataSecurityList(metadata.getMetadataSecurityList().stream().distinct().collect(Collectors.toList()));
		 for(HIMetadataConnections connection : connections) {
			 List<MetadataDatabases> databases =  connection.getMetadataDatabases()
					 .stream().distinct()
					 .collect(Collectors.toList());
					databases.stream().forEach(db -> {
						List<HIMetadataTables> tables = db.getMetadataTablesList().stream().distinct().collect(Collectors.toList());
						tables.stream().forEach(table -> {
							List<HIMetadataColumns> columns = table.getColumnsList().stream().distinct()
									.collect(Collectors.toList());
							table.setColumnsList(columns);
					});
					db.setMetadataTablesList(tables);
					
					List<HIMetadataView> views = db.getMetadataViewList().stream().distinct().collect(Collectors.toList());
					db.setMetadataViewList(views);
					List<HIMetadataRelationships> relationships =  db.getMetadataRelationShipList().stream().distinct().collect(Collectors.toList());
					db.setMetadataRelationShipList(relationships);
				});
			 connection.setMetadataDatabases(databases);
		 }
	} 
	**/
}
