package com.helicalinsight.export.handler.importres;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

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
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HIResourceMetadataDTO;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.efw.utility.SplitterUtils;

/**
 * Manages metadata resource import, updating tables, columns, views,
 * relationships, and security settings in collaboration with various components.
 */
@Component
public class MetadataUpdateHandler extends MetadataImportHandler {
	
	private static final String TABLE = "table";
	private static final String GLOBAL = "global";
	private static final String COLUMN = "column";
	
	
	/**
     * Synchronizes fileMetadata with existing dbMetadata, updating tables,
     * columns, views, relationships, and security settings.
     *
     * @param fileMetadata 		object used to get metadata connection
     * @param dbMetadata   		The existing metadata in the system to be updated.
     */
	public  void update(HIResourceMetadataDTO fileMetadata, HIResourceMetadata dbMetadata) {
		
		Map<String,HIMetadataTableDTO> fileTableMap = new HashMap<>();
		Map<String,TableColumnPair> fileColumnMap = new HashMap<>();
		Map<String,HIMetadataTables> oldNewTableMap = new HashMap<>();
		Map<String,HIMetadataViewDTO> fileViewMap = new HashMap<>();
		Map<String,HIMetadataView> dbViewMap = new HashMap<>(); 
		Map<String,HIMetadataColumns> dbColumnMap  = new HashMap<>();
		Map<Integer,HIMetadataTables> tableIdMap = new HashMap<>();
		Map<Integer,HIMetadataColumns> columnIdMap = new HashMap<>();
		List<HIMetadataRelationshipsDTO> fileRelationList = new ArrayList<>();
		Map<MetadataDatabases,List<HIMetadataRelationshipsDTO>> databaseRelationMap = new HashMap<>();
		
		List<HIMetadataConnectionDTO> fileConnections = fileMetadata.getHiMetadataConnections();
		List<HIMetadataConnections> connections = mdServiceDb.getHIMetadataConnections(dbMetadata.getId());
		
		for(HIMetadataConnectionDTO connection : fileConnections) {
			
			for(MetadataDatabasesDTO database : connection.getMetadataDatabases()) {
				for(HIMetadataTableDTO table :  database.getMetadataTablesList()) {
					String tableKey = hash(table.getTableName()+"#"+table.getTableAliasName());
					fileTableMap.put(tableKey, table);
					for(HIMetadataColumnsDTO column : table.getColumnsList()) {
						String columnKey = hash(table.getTableName()+"#"+column.getColumnName()+"#"+column.getColumnAliasName());
						TableColumnPair pair = new TableColumnPair(column, table);
						fileColumnMap.put(columnKey,pair);
					}
				}
				for(HIMetadataViewDTO view : database.getMetadataViewList()) {
					fileViewMap.put(hash(view.getViewName()+"#"+view.getViewAlias()), view);
				}				
				fileRelationList.addAll(database.getMetadataRelationShipList());
			}
		}
		
		handleConnections(connections, fileConnections, dbMetadata);
		
		for (HIMetadataConnections connection : connections) {
			
			MetadataDatabases database = mdServiceDb.getHIMetadataDatabases(dbMetadata.getId(),String.valueOf(connection.getId()));
			List<HIMetadataTables> tables = mdServiceDb.getMetadataTablesList(dbMetadata.getId(), database.getId());
			List<HIMetadataView> views = mdServiceDb.getMetadataViewList(dbMetadata.getId(), database.getId());
			views.forEach(view -> dbViewMap.put(hash(view.getViewName() + "#" + view.getViewAlias()), view));
			for (HIMetadataTables dbTable : tables) {
				String key = hash(dbTable.getTableName() + "#" + dbTable.getTableAliasName());
				HIMetadataTableDTO fileTable = fileTableMap.get(key);
				if (fileTable != null) {
					dbTable.setOriginalName(fileTable.getOriginalName());
					dbTable.setTableAliasName(fileTable.getTableAliasName());
					dbTable.setTableName(fileTable.getTableName());
					for (HIMetadataColumns dbCol : dbTable.getColumnsList()) {
						String colKey = hash(dbTable.getTableName() + "#" + dbCol.getColumnName() + "#"
								+ dbCol.getColumnAliasName());
						TableColumnPair pair = fileColumnMap.get(colKey);
						if (pair != null) {
							HIMetadataColumnsDTO fileCol = pair.column;
							dbCol.setColumn_type(fileCol.getColumn_type());
							dbCol.setColumnAliasName(fileCol.getColumnAliasName());
							dbCol.setColumnName(fileCol.getColumnName());
							dbCol.setDefaultFunction(fileCol.getDefaultFunction());
							dbCol.setOriginalName(fileCol.getOriginalName());
							mdServiceDb.editHIMetadataColumns(dbCol);
							columnIdMap.put(fileCol.getId(), dbCol);
						} else {
							mdServiceDb.deleteHIMetadataColumnById(dbCol.getId());
						}
						fileColumnMap.remove(colKey);
						String justColKey = hash(dbCol.getColumnAliasName() + "#" + dbCol.getColumnName());
						dbColumnMap.put(justColKey, dbCol);
					}

					boolean isView = fileTable.getView() != null && fileTable.getView();
					dbTable.setView(isView);
					mdServiceDb.editHIMetadataTables(dbTable);
					if (isView) {
						upsertView(dbMetadata, database, fileViewMap, dbViewMap, dbTable);
					}
					oldNewTableMap.put(key, dbTable);
					tableIdMap.put(fileTable.getId(), dbTable);
					
				} else {
					dbTable.getColumnsList().forEach(col -> mdServiceDb.deleteHIMetadataColumnById(col.getId()));
					mdServiceDb.deleteHIMetadataTable(dbTable.getId(), dbTable.getHiMetadataDatabases().getId());
				}
				fileTableMap.remove(key);
			}
			databaseRelationMap.put(database, fileRelationList);
		}
		
	  dbMetadata.getHiMetadataConnections().forEach(connection -> connection.getMetadataDatabases()
			  .stream().distinct().forEach(db -> {
		  	if(!fileTableMap.isEmpty()) {
				fileTableMap.forEach((k,v) -> {
					HIMetadataTables table = mapper.map(v);
					table.setHiMetadataDatabases(db);
					table.setHiResourceMetadata(dbMetadata);
					int tabId = v.getId();
					table.setId(null);
					List<HIMetadataColumns> columns =  new ArrayList<>();
					
					columns.addAll(v.getColumnsList().stream()
					.map(dto -> mapper.map(dto))
					.toList());
					table.setColumnsList(columns);
					mdServiceDb.addHIMetadataTables(table);
					boolean isView = table.getView() != null && table.getView();
					if (isView) {
						upsertView(dbMetadata, db, fileViewMap, dbViewMap, table);
					}
					tableIdMap.put(tabId, table);
					columns.forEach(col -> {
						
						col.setHiResourceMetadata(dbMetadata);
						col.setHiMetadataTables(table);
						int id = col.getId();
						col.setId(null);
						mdServiceDb.addHIMetadataColumns(col);
						String colKey = hash(v.getTableName()+"#"+col.getColumnName()+"#"+col.getColumnAliasName());
						fileColumnMap.remove(colKey);
						columnIdMap.put(id, col);
					});
				});
			}
		  	
		  	if (!fileColumnMap.isEmpty()) {
		  		fileColumnMap.forEach((k,v) -> {
		  			HIMetadataTables oldTable =  mapper.map(v.table);
		  			String key = hash(oldTable.getTableName()+"#"+oldTable.getTableAliasName());
		  			HIMetadataTables newTable =  oldNewTableMap.get(key);
		  			HIMetadataColumns column = mapper.map(v.column);
		  			int id = column.getId();
		  			column.setId(null);
		  			column.setHiMetadataTables(newTable);
		  			column.setHiResourceMetadata(dbMetadata);
		  			mdServiceDb.addHIMetadataColumns(column);
		  			columnIdMap.put(id, column);
		  		});
		  	}
	  }));	
	  databaseRelationMap.forEach((db,rList) -> handleJoins(rList, dbMetadata, db, columnIdMap));
	  handleSecurity(fileMetadata.getMetadataSecurityList(),dbMetadata,tableIdMap,columnIdMap);
	}
	
	/**
     * Updates metadata security settings based on fileMetadata, handling expressionOn
     * values for tables and columns.
     *
     * @param securityList 		List of security settings from the file metadata.
     * @param metadata     		The metadata resource to which security settings are applied.
     * @param tableIdMap   		Map of table IDs for reference in security expressions.
     * @param columnIdMap   	Map of column IDs for reference in security expressions.
     */
	private void handleSecurity(List<HIMetadataSecurityDTO> securityList, HIResourceMetadata metadata,
			Map<Integer,HIMetadataTables> tableIdMap , Map<Integer,HIMetadataColumns> columnIdMap) {
		
		mdServiceDb.deleteAllSecuritiesByMetadataId(metadata.getId());
		
		for( HIMetadataSecurityDTO security : securityList ) {
			HIMetadataSecurity entity = mapper.map(security);
			entity.setId(null);
			entity.setHiResourceMetadata(metadata);
			String expressiOn = getExpressionOn(security.getExpressionOn(), security.getExpressionType(),tableIdMap, columnIdMap);
			entity.setExpressionOn(expressiOn);
			mdServiceDb.addHIMetadataSecurity(entity);
		}
	} 
	
	/**
	 * Generates an expression string based on the given file expression, expression type, and mapping of IDs.
	 *
	 * @param fileExpressionOn 		file expression string.
	 * @param expressionType   	    type of expression (TABLE, COLUMN, GLOBAL).
	 * @param tableIdMap            Mapping of table IDs.
	 * @param columnIdMap           Mapping of column IDs.
	 * @return The generated expression string.
	 */
	private final String getExpressionOn(String fileExpressionOn , String expressionType,
			Map<Integer,HIMetadataTables> tableIdMap , Map<Integer,HIMetadataColumns> columnIdMap) {
		String[] expressionOnArr = fileExpressionOn.split(",");
		
		if(TABLE.equalsIgnoreCase(expressionType) || GLOBAL.equalsIgnoreCase(expressionType)) {
			StringBuilder sb = new StringBuilder();
			for(String expressionOn : expressionOnArr) {
				String newId = String.valueOf(tableIdMap.get(Integer.valueOf(expressionOn)).getId());
				sb.append(newId+",");
			}
			return sb.length() > 0 ? sb.substring(0,sb.length()-1):"";
		}
		else if(COLUMN.equalsIgnoreCase(expressionType)) {
			StringBuilder sb = new StringBuilder();
			for(String expressionOn : expressionOnArr) {
				String newId = String.valueOf(columnIdMap.get(Integer.valueOf(expressionOn)).getId());
				sb.append(newId+",");
			}
			return  sb.length() > 0 ? sb.substring(0,sb.length()-1):"";
		}
		return "";
		
	}
	/**
	 * Handles the import of metadata relationships, deleting existing relationships, and adding new ones.
	 *
	 * @param fileRelationships  List of file metadata relationships.
	 * @param metadata           The metadata resource.
	 * @param db                 The metadata database.
	 * @param columnIdMap        Mapping of column IDs.
	 */
	private void handleJoins(List<HIMetadataRelationshipsDTO> fileRelationships , HIResourceMetadata metadata, MetadataDatabases db
			,Map<Integer,HIMetadataColumns> columnIdMap) {
		
		
		mdServiceDb.deleteAllRelationships(metadata.getId(), db.getId());
		mdServiceDb.deleteAllExternalRelationships(metadata.getId());
		
		
		for(HIMetadataRelationshipsDTO fileRel : fileRelationships ) {
			
			HIMetadataRelationships entity = mapper.map(fileRel);
			
			HIMetadataColumns leftDbCol =  columnIdMap.get(fileRel.getLeftMetadataColumns().getId());
			HIMetadataColumns rightDbCol = columnIdMap.get(fileRel.getRightMetadataColumns().getId());
			entity.setId(null);
			entity.setHiResourceMetadata(metadata);
			entity.getJoinsPositions().setMetadataId(metadata.getId());
			
			if(Boolean.TRUE.equals(entity.getExternal())) {
				entity.setHiMetadataDatabases(null);
			}
			else {
				entity.setHiMetadataDatabases(db);
			}
			if(leftDbCol != null && rightDbCol != null) {
				entity.setLeftMetadataColumns(leftDbCol);
				entity.setRightMetadataColumns(rightDbCol);
				mdServiceDb.addHIMetadataRelationships(entity);
			}
		}
	}
	/**
	 * Upserts (adds or updates) a view based on the provided metadata and database information.
	 *
	 * @param dbMetadata    The metadata resource.
	 * @param db            The metadata database.
	 * @param fileViewMap   Mapping of file views.
	 * @param dbViewMap     Mapping of database views.
	 * @param viewTable     The metadata table representing the view.
	 */
	private void upsertView(HIResourceMetadata dbMetadata, MetadataDatabases db, 
			Map<String,HIMetadataViewDTO> fileViewMap,Map<String,HIMetadataView> dbViewMap,
			HIMetadataTables viewTable) {
		String viewKey = hash(viewTable.getTableName()+"#"+viewTable.getTableAliasName());
		HIMetadataView dbView = dbViewMap.get(viewKey);
		HIMetadataViewDTO fileView = fileViewMap.get(String.valueOf(viewKey));
		boolean isCreate= dbView == null;
		dbView =  Optional.ofNullable(dbView).orElse(new HIMetadataView());
		if(fileView != null) {
			dbView.setHiMetadataDatabases(db);
			dbView.setHiResourceMetadata(dbMetadata);
			dbView.setViewId(String.valueOf(viewTable.getId()));
			dbView.setHasStoredProcedure(fileView.getHasStoredProcedure());
			dbView.setViewAlias(fileView.getViewAlias());
			dbView.setViewName(fileView.getViewName());
			dbView.setViewQuery(fileView.getViewQuery());
			dbView .setViewType(fileView.getViewType());
			if(isCreate) mdServiceDb.addHIMetadataView(dbView);
			else mdServiceDb.editHIMetadataView(dbView);
		}
		
		
	}
	
	/**
	 * Updates metadata connections based on file connections, ensuring synchronization
	 * with existing database connections and handling both Global and EFWD connection types.
	 *
	 * @param dbConnections    List of existing metadata connections in the system.
	 * @param fileConnections  List of metadata connections from the file to be imported.
	 * @param metadata         The metadata resource to which connections are applied.
	 */
	private final void handleConnections(List<HIMetadataConnections> dbConnections , 
			List<HIMetadataConnectionDTO> fileConnections,HIResourceMetadata metadata) {
		
		Map<String,HIMetadataConnections> dbConnectionMap = new HashMap<>();
		for(HIMetadataConnections dbConn : dbConnections ) {
			MetadataDatabases db =  mdServiceDb.getHIMetadataDatabases(metadata.getId(), String.valueOf(dbConn.getId()));
			String key = hash(db.getName() + db.getCatalog() + db.getSchema());
			dbConnectionMap.put(key, dbConn);
		}
		Set<HIMetadataConnectionDTO> processed = new HashSet<>();
		for(HIMetadataConnectionDTO fileConnection : fileConnections) {
			MetadataDatabasesDTO db =  fileConnection.getMetadataDatabases().get(0);
			String key = hash(db.getName() + db.getCatalog() + db.getSchema());
			HIMetadataConnections dbConnection = dbConnectionMap.get(key);
			if ( dbConnection != null ) {
				updateConnection(dbConnection, fileConnection,metadata);
				processed.add(fileConnection);
				dbConnectionMap.remove(key);
			}
		}
		
		if(!dbConnectionMap.isEmpty()) {
			dbConnectionMap.forEach((k,v) -> {
				 
				for(HIMetadataConnectionDTO fileConnection : fileConnections) {
					if(!processed.contains(fileConnection)) {
						updateConnection(v, fileConnection,metadata);
						processed.add(fileConnection);
					}
				}
			});
		}
		
	}
	/**
	 * Updates a specific metadata connection based on file connection details,
	 * handling different connection types: Global to Global, Global to EFWD, EFWD to Global, and EFWD to EFWD.
	 *
	 * @param dbConnection      	existing metadata connection in the system.
	 * @param fileConnection    	metadata connection details from the file to be imported.
	 * @param metadata          	metadata resource to which the connection is applied.
	 */
	private final void updateConnection(HIMetadataConnections dbConnection , HIMetadataConnectionDTO fileConnection,HIResourceMetadata metadata) {
		
		String targetConnectionType = fileConnection.getConnectionType();
		String sourceConnectionType = dbConnection.getConnectionType();
		if(GlobalJdbcTypeUtils.isJustGlobal(targetConnectionType)) {
			HIMetadataConnectionGlobalDTO fileGlobal =  fileConnection.getMetadataGlobalConnList().get(0);
			// Global to Global
			if(GlobalJdbcTypeUtils.isGlobalJdbc(sourceConnectionType)) {
				HIMetadataConnectionGlobal dbGlobal =  dbConnection.getMetadataGlobalConnList().get(0);
				globalToGlobal(dbGlobal, fileGlobal);			}
			else {
				// Efwd to Global
				HIMetadataConnectionEFWD dbEfwd = dbConnection.getMetadataConnectionEfwd().get(0);
				plainToGlobal(dbEfwd, fileGlobal, metadata);
			}
		}
		else {
			
			//  GLobal to EFWD
			if(GlobalJdbcTypeUtils.isJustGlobal(sourceConnectionType)) {
				HIMetadataConnectionGlobal dbGlobal =  dbConnection.getMetadataGlobalConnList().get(0);
				HIMetadataConnectionEFWDDTO fileEfwd = fileConnection.getMetadataConnectionEfwd().get(0);
				globalToPlain(dbGlobal, fileEfwd, metadata);
			}
			// EFWD to EFWD
			else {
				HIMetadataConnectionEFWD dbEfwd = dbConnection.getMetadataConnectionEfwd().get(0);
				HIMetadataConnectionEFWDDTO fileEfwd = fileConnection.getMetadataConnectionEfwd().get(0);
				plainToPlain(dbEfwd, fileEfwd);
			}
		}
		dbConnection.setConnectionType(targetConnectionType);
		mdServiceDb.editHIMetadataConnections(dbConnection);
		MetadataDatabasesDTO fileDB = fileConnection.getMetadataDatabases().get(0);
		MetadataDatabases db = dbConnection.getMetadataDatabases().get(0);

		db.setCatalog(fileDB.getCatalog());
		db.setMetadataConnections(dbConnection);
		db.setName(fileDB.getName());
		db.setSchema(fileDB.getSchema());
		mdServiceDb.editHIMetadataDatabases(db);
	}
	/**
	 * Handles the synchronization of Global to Global connection details.
	 *
	 * @param dbGlobal    	existing Global metadata connection in the system.
	 * @param fileGlobal  	Global metadata connection details from the file to be imported.
	 */
	private void globalToGlobal(HIMetadataConnectionGlobal dbGlobal , HIMetadataConnectionGlobalDTO fileGlobal) {
		dbGlobal.setDialect(fileGlobal.getDialect());
		dbGlobal.setDriverClass(fileGlobal.getDriverClass());
		dbGlobal.setDriverClassReference(fileGlobal.getDriverClassReference());
		GlobalConnections global = context.getGlobalConnection(fileGlobal.getGlobalConnections().getGlobalId());
		if(null != global) {
			dbGlobal.setGlobalConnections(global);
		}
		mdServiceDb.editHIMetadataConnectionGlobal(dbGlobal);
	}
	/**
	 * Handles the transition from Global to EFWD connection details.
	 *
	 * @param dbGlobal   	existing Global metadata connection in the system.
	 * @param fileEfwd    	EFWD metadata connection details from the file to be imported.
	 * @param metadata      metadata resource to which the connection is applied.
	 */
	private void globalToPlain(HIMetadataConnectionGlobal dbGlobal , HIMetadataConnectionEFWDDTO fileEfwd , HIResourceMetadata metadata) {
		HIMetadataConnectionEFWD dbEfwd = new HIMetadataConnectionEFWD();
		dbEfwd.setDialect(fileEfwd.getDialect());
		dbEfwd.setDriverClass(fileEfwd.getDriverClass());
		dbEfwd.setDriverClassReference(fileEfwd.getDriverClassReference());
		HIEfwdConnection connection = context.getEfwdConnection(fileEfwd.getHiEfwdConnection().getId());
		if(null != connection) {
			dbEfwd.setHiEfwdConnection(connection);
		}
		dbEfwd.setHiMetadataConnections(dbGlobal.getHiMetadataConnections());
		mdServiceDb.saveHIMetadataConnectionEfwd(dbEfwd);
		mdServiceDb.deleteMetadataGlobalConnection(dbGlobal);
	}
	/**
	 * Handles the transition from EFWD to Global connection details.
	 *
	 * @param dbEfwd      	existing EFWD metadata connection in the system.
	 * @param fileGlobal  	Global metadata connection details from the file to be imported.
	 * @param metadata      metadata resource to which the connection is applied.
	 */
	private void plainToGlobal(HIMetadataConnectionEFWD dbEfwd , HIMetadataConnectionGlobalDTO fileGlobal, HIResourceMetadata metadata) {
		
		HIMetadataConnectionGlobal dbGlobal = new HIMetadataConnectionGlobal();
		dbGlobal.setDialect(fileGlobal.getDialect());
		dbGlobal.setDriverClass(fileGlobal.getDriverClass());
		dbGlobal.setDriverClassReference(fileGlobal.getDriverClassReference());
		GlobalConnections global = context.getGlobalConnection(fileGlobal.getGlobalConnections().getGlobalId());
		if(null != global) {
			dbGlobal.setGlobalConnections(global);
		}
		dbGlobal.setHiMetadataConnections(dbEfwd.getHiMetadataConnections());
		mdServiceDb.saveHIMetadataConnectionGlobal(dbGlobal);
		mdServiceDb.deleteMetadataEfwdConnection(dbEfwd);
		
	}
	/**
	 * Handles the synchronization of EFWD to EFWD connection details.
	 *
	 * @param dbEfwd     	existing EFWD metadata connection in the system.
	 * @param fileEfwd  	EFWD metadata connection details from the file to be imported.
	 */
	private void plainToPlain(HIMetadataConnectionEFWD dbEfwd, HIMetadataConnectionEFWDDTO fileEfwd) {
		dbEfwd.setDialect(fileEfwd.getDialect());
		dbEfwd.setDriverClass(fileEfwd.getDriverClass());
		dbEfwd.setDriverClassReference(fileEfwd.getDriverClassReference());
		HIEfwdConnection connection = context.getEfwdConnection(fileEfwd.getHiEfwdConnection().getId());
		if(null != connection) {
			dbEfwd.setHiEfwdConnection(connection);
		}
		dbEfwd.setHiMetadataConnections(dbEfwd.getHiMetadataConnections());
		mdServiceDb.editHIMetadataConnectionEfwd(dbEfwd);
	}
	
	
	/**
	 * Represents a pair of metadata column and table for simplified handling and mapping.
	 */
	private static class TableColumnPair {
		private final  HIMetadataColumnsDTO column;
		private final  HIMetadataTableDTO table;
		
		public TableColumnPair(HIMetadataColumnsDTO column, HIMetadataTableDTO table) {
			this.column = column;
			this.table = table;
		}
	}
	
	/**
	 * Generates a hash using the provided key, ensuring consistency in service ID preparation.
	 *
	 * @param key 		input key for hash generation.
	 * @return The prepared service ID.
	 */
	protected  final String hash(String key) {
		return  SplitterUtils.prepareServiceId(key);
	}

}
