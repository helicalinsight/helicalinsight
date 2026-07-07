package com.helicalinsight.export.handler.importres;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.metadata.genericdb.EnhancedJoinsHandler;
import com.helicalinsight.admin.dto.HIMetadataColumnsDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionEFWDDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionGlobalDTO;
import com.helicalinsight.admin.dto.HIMetadataRelationshipsDTO;
import com.helicalinsight.admin.dto.HIMetadataSecurityDTO;
import com.helicalinsight.admin.dto.HIMetadataTableDTO;
import com.helicalinsight.admin.dto.HIMetadataViewDTO;
import com.helicalinsight.admin.dto.MetadataDatabasesDTO;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIMetadataSecurity;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIMetadataView;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HIResourceMetadataDTO;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Handles the import of metadata resources, including database connections, tables, columns,
 * relationships, views, and security settings. It extends {@link AbstractResourceImportHandler} .
 */
@Component("metadataImportHandler")
@Scope("prototype")
public class MetadataImportHandler extends AbstractResourceImportHandler {

	@Autowired
	private EnhancedJoinsHandler enhancedJoinsHandler;
	
	@Autowired
	private MetadataUpdateHandler metadataUpdateHandler;
	
	private Map<Integer,Integer> oldNewTableIdMap;
	private Map<Integer,Integer> oldNewColumnIdMap;
	private Map<String,Integer> joinsOrder;
	private  int joinCounter = 1;

	/**
     * Imports a metadata resource identified by the given resource URL. The import process involves parsing
     * the metadata file, handling conflicts, updating or creating metadata resources, and managing associated
     * datasource connections. Relationships, views, tables, columns, and security settings are also handled
     * during the import process.
     *
     * @param resourceUrl		URL of the metadata resource to be imported
     * @return the imported HIResource representing the metadata resource
     */
	@Override
	public HIResource importResource(String resourceUrl) {
		
		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		String conflictMode = context.getRequest().getOnConflict();
		HIResourceMetadataDTO metadata =  fileReader.read(context, resourceUrl, HIResourceMetadataDTO.class);
		HIResource resource = mapper.toEntity(metadata.getHiResource());
		boolean dsExists = manifestUtils.compareOptions(context.getRequest().getOptions(), context.getManifest(), "datasource");
		if (dsExists) {
			String dsFileName = manifestUtils.getDatasource(context.removeDestination(resourceUrl), context.getManifest());
			if (null != dsFileName) {
				DatasourceHandler dsHandler = DatasourceFactory.getHandler(metadata.getConnectionType());
				dsHandler.setContext(context);
				dsHandler.importResource(null, dsFileName, conflictMode);
			}
		}
		
		HIResource parent = context.getResourceUrlMap().get(parentUrl+"."+JsonUtils.getFolderFileExtension());
		resource.setParentId(parent!=null ?parent.getResourceId() : null);
		HIResource existingMdResorce = serviceDb.getResourceByUrl(resourceUrl,Deleted.FALSE);
		
		oldNewTableIdMap = new HashMap<>();
		oldNewColumnIdMap = new HashMap<>();
		joinsOrder = new HashMap<>();
		
		if (null != existingMdResorce) {
			existingMdResorce.setCreatedBy(resource.getCreatedBy());
			resource = existingMdResorce;
			if("update".equalsIgnoreCase(conflictMode) && context.recover(existingMdResorce)) {
				existingMdResorce.setTitle(metadata.getFileName());
				Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? existingMdResorce.getCreatedBy() : 
					Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
				existingMdResorce.setCreatedBy(metadata.getCreatedBy() == null ? null : importedBy);
				resource = updateMetadataResource(existingMdResorce,metadata);
				context.appendUpdate(resource.getResourceURL());
			}
			else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			metadata.getHiResource().setPath(resourceUrl);
			metadata.getHiResource().setParentId(resource.getParentId());
			resource = createNewMetadataResource(metadata);
			context.appendInsert(resource.getResourceURL());
		}
		context.setColumnIdMap(resourceUrl, oldNewColumnIdMap);
		context.setTableIdMap(resourceUrl, oldNewTableIdMap);
		shareHandler.setContext(context);
		shareHandler.importResource(resource,context.getRequest(),context.getManifest());
		
		return resource;
	}
	/**
     * Creates a new metadata resource by parsing the provided metadata information and associating it with
     * the corresponding database connections, tables, columns, relationships, views, and security settings.
     * The method generates a new timestamp and creates a temporary directory to handle the import process.
     *
     * @param metadata 		 HIResourceMetadata object containing metadata information
     * @return the newly created HIResource representing the metadata resource
     */
	private HIResource createNewMetadataResource(HIResourceMetadataDTO metadata) {

		HIResourceMetadata newMetadata = new HIResourceMetadata();
		Date date = context.getDate();
		newMetadata.setLastUpdatedTime(date);
		newMetadata.setConnectionType(metadata.getConnectionType());
		newMetadata.setDatabaseType(metadata.getDatabaseType());
		newMetadata.setType(metadata.getType());
		newMetadata.setFileName(metadata.getFileName());
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? metadata.getCreatedBy() : null;
		HIResourceDTO metadataResource = metadata.getHiResource();
		String resourcePath = metadataResource.getName();
		if (StringUtils.isNotBlank(resourcePath) && resourcePath.contains(".")) {
			resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("."));
		}
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getMetadataExtension(),context.getDate(),ownerId,metadataResource.getPath(),
				resourcePath,metadataResource.getTitle(),metadataResource.getParentId(),metadata.getCreatedBy() == null);
		serviceDb.addHIResource(resource);
		newMetadata.setHiResource(resource);
		newMetadata.setCreatedBy(resource.getCreatedBy());
		mdServiceDb.addHIResourceMetadata(newMetadata);
		List<HIMetadataConnectionDTO> connections = metadata.getHiMetadataConnections();
		for (HIMetadataConnectionDTO connection : connections) {

			HIMetadataConnections newConnection = new HIMetadataConnections();
			newConnection.setConnectionType(connection.getConnectionType());
			newConnection.setHiResourceMetadata(newMetadata);
			mdServiceDb.addHIMetadataConnections(newConnection);
			List<HIMetadataConnectionGlobalDTO> globalConnectionList = connection.getMetadataGlobalConnList();
			List<HIMetadataConnectionEFWDDTO> efwdConnectionList = connection.getMetadataConnectionEfwd();
			for (HIMetadataConnectionGlobalDTO globalConnectionDTO : globalConnectionList) {
				HIMetadataConnectionGlobal globalConnection = mapper.map(globalConnectionDTO);
				GlobalConnections gConnections = mapper.toEntity(globalConnectionDTO.getGlobalConnections());
				GlobalConnections dbConnection = context.getGlobalConnection(gConnections.getGlobalId());
				if ( dbConnection != null ) {
					globalConnection.setGlobalConnections(dbConnection);
					globalConnection.setHiMetadataConnections(newConnection);
					mdServiceDb.saveHIMetadataConnectionGlobal(globalConnection);
				}
			}
			for (HIMetadataConnectionEFWDDTO efwdConn : efwdConnectionList) {
				HIMetadataConnectionEFWD efwdConnection = mapper.map(efwdConn);
				HIEfwdConnection dbConnection = context.getEfwdConnection(efwdConn.getHiEfwdConnection().getId());
				if ( dbConnection != null ) {
					efwdConnection.setHiEfwdConnection(dbConnection);
					efwdConnection.setHiMetadataConnections(newConnection);
					mdServiceDb.saveHIMetadataConnectionEfwd(efwdConnection);
				}
			}
			List<MetadataDatabases> newDbs = new ArrayList<>();
			List<MetadataDatabasesDTO> databases = connection.getMetadataDatabases();
			for (MetadataDatabasesDTO database : databases) {
				
				MetadataDatabases db = saveDatabase(database, newConnection, newMetadata);
				newDbs.add(db);
			}
			newConnection.setMetadataDatabases(newDbs);
			
			handleSecurity(metadata,newMetadata);
			
		}
		return resource;
	}
	
	/**
     * Saves or updates a database metadata entry based on the provided MetadataDatabases object,
     * associating it with tables, columns, relationships, views, and security settings.
     *
     * @param database  	MetadataDatabases object representing the database metadata
     * @param mdCon     	associated HIMetadataConnections object
     * @param metadata  	parent HIResourceMetadata object
     * @return the updated or newly created MetadataDatabases object
     */
	private MetadataDatabases saveDatabase(MetadataDatabasesDTO database , HIMetadataConnections mdCon , HIResourceMetadata metadata) {
		MetadataDatabases db = null;
		boolean isCreate = true;
		if (database.getId() != null && mdCon.getId() != null) {
			db = mdServiceDb.getHIMetadataDatabases(metadata.getId(), "" + mdCon.getId());
			isCreate = db == null;
		} 
		db = Optional.ofNullable(db).orElse(new MetadataDatabases());
		db.setCatalog(database.getCatalog());
		db.setName(database.getName());
		db.setSchema(database.getSchema());
		db.setMetadataConnections(mdCon);
		db.setHiResourceMetadata(metadata);
		if (isCreate)
			mdServiceDb.addHIMetadataDatabases(db);
		else 
			mdServiceDb.editHIMetadataDatabases(db);
		return saveDatabase(database, db, metadata);
	}
	
	
	
	private Map<String,Integer> viewMap;
	private MetadataDatabases saveDatabase(MetadataDatabasesDTO database,MetadataDatabases db,HIResourceMetadata metadata) {

		List<HIMetadataTableDTO> tables = database.getMetadataTablesList();
		List<HIMetadataRelationshipsDTO> relationShips = database.getMetadataRelationShipList();
		List<HIMetadataViewDTO> views = database.getMetadataViewList();
		List<HIMetadataColumns> columnList = new ArrayList<>();
		viewMap = new HashMap<>();
		
		Map<String,HIMetadataTables> existingTableMap =  getTableMap(mdServiceDb.getMetadataTablesList(metadata.getId(), db.getId()));
		Map<Integer,HIMetadataColumns> existingColumnMap = getColumnMap(mdServiceDb.getMetadataColumnsList(metadata.getId(), db.getId()));
		for (HIMetadataTableDTO table : tables) {
			boolean create = false;
			HIMetadataTables newTable =  existingTableMap.getOrDefault(table.getTableName(),null);
			if(newTable == null) {
				create = true;
				newTable = new HIMetadataTables();
			}
			newTable.setHiResourceMetadata(metadata);
			newTable.setOriginalName(table.getOriginalName());
			newTable.setTableAliasName(table.getTableAliasName());
			newTable.setTableName(table.getTableName());
			newTable.setHiMetadataDatabases(db);
			newTable.setView(table.getView());
			if(create) mdServiceDb.addHIMetadataTables(newTable);
			else mdServiceDb.editHIMetadataTables(newTable);
			oldNewTableIdMap.put(table.getId(), newTable.getId());
			
			if (table.getView()) {
				viewMap.put(newTable.getTableName(), newTable.getId());
			}
			List<HIMetadataColumnsDTO> columns = table.getColumnsList();
			
			for (HIMetadataColumnsDTO column : columns) {
				boolean isNew = false;
				HIMetadataColumns newColumn = existingColumnMap.getOrDefault(column.getId(), null);
				if( newColumn == null ) {
					isNew = true;
					newColumn = new HIMetadataColumns();
				}
				newColumn.setColumn_type(column.getColumn_type());
				newColumn.setColumnAliasName(column.getColumnAliasName());
				newColumn.setColumnName(column.getColumnName());
				newColumn.setOriginalName(column.getOriginalName());
				newColumn.setDefaultFunction(column.getDefaultFunction());
				newColumn.setHiMetadataTables(newTable);
				newColumn.setHiResourceMetadata(metadata);
				if(isNew) mdServiceDb.addHIMetadataColumns(newColumn);
				else mdServiceDb.editHIMetadataColumns(newColumn);
				
				oldNewColumnIdMap.put(column.getId(), newColumn.getId());
				columnList.add(newColumn);

			}
		}

		Map<Integer, HIMetadataColumns> map = getColumnMap(columnList);
		
		Map<String, List<HIMetadataRelationships>> existingRelations = getRelationshipMap(
				mdServiceDb.getRelationshipListByMetadataIdAndDbId(metadata.getId(), db.getId()));
		for (HIMetadataRelationshipsDTO relationship : relationShips) {

			String key = relationship.getLeftMetadataColumns().getColumnAliasName() + relationship.getOperator()
					+ relationship.getRightMetadataColumns().getColumnAliasName();
			List<HIMetadataRelationships> relList = existingRelations.getOrDefault(key, null);
			if (relList != null) {
				ListIterator<HIMetadataRelationships> iterator = relList.listIterator();
				while (iterator.hasNext()) {
					upsertRelation(iterator.next(), relationship, metadata, db, map);
					iterator.remove();
				}
			} else {
				HIMetadataRelationships newRelationShip = new HIMetadataRelationships();
				upsertRelation(newRelationShip, relationship, metadata, db, map);
			}
		}
		enhancedJoinsHandler.setJoinsOrder(this.joinsOrder);
		enhancedJoinsHandler.updatePositions(metadata);
		
		if (views != null && !views.isEmpty()) {
			Map<String,HIMetadataView> existingViewMap =  getViewMap(mdServiceDb.getMetadataViewList(metadata.getId(), db.getId()));
			for (HIMetadataViewDTO view : views) {
				boolean create = false;
				HIMetadataView newView = existingViewMap.getOrDefault(view.getViewName(), null);
				if(newView == null) {
					create = true;
					newView = new HIMetadataView();
				}
				newView.setHasStoredProcedure(view.getHasStoredProcedure());
				newView.setHiResourceMetadata(metadata);
				newView.setViewAlias(view.getViewAlias());
				newView.setViewId(""+viewMap.get(view.getViewName()));
				newView.setViewName(view.getViewName());
				newView.setViewQuery(view.getViewQuery());
				newView.setViewType(view.getViewType());
				newView.setHiMetadataDatabases(db);
				if (create) mdServiceDb.addHIMetadataView(newView);
				else mdServiceDb.editHIMetadataView(newView);
			}
		}

		return db;
	}
	/**
     * Upserts a relationship entry in the metadata, handling the association of columns,
     * join types, and other relationship attributes.
     *
     * @param newRelationship 		new HIMetadataRelationships object to be upserted
     * @param relationship   		existing HIMetadataRelationships object to be updated
     * @param metadata       		parent HIResourceMetadata object
     * @param db             		associated MetadataDatabases object
     * @param map            		a mapping of column IDs to HIMetadataColumns objects
     */
	private void upsertRelation(HIMetadataRelationships newRelationship , HIMetadataRelationshipsDTO relationship , HIResourceMetadata metadata , MetadataDatabases db,
			Map<Integer,HIMetadataColumns> map) {
		newRelationship.setHiResourceMetadata(metadata);
		newRelationship.setJoinType(relationship.getJoinType());
		newRelationship.setOperator(relationship.getOperator());
		
		HIMetadataColumnsDTO leftColumnDTO = relationship.getLeftMetadataColumns();
		HIMetadataColumnsDTO rightColumnDTO = relationship.getRightMetadataColumns();
		
		int newLeftColId = oldNewColumnIdMap.get(leftColumnDTO.getId());
		newRelationship.setLeftMetadataColumns(map.get(newLeftColId));
		
		int newRightColId = oldNewColumnIdMap.get(rightColumnDTO.getId());
		newRelationship.setRightMetadataColumns(map.get(newRightColId));
		newRelationship.setHiMetadataDatabases(db);
		newRelationship.setExternal(relationship.isExternal());
	    mdServiceDb.addHIMetadataRelationships(newRelationship);
	    if(joinsOrder != null) {
	    	joinsOrder.put(""+newRelationship.getId(), relationship.getPosition() == null ?joinCounter++: relationship.getPosition());
	    }
	}
	
	/**
     * Creates a mapping of column IDs to HIMetadataColumns based on a list of columns.
     * 
     * @param mdColumns 		list of HIMetadataColumns to be organized into the map
     * @return a map where each key represents a column ID and maps to the corresponding HIMetadataColumns
     */
	private Map<Integer, HIMetadataColumns> getColumnMap(List<HIMetadataColumns> mdColumns) {
		Map<Integer, HIMetadataColumns> map = new HashMap<>();
		if(mdColumns == null) return map;
		for (HIMetadataColumns column : mdColumns) {
			map.put(column.getId(), column);
		}
		return map;
	}
	/**
     * Creates a mapping of table names to HIMetadataTables based on a list of tables.
     * 
     * @param mdTables 		list of HIMetadataTables to be organized into the map
     * @return a map where each key represents a table name and maps to the corresponding HIMetadataTables
     */
	private Map<String,HIMetadataTables> getTableMap(List<HIMetadataTables> mdTables){
		Map<String,HIMetadataTables> map = new HashMap<>();
		if(mdTables == null) {
			return map;
		}
		mdTables.forEach(it -> map.put(it.getTableName(), it));
		return map;
	}
	/**
     * Creates a mapping of view names to HIMetadataView based on a list of views.
     * 
     * @param views 		list of HIMetadataView to be organized into the map
     * @return a map where each key represents a view name and maps to the corresponding HIMetadataView
     */
	private Map<String,HIMetadataView> getViewMap(List<HIMetadataView> views){
		Map<String,HIMetadataView> map = new HashMap<>();
		if(views == null) {
			return map;
		}
		views.forEach(it -> map.put(it.getViewName(), it));
		return map;
	}
	/**
     * Generates a map of relationship keys to lists of HIMetadataRelationships.
     * Each key is created by concatenating the column alias names, operator, and right column alias name.
     * This method is used to efficiently manage relationships during metadata import.
     *
     * @param relations 			list of HIMetadataRelationships to be organized into the map
     * @return a map where each key represents a unique relationship and maps to a list of corresponding relationships
     */
	protected Map<String,List<HIMetadataRelationships>> getRelationshipMap(List<HIMetadataRelationships> relations){
		Map<String,List<HIMetadataRelationships>> map = new HashMap<>();
		
		for(HIMetadataRelationships rel : relations) {
			String key = rel.getLeftMetadataColumns().getColumnAliasName() + rel.getOperator() + rel.getRightMetadataColumns().getColumnAliasName();
			List<HIMetadataRelationships> relationList = map.get(key);
			if (relationList == null) {
				relationList = new LinkedList<>();
				relationList.add(rel);
				map.put(key, relationList);
			}
			else {
				relationList.add(rel);
				map.put(key, relationList);
			}
			
		}
		return map;
	}
	/**
     * Updates an existing metadata resource with the information provided in the fileMetadata.
     *
     * @param resource      the existing HIResource object to be updated
     * @param fileMetadata  the new metadata information to update the existing resource
     * @return the updated HIResource object
     */
	private HIResource updateMetadataResource(HIResource resource, HIResourceMetadataDTO fileMetadata) {
		Date date = context.getDate();
		resource.setLastUpdatedTime(date);
		HIResourceMetadata metadata = mdServiceDb.giveHIResourceMetadataByResourceId(resource.getResourceId());
		metadataUpdateHandler.setContext(context);
		metadataUpdateHandler.update(fileMetadata, metadata);
		metadata.setFileName(resource.getTitle());
		metadata.setLastUpdatedTime(date);
		metadata.setCreatedBy(resource.getCreatedBy());
		metadata.setHiResource(resource);
		mdServiceDb.editHIResourceMetadata(metadata);
		serviceDb.editHIResource(resource);
		return resource;
	}
	/**
     * Handles the import of security settings associated with the metadata resource.
     * It parses and associates security settings, considering the expression types, conditions, and filters.
     *
     * @param metadata    	original HIResourceMetadata object
     * @param newMetadata 	updated or newly created HIResourceMetadata object
     */
	private void handleSecurity(HIResourceMetadataDTO metadata, HIResourceMetadata newMetadata) {
		
		List<HIMetadataSecurityDTO> securities =  metadata.getMetadataSecurityList();
		if (securities != null && !securities.isEmpty()) {
			for (HIMetadataSecurityDTO security : securities) {
				HIMetadataSecurity newSecurity = new HIMetadataSecurity();
				newSecurity.setAccessType(security.getAccessType());
				newSecurity.setExpressionCondition(security.getExpressionCondition());
				newSecurity.setExpressionFilter(security.getExpressionFilter());
				newSecurity.setExpressionId(security.getExpressionId());
				newSecurity.setExpressionName(security.getExpressionName());
				String expressionType = security.getExpressionType();
				String[] expressionOnArr = security.getExpressionOn().split(",");
				if("table".equalsIgnoreCase(expressionType) || "global".equalsIgnoreCase(expressionType)) {
					StringBuilder sb = new StringBuilder();
					for(String expressionOn : expressionOnArr) {
						String newId = ""+oldNewTableIdMap.get(Integer.valueOf(expressionOn));
						sb.append(newId+",");
					}
					String newExpressionOn = sb.length() > 0 ? sb.substring(0,sb.length()-1):"";
					newSecurity.setExpressionOn(newExpressionOn);
				}
				else if("column".equalsIgnoreCase(expressionType)) {
					StringBuilder sb = new StringBuilder();
					for(String expressionOn : expressionOnArr) {
						String newId = ""+oldNewColumnIdMap.get(Integer.valueOf(expressionOn));
						sb.append(newId+",");
					}
					String newExpressionOn = sb.length() > 0 ? sb.substring(0,sb.length()-1):"";
					newSecurity.setExpressionOn(newExpressionOn);
				}
				newSecurity.setExpressionType(security.getExpressionType());
				newSecurity.setHiResourceMetadata(newMetadata);
				newSecurity.setType(security.getType());
				mdServiceDb.addHIMetadataSecurity(newSecurity);
			}
		}
		
	}
}
