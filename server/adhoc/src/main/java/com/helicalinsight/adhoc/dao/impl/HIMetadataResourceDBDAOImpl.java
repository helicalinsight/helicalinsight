package com.helicalinsight.adhoc.dao.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.MetadataCacheStatus;
import com.helicalinsight.adhoc.MetadataDriverReferences;
import com.helicalinsight.adhoc.MetadataProperties;
import com.helicalinsight.adhoc.dao.HIMetadataResourceDAO;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataRetrievalException;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import com.helicalinsight.adhoc.utils.MapperUtils;
import com.helicalinsight.admin.dao.OrganizationDao;
import com.helicalinsight.admin.dao.RoleDao;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GroovyManagedJdbcHandler;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcedb.MetadataDumpDTO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class HIMetadataResourceDBDAOImpl implements HIMetadataResourceDAO {

    private static final Logger logger = LoggerFactory.getLogger(HIMetadataResourceDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RoleDao roleDao;
    
    @Autowired
    private MapperUtils mapper;


    @Override
    public Integer addHIResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to add the hi resource metadata " + hiResourceMetadata);
            }
            sessionFactory.getCurrentSession().save(hiResourceMetadata);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return 1;
    }

    @Override
    public Integer editHIResourceMetadata(HIResourceMetadata hiResourceMetadata) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to update the hi resource metadata " + hiResourceMetadata);
            }
            sessionFactory.getCurrentSession().update(hiResourceMetadata);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return 1;
    }

    @Override
    public void deleteHIResourceMetadata(Integer id) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to delete the hi resource metadata " + id);
            }
            sessionFactory.getCurrentSession().delete(id);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }


    @Override
    public HIResourceMetadata giveHIResourceMetadataByResourceId(Integer resourceId) {
        HIResourceMetadata metadataPojo;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIResourceMetadata metadata where metadata.hiResource.resourceId=:id");
            query.setParameter("id", resourceId);
            // query.setCacheable(true);
            return (HIResourceMetadata) query.uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }


    @Override
    public Metadata getHIResourceMetadataByResourceId(Integer resourceId) {
        HIResourceMetadata metadataPojo;
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("FROM HIResourceMetadata metadata where metadata.hiResource.resourceId=:id");
        query.setParameter("id", resourceId);
        //query.setCacheable(true);
        metadataPojo = (HIResourceMetadata) query.uniqueResult();
        return preparePojoToMetadata(metadataPojo);

    }


    @Override
    public MetadataDatabases getHIMetadataDatabases(Integer metadataId, String connectionId) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM MetadataDatabases mdb where mdb.hiResourceMetadata.id=:metadataId and mdb.metadataConnections.id =:connectionId");
            query.setParameter("metadataId", metadataId);
            query.setParameter("connectionId", Integer.valueOf(connectionId));
            return (MetadataDatabases) query.uniqueResult();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }
    

    @Override
    public void editHIMetadataDatabases(MetadataDatabases metadataDatabase) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the metadataDatabase " + metadataDatabase);
            }
            sessionFactory.getCurrentSession().update(metadataDatabase);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void editHIMetadataTables(HIMetadataTables hiMetadataTables) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataTables " + hiMetadataTables);
            }
            sessionFactory.getCurrentSession().update(hiMetadataTables);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void editHIMetadataColumns(HIMetadataColumns hiMetadataColumns) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataColumns " + hiMetadataColumns);
            }
            sessionFactory.getCurrentSession().update(hiMetadataColumns);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    public List<HIMetadataRelationships> getHIMetadataRelationships(Integer metadataId , Integer databaseId) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataRelationships mdb where mdb.hiResourceMetadata.id=:metadataId and mdb.hiMetadataDatabases.id =:databaseId and mdb.external = :falseValue");
            query.setParameter("metadataId", metadataId);
            query.setParameter("databaseId", databaseId);
            query.setParameter("falseValue", false);
            //  query.setCacheable(true);
            return (List<HIMetadataRelationships>) query.list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    public List<HIMetadataRelationships> getHIMetadataExternalRelationships(Integer metadataId) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataRelationships mdb where mdb.hiResourceMetadata.id=:metadataId and mdb.external =:trueValue");
            query.setParameter("metadataId", metadataId);
            query.setParameter("trueValue", true);
            //  query.setCacheable(true);
            return (List<HIMetadataRelationships>) query.list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    private Metadata preparePojoToMetadata(HIResourceMetadata metadataPojo) {
        Metadata metadata = ApplicationContextAccessor.getBean(Metadata.class);
        metadata.setMetadataId("" + metadataPojo.getId());
        metadata.setType(metadataPojo.getType());
        metadata.setVersion("5.0");
        metadata.setDatabaseType(metadataPojo.getDatabaseType());
        metadata.setConnectionType(metadataPojo.getConnectionType());
        metadata.setCached(metadataPojo.getCached());
        List<HIMetadataConnections> hiMetadataConnections = metadataPojo.getHiMetadataConnections();
        List<ConnectionDetails> metadataConnections = new ArrayList<>();
        List<Database> databaseList = new ArrayList<>();
        for (HIMetadataConnections connections : hiMetadataConnections) {
            String connectionType = connections.getConnectionType();
				if (connections.getMetadataConnectionEfwd() != null
						&& !connections.getMetadataConnectionEfwd().isEmpty()) {
					List<HIMetadataConnectionEFWD> plainConnections = connections.getMetadataConnectionEfwd().stream()
							.distinct().collect(Collectors.toList());
					for (HIMetadataConnectionEFWD firstConnection : plainConnections) {
						ConnectionDetails newConn = ApplicationContextAccessor.getBean(ConnectionDetails.class);
						HIResource parent = firstConnection.getHiEfwdConnection().getHiResourceEFWD()
								.getParentResource();
						newConn.setConnectionType(connectionType);
						Database database = ApplicationContextAccessor.getBean(Database.class);
						if (GlobalJdbcTypeUtils.isManagedGroovyDataSource(connectionType)) {
							GroovyManagedJdbcHandler handler = new GroovyManagedJdbcHandler();
							ObjectNode switchedConnection = handler
									.readDS("" + firstConnection.getHiEfwdConnection().getId(), connectionType);
							String driverClassName = switchedConnection.get("driverClassName").asText();
							String reference = JsonUtils.functionsReference(driverClassName);
							DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
							driverClass.setDriverClass(driverClassName);
							driverClass.setReference(reference);
							newConn.setDriverClass(driverClass);
							newConn.setDialect(MetadataUtils.dialectOfDatabase(driverClassName));
							if (switchedConnection.has("database"))
								database.setName(switchedConnection.get("database").asText());
							if (switchedConnection.has("catalog"))
								database.setCatalog(switchedConnection.get("catalog").asText());
							if (switchedConnection.has("schema"))
								database.setSchema(switchedConnection.get("schema").asText());
						} else {
							DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
							driverClass.setDriverClass(firstConnection.getDriverClass());
							driverClass.setReference(firstConnection.getDriverClassReference());
							newConn.setDriverClass(driverClass);
							newConn.setDialect(firstConnection.getDialect());
						}
						if (parent != null) {
							newConn.setDirectory(parent.getResourceURL());
						}
						Integer efwdId = firstConnection.getHiEfwdConnection().getId();
						newConn.setConnectionId("" + efwdId);
						metadataConnections.add(newConn);

						Integer dbId = null;
						MetadataDatabases databases = getHIMetadataDatabases(metadataPojo.getId(),
								String.valueOf(firstConnection.getHiMetadataConnections().getId()));
						dbId = databases.getId();
						database.setId("" + databases.getId());
						if (database.getCatalog() == null)
							database.setCatalog(databases.getCatalog());
						if (database.getName() == null)
							database.setName(databases.getName());
						if (database.getSchema() == null)
							database.setSchema(databases.getSchema());
						List<HIMetadataTables> metadataTablesList = getMetadataTablesList(metadataPojo.getId(), dbId);
						Map<String, Table> idForView = new HashMap<>();
						if (metadataTablesList != null) {
							handleGetTable(metadataPojo, database, metadataTablesList, idForView);
							handleGetView(metadataPojo, database, dbId, idForView);
						}
						handleGetRelationships(metadataPojo, database);
						databaseList.add(database);
					}
				} else {

					List<HIMetadataConnectionGlobal> metadataGlobalConnList = connections.getMetadataGlobalConnList();
					for (HIMetadataConnectionGlobal firstConnection : metadataGlobalConnList) {
						ConnectionDetails newConn = ApplicationContextAccessor.getBean(ConnectionDetails.class);
						newConn.setConnectionType(connectionType);
						DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
						driverClass.setDriverClass(firstConnection.getDriverClass());
						driverClass.setReference(firstConnection.getDriverClassReference());
						newConn.setDriverClass(driverClass);
						newConn.setDialect(firstConnection.getDialect());
						newConn.setSubType(firstConnection.getGlobalConnections().getType());
						newConn.setVendorName(firstConnection.getGlobalConnections().getVendor());
						Integer globalId = firstConnection.getGlobalConnections().getGlobalId();
						newConn.setConnectionId("" + globalId);
						metadataConnections.add(newConn);
						Database database = ApplicationContextAccessor.getBean(Database.class);
						Integer dbId = null;
						MetadataDatabases databases = getHIMetadataDatabases(metadataPojo.getId(),
								String.valueOf(firstConnection.getHiMetadataConnections().getId()));
						// MetadataDatabases databases =
						// firstConnection.getMetadataDatabases().stream().filter(r ->
						// r.gethIMetadataConnectionGlobal().getId() == globalId).findFirst().get();
						dbId = databases.getId();
						database.setId("" + databases.getId());
						database.setCatalog(databases.getCatalog());
						database.setName(databases.getName());
						database.setSchema(databases.getSchema());

						List<HIMetadataTables> metadataTablesList = getMetadataTablesList(metadataPojo.getId(), dbId);
						Map<String, Table> idForView = new HashMap<>();
						if (metadataTablesList != null) {
							handleGetTable(metadataPojo, database, metadataTablesList, idForView);
							handleGetView(metadataPojo, database, dbId, idForView);
						}

						handleGetRelationships(metadataPojo, database);
						databaseList.add(database);
					}

				}
        }
        handleGetSecurity(metadataPojo, metadata);
        metadata.setConnectionDetails(metadataConnections.get(0));
        metadata.setDatabase(databaseList.get(0));
        if (metadataConnections.size() > 1) {
            Connections connBeans = ApplicationContextAccessor.getBean(Connections.class);
            List<ConnectionDatabase> connectionDatabases = new ArrayList<>();
            for (int counter = 1; counter < databaseList.size(); counter++) {
                ConnectionDatabase connectionDatabase = ApplicationContextAccessor.getBean(ConnectionDatabase.class);
                Database database = databaseList.get(counter);
                ConnectionDetails connectionDetails = metadataConnections.get(counter);
                connectionDatabase.setDatabase(database);
                connectionDatabase.setConnectionDetails(connectionDetails);
                connectionDatabases.add(connectionDatabase);
            }
            connBeans.setConnectionDatabase(connectionDatabases);
            metadata.setConnections(connBeans);
           ExternalRelationships external =  handleGetExternalRelationships(metadataPojo);
           metadata.setExternalRelationships(external);
        }


        metadata.setFileName(metadataPojo.getFileName());
        return metadata;
    }
    
    private void handleGetTable(HIResourceMetadata metadataPojo, Database database, List<HIMetadataTables> metadataTablesList, Map<String, Table> idForView) {
        Tables tables = ApplicationContextAccessor.getBean(Tables.class);
        List<Table> tableList = new ArrayList<>();
        List<HIMetadataColumns> columnsList = getMetadataColumnsList(metadataPojo.getId(),Integer.valueOf(database.getId()));
        Map<Integer, List<HIMetadataColumns>> columnGrouped =
                columnsList.stream().collect(Collectors.groupingBy(w -> w.getHiMetadataTables().getId()));
        for (HIMetadataTables dbTables : metadataTablesList) {
            Table table = ApplicationContextAccessor.getBean(Table.class);
            table.setName(dbTables.getTableName());
            table.setAliasName(dbTables.getTableAliasName());
            table.setId(""+dbTables.getId());
            table.setDbId(""+dbTables.getHiMetadataDatabases().getId());
            table.setType(dbTables.getView() ? "view" : null);
            table.setOriginalName(dbTables.getOriginalName());
            List<HIMetadataColumns> hiMetadataColumns = columnGrouped.get(dbTables.getId());

            if (hiMetadataColumns != null) {
                Columns columns = ApplicationContextAccessor.getBean(Columns.class);
                List<Column> columnList = new ArrayList<>();
                for (HIMetadataColumns dbCol : hiMetadataColumns) {
                    Column column = ApplicationContextAccessor.getBean(Column.class);
                    column.setAliasName(dbCol.getColumnAliasName());
                    column.setDefaultFunction(dbCol.getDefaultFunction());
                    column.setId(""+dbCol.getId());
                    column.setType(dbCol.getColumn_type());
                    column.setOriginalName(dbCol.getColumnName());
                    column.setName(dbCol.getColumnName());
                    column.setOriginalName(dbCol.getOriginalName());
                    columnList.add(column);
                }
                columns.setColumn(columnList);
                table.setColumns(columns);
            }
            tableList.add(table);
            idForView.put(table.getId(), table);
        }
        tables.setTableList(tableList);
        database.setTables(tables);
    }

    private void handleGetView(HIResourceMetadata metadataPojo, Database database, Integer dbId, Map<String, Table> idForView) {
        Views newViews = ApplicationContextAccessor.getBean(Views.class);
        List<HIMetadataView> metadataViewList = getMetadataViewList(metadataPojo.getId(), dbId);
        List<View> viewList = new ArrayList<>();

        for (HIMetadataView viewItem : metadataViewList) {
            View item = ApplicationContextAccessor.getBean(View.class);
            item.setId(viewItem.getViewId());
            item.setName(viewItem.getViewName());
            item.setAlias(viewItem.getViewAlias());
            item.setHasStoredProcedure(viewItem.getHasStoredProcedure());
            View.Query qBean = ApplicationContextAccessor.getBean(View.Query.class);
            qBean.setType(viewItem.getViewType());
            qBean.setQuery(viewItem.getViewQuery());
            Table table = idForView.get(viewItem.getViewId());
            item.setTable(table);
            item.setQuery(qBean);
            viewList.add(item);

        }
        newViews.setViewList(viewList);
        database.setViews(newViews);
    }

    private void handleGetSecurity(HIResourceMetadata metadataPojo, Metadata metadata) {
        List<HIMetadataSecurity> metaSecurity = getMetaSecurity(metadataPojo.getId());
        MetadataSecurity metadataSecurity = ApplicationContextAccessor.getBean(MetadataSecurity.class);
        List<SecurityExpression> securityExpressions = new ArrayList<>();
        for (HIMetadataSecurity item : metaSecurity) {
            SecurityExpression securityExpression = ApplicationContextAccessor.getBean(SecurityExpression.class);
            securityExpression.setExpressionName(item.getExpressionName());
            securityExpression.setExpressionType(item.getExpressionType());
            securityExpression.setType(item.getType());
            securityExpression.setCondition(item.getExpressionCondition());
            securityExpression.setOn(item.getExpressionOn());
            securityExpression.setId(item.getExpressionId());
            securityExpression.setAccessType(item.getAccessType());
            securityExpression.setFilter(item.getExpressionFilter());
            securityExpressions.add(securityExpression);

        }
        metadataSecurity.setExpressions(securityExpressions);
        metadata.setMetadataSecurity(metadataSecurity);
    }
    
    private ExternalRelationships  handleGetExternalRelationships(HIResourceMetadata metadataPojo) {
    	List<HIMetadataRelationships> external =  getHIMetadataExternalRelationships(metadataPojo.getId());
    	ExternalRelationships externalRelationships = ApplicationContextAccessor.getBean(ExternalRelationships.class);
    	if(external != null) {
    		List<Relationship> listOfRelations = new ArrayList<>();
    		for(HIMetadataRelationships item : external) {
    			List<Join> joinList = new ArrayList<>();
    			Join join = ApplicationContextAccessor.getBean(Join.class);
    			join.setId(""+item.getId());
    			join.setOperator(item.getOperator());
    			join.setType(item.getJoinType());
    			LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
    			RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
    			
    			HIMetadataColumns leftMetadataColumns = item.getLeftMetadataColumns();
    			HIMetadataColumns rightMetadataColumns = item.getRightMetadataColumns();
    			
    			if(leftMetadataColumns == null || rightMetadataColumns == null) {
    				 throw new MetadataRetrievalException("The joins columns are either empty or malformed");
    			}
    			HIMetadataTables hiMetadataTables = leftMetadataColumns.getHiMetadataTables();
    			leftTable.setTable(hiMetadataTables.getTableName());
                leftTable.setColumn(leftMetadataColumns.getColumnName());
                leftTable.setColumnId(""+leftMetadataColumns.getId());
                leftTable.setId(""+hiMetadataTables.getId());
                leftTable.setTableId(""+hiMetadataTables.getId());
                leftTable.setDbId(""+hiMetadataTables.getHiMetadataDatabases().getId());
                HIMetadataTables hiMetadataRightTables = rightMetadataColumns.getHiMetadataTables();
                rightTable.setTable(hiMetadataRightTables.getTableName());
                rightTable.setColumn(rightMetadataColumns.getColumnName());
                rightTable.setColumnId(""+rightMetadataColumns.getId());
                rightTable.setId(""+hiMetadataRightTables.getId());
                rightTable.setDbId(""+hiMetadataRightTables.getHiMetadataDatabases().getId());
                rightTable.setTableId(""+hiMetadataRightTables.getId());
                Relationship relationship = ApplicationContextAccessor.getBean(Relationship.class);
                relationship.setTable(hiMetadataTables.getTableName());
                relationship.setReferenceTable(hiMetadataRightTables.getTableName());
                join.setLeftTable(leftTable);
                join.setRightTable(rightTable);
                if ( item.getJoinsPositions() != null) {
                	join.setPosition(item.getJoinsPositions().getPosition());
                }
                joinList.add(join);
                relationship.setJoin(joinList);
                listOfRelations.add(relationship);
    		}
    		externalRelationships.setListOfRelations(listOfRelations);
    	}
    	
    	return externalRelationships;
    }
   


    private void handleGetRelationships(HIResourceMetadata metadataPojo, Database database) {
        List<HIMetadataRelationships> hiMetadataRelationships = getHIMetadataRelationships(metadataPojo.getId(),Integer.valueOf(database.getId()));
        if (hiMetadataRelationships != null) {
            Relationships relationships = ApplicationContextAccessor.getBean(Relationships.class);
            List<Relationship> listOfRelations = new ArrayList<>();


            for (HIMetadataRelationships item : hiMetadataRelationships) {
                List<Join> joinList = new ArrayList<>();
                Join join = ApplicationContextAccessor.getBean(Join.class);
                join.setId(""+item.getId());
                join.setOperator(item.getOperator());
                join.setType(item.getJoinType());
                LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
                RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
                HIMetadataColumns leftMetadataColumns = item.getLeftMetadataColumns();
                HIMetadataColumns rightMetadataColumns = item.getRightMetadataColumns();
                if (leftMetadataColumns == null || rightMetadataColumns == null) {
                    throw new MetadataRetrievalException("The joins columns are either empty or malformed");
                }
                HIMetadataTables hiMetadataTables = leftMetadataColumns.getHiMetadataTables();
                leftTable.setTable(hiMetadataTables.getTableName());
                leftTable.setColumn(leftMetadataColumns.getColumnName());
                leftTable.setColumnId(""+leftMetadataColumns.getId());
                leftTable.setId(""+hiMetadataTables.getId());
                leftTable.setDbId(""+hiMetadataTables.getHiMetadataDatabases().getId());
                leftTable.setTableId(""+hiMetadataTables.getId());
                HIMetadataTables hiMetadataRightTables = rightMetadataColumns.getHiMetadataTables();
                rightTable.setTable(hiMetadataRightTables.getTableName());
                rightTable.setColumn(rightMetadataColumns.getColumnName());
                rightTable.setColumnId(""+rightMetadataColumns.getId());
                rightTable.setId(""+hiMetadataRightTables.getId());
                rightTable.setDbId(""+hiMetadataRightTables.getHiMetadataDatabases().getId());
                rightTable.setTableId(""+hiMetadataRightTables.getId());
                Relationship relationship = ApplicationContextAccessor.getBean(Relationship.class);
                relationship.setTable(hiMetadataTables.getTableName());
                relationship.setReferenceTable(hiMetadataRightTables.getTableName());
                join.setLeftTable(leftTable);
                join.setRightTable(rightTable);
                if ( item.getJoinsPositions() != null) {
                	join.setPosition(item.getJoinsPositions().getPosition());
                }
                joinList.add(join);
                relationship.setJoin(joinList);
                listOfRelations.add(relationship);
            }
            relationships.setListOfRelations(listOfRelations);

            database.setRelationships(relationships);
            

        }
    }

    @Override
    public List<HIMetadataTables> getMetadataTablesList(Integer id, Integer dbId) {
        List<HIMetadataTables> metadataPojo;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataTables mdb where mdb.hiResourceMetadata.id=:metadataId and mdb.hiMetadataDatabases.id=:dbId");
            query.setParameter("metadataId", id);
            query.setParameter("dbId", dbId);
            //query.setCacheable(true);
            return (List<HIMetadataTables>) query.list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public List<HIMetadataView> getMetadataViewList(Integer id, Integer dbId) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataView mdb where mdb.hiResourceMetadata.id=:metadataId and mdb.hiMetadataDatabases.id=:dbId");
            query.setParameter("metadataId", id);
            query.setParameter("dbId", dbId);
            //query.setCacheable(true);
            return (List<HIMetadataView>) query.list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public void addHIMetadataView(HIMetadataView mdview) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the HIMetadataView " + mdview);
            }
            sessionFactory.getCurrentSession().save(mdview);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void editHIMetadataView(HIMetadataView mdview) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the HIMetadataView " + mdview);
            }
            sessionFactory.getCurrentSession().update(mdview);
        } catch (Exception e) {
            logger.error("Exception", e);

        }

    }

    @Override
    public void deleteHIMetadataColumn(Integer columnId, Integer id) {
        Query relationshipQuery = sessionFactory.getCurrentSession().createQuery("update   HIMetadataRelationships  set leftMetadataColumns=null , rightMetadataColumns=null  where  leftMetadataColumns.id=:columnId  or rightMetadataColumns.id=:columnId");
        relationshipQuery.setParameter("columnId", id);
        relationshipQuery.executeUpdate();
        Query updateResourceQuery = sessionFactory.getCurrentSession().createQuery("delete HIMetadataColumns where  id=:columnId ");
        updateResourceQuery.setParameter("columnId", id);
        //updateResourceQuery.setParameter("id", id);
        updateResourceQuery.executeUpdate();
    }

    @Override
    public void deleteHIMetadataTable(Integer tableId, Integer dbId) {
        Query relationshipQuery = sessionFactory.getCurrentSession().createQuery("delete HIMetadataTables mdb where  id=:id and mdb.hiMetadataDatabases.id=:dbId");
        relationshipQuery.setParameter("id", tableId);
        relationshipQuery.setParameter("dbId", dbId);
        relationshipQuery.executeUpdate();
try {
    Query relationshipQueryView = sessionFactory.getCurrentSession().createQuery("delete HIMetadataView view where view.viewId=:id and view.hiMetadataDatabases.id=:dbId");
    relationshipQueryView.setParameter("id", ""+tableId);
    relationshipQueryView.setParameter("dbId", dbId);
    relationshipQueryView.executeUpdate();
}catch (Exception e){

}

    }

    @Override
    public void deleteAllRelationships(Integer metadataId, Integer dbId) {
        Query relationshipQuery = sessionFactory.getCurrentSession().createQuery("delete HIMetadataRelationships  hrsps where  hrsps.hiResourceMetadata.id=:id and  hrsps.hiMetadataDatabases.id=:dbId and hrsps.external =:falseValue");
        relationshipQuery.setParameter("id", metadataId);
        relationshipQuery.setParameter("dbId", dbId);
        relationshipQuery.setParameter("falseValue", false);
        relationshipQuery.executeUpdate();
    }

    @Override
    public List<HIMetadataColumns> getMetadataColumnsList(Integer metadataId,Integer dbId) {
        List<HIMetadataColumns> metadataPojo;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataColumns mdb where mdb.hiResourceMetadata.id=:metadataId");
            query.setParameter("metadataId", metadataId);
            //query.setCacheable(true);
            List<HIMetadataColumns> dbColumns = (List<HIMetadataColumns>) query.list();
            List<HIMetadataColumns> actual = new ArrayList<>();
            for(HIMetadataColumns dbColumn : dbColumns) {
            	if(dbColumn.getHiMetadataTables().getHiMetadataDatabases().getId().equals(dbId)) {
            		actual.add(dbColumn);
            	}
            }
            return actual;

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public List<HIMetadataSecurity> getMetaSecurity(Integer id) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataSecurity mdb where mdb.hiResourceMetadata.id=:metadataId");
            query.setParameter("metadataId", id);
            //query.setCacheable(true);
            return (List<HIMetadataSecurity>) query.list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public void addHIMetadataSecurity(HIMetadataSecurity metadataSecurity) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the HIMetadataView " + metadataSecurity);
            }
            sessionFactory.getCurrentSession().save(metadataSecurity);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void editHIMetadataSecurity(HIMetadataSecurity metadataSecurity) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the metadataSecurity " + metadataSecurity);
            }
            sessionFactory.getCurrentSession().update(metadataSecurity);
        } catch (Exception e) {
            logger.error("Exception", e);

        }
    }

    @Override
    public void deleteSecurity(Integer id) {
        Query relationshipQuery = sessionFactory.getCurrentSession().createQuery("delete HIMetadataSecurity where id=:id");
        relationshipQuery.setParameter("id", id);
        relationshipQuery.executeUpdate();
    }

    @Override
    public void addCube(HIMetadataCube cube) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the HIMetadataView " + cube);
            }
            sessionFactory.getCurrentSession().save(cube);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void add(Object obj) {
        try {

            sessionFactory.getCurrentSession().save(obj);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    @Override
    public void addHIMetadataTables(HIMetadataTables hiMetadataTables) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataTables " + hiMetadataTables);
            }
            sessionFactory.getCurrentSession().save(hiMetadataTables);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

    }


    @Override
    public void addHIMetadataRelationships(HIMetadataRelationships hiMetadataRelationships) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataRelationships " + hiMetadataRelationships);
            }
            sessionFactory.getCurrentSession().saveOrUpdate(hiMetadataRelationships);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
        }
    }
    


    @Override
    public Integer addHIMetadataConnections(HIMetadataConnections hiMetadataConnections) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataConnections " + hiMetadataConnections);
            }
            sessionFactory.getCurrentSession().save(hiMetadataConnections);
        } catch (Exception e) {
            logger.error("Exception", e);
            return null;
        }
        return hiMetadataConnections.getId();
    }

    @Override
    public Integer editHIMetadataConnections(HIMetadataConnections hiMetadataConnections) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataConnections " + hiMetadataConnections);
            }
            sessionFactory.getCurrentSession().update(hiMetadataConnections);
        } catch (Exception e) {
            logger.error("Exception", e);
            return null;
        }
        return hiMetadataConnections.getId();
    }

    @Override
    public List<HIMetadataConnections> getHIMetadataConnections(Integer metadataId) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataConnections mdb where mdb.hiResourceMetadata.id=:metadataId");
            query.setParameter("metadataId", metadataId);
            // query.setCacheable(true);
            return query.list();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public Integer editHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataConnections " + hiMetadataConnectionGlobal);
            }
            sessionFactory.getCurrentSession().update(hiMetadataConnectionGlobal);
        } catch (Exception e) {
            logger.error("Exception", e);
            return null;
        }
        return hiMetadataConnectionGlobal.getId();
    }

    @Override
    public HIMetadataConnectionGlobal getHIMetadataConnectionGlobal(Integer connectionId, String xmlGlobalConnectionId) {
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataConnectionGlobal mdb where mdb.hiMetadataConnections.id=:connectionId");
            query.setParameter("connectionId", connectionId);
            //query.setParameter("globalId", Integer.valueOf(xmlGlobalConnectionId));
            //query.setCacheable(true);
            return (HIMetadataConnectionGlobal) query.uniqueResult();

        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
    }

    @Override
    public Integer addHIMetadataColumns(HIMetadataColumns hiMetadataColumns) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataColumns " + hiMetadataColumns);
            }
            sessionFactory.getCurrentSession().save(hiMetadataColumns);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
        }
        return hiMetadataColumns.getId();
    }

    @Override
    public Integer saveHIMetadataConnectionGlobal(HIMetadataConnectionGlobal hiMetadataConnectionGlobal) {
        try {
            sessionFactory.getCurrentSession().save(hiMetadataConnectionGlobal);
            return hiMetadataConnectionGlobal.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hiMetadataConnectionGlobal.getId();
    }

    @Override
    public Integer addHIMetadataDatabases(MetadataDatabases hiMetadataDatabases) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to save the hiMetadataDatabases " + hiMetadataDatabases);
            }
            sessionFactory.getCurrentSession().save(hiMetadataDatabases);
        } catch (Exception e) {
            logger.error("Exception", e);
            e.printStackTrace();
        }
        return hiMetadataDatabases.getId();
    }

	@Override
	public HIMetadataConnectionEFWD getHIMetadataConnectionEFWD(Integer efwdConnectionId) {
		try {
			Session currentSession = sessionFactory.getCurrentSession();
			Query query = currentSession.createQuery(
					"FROM HIMetadataConnectionEFWD mdb where mdb.hiMetadataConnections.id=:efwdConnectionId");
			query.setParameter("efwdConnectionId", efwdConnectionId);
			return (HIMetadataConnectionEFWD) query.uniqueResult();

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return null;
	}

	@Override
	public Integer saveHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd) {
		try {
			sessionFactory.getCurrentSession().save(metadataConnectionEfwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadataConnectionEfwd.getId();
	}

	@Override
	public Integer editHIMetadataConnectionEfwd(HIMetadataConnectionEFWD metadataConnectionEfwd) {
		try {

			sessionFactory.getCurrentSession().update(metadataConnectionEfwd);
		} catch (Exception e) {
			logger.error("Exception", e);
			return null;
		}
		return metadataConnectionEfwd.getId();
	}


	@Override
	public void deleteMetadataGlobalConnection(HIMetadataConnectionGlobal globalCon) {
		try {
			Session session = sessionFactory.getCurrentSession();
			session.delete(globalCon);
		}
		catch (Exception e) {
			logger.error("Exception",e);
		}
	}
	
	@Override
	public void deleteMetadataEfwdConnection(HIMetadataConnectionEFWD efwdCon) {
		try {
			Session session = sessionFactory.getCurrentSession();
			session.delete(efwdCon);
		}
		catch (Exception e) {
			logger.error("Exception",e);
		}
		
	}

	@Override
	public HIMetadataConnections getHIMetadataConnection(Integer metadataId , Integer dsId,String dsType) {
		try {
			Query query = sessionFactory.getCurrentSession().createQuery("FROM HIMetadataConnections where hiResourceMetadata.id =:metadataId and connectionType =:dsType");
			query.setParameter("metadataId", metadataId);
			query.setParameter("dsType", dsType);
			List<HIMetadataConnections> connections =  (List<HIMetadataConnections>) query.list();
			for(HIMetadataConnections connection  : connections) {
				List<HIMetadataConnectionEFWD> efwdConnections = connection.getMetadataConnectionEfwd();
				List<HIMetadataConnectionGlobal> globalConnections = connection.getMetadataGlobalConnList();
				if(efwdConnections != null && !efwdConnections.isEmpty()) {
					for(HIMetadataConnectionEFWD efwd : efwdConnections) {
						if(efwd.getHiEfwdConnection().getId().equals(dsId)) {
							return connection;
						}
					}
				}
				else if(globalConnections != null && !globalConnections.isEmpty()) {
					for(HIMetadataConnectionGlobal global : globalConnections) {
						if(global.getGlobalConnections().getId().equals(dsId)) {
							return connection;
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception",e);
		}
		return null;
	}

	@Override
	public List<MetadataDumpDTO> getDumpedMetadataList() {
		List<MetadataDumpDTO> hiMetadataList = new ArrayList<>();
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery("SELECT status FROM HIResourcePhaseStatus status"
					+ " JOIN status.hiResource resource "
					+ "JOIN resource.resourceType resType WHERE status.action like '%DUMP%' and  resType.name= :metadata");
			query.setParameter("metadata", "metadata");
			
			List<HIResourcePhaseStatus> cubePhaseDetails = query.list();
			for (HIResourcePhaseStatus status : cubePhaseDetails) {
				MetadataDumpDTO dto = new MetadataDumpDTO();
				dto.setStatus(status.getHiPhase().getStatus().equalsIgnoreCase("COMPLETED")?"SUCCESS":"FAILED");
				dto.setLastUpdatedTime(status.getLastUpdatedDate());
				dto.setId(Long.valueOf(status.getId()));
				dto.setName(status.getHiResource().getResourcePath());
				dto.setPath(status.getHiResource().getResourceURL());
				dto.setTitle(status.getHiResource().getTitle());
				dto.setDumpType(status.getAction());
				hiMetadataList.add(dto);
			}
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		return hiMetadataList;
	}

	@Override
	public void deleteAllExternalRelationships(Integer metadataId) {
		Query relationshipQuery = sessionFactory.getCurrentSession().createQuery(
				"delete HIMetadataRelationships  hrsps where  hrsps.hiResourceMetadata.id=:id  and hrsps.external =:trueValue");
		relationshipQuery.setParameter("id", metadataId);
		relationshipQuery.setParameter("trueValue", true);
		relationshipQuery.executeUpdate();
	}


	@Override
	public void setCache(Integer metadataId,boolean value) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("UPDATE  HIResourceMetadata set isCached=:value  where id = :id");
		query.setParameter("value", value);
		query.setParameter("id", metadataId);
		query.executeUpdate();
	}
	

	@Override
	public Integer removeMetadataConnection(String metadataId, String databaseId, String mode) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery(
					"FROM MetadataDatabases  where id =:databaseId and  hiResourceMetadata.id =:metadataId");
			query.setParameter("databaseId", Integer.valueOf(databaseId));
			query.setParameter("metadataId", Integer.valueOf(metadataId));
			MetadataDatabases db = (MetadataDatabases) query.uniqueResult();
			int connectionId = db.getMetadataConnections().getId();
			List<HIMetadataConnectionGlobal> globalList =  db.getMetadataConnections().getMetadataGlobalConnList();
			List<HIMetadataConnectionEFWD> efwdList = db.getMetadataConnections().getMetadataConnectionEfwd();
			Integer dsId = null;
			String hql = "";
			if(globalList != null && !globalList.isEmpty()) {
				hql = "DELETE HIMetadataConnectionGlobal where hiMetadataConnections.id =:id";
				dsId =  globalList.get(0).getGlobalConnections().getGlobalId();
			}
			else if(efwdList != null && !efwdList.isEmpty()) {
				
				hql = "DELETE HIMetadataConnectionEFWD where hiMetadataConnections.id =:id";
				dsId =  efwdList.get(0).getHiEfwdConnection().getId();
			}
			
			if ("cascade".equalsIgnoreCase(mode)) {
				session.delete(db.getMetadataConnections());
			}
			else {
				Query removeMdConnection = session.createQuery(hql);
				removeMdConnection.setParameter("id", connectionId);
				removeMdConnection.executeUpdate();
			}
			return dsId;
		} catch (Exception e) {
			logger.error("Exception occurred during removeMetadataConnection :: {}", e);
		}
		return null;
	}

	@Override
	public HIMetadataRelationships findJoinById(String joinId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			return (HIMetadataRelationships) session.get(HIMetadataRelationships.class, Integer.valueOf(joinId));
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching HIMetadataRelationships");
		}
		return null;
	}

	@Override
	public void deleteHIMetadataRelationship(List<Integer> joinsToDelete) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery("DELETE HIMetadataRelationships where id in (:joinsToDelete) ");
			query.setParameterList("joinsToDelete", joinsToDelete);
			query.executeUpdate();
		}
		catch (Exception e) {
			logger.error("Error occurred while removing HIMetadataRelationships");
		}
	}

	@Override
	public MetadataDatabases getHIMetadataDatabaseById(Integer dbId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			return  (MetadataDatabases) session.get(MetadataDatabases.class, dbId);
		}
		catch (Exception e) {
			logger.error("Error occurred while removing MetadataDatabases");
		}
		return null;
	}
	
	
	private List<Column> findColumnByTableId(Integer tableId) {
		List<Column> columns = new ArrayList<>();
		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createQuery("FROM HIMetadataColumns where hiMetadataTables.id = :tableId");
			query.setParameter("tableId", tableId);
			List<HIMetadataColumns> metadataCoumnsList = query.list();
			if (metadataCoumnsList != null && !metadataCoumnsList.isEmpty()) {
				for (HIMetadataColumns dbCol : metadataCoumnsList) {
					mapper.mapToColumnList(dbCol, columns);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occurred during fetching columns");
		}
		return columns;
	}

	@Override
	public Table findTableById(Integer id) {
		try {
			Session session = sessionFactory.getCurrentSession();
			HIMetadataTables table = (HIMetadataTables) session.get(HIMetadataTables.class, id);
			List<Column> columnList =  findColumnByTableId(id);
			Columns columns = ApplicationContextAccessor.getBean(Columns.class);
			columns.setColumn(columnList);
			Table mappedTable =  mapper.mapToTable(table);
			mappedTable.setColumns(columns);
			return mappedTable;
		}
		catch (Exception e) {
			logger.error("Error occurred during fetching Table By Id , root cause : {}",e.getMessage());
		}
		return null;
	}

	@Override
	public HIMetadataColumns findColumnById(Integer colId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			HIMetadataColumns dbCol = (HIMetadataColumns) session.get(HIMetadataColumns.class, colId);
			return dbCol;
		}
		catch (Exception e) {
			logger.error("Error occured during fetching column By Id , root case : {}",e.getMessage());
		}
		return null;
	}

	@Override
	public List<HIMetadataRelationships> getRelationshipListByMetadataIdAndDbId(Integer mdId, Integer dbId) {
		List<HIMetadataRelationships> list = new ArrayList<>();
		try {
			String hql = "FROM HIMetadataRelationships where hiResourceMetadata.id =:mdId and  hiMetadataDatabases.id =:dbId";
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			query.setParameter("mdId", mdId);
			query.setParameter("dbId", dbId);
			list = query.list();
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching relationships");
		}
		return list;
	}

	@Override
	public MetadataCacheStatus getMetadataCacheStatusAndUpdateTime(Integer resourceId) {
		MetadataCacheStatus metadataPojo;
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery("select new com.helicalinsight.adhoc.MetadataCacheStatus(metadata.isCached, metadata.lastUpdatedTime,metadata.id)"
                + " FROM HIResourceMetadata metadata WHERE metadata.hiResource.resourceId=:id");
        query.setParameter("id", resourceId);
        metadataPojo=(MetadataCacheStatus)query.uniqueResult();
        return metadataPojo;
	}

	@Override
	public MetadataProperties loadHiResourceMetadataPropertiesById(Integer resourceId) {
		MetadataProperties metadataPojo;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("select new com.helicalinsight.adhoc.MetadataProperties(metadata.id,metadata.connectionType,metadata.fileName)"
            		            		+ " FROM HIResourceMetadata metadata WHERE metadata.hiResource.resourceId=:id");
            query.setParameter("id", resourceId);
            return (MetadataProperties) query.uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return null;
	}

	@Override
	public List<ConnectionDetails> getMetadataConnection(Integer metadataId) {
		List<HIMetadataConnections> hiMetadataConnections = getHIMetadataConnections(metadataId);
        List<ConnectionDetails> metadataConnections = new ArrayList<>();
        for (HIMetadataConnections connections : hiMetadataConnections) {
            String connectionType = connections.getConnectionType();

            if (connections.getMetadataConnectionEfwd() != null && !connections.getMetadataConnectionEfwd().isEmpty()) {
                for (HIMetadataConnectionEFWD firstConnection : connections.getMetadataConnectionEfwd()) {

                    ConnectionDetails newConn = ApplicationContextAccessor.getBean(ConnectionDetails.class);
                    HIResource parent = firstConnection.getHiEfwdConnection().getHiResourceEFWD().getParentResource();
                    newConn.setConnectionType(connectionType);
                    Database database = ApplicationContextAccessor.getBean(Database.class);
                    if (GlobalJdbcTypeUtils.isManagedGroovyDataSource(connectionType)) {
                        GroovyManagedJdbcHandler handler = new GroovyManagedJdbcHandler();
                        ObjectNode switchedConnection = handler.readDS("" + firstConnection.getHiEfwdConnection().getId(), connectionType);
                        String driverClassName = switchedConnection.get("driverClassName").asText();
                        String reference = JsonUtils.functionsReference(driverClassName);
                        DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
                        driverClass.setDriverClass(driverClassName);
                        driverClass.setReference(reference);
                        newConn.setDriverClass(driverClass);
                        newConn.setDialect(MetadataUtils.dialectOfDatabase(driverClassName));
                        if (switchedConnection.has("database"))
                            database.setName(switchedConnection.get("database").asText());
                        if (switchedConnection.has("catalog"))
                            database.setCatalog(switchedConnection.get("catalog").asText());
                        if (switchedConnection.has("schema"))
                            database.setSchema(switchedConnection.get("schema").asText());
                    } else {
                        DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
                        driverClass.setDriverClass(firstConnection.getDriverClass());
                        driverClass.setReference(firstConnection.getDriverClassReference());
                        newConn.setDriverClass(driverClass);
                        newConn.setDialect(firstConnection.getDialect());
                    }
                    if (parent != null) {
                        newConn.setDirectory(parent.getResourceURL());
                    }
                    Integer efwdId = firstConnection.getHiEfwdConnection().getId();
                    newConn.setConnectionId("" + efwdId);
                    metadataConnections.add(newConn);



                }
            } else {

                List<HIMetadataConnectionGlobal> metadataGlobalConnList = connections.getMetadataGlobalConnList();
                for (HIMetadataConnectionGlobal firstConnection : metadataGlobalConnList) {
                    ConnectionDetails newConn = ApplicationContextAccessor.getBean(ConnectionDetails.class);
                    newConn.setConnectionType(connectionType);
                    DriverClass driverClass = ApplicationContextAccessor.getBean(DriverClass.class);
                    driverClass.setDriverClass(firstConnection.getDriverClass());
                    driverClass.setReference(firstConnection.getDriverClassReference());
                    newConn.setDriverClass(driverClass);
                    newConn.setDialect(firstConnection.getDialect());
                    newConn.setSubType(firstConnection.getGlobalConnections().getType());
                    newConn.setVendorName(firstConnection.getGlobalConnections().getVendor());
                    Integer globalId = firstConnection.getGlobalConnections().getGlobalId();
                    newConn.setConnectionId("" + globalId);
                    metadataConnections.add(newConn);
                }

            }
        }
        return  metadataConnections;
	}

	@Override
	public MetadataDriverReferences getConnectionRefAndDriver(Integer metadaid) {
		MetadataDriverReferences driverReferences=null;
		try {
			List<HIMetadataConnections> connections;
            Session currentSession = sessionFactory.getCurrentSession();
            SelectionQuery<HIMetadataConnections> query=currentSession.createSelectionQuery("select new HIMetadataConnections(conn.id, conn.connectionType) FROM HIMetadataConnections conn WHERE conn.hiResourceMetadata.id=:metadaid",HIMetadataConnections.class);
            query.setParameter("metadaid", metadaid);
            connections=query.list();
            if(connections!=null) {
            	if(connections.size()>1) {
            		driverReferences=new MetadataDriverReferences(null, null, "default", null, null);
            	}
            	else {
            		HIMetadataConnections conn=connections.get(0);
                    String queryString=null;
            		if(conn.getConnectionType()!=null && conn.getConnectionType().equalsIgnoreCase("global.jdbc")) {
                        queryString = "select new com.helicalinsight.adhoc.MetadataDriverReferences(conn.dialect, conn.driverClass, conn.driverClassReference, conn.globalConnections.globalId, -99)"
                                + " FROM HIMetadataConnectionGlobal conn WHERE conn.hiMetadataConnections.id=:connid";

            		}
            		else {
                        queryString = "select new com.helicalinsight.adhoc.MetadataDriverReferences(conn.dialect, conn.driverClass, conn.driverClassReference, -99, conn.hiEfwdConnection.id)"
                                + " FROM HIMetadataConnectionEFWD conn WHERE conn.hiMetadataConnections.id=:connid";

            		}
                    SelectionQuery<MetadataDriverReferences> query2=currentSession.createQuery(queryString,MetadataDriverReferences.class);
                    query2.setParameter("connid", conn.getId());
                    driverReferences= query2.uniqueResult();
            		driverReferences.setConnectionType(conn.getConnectionType());
            	}
            	
            }
            
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return driverReferences;
	}

	@Override
	public void deleteHIMetadataColumnById(Integer id) {
		try {
			Session session = sessionFactory.getCurrentSession();
			
			String deleteRelationships = "delete HIMetadataRelationships where  leftMetadataColumns.id=:columnId  or rightMetadataColumns.id=:columnId";
			Query deleteRelationshipsQuery = session.createQuery(deleteRelationships);
			deleteRelationshipsQuery.setParameter("columnId", id);
			int noOfRecords = deleteRelationshipsQuery.executeUpdate();
			if(logger.isDebugEnabled()) {
				logger.debug("Deleted < {} > Relationships",noOfRecords);
			}
			Query deleteColumn = session.createQuery("delete HIMetadataColumns where id =:columnId");
			deleteColumn.setParameter("columnId",id);
			deleteColumn.executeUpdate();
		}
		catch (Exception e) {
			logger.error("Error occurred while deleting column, root cause : {}",e.getMessage());;
		}
	}

	@Override
	public void deleteAllSecuritiesByMetadataId(Integer metadataId) {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "DELETE  HIMetadataSecurity where hiResourceMetadata.id = :metadataId";
			Query query = session.createQuery(hql);
			query.setParameter("metadataId", metadataId);
			int noOfRecords = query.executeUpdate();
			logger.debug("No Of Records deleted : {}", noOfRecords);
		}
		catch (Exception e) {
			logger.error("Error occurred while deleting securities , Root cause : {}", e.getMessage());
			if(logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public List<HIMetadataColumns> getMetadataColumns(Integer tableId, Integer metadataId) {
        List<HIMetadataColumns> hiMetadataColumns=null;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            SelectionQuery<HIMetadataColumns> query = currentSession.createQuery("FROM HIMetadataColumns mdb where mdb.hiResourceMetadata.id=:metadataId AND mdb.hiMetadataTables.id=:tableId",HIMetadataColumns.class);
            query.setParameter("tableId", tableId);
            query.setParameter("metadataId", metadataId);
            return query.list();
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        return hiMetadataColumns;
	}

	@Override
	public Integer getViewEntryByViewNameAndDbIdAndMetadataId(String viewName, Integer dbId,
			Integer metadataId) {
		Integer viewId=null;
		try {
			Session curSession=sessionFactory.getCurrentSession();
			Query query=curSession.createQuery("SELECT tab.id FROM HIMetadataTables tab WHERE tab.hiMetadataDatabases.id=:dbId AND tab.hiResourceMetadata.id=:metadataId "
					+ "AND tab.isView=:isView AND tab.tableName=:viewName");
			query.setParameter("dbId", dbId);
			query.setParameter("metadataId", metadataId);
			query.setParameter("viewName", viewName);
			query.setParameter("isView", Boolean.TRUE);
			viewId=(Integer)query.uniqueResult();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return viewId;
	}

	@Override
	public List<HIMetadataView> getMetadataViewList(Integer metadata) {
		List<HIMetadataView> metadataViews=null;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Query query = currentSession.createQuery("FROM HIMetadataView view WHERE view.hiResourceMetadata.id=:metadata");
            query.setParameter("metadata", metadata);
            return query.list();
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
        return metadataViews;
	}

}
