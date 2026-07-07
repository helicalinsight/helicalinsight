package com.helicalinsight.adhoc.metadata.genericdb;

import static com.helicalinsight.resourcedb.ResourceConstants.METADATA;
import static com.helicalinsight.resourcedb.ResourceConstants.METADATA_EXTENSION;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.MetadataCacheStatus;
import com.helicalinsight.adhoc.MetadataDriverReferences;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.metadata.jaxb.Views;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataSecurity;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIMetadataView;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.AuditContext;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.resourcedb.AuditDetails;
import com.helicalinsight.resourcedb.HIPhaseConstants;
import com.helicalinsight.resourcedb.MetadataDumpDTO;
import com.helicalinsight.resourcedb.processor.DBProcessor;


/**
 * The MetadataDBUtility class provides utility methods for managing metadata within application.
 * It includes functionalities for saving metadata to the database, handling security expressions, managing views,
 * fetching metadata from the database, and performing various utility operations related to metadata management.
 */
public class MetadataDBUtility {

    private Map<String, Object> columnMap = new HashMap<>();
    
   
    private JsonObject newFormJson;
    private Map<String,Integer> tableIdMap;
    private Map<String,Integer> columnIdMap;
    private  String mode;
    private AuditContext auditContext;
    /**
     * Saves the provided metadata to the database.
     * This method creates or updates records for various metadata entities such as resources, metadata, connections,
     * databases, tables, columns, views, and security expressions.
     *
     * @param metadata             		 metadata object provides connection and connection details object.
     * @param dir                  		 directory where the metadata file is located.
     * @param fileNameWithExtension 	 name of the metadata file with its extension.
     * @param mode                 		 mode indicating whether the metadata is being created or edited.
     * @return The URL of the saved resource.
     */
    public String saveMetadataToDB(Metadata metadata, String dir, String fileNameWithExtension, String mode) {
    	auditContext.setAction(mode.equalsIgnoreCase("edit")?"UPDATE":"CREATE");
    	AuditDetails initiated = auditContext.init(HIPhaseConstants.MD_INITIATED);
    	this.mode = mode;
    	tableIdMap = new HashMap<>();
    	columnIdMap = new HashMap<>();
        List<Relationships> relationList = new ArrayList<>();
    	HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResource hiResource = saveHIResource(dir, fileNameWithExtension, metadata, mode);
        auditContext.setHIResource(hiResource);
        initiated.setEndTime(new Date());
        
        if (null == hiResource) {
            throw new EfwServiceException("Could not Save metadata");
        }

        HIResourceMetadata hiResourceMetadata = saveHIMetadata(metadata, hiResource);
        if (null == hiResourceMetadata) {
            throw new EfwServiceException("Metadata Not Found");
        }


        //HIMetadataTables
        List<Database> databases = new ArrayList<>();
        List<ConnectionDetails> connectionDetails = new ArrayList<>();
        databases.add(metadata.getDatabase());
        connectionDetails.add(metadata.getConnectionDetails());
        Connections connections = metadata.getConnections();
        if (connections != null) {
            List<ConnectionDatabase> connectionDatabase = connections.getConnectionDatabase();
            if (connectionDatabase != null && !connectionDatabase.isEmpty()) {
                for (ConnectionDatabase cdm : connectionDatabase) {
                    databases.add(cdm.getDatabase());
                    connectionDetails.add(cdm.getConnectionDetails());
                }
            }
        }
        Integer metadataId = hiResourceMetadata.getId();
        int counter = 0;
        List<Column> allColumnList = new ArrayList<>();
        Map<String,Map<Integer,List<HIMetadataTables>>> tableMap = new HashMap<>();
        Map<String,Map<Integer,List<HIMetadataColumns>>> columnsMap = new HashMap<>();
        for (Database database : databases) {
			ConnectionDetails conDetails = connectionDetails.get(counter);
			if (conDetails.getDbId() == null ) {
				conDetails.setDbId(database.getId());
			}
			else {
				database.setId(conDetails.getDbId());
			}
			HIMetadataConnections metadataConnections = saveHIMetaDataConnections(metadata, hiResourceMetadata,conDetails);
			MetadataDatabases metadataDatabases;
			
			if (conDetails.getDirectory() != null && !conDetails.getDirectory().equals("")) {
				HIMetadataConnectionEFWD efwdConnection = saveHIMetadataConnectionEfwd(conDetails, metadataConnections);
				if (efwdConnection == null) {
					throw new EfwServiceException("Metadata Connections Not Found");
				}
				metadataDatabases = saveHIMetadataDatabases(database, hiResourceMetadata, metadataConnections);

				counter++;
			} else {
				HIMetadataConnectionGlobal metadataConnectionGlobal = saveHIMetadataConnectionGlobal(conDetails,
						metadataConnections);
				if (null == metadataConnections) {
					throw new EfwServiceException("Metadata Connections Not Found");
				}
				metadataDatabases = saveHIMetadataDatabases(database, hiResourceMetadata, metadataConnections);
				counter++;
			}
            
            if (null == metadataDatabases) {
                throw new EfwServiceException("Metadata Databases Not Found");
            }

            Tables tables = database.getTables();
            List<Table> tableList = tables.getTableList();
            List<HIMetadataTables> hiMetadataTableList = new ArrayList<>();
            List<HIMetadataColumns> hiMetadataColumnsList = new ArrayList<>();

            List<HIMetadataTables> existingTablesInDb = hiResourceServiceDB.getMetadataTablesList(metadataId, metadataDatabases.getId());
            List<HIMetadataColumns> existingColumnInDb = hiResourceServiceDB.getMetadataColumnsList(metadataId,metadataDatabases.getId());
            Map<String, HIMetadataTables> mappedTableDb = new HashMap<>();
            Map<String, HIMetadataColumns> mappedColumnsDb = new HashMap<>();
            if (existingTablesInDb != null) {
            	existingTablesInDb.forEach(item -> mappedTableDb.put(""+item.getId(), item));
            }
            if (existingColumnInDb != null) {
                existingColumnInDb.forEach(item -> mappedColumnsDb.put(""+item.getId(), item));
            }
            
            Map<String, String> renamedView = new HashMap<>();
            for (Table table : tableList) {
                HIMetadataTables hiMetadataTables = mappedTableDb.get(table.getId());
                boolean isCreate = hiMetadataTables == null;
                if (isCreate) {
                    hiMetadataTables = new HIMetadataTables();
                }
                hiMetadataTables.setTableName(table.getName());
                if (table.getType() != null) {
                    renamedView.put(table.getId(), table.getAliasName());
                }
                hiMetadataTables.setTableAliasName(table.getAliasName());
                hiMetadataTables.setHiResourceMetadata(hiResourceMetadata);
                hiMetadataTables.setHiMetadataDatabases(metadataDatabases);
                hiMetadataTables.setOriginalName(table.getOriginalName());
                hiMetadataTables.setView("view".equalsIgnoreCase(table.getType()));
                hiMetadataTableList.add(hiMetadataTables);
                if (isCreate) {
                    hiResourceServiceDB.addHIMetadataTables(hiMetadataTables);
                    mappedTableDb.put(""+hiMetadataTables.getId(), hiMetadataTables);
                    if( StringUtils.isNumeric(table.getId()) ) {
                    	tableIdMap.put(table.getId(),hiMetadataTables.getId());
					}
                    tableIdMap.put(table.getId()+"_"+conDetails.getDbId(), hiMetadataTables.getId());
                } else {
                    hiResourceServiceDB.editHIMetadataTables(hiMetadataTables);
                    tableIdMap.put(table.getId(),hiMetadataTables.getId());
                }
                table.setId(""+hiMetadataTables.getId());
                Columns columns = table.getColumns();
                List<Column> columnList = columns.getColumn();
                //HIMetdataColumns
				if (columnList != null && !columnList.isEmpty()) {
					for (Column column : columnList) {
						HIMetadataColumns hiMetadataColumns = mappedColumnsDb.get(column.getId());
						boolean isColumnCreate = hiMetadataColumns == null;
						if (isColumnCreate) {
							hiMetadataColumns = new HIMetadataColumns();
						}
						hiMetadataColumns.setColumnName(column.getName());
						hiMetadataColumns.setColumn_type(column.getType());
						hiMetadataColumns.setColumnAliasName(column.getAliasName());
						hiMetadataColumns.setDefaultFunction(column.getDefaultFunction());
						hiMetadataColumns.setHiMetadataTables(hiMetadataTables);
						hiMetadataColumns.setHiResourceMetadata(hiResourceMetadata);
						hiMetadataColumns.setOriginalName(column.getOriginalName());
						hiMetadataColumnsList.add(hiMetadataColumns);
						if (isColumnCreate) {
							mappedColumnsDb.put(column.getId(), hiMetadataColumns);
							hiResourceServiceDB.addHIMetadataColumns(hiMetadataColumns);
							if( StringUtils.isNumeric(column.getId()) ) {
								columnIdMap.put(column.getId(),hiMetadataColumns.getId());
							}
							columnIdMap.put(column.getId()+"_"+conDetails.getDbId(),hiMetadataColumns.getId());
						} else {
							hiResourceServiceDB.editHIMetadataColumns(hiMetadataColumns);
							columnIdMap.put(column.getId(),hiMetadataColumns.getId());
						}
						column.setId(""+hiMetadataColumns.getId());
						columnMap.put(conDetails.getDbId() + table.getName() + column.getName(), hiMetadataColumns);

					}
					allColumnList.addAll(columnList);
				}
                

            }
            //tkr
            Collection<HIMetadataColumns> existingColumns = mappedColumnsDb.values();
            Map<String, Column> currentColumn = allColumnList.stream().collect(Collectors.toMap(Column::getId, Function.identity()));
            existingColumns.stream().forEach(r ->
            {
                String columnId = ""+r.getId();
                Column column = currentColumn.get(columnId);
                if (column == null) {
                    hiResourceServiceDB.deleteHIMetadataColumn(metadataId, r.getId());
                }
            });
            Collection<HIMetadataTables> existingTablesList = mappedTableDb.values();
            Map<String, Table> existingMap = tableList.stream().collect(Collectors.toMap(Table::getId, Function.identity()));
            existingTablesList.stream().forEach(r ->
            {
                String tableId = ""+r.getId();
                Table tab = existingMap.get(tableId);
                if (tab == null) {
                    hiResourceServiceDB.deleteHIMetadataTable(r.getId(), metadataDatabases.getId());
                }
            });
            List<HIMetadataView> metadataViewList = hiResourceServiceDB.getMetadataViewList(metadataId, metadataDatabases.getId());
            Map<String, HIMetadataView> mappedView = metadataViewList.stream().collect(Collectors.toMap(HIMetadataView::getViewId, Function.identity()));
            handleViews(database, hiResourceServiceDB, hiResourceMetadata, metadataDatabases, renamedView, mappedView);
            relationList.add(database.getRelationships());
        }
        
        EnhancedJoinsHandler joinsHandler = (EnhancedJoinsHandler) ApplicationContextAccessor.getBean("enhancedJoinsHandler");
        JsonObject obj = JsonParser.parseString(newFormJson.toString()).getAsJsonObject();
        joinsHandler.handle(obj, hiResourceMetadata, columnMap,relationList, mode);
        handleSecurity(metadata, hiResourceServiceDB, hiResourceMetadata, metadataId);
        String resourceURL = hiResource.getResourceURL();
        String[] split = resourceURL.split("/");
        
        AuditDetails completed = auditContext.init(HIPhaseConstants.MD_COMPLETED);
        completed.setEndTime(new Date());
        auditContext.commit();
        if (split.length >= 2) {
            return split[split.length - 1];
        } else {
            return split[0];
        }
    }

    /**
     * Handles security expressions associated with the provided metadata.
     * This method creates, edits, or deletes security expressions based on the metadata provided.
     *
     * @param metadata             		 metadata containing security expressions.
     * @param hiResourceServiceDB  		 service for interacting with HIResource entities in the database.
     * @param hiResourceMetadata   		 metadata associated with the HIResource.
     * @param metadataId           		 ID of the metadata.
     */
	private void handleSecurity(Metadata metadata, HIMetadataResourceServiceDB hiResourceServiceDB, 
			HIResourceMetadata hiResourceMetadata, Integer metadataId ) {
        MetadataSecurity metadataSecurity = metadata.getMetadataSecurity();
        List<HIMetadataSecurity> metaSecurity = hiResourceServiceDB.getMetaSecurity(metadataId);
        Map<String, HIMetadataSecurity> expressionMap = metaSecurity.stream().collect(Collectors.toMap(HIMetadataSecurity::getExpressionId, Function.identity()));


        if (metadataSecurity != null) {
            List<SecurityExpression> expressions = metadataSecurity.getExpressions();
            Map<String, SecurityExpression> collect = expressions.stream().collect(Collectors.toMap(SecurityExpression::getId, Function.identity()));


            for (SecurityExpression item : expressions) {
                HIMetadataSecurity hiMetadataSecurity = expressionMap.get(item.getId());
                boolean create = false;
                if (hiMetadataSecurity == null) {
                    hiMetadataSecurity = new HIMetadataSecurity();
                    create = true;
                }
                hiMetadataSecurity.setAccessType(item.getAccessType());
                hiMetadataSecurity.setExpressionCondition(item.getCondition());
                hiMetadataSecurity.setExpressionFilter(item.getFilter());
                hiMetadataSecurity.setExpressionName(item.getExpressionName());
                hiMetadataSecurity.setExpressionType(item.getExpressionType());
                hiMetadataSecurity.setExpressionId(item.getId());
                hiMetadataSecurity.setType(item.getType());
                String on =  item.getOn();
                String[] entities = on.split(",");
				if ("global".equalsIgnoreCase(item.getExpressionType()) || "table".equalsIgnoreCase(item.getExpressionType())) {
					StringBuilder tablesToBeSecured = new StringBuilder();
					for(String entity : entities ) {
						Integer tableToBeSecured = tableIdMap.getOrDefault(entity, null);
						if(tableToBeSecured != null  ) {
							tablesToBeSecured.append(tableToBeSecured+",");
						}
					}
					hiMetadataSecurity.setExpressionOn(tablesToBeSecured.length()>0 ? tablesToBeSecured.substring(0,tablesToBeSecured.length()-1):"");
				}
				else if ("column".equalsIgnoreCase(item.getExpressionType())) {
					StringBuilder columnsToBeSecured = new StringBuilder();
					for(String entity : entities ) {
						Integer columnToBeSecured = columnIdMap.getOrDefault(entity,null);
						if(columnToBeSecured != null) {
							columnsToBeSecured.append(columnToBeSecured+",");
						}
					}
					hiMetadataSecurity.setExpressionOn(columnsToBeSecured.length() > 0 ? columnsToBeSecured.substring(0,columnsToBeSecured.length()-1):"");
				}
                hiMetadataSecurity.setHiResourceMetadata(hiResourceMetadata);
                if (create && !StringUtils.isBlank(hiMetadataSecurity.getExpressionOn())) {
                		hiResourceServiceDB.addHIMetadataSecurity(hiMetadataSecurity);
                	
                } else {
                	if(!StringUtils.isBlank(hiMetadataSecurity.getExpressionOn())) {
                		hiResourceServiceDB.editHIMetadataSecurity(hiMetadataSecurity);
                	}
                	else {
                		hiResourceServiceDB.deleteSecurity(hiMetadataSecurity.getId());
                	}
                }
            }

            metaSecurity.stream().forEach(r -> {
                String expressionId = r.getExpressionId();
                SecurityExpression securityExpression = collect.get(expressionId);
                if (securityExpression == null) {
                    hiResourceServiceDB.deleteSecurity(r.getId());
                }
            });
        } else {

            metaSecurity.stream().forEach(r -> {
                hiResourceServiceDB.deleteSecurity(r.getId());

            });
        }
    }

	/**
     * Handles views associated with the provided metadata.
     * This method manages view records in the database, creating or updating them as needed.
     *
     * @param database             		 database metadata containing views.
     * @param hiResourceServiceDB  		 service for interacting with HIResource entities in the database.
     * @param hiResourceMetadata   		 metadata associated with the HIResource.
     * @param metadataDatabases    		 metadata databases associated with the views.
     * @param renamedView                A map containing renamed views.
     * @param mappedView                 A map containing existing views mapped by ID.
     */
    private void handleViews(Database database, HIMetadataResourceServiceDB hiResourceServiceDB, HIResourceMetadata hiResourceMetadata, MetadataDatabases metadataDatabases, Map<String, String> renamedView, Map<String, HIMetadataView> mappedView) {
        Views views = database.getViews();
        if (views != null) {
            List<View> viewList = views.getViewList();
            List<HIMetadataView> viewsDb = new ArrayList<>();
            for (View item : viewList) {
                HIMetadataView metadataView = mappedView.get(item.getId());
                Boolean create = false;
                String key = item.getId();
                if (metadataView == null) {
                    metadataView = new HIMetadataView();
                    create = true;
                    key = item.getId()+"_"+database.getId();
                }
                String aliasChanged = renamedView.get(item.getId());
                Integer viewId = tableIdMap.getOrDefault(key,null);
                metadataView.setViewId(""+viewId);
                aliasChanged = aliasChanged != null ? aliasChanged : item.getAlias();
                metadataView.setViewAlias(aliasChanged);
                metadataView.setHasStoredProcedure(item.getHasStoredProcedure());
                View.Query query = item.getQuery();
                metadataView.setViewQuery(query.getQuery());
                metadataView.setViewName(item.getName());
                metadataView.setViewType(query.getType());
                metadataView.setHiResourceMetadata(hiResourceMetadata);
                metadataView.setHiMetadataDatabases(metadataDatabases);
                
                if (create) 
                    hiResourceServiceDB.addHIMetadataView(metadataView);
                	
                else
                    hiResourceServiceDB.editHIMetadataView(metadataView);
                viewsDb.add(metadataView);
            }
        }
    }
    
   
    /**
     * Retrieves metadata from the database based on the provided location and metadata file name.
     *
     * @param location           		 location of the metadata file.
     * @param metadataFileName   		 name of the metadata file.
     * @return The metadata retrieved from the database.
     */
    public static Metadata getMetadata(String location, String metadataFileName) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + metadataFileName);
        if (metadataResource == null) {
            throw new EfwServiceException("The metadata file requested doesn't exist.");
        }

        Metadata databaseMetadata = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());

        return databaseMetadata;
    }
    /**
     * Saves the HIResource associated with the metadata to the database.
     * This method creates or updates records for HIResource entities in the database.
     *
     * @param dir                   		 directory where the metadata file is located.
     * @param fileNameWithExtension 		 name of the metadata file with its extension.
     * @param metadata              		 metadata associated with the HIResource.
     * @param mode                  		 mode indicating whether the metadata is being created or edited.
     * @return The saved HIResource.
     */
    public HIResource saveHIResource(String dir, String fileNameWithExtension, Metadata metadata, String mode) {
        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        ResourceTypeServiceDB resourceTypeServiceDB = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);
        HIResource resourceByUrl = serviceDB.getResourceByUrl(dir);
        if (resourceByUrl == null) {
            throw new EfwServiceException("The " + dir + " location does not exists");
        }
        String mdFilePath = dir + "/" + fileNameWithExtension;
        HIResource hiResource = serviceDB.getResourceByUrl(mdFilePath,false);
        String fileName = metadata.getFileName();
        // fileName = DBProcessor.checkAndReplaceSpecialChars(fileName).trim();
        if ((mode.equals("create") || mode.equals("saveas")) && hiResource != null) {
        	/**
        	 * Handling this  at common location ( which is common for all resources ) 
        	 */
//            mdFilePath = dir + "/" + fileNameWithExtension.replace("." + JsonUtils.getMetadataExtension(), "_" + String.valueOf(System.currentTimeMillis()).substring(11, 13) + "." + JsonUtils.getMetadataExtension());
            hiResource = null;
        }

        if (hiResource == null) {
            hiResource = ResourceUtils.newHIResource(dir, false, fileName, DBProcessor.checkAndReplaceSpecialChars(fileName).trim(), mdFilePath, JsonUtils.getMetadataExtension());
            ResourceType metadataExtension = resourceTypeServiceDB.getResourceTypeByTypeAndExtension(METADATA, METADATA_EXTENSION);
            hiResource.setResourceType(metadataExtension);
            serviceDB.addHIResource(hiResource);
        } else {
            hiResource.setLastUpdatedTime(new Date());
            hiResource.setTitle(fileName);
            serviceDB.editHIResource(hiResource);
        }

        return hiResource;
    }
    /**
     * Saves the HIResourceMetadata associated with the metadata to the database.
     * This method creates or updates records for HIResourceMetadata entities in the database.
     *
     * @param metadata         			 metadata associated with the HIResource.
     * @param hiResource       			 HIResource associated with the metadata.
     * @return The saved HIResourceMetadata.
     */
    public HIResourceMetadata saveHIMetadata(Metadata metadata, HIResource hiResource) {
        HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResourceMetadata hiResourceMetadata = hiResourceServiceDB.giveHIResourceMetadataByResourceId(hiResource.getResourceId());
        Boolean create = hiResourceMetadata == null;
        if (create) {
            hiResourceMetadata = new HIResourceMetadata();
            Integer createdBy = Integer.valueOf(AuthenticationUtils.getUserId());
            hiResourceMetadata.setCreatedBy(createdBy);
        }
        hiResourceMetadata.setCached(metadata.getCached());
        hiResourceMetadata.setFileName(hiResource.getTitle());
        hiResourceMetadata.setDatabaseType(metadata.getDatabaseType());
        hiResourceMetadata.setType(metadata.getType());
        hiResourceMetadata.setConnectionType(metadata.getConnectionType());
        hiResourceMetadata.setHiResource(hiResource);
        if (create) {
            hiResourceServiceDB.addHIResourceMetadata(hiResourceMetadata);
        } else {
        	hiResourceMetadata.setLastUpdatedTime(new Date());
            hiResourceServiceDB.editHIResourceMetadata(hiResourceMetadata);
        }
        
        return hiResourceMetadata;
    }
    /**
     * Saves the HIMetadataConnections associated with the metadata to the database.
     * This method creates or updates records for HIMetadataConnections entities in the database.
     *
     * @param metadata      			 metadata associated with the HIResource.
     * @param hiResourceMetadata  		 HIResourceMetadata associated with the metadata.
     * @param con           			 connection details associated with the metadata.
     * @return The saved HIMetadataConnections.
     */
    public HIMetadataConnections saveHIMetaDataConnections(Metadata metadata, HIResourceMetadata hiResourceMetadata, ConnectionDetails con) {
        HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIMetadataConnections hiMetadataConnections = hiResourceServiceDB.getHIMetadataConnection(hiResourceMetadata.getId(),Integer.valueOf(con.getConnectionId()),con.getConnectionType());
        Boolean create = hiMetadataConnections == null;

        if (create) {
            hiMetadataConnections = new HIMetadataConnections();
        }
        hiMetadataConnections.setConnectionType(con.getConnectionType());
        hiMetadataConnections.setHiResourceMetadata(hiResourceMetadata);
        if (create) {
            hiResourceServiceDB.addHIMetadataConnections(hiMetadataConnections);
        } else {
            hiResourceServiceDB.editHIMetadataConnections(hiMetadataConnections);
        }
        return hiMetadataConnections;
    }
    /**
     * Saves the HIMetadataConnectionEfwd associated with the connection details to the database.
     * This method creates or updates records for HIMetadataConnectionEfwd entities in the database.
     *
     * @param connections       		 connection details associated with the metadata.
     * @param mdConnections     		 metadata connections associated with the connection.
     * @return The saved HIMetadataConnectionEfwd.
     */
    public HIMetadataConnectionEFWD saveHIMetadataConnectionEfwd(ConnectionDetails connections , HIMetadataConnections mdConnections) {
    	
		HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
		Integer dataSourceConnection = Integer.valueOf(connections.getConnectionId());
		EFWDConnectionService efwdConnectionService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
		HIEfwdConnection efwdConnection = efwdConnectionService.findConnectionByIDAndType(dataSourceConnection,connections.getConnectionType());
		HIMetadataConnectionEFWD metadataConnectionEfwd = hiResourceServiceDB.getHIMetadataConnectionEFWD(mdConnections.getId());
		Boolean create = metadataConnectionEfwd == null;
		if (create) {
			metadataConnectionEfwd = new HIMetadataConnectionEFWD();
		}
		metadataConnectionEfwd.setHiEfwdConnection(efwdConnection);
		metadataConnectionEfwd.setDialect(connections.getDialect());
		metadataConnectionEfwd.setDriverClass(connections.getDriverClass().getDriverClass());
		metadataConnectionEfwd.setDriverClassReference(connections.getDriverClass().getReference());
		metadataConnectionEfwd.setHiMetadataConnections(mdConnections);
		if (create) {
			hiResourceServiceDB.saveHIMetadataConnectionEfwd(metadataConnectionEfwd);
		} else {
			hiResourceServiceDB.editHIMetadataConnectionEfwd(metadataConnectionEfwd);
		}
		return metadataConnectionEfwd;

	}
    
    /**
     * Saves the HIMetadataConnectionGlobal associated with the connection details to the database.
     * This method creates or updates records for HIMetadataConnectionGlobal entities in the database.
     *
     * @param connections       		 connection details associated with the metadata.
     * @param mdConnections     		 metadata connections associated with the connection.
     * @return The saved HIMetadataConnectionGlobal.
     */
    public HIMetadataConnectionGlobal saveHIMetadataConnectionGlobal(ConnectionDetails connections, HIMetadataConnections mdConnections) {
        GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
        HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        Integer dataSourceConnection = Integer.valueOf(connections.getConnectionId());
		GlobalConnections globalConnectionById = globalConnectionService.findGlobalConnectionById(dataSourceConnection);
		if (null == globalConnectionById) {
			throw new EfwServiceException("GlobalConnections Not Found");
		}
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = hiResourceServiceDB
				.getHIMetadataConnectionGlobal(mdConnections.getId(), connections.getConnectionId());
		Boolean create = hiMetadataConnectionGlobal == null;
		if (create) {
			hiMetadataConnectionGlobal = new HIMetadataConnectionGlobal();
		}
		hiMetadataConnectionGlobal.setGlobalConnections(globalConnectionById);
		hiMetadataConnectionGlobal.setDialect(connections.getDialect());
		DriverClass driverClass = connections.getDriverClass();
		hiMetadataConnectionGlobal.setDriverClass(driverClass.getDriverClass());
		hiMetadataConnectionGlobal.setDriverClassReference(driverClass.getReference());
		hiMetadataConnectionGlobal.setHiMetadataConnections(mdConnections);
		if (create) {
			hiResourceServiceDB.saveHIMetadataConnectionGlobal(hiMetadataConnectionGlobal);
		} else {
			hiResourceServiceDB.editHIMetadataConnectionGlobal(hiMetadataConnectionGlobal);
		}
		return hiMetadataConnectionGlobal;
		
    }
    /**
     * Saves the HIMetadataDatabases associated with the metadata to the database.
     * This method creates or updates records for HIMetadataDatabases entities in the database.
     *
     * @param database            		 database metadata associated with the HIMetadataDatabases.
     * @param hiResourceMetadata  		 HIResourceMetadata associated with the metadata.
     * @param metadataConnections 		 metadata connections associated with the database.
     * @return The saved HIMetadataDatabases.
     */
    public MetadataDatabases saveHIMetadataDatabases(Database database, HIResourceMetadata hiResourceMetadata, HIMetadataConnections metadataConnections) {
        HIMetadataResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        MetadataDatabases metadataDatabase = null;
        if(StringUtils.isNumeric(database.getId()) && !this.mode.equalsIgnoreCase("saveas")){
        	metadataDatabase = hiResourceServiceDB.getHIMetadataDatabaseById(Integer.valueOf(database.getId()));
        }
        else {
        	metadataDatabase = hiResourceServiceDB.getHIMetadataDatabases(Integer.valueOf(hiResourceMetadata.getId()), String.valueOf(metadataConnections.getId()));
        }
        Boolean create = false;
        if (metadataDatabase == null) {
            metadataDatabase = new MetadataDatabases();
            create = true;
        }
        if (null != database.getCatalog()) {
            metadataDatabase.setCatalog(database.getCatalog());
        }

        metadataDatabase.setName(database.getName());
        if (null != database.getSchema()) {
            metadataDatabase.setSchema(database.getSchema());
        }


        metadataDatabase.setHiResourceMetadata(hiResourceMetadata);
        metadataDatabase.setMetadataConnections(metadataConnections);
        if (create) {
            hiResourceServiceDB.addHIMetadataDatabases(metadataDatabase);
        } else {
            hiResourceServiceDB.editHIMetadataDatabases(metadataDatabase);
        }
        return metadataDatabase;
    }
    /**
     * Retrieves a list of cached {@code MetadataDumpDTO} from the database.
     * @return A list of cached metadata.
     */
	public List<MetadataDumpDTO> getCachedMetadata() {
		return ApplicationContextAccessor
				.getBean(HIMetadataResourceServiceDB.class)
				.getDumpedMetadataList();
	}
	
	
	public void setFormJson(JsonObject fromJson) {
		this.newFormJson = fromJson;
	}
	public void setContext(AuditContext context) {
		this.auditContext = context;
	}
	/**
	 * Retrieves the metadata cache status and updates the last accessed time for the specified HIResource.
	 *
	 * @param metadataResource 			 HIResource provides resourceId.
	 * @return  MetadataCacheStatus containing cache status and last accessed time information.
	 */
	public static MetadataCacheStatus getMetadataCacheStatusAndUpdateTime(HIResource  metadataResource) {
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        MetadataCacheStatus result=metadataService.getMetadataCacheStatusAndUpdateTime(metadataResource.getResourceId());
        return result;
    }
	/**
	 * Retrieves the connection reference and driver information for the specified metadata ID.
	 *
	 * @param metadataId 			 ID of the metadata for which to retrieve connection reference and driver information.
	 * @return MetadataDriverReferences containing connection reference and driver information.
	 */
    public static MetadataDriverReferences getConnectionRefAndDriver(Integer metadaid) {
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        MetadataDriverReferences result=metadataService.getConnectionRefAndDriver(metadaid);
        return result;
    }

}
