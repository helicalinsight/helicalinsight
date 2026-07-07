package com.helicalinsight.datasource.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.model.EFWDConnGroovy;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIEfwdDataMap;
import com.helicalinsight.admin.model.HIHcrConnections;
import com.helicalinsight.admin.model.HIHcrConnectionsEfwd;
import com.helicalinsight.admin.model.HIHcrQueryParameters;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinHIEfwdConnection;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HiHcrQuery;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.admin.dto.EfwdConnDTO;
import com.helicalinsight.admin.dto.EfwdDataSourceLookupDTO;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.admin.dto.PlainConnDTO;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.SplitterUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class EFWDConnectionDAOImpl implements EFWDConnectionDAO {
	
	private static final String IS_DELETED_FILTER = "isDeletedFilter";


	@Autowired
	private HIResourceServiceDB serviceDb;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private HIRecycleBinService recycleBinService;
	
    @Autowired
    private HIResourceServiceDB hiServiceDB;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    
    private Session getSession() {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
		}
		catch (Exception e) {
			session = sessionFactory.openSession();
		}
		session.disableFilter(IS_DELETED_FILTER);
		return session;
	}

	@Override
	public HIEfwdConnection saveEFWDConnection(HIEfwdConnection hiEfwdConnection) {

		try {
			hiEfwdConnection.setDeleted(false);
			getSession().save(hiEfwdConnection);
			return hiEfwdConnection;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public HIEFWD saveHIResourceEFWD(HIEFWD hiefwd) {
		try {
			getSession().save(hiefwd);
			return hiefwd;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<HIEfwdConnection> findAllFEWDConnection() {
		try {
			Boolean st=Boolean.FALSE;
			Session currentSession = getSession();
			SelectionQuery<HIEfwdConnection> query = currentSession.createSelectionQuery("FROM HIEfwdConnection WHERE deleted=:st",HIEfwdConnection.class);
			query.setParameter("st", st);
			return query.list();
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<HIEfwdConnection> findAllRequiredEFWDConnection(Set<Integer> connIds) {
		try {
			Boolean st=Boolean.FALSE;
			Session currentSession = getSession();
			SelectionQuery<HIEfwdConnection> query = currentSession.createSelectionQuery("FROM HIEfwdConnection WHERE deleted=:st AND id in (:connIds)",HIEfwdConnection.class);
			query.setParameter("st", st);
			query.setParameterList("connIds", connIds);
			return query.list();
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public EFWDConnSqlJDBC save(EFWDConnSqlJDBC efwdConnSqlJDBC) {

		try {
			getSession().save(efwdConnSqlJDBC);
			return efwdConnSqlJDBC;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return null;
		}
	}


	@Override
	public EFWDConnSqlJDBC edit(EFWDConnSqlJDBC efwdConnSqlJDBC) {
		try {
			getSession().merge(efwdConnSqlJDBC);
			return efwdConnSqlJDBC;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findAllFEWDConnections(String subType) {
		List<Integer> connectionIds = new ArrayList<>();
		try {
			SelectionQuery<Integer> query = getSession().createSelectionQuery("select id From HIEfwdConnection where type = :subType",Integer.class);
			query.setParameter("subType", subType);
			connectionIds = query.list();
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}

		return connectionIds;
	}

	@Override
	public EFWDConnSqlJDBC findSQLJDBCConnectionById(Integer efwdConnectionId) {
		try {
			SelectionQuery<EFWDConnSqlJDBC> query = getSession().createSelectionQuery("FROM EFWDConnSqlJDBC conn where conn.hiEfwdConnection.id = :connectionId",EFWDConnSqlJDBC.class);
			query.setParameter("connectionId", efwdConnectionId);
			return  query.uniqueResult();
		}
		catch (HibernateException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public EFWDConnGroovy findGroovyConnectionByIdAndType(Integer connectionId,String type) {
		try {
			SelectionQuery<HIEfwdConnection> query = getSession().createSelectionQuery("FROM HIEfwdConnection conn JOIN FETCH conn.efwdConnGroovy where conn.id = :connectionId and conn.type = :type",HIEfwdConnection.class);
			query.setParameter("type", type);
			query.setParameter("connectionId", connectionId);
			HIEfwdConnection object =  query.uniqueResult();
			return object != null?object.getEfwdConnGroovy().get(0):null;

		}catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public EFWDConnSqlJDBC findSQLJDBCConnectionByIdAndType(Integer connectionId,String type) {
		try {
			SelectionQuery<HIEfwdConnection> query = getSession().createSelectionQuery("FROM HIEfwdConnection conn JOIN FETCH conn.efwdConnSqlJDBC where conn.id = :connectionId and conn.type = :type",HIEfwdConnection.class);
			query.setParameter("type", type);
			query.setParameter("connectionId", connectionId);
			HIEfwdConnection object = query.uniqueResult();
			return object != null ? object.getEfwdConnSqlJDBC().get(0) : null;
		}
		catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private final Integer softDelete(final HIEfwdConnection connection) {
		connection.setDeleted(true);
		HIRecycleBin bin = new HIRecycleBin();
    	bin.setRecycleBinType(RecycleBinType.HI_EFWD_CONNECTION);
    	bin.setDeletedBy(userDao.findUser(Integer.valueOf(SecurityUtils.securityObject().getCreatedBy())));
    	User owner = userDao.findUser(connection.getHiResourceEFWD().getCreatedBy());
    	bin.setCreatedBy(owner);
    	bin.setOrgId(owner.getOrganization());
    	HIRecycleBinHIEfwdConnection binConnection = new HIRecycleBinHIEfwdConnection();
    	binConnection.setEfwdConnection(connection);
    	binConnection.setRecycleBin(bin);
    	bin.setHiRecycleBinHIEfwdConnection(binConnection);
    	bin.setDeletedOn(new Date());
    	recycleBinService.save(bin);
		getSession().merge(connection);
		return connection.getId();
	
	}
	
	private final Integer hardDelete(final HIEfwdConnection connectionToDelete) {
		try {
		Session session = getSession();
		deleteReports(connectionToDelete);
		List<HIResource> hiResourceList = deleteHCRReports(connectionToDelete);
		deleteCache(connectionToDelete);
		
		Integer connectionId = connectionToDelete.getId();
		
		// sql plain connection
		MutationQuery deletePlainSqlConnections = session.createMutationQuery("DELETE EFWDConnSqlJDBC  jdbc where jdbc.hiEfwdConnection.id =:connectionId");
		deletePlainSqlConnections.setParameter("connectionId", connectionId);
		int sqlJdbcCount = deletePlainSqlConnections.executeUpdate();
		log.debug("No of SQL.JDBC connection deleted : {}", sqlJdbcCount);
		
		// groovy plain & managed
		MutationQuery deleteGroovyConnections = session.createMutationQuery("DELETE EFWDConnGroovy  groovy where groovy.hiEfwdConnection.id =:connectionId");
		deleteGroovyConnections.setParameter("connectionId", connectionId);
		int groovyCount = deleteGroovyConnections.executeUpdate();
		log.debug("No of groovy connection deleted : {}", groovyCount);
		
		// fetch HIEFWD resource 
		String query = "select c.hiResourceEFWD.id from HIEfwdConnection c where c.id = :connectionId";
		SelectionQuery<Integer> efwdListQuery = session.createSelectionQuery(query, Integer.class);
		efwdListQuery.setParameter("connectionId", connectionId);
		List<Integer> efwdList = efwdListQuery.list();
		
		// Securities
		deleteConnectionSecuritiesById(connectionId);

		// BIN
		HIRecycleBin bin = getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(connectionId);
		if (bin != null) {
			recycleBinService.delete(bin.getId());
		}
		
		// HIEfwdConnection 
		MutationQuery mainConnection = session.createMutationQuery("DELETE HIEfwdConnection  conn where conn.id =:connectionId");
		mainConnection.setParameter("connectionId", connectionId);
		int mainCount = mainConnection.executeUpdate();
		log.debug("No of main connections deleted : {}", mainCount);
		
		// Delete HIEFWD resources
		MutationQuery resourceDeleteQuery = session.createMutationQuery("DELETE HIEFWD  where id in (:efwdList)");
		resourceDeleteQuery.setParameterList("efwdList", efwdList);
		int count = resourceDeleteQuery.executeUpdate();
		log.debug("deleted resources : {}", count);
		
		for(HIResource resource: hiResourceList) {
			serviceDb.hardDelete(resource);
		}
		
		return connectionToDelete.getId();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public Integer hardDeleteEFConnectionById(Integer connectionId) {
		Session session = getSession();
		recycleBinService.deleteHIRecycleByEfwdId(connectionId);
		
		SelectionQuery<HIEfwdConnection> query = session.createSelectionQuery("FROM HIEfwdConnection where id = :connectionId",HIEfwdConnection.class);
		query.setParameter("connectionId", connectionId);
		HIEfwdConnection connectionToDelete =  query.uniqueResult();
		return hardDelete(connectionToDelete);
		
	}

	@Override
	public Integer deleteEFConnectionById(Integer connectionId) {

		try {
			SelectionQuery<HIEfwdConnection> query = getSession().createSelectionQuery("FROM HIEfwdConnection where id = :connectionId",HIEfwdConnection.class);
			query.setParameter("connectionId", connectionId);
			HIEfwdConnection connectionToDelete =  query.uniqueResult();
			return !connectionToDelete.isDeleted() ? softDelete(connectionToDelete) : hardDelete(connectionToDelete);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void deleteReports(HIEfwdConnection connection) {
		List<HIMetadataConnectionEFWD> connections = connection.getMetadataConnections();
		for (HIMetadataConnectionEFWD con : connections) {
			for (MetadataDatabases db : con.getHiMetadataConnections().getMetadataDatabases()) {
				serviceDb.hardDelete(db.getHiResourceMetadata().getHiResource());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<HIResource> deleteHCRReports(HIEfwdConnection connection) {
		List<HIResource> result = new ArrayList<>();
		try {
			Session currentSession = this.getSession();
			String sql = "select mdc.hiResourceHcr  FROM HIHcrConnections as mdc "
					+ " where  mdc.hiHcrConnectionsEfwd.hiEfwdConnection.id =" + connection.getId();
			Query query = currentSession.createQuery(sql);
			result = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  result;

	}


	private void deleteCache(HIEfwdConnection connection) {
		JsonObject formData = new JsonObject();
		JsonArray cacheArray = new JsonArray();
		JsonObject cache = new JsonObject();
		cache.addProperty("id", ""+connection.getId());
		cache.addProperty("baseType", connection.getType());
		cache.addProperty("dir", connection.getHiResourceEFWD().getParentResource().getResourcePath());
		cacheArray.add(cache);
		formData.add("ids", cacheArray);
        IComponent managedDsShutdown = FactoryMethodWrapper.getTypedInstance("com.helicalinsight.admin.management.ManagedDataSourceShutdownHandler", IComponent.class);
        managedDsShutdown.executeComponent(formData.toString());
	}

	@Override
	public void insertOrUpdate(HIEfwdConnSecurity security) {
		try {
			getSession().saveOrUpdate(security);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void deleteEFConnectionecurityByConnectionId(Integer connectionId, Integer userId, Integer roleId,
														Integer orgId) {
		try {
			String userVal = null == userId ? "userId.id=NULL" : "userId.id = :userId";
			String roleVal = null == roleId ? "roleId.id=NULL" : "roleId.id = :roleId";
			String orgVal = null == orgId ? "orgId.id=NULL" : "orgId.id= :orgId";
			String hql = "DELETE  HIEfwdConnSecurity  where hiEfwdConnection.id = :connectionId" + " and " + userVal
					+ " and " + roleVal + " and " + orgVal;

			Session session = getSession();
			jakarta.persistence.Query query = session.createQuery(hql);
			query.setParameter("connectionId", connectionId);

			if (null != userId) {
				query.setParameter("userId", userId);
			}
			if (null != roleId) {
				query.setParameter("roleId", roleId);
			}
			if (null != orgId) {
				query.setParameter("orgId", orgId);
			}
			// TODO: Optimize this
			 query.executeUpdate();
//			List<HIEfwdConnSecurity> securityList =  query.list();
//			
//			if(securityList != null  && !securityList.isEmpty()) {
//				securityList.forEach(session::remove);
//			}
//			session.flush();
			
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public HIEfwdConnSecurity findEfConnectionSecurityByConnectionId(Integer connectionId) {
		HIEfwdConnSecurity security = null;
		try {
			SelectionQuery<HIEfwdConnSecurity> query = getSession().createSelectionQuery("FROM HIEfwdConnSecurity as s where s.hiEfwdConnection.id = :connectionId",HIEfwdConnSecurity.class);
			query.setParameter("connectionId", connectionId);
			List<?> list = query.list();
			if (!list.isEmpty()) {
				security = (HIEfwdConnSecurity) list.get(0);
				return security;
			}
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}




	@Override
	public Boolean deleteEFConnectionecurityByConnectionId(Integer connectionId) {
		try {
			Query query = getSession()
					.createQuery("delete HIEfwdConnSecurity where hiEfwdConnection.id = :connectionId");
			query.setParameter("connectionId", connectionId);
			query.executeUpdate();
			return true;
		} catch (HibernateException ex) {
			ex.printStackTrace();
			return false;
		}
	}


	@Override
	public EFWDConnGroovy save(EFWDConnGroovy groovyCon) {
		try {
			getSession().save(groovyCon);
			return groovyCon;
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public EFWDConnGroovy findGroovyByEFWDConnectionId(Integer connectionId) {
		try {
			SelectionQuery<EFWDConnGroovy> query = getSession().createSelectionQuery("FROM EFWDConnGroovy conn where conn.hiEfwdConnection.id = :connectionId",EFWDConnGroovy.class);
			query.setParameter("connectionId", connectionId);
			return  query.uniqueResult();

		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public EFWDConnGroovy edit(EFWDConnGroovy groovyConnection) {
		try {
			getSession().merge(groovyConnection);
			return groovyConnection;
		}
		catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<HIEfwdConnSecurity> findEFWDSecurityByConnectionId(Integer connectionId) {
		try {
			SelectionQuery<HIEfwdConnSecurity> query = getSession().createSelectionQuery("FROM HIEfwdConnSecurity security where security.hiEfwdConnection.id = :connectionId",HIEfwdConnSecurity.class);
			query.setParameter("connectionId", connectionId);
			return  query.list();
		}
		catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public List<EFWDConnSqlJDBC> findConnectionByParentId(int parentId) {
		try {
			String hql = "FROM EFWDConnSqlJDBC where  hiEfwdConnection.hiResourceEFWD.parentResource.resourceId = :parentId";
			SelectionQuery<EFWDConnSqlJDBC> query = getSession().createSelectionQuery(hql,EFWDConnSqlJDBC.class);
			query.setParameter("parentId", parentId);
			return  query.list();
		}
		catch (HibernateException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public List<EFWDConnGroovy> findGroovyConnectionByParentId(int parentId) {
		try {
			String hql = "FROM EFWDConnGroovy where  hiEfwdConnection.hiResourceEFWD.parentResource.resourceId = :parentId";
			SelectionQuery<EFWDConnGroovy> query = getSession().createSelectionQuery(hql,EFWDConnGroovy.class);
			query.setParameter("parentId", parentId);
			return query.list();
		}
		catch (HibernateException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public List<HIEfwdConnSecurity> getAllConnectionsFromShared(String type) {
		List<String> userRolesIds = AuthenticationUtils.getUserRolesIds();
		String userId = AuthenticationUtils.getUserId();
		String organizationId = AuthenticationUtils.getOrganizationId();


		try {
			String userVal = !userId.isEmpty() ? "userId.id = :userId" : "";
			String orgVal = organizationId != null ? " or orgId.id = :orgId" : "";
			String roleVal = !userRolesIds.isEmpty() ? " or roleId.id in  (:roleId) " : "";
			String typeCondition = "";
			if (!type.equalsIgnoreCase("all")) {
				typeCondition = "hiEfwdConnection.type = :type and ";
			}
			String hql = "select distinct hiEfwdConnection.id as id,max(permission) as permission FROM  HIEfwdConnSecurity  where " + typeCondition + userVal + orgVal + roleVal + " group by hiEfwdConnection.id";
			Session currentSession = getSession();
			Query query = currentSession.createQuery(hql);
			if (!typeCondition.isBlank()) {
				query.setParameter("type", type);
			}
			if (StringUtils.isNotEmpty(organizationId)) {
				query.setParameter("orgId", Integer.valueOf(organizationId));
			}

			query.setParameter("userId", Integer.valueOf(userId));
			List<Integer> roleId = new ArrayList<>();
			if (userRolesIds.size() > 0) {
				for (String role : userRolesIds) {
					roleId.add(Integer.valueOf(role));
				}
				query.setParameterList("roleId", roleId);
			}
			query.setResultTransformer(Transformers.aliasToBean(HIEfwdConnSecurity.class));
			query.setCacheable(true);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public List<EfwdConnDTO> getAllConnectionOfLoggedInUser(Integer createdBy, List<Integer> sharedefwdId, String type) {
		try {
			Session currentSession = getSession();
			
			String optimizedQuery1 = """
					SELECT DISTINCT con
					FROM HIEfwdConnection con
					JOIN con.hiResourceEFWD res
					WHERE  res.createdBy = :createdBy 
						OR res.createdBy IS NULL
						OR con.id IN (:efwdIds) 
					
					""";
			
			
			SelectionQuery<HIEfwdConnection> query = currentSession.createQuery(optimizedQuery1,HIEfwdConnection.class);
			query.setParameter("createdBy", createdBy);
			query.setParameterList("efwdIds", sharedefwdId.isEmpty() ? List.of(-1) : sharedefwdId);
			
			query.setReadOnly(true);
			query.setCacheable(true);
			
			List<HIEfwdConnection> efwdConnections =   query.list();
			
			
			Map<Integer, List<PlainConnDTO>> connectionMap = prepareConnectionMap(efwdConnections, type);
			
			return prepareEfwdConnectionOpt(efwdConnections,connectionMap, Boolean.FALSE,Boolean.TRUE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
	
	
	private Map<Integer,List<PlainConnDTO>> prepareConnectionMap(List<HIEfwdConnection> efwdConnections, String type) {
		
		List<Integer> connectionIds = efwdConnections
				.stream()
				.filter(con -> type.equals("all") || con.getType().equals(type))
				.map(HIEfwdConnection::getId)
				.toList();
		
		Map<Integer, List<PlainConnDTO>> connectionMap = new HashMap<>();

		if ("all".equals(type) ||  "sql.jdbc".equals(type)) {
			List<PlainConnDTO> sqlList = fetchSqlConnections(connectionIds);
			addConnections(connectionMap, sqlList);
		}
		
		if ("all".equals(type) || !"sql.jdbc".equals(type)) { 
			List<PlainConnDTO> groovyList = fetchGroovyConnections(connectionIds);
			addConnections(connectionMap, groovyList);
		}
		
		return connectionMap;
	}
	
	private void addConnections(Map<Integer, List<PlainConnDTO>> connectionMap,List<PlainConnDTO> connections) {

	    for (PlainConnDTO connection : connections) {
	        connectionMap.computeIfAbsent(connection.getEfwdId(), k -> new ArrayList<>()).add(connection);
	    }
	}
	
	
	
	public List<PlainConnDTO> fetchSqlConnections(List<Integer> connectionIds) {

		try {

			Session session = getSession();

			String sqlQuery = """
					SELECT new com.helicalinsight.admin.dto.PlainConnDTO (
					    c.id,
					    c.database,
					    c.driver,
					    c.efwdConnId,
					    c.name,
					    c.pass,
					    c.url,
					    c.userName,
					    new com.helicalinsight.admin.dto.HIEfwdDTO (
				            r.resourceId,
				            r.resourceURL,
				            efwd.createdBy,
				            r.isDeleted,
				            r.title,
				            r.resourcePath
				         )				
				        )
					FROM EFWDConnSqlJDBC c
					JOIN c.hiEfwdConnection conn
					JOIN conn.hiResourceEFWD efwd
				    JOIN efwd.parentResource r
					WHERE c.efwdConnId IN (:ids)
					""";

			return session.createQuery(sqlQuery, PlainConnDTO.class).setParameterList("ids", connectionIds).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	public List<PlainConnDTO> fetchGroovyConnections(List<Integer> connectionIds) {
		try {
			Session session = getSession();

			String groovyQuery = """
					SELECT new com.helicalinsight.admin.dto.PlainConnDTO(
					    c.id,
					    c.database,
					    c.driver,
					    c.efwdConnId,
					    c.name,
					    c.pass,
					    c.url,
					    c.userName,
					    new com.helicalinsight.admin.dto.HIEfwdDTO(
				            r.resourceId,
				            r.resourceURL,
				            efwd.createdBy,
				            r.isDeleted,
				            r.title,
				            r.resourcePath
				        ),
					    c.condition
					)
					FROM EFWDConnGroovy c
					JOIN c.hiEfwdConnection conn
					JOIN conn.hiResourceEFWD efwd
				    JOIN efwd.parentResource r
					WHERE c.efwdConnId IN (:ids)
					""";

			Query<PlainConnDTO> query =  session.createQuery(groovyQuery, PlainConnDTO.class);
			query.setReadOnly(true);
			List<PlainConnDTO> list =  query.setParameterList("ids", connectionIds).list();
		    return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	
	private List<EfwdConnDTO> prepareEfwdConnectionOpt(List<HIEfwdConnection> efwd, Map<Integer, List<PlainConnDTO>> connectionMap, Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion) {
		List<EfwdConnDTO> dtoList = new ArrayList<>();
		
		List<Integer> efwdResourceIds = getEfwdResourceIds();
		Map<Integer,HIResource> hierarchyMap =  getResourceHierarchyMap(efwdResourceIds);
		Map<String,Boolean> permissionMap = new HashMap<>();
		
		for(HIEfwdConnection conn:efwd) {
			
			HIResource resource  =  conn.getHiResourceEFWD().getParentResource();
			
				final EfwdConnDTO dto = new EfwdConnDTO();
				dto.setCreatedBy(String.valueOf(conn.getHiResourceEFWD().getCreatedBy()));
				HIEfwdDTO resourceDto = new HIEfwdDTO();
				resourceDto.setResourceId(resource.getResourceId());
				resourceDto.setResourceUrl(resource.getResourceURL());
				resourceDto.setCreatedBy(resource.getCreatedBy());
				resourceDto.setIsDeleted(resource.isDeleted());
				
				dto.setResource(resourceDto);				
				dto.setId(conn.getId());	
				dto.setType(conn.getType());
				dto.setDeleted(conn.isDeleted());
				List<PlainConnDTO> plainConnList = new ArrayList<>();
				List<PlainConnDTO> plainSql = connectionMap.getOrDefault(conn.getId(), new ArrayList<>());
				boolean hasPermission  = EfwdDatasourceUtils.hasPermission(resourceDto.getResourceUrl(), hierarchyMap, permissionMap);
				plainSql.forEach(it ->  {
					dto.setName(it.getName());
					it.setIsPublic(resourceDto.getCreatedBy() == null);
					it.setDirectory(resourceDto.getResourceUrl());
					it.setUrl(it.getUrl());
					it.setType(conn.getType());
					if((resourceDto.getIsDeleted() == null || !resourceDto.getIsDeleted()) && hasPermission) {
						plainConnList.add(it);
					}
				});
				dto.setPlainConnections(plainConnList);
				dtoList.add(dto);
		}
		
		return dtoList;

	}

	public List<EfwdConnDTO> prepareEfwdConnection(List<HIEfwdConnection> efwd,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion) {
		
		List<EfwdConnDTO> dtoList = new ArrayList<>();
		Boolean flag=false;
		
		if(!isRequiredForRecycleBinFetch && isRequiredToCheckTopLevelRootDeletion)
			flag=true;
		
		for(HIEfwdConnection conn:efwd) {
			if(isRequiredForRecycleBinFetch || (!conn.isDeleted())) {
				final EfwdConnDTO dto = new EfwdConnDTO();
				dto.setCreatedBy(String.valueOf(conn.getHiResourceEFWD().getCreatedBy()));
				HIEfwdDTO  efwdResource = new HIEfwdDTO();
				HIResource resource =  conn.getHiResourceEFWD().getParentResource();
				efwdResource.setCreatedBy(resource.getCreatedBy());
				efwdResource.setIsDeleted(resource.getDeleted());
				efwdResource.setResourceId(resource.getResourceId());
				efwdResource.setResourceUrl(resource.getResourceURL());
				dto.setResource(efwdResource);
				dto.setId(conn.getId());
				dto.setType(conn.getType());
				dto.setDeleted(conn.isDeleted());
				List<PlainConnDTO> plainConnList = new ArrayList<>();
				Set<EFWDConnSqlJDBC> plainSql = new HashSet<>(conn.getEfwdConnSqlJDBC());
				plainSql.forEach(it ->  {
					final PlainConnDTO plainCon = new PlainConnDTO();
					plainCon.setId(it.getId());
					plainCon.setDatabase(it.getDatabase());
					plainCon.setDriver(it.getDriver());
					plainCon.setEfwdId(conn.getId());
					plainCon.setName(it.getName());
					dto.setName(it.getName());
					plainCon.setPass(it.getPass());
					plainCon.setUrl(it.getUrl());
					plainCon.setUserName(it.getUserName());
					plainCon.setType(conn.getType());
					plainCon.setIsPublic(resource.getCreatedBy() == null);
					plainCon.setDirectory(resource.getResourceURL());
					plainCon.setUrl(it.getUrl());
					if((resource.getDeleted() == null || !resource.getDeleted()) && EfwdDatasourceUtils.hasPermission(resource.getResourceURL()) ) {
						plainConnList.add(plainCon);
					}
				});
				Set<EFWDConnGroovy> groovyCons = new HashSet<>(conn.getEfwdConnGroovy());
				groovyCons.forEach(it ->  {
					final PlainConnDTO plainCon = new PlainConnDTO();
					plainCon.setId(it.getId());
					plainCon.setDatabase(it.getDatabase());
					plainCon.setDriver(it.getDriver());
					plainCon.setEfwdId(conn.getId());
					plainCon.setName(it.getName());
					dto.setName(it.getName());
					plainCon.setPass(it.getPass());
					plainCon.setUrl(it.getUrl());
					plainCon.setIsPublic(resource.getCreatedBy() == null);
					plainCon.setDirectory(resource.getResourceURL());
					plainCon.setUserName(it.getUserName());
					plainCon.setType(conn.getType());
					plainCon.setCondition(it.getCondition());
					plainCon.setUrl(it.getUrl());
					if( (resource.getDeleted() == null || !resource.getDeleted()) && EfwdDatasourceUtils.hasPermission(resource.getResourceURL()) ) {
						plainConnList.add(plainCon);
					}
	
				});
				dto.setPlainConnections(plainConnList);
				dtoList.add(dto);
		}}
		return dtoList;

	}
	
	
	private List<Integer> getEfwdResourceIds() {
		try {
			Session session = getSession();
			String query = """
					SELECT  DISTINCT  efwd.parentResource.resourceId FROM HIEFWD efwd
					""";
			SelectionQuery<Integer> resourceListQuery = session.createSelectionQuery(query, Integer.class);
			return resourceListQuery.getResultList();
		} catch (Exception e) {
			log.error("Error occurred while fetching the efwd resources. due to {}", e);
		}
		return Collections.emptyList();
	}
	
	private Map<Integer,HIResource> getResourceHierarchyMap(List<Integer> resourceIds) {
		try {
			Session session = getSession();
			Set<Integer> pendingIds =  new HashSet<>(resourceIds);
			Map<Integer, HIResource> hierarchyMap = new HashMap<>();
			while (!pendingIds.isEmpty()) {

				String resourceQuery = "SELECT new com.helicalinsight.admin.model.HIResource(resourceId, parentId, isDeleted)  FROM HIResource resource where resourceId in (:resourceIds)";
				SelectionQuery<HIResource> initialResources = session.createSelectionQuery(resourceQuery,HIResource.class);
				initialResources.setParameterList("resourceIds", pendingIds);
				List<HIResource> list = initialResources.getResultList();
			    
				pendingIds = new HashSet<>();

			    for (HIResource r : list) {
					hierarchyMap.put(r.getResourceId(), new HIResource(r.getParentId(), r.isDeleted()));
			        if (r.getParentId() != null && !hierarchyMap.containsKey(r.getParentId())) {
			            pendingIds.add(r.getParentId());
			        }
			    }
			}
			return hierarchyMap;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
	
	private boolean isItsRootDeleted(Integer resourceId, Map<Integer,HIResource> hierarchyMap, Map<Integer,Boolean> deleteMap,   Boolean flag) {
		//This flag condition always returns TRUE because at this point we are NOT willing to find ROOT folder delete status 
		if(!flag) return false;
		while (resourceId != null) {

	        HIResource node = hierarchyMap.get(resourceId);

	        if (node == null) {
	    		deleteMap.put(resourceId, false);
	            return false;
	        }

	        if (node.getDeleted()) {
	    		deleteMap.put(resourceId, true);
	            return true;
	        }
	        resourceId = node.getParentId();
	    }
		deleteMap.put(resourceId, false);
		return false;
	}

	@Override
	public HIEfwdConnection findConnectionByConnectionIdAndType(String connectionId, String type) {
		try {
			SelectionQuery<HIEfwdConnection> query = getSession().createQuery("FROM HIEfwdConnection where connectionId = :connectionId and type = :type",HIEfwdConnection.class);
			query.setParameter("connectionId", connectionId);
			query.setParameter("type", type);
			return query.uniqueResult();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<EfwdConnDTO> findConnectionByResourceId(int resourceId,Boolean isRequiredForRecycleBinFetch,Boolean isRequiredToCheckTopLevelRootDeletion) {
		try {
			SelectionQuery<HIEfwdConnection> query = getSession().createQuery("FROM HIEfwdConnection where hiResourceEFWD.parentResource.resourceId = :resourceId",HIEfwdConnection.class);
			query.setParameter("resourceId", resourceId);
			List<HIEfwdConnection> connections = query.list();
			Map<Integer, List<PlainConnDTO>> connectionMap = prepareConnectionMap(connections, "all");
			List<EfwdConnDTO> cns =  prepareEfwdConnectionOpt(connections,connectionMap, isRequiredForRecycleBinFetch,isRequiredToCheckTopLevelRootDeletion);
			return cns;
		}
		catch (Exception e) {
			log.error("Error occured while fetching efwd connection  by resourceId : {}", resourceId);
		}
		return Collections.emptyList();
	}

	@Override
	public Integer edit(HIEfwdConnection connection) {
		try {
			Session session = getSession();
			session.merge(connection);
			return connection.getId();
		}
		catch (Exception e) {
			log.error("Error occurred while update HIEfwdConnection. root cause : {} ",e.getMessage());
			return null;
		}
	}

	@Override
	public Optional<HIEfwdConnection> findConnectionById(String connectionId, boolean applyFilter) {
		HIEfwdConnection connection = null;
		try {
			Session session = getSession();
			if(applyFilter) session.enableFilter(IS_DELETED_FILTER).setParameter("isDeleted", false);
			SelectionQuery<HIEfwdConnection> query = session.createQuery("FROM HIEfwdConnection where id =:connectionId",HIEfwdConnection.class);
			query.setParameter("connectionId", Integer.valueOf(connectionId));
			connection =  query.uniqueResult();
		}
		catch (Exception e) {
			if(log.isErrorEnabled()) {
				e.printStackTrace();
			}
		}
		return Optional.ofNullable(connection);
	}

	@Override
	public Boolean isDeleted(String id) {
		
		Session session = getSession();
		try {
			String hql = "SELECT deleted FROM HIEfwdConnection where id = :id";
			SelectionQuery<Boolean> query = session.createQuery(hql,Boolean.class);
			query.setParameter("id", Integer.valueOf(id));
			return  query.uniqueResult();
		}
		catch (Exception e) {
			if(log.isErrorEnabled() || log.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public HIRecycleBin getHiRecycleBinIdOfSoftDeletedItemByEfwdConnId(Integer connId) {
		HIRecycleBin hiRecycleBin=null;
		Session session=getSession();
		try {
			SelectionQuery<HIRecycleBin> query=session.createSelectionQuery("select efwdRb.recycleBin from HIRecycleBinHIEfwdConnection efwdRb WHERE efwdRb.efwdConnection.id=:connId",HIRecycleBin.class);
			query.setParameter("connId", connId);
			hiRecycleBin= query.uniqueResult();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return hiRecycleBin;
	}

	@Override
	public Integer deleteConnectionSecuritiesById(int connectionId) {
		
		try {
			Session session = getSession();
			String hql = "DELETE HIEfwdConnSecurity where  hiEfwdConnection.id = :connectionId";
			Query query = session.createQuery(hql);
			query.setParameter("connectionId", connectionId);
			int noOfRecords =  query.executeUpdate();
			log.debug("No Of Records Deleted : {}",noOfRecords);
			
			return noOfRecords;
		}
		catch (Exception e) {
			log.error("Error occurred while deleting EFWD Connection securities , Root Cause: {}",e.getMessage());
			return 0;
		}
	}

	@Override
	public HIEfwdConnection findConnectionByLookup(EfwdDataSourceLookupDTO lookup) {
		
		try {
			StringBuilder hql = new StringBuilder();
			String type = lookup.getType();
			
			boolean isGroovyManaged = GlobalJdbcTypeUtils.isManagedGroovyDataSource(type);
			
			hql.append("FROM HIEfwdConnection con ");
			
			if(GlobalJdbcTypeUtils.isPlainDataSource(type)) {
				hql.append("JOIN FETCH con.efwdConnSqlJDBC  jdbc ");
			}
			else {
				hql.append("JOIN FETCH  con.efwdConnGroovy jdbc ");
			}
			
			hql.append("WHERE  jdbc.name = :name  and con.type =:type and con.hiResourceEFWD.parentResource.resourceURL = :resourceUrl ");
			
			if(!isGroovyManaged) {
				hql.append("and jdbc.userName =:userName and jdbc.pass =:pass and jdbc.url =:url");
			}
			
			Session session = getSession();
			SelectionQuery<HIEfwdConnection> query = session.createSelectionQuery(hql.toString(),HIEfwdConnection.class);
			
			if(!isGroovyManaged) {
				query.setParameter("userName", lookup.getUserName());
				query.setParameter("pass", CipherUtils.encrypt(lookup.getPassword()));
				query.setParameter("url", lookup.getJdbcUrl());
			}
			query.setParameter("name", lookup.getName());
			query.setParameter("type", type);
			query.setParameter("resourceUrl", lookup.getDirectory());
			
			@SuppressWarnings("unchecked")
			List<HIEfwdConnection> connections =  query.list();
			
			if(connections != null && !connections.isEmpty()) {
				
				if(isGroovyManaged) {
					connections.forEach(conn -> conn.getEfwdConnGroovy().stream()
							.filter(groovy -> SplitterUtils.prepareServiceId( groovy.getCondition())
									.equals(SplitterUtils.prepareServiceId(lookup.getCondition()))));
				}
				
				
				return connections.get(0);
			}
			
		}
		catch (Exception e) {
			log.error("Error occurred while fetching EFWD connection. Root cause: {}",e.getMessage());
		}
		
		
		
		return null;
	}
	@Override
	public List<EfwdConnDTO> findConnectionByResourceIds(List<Integer> resourceIds, Boolean isRequiredForRecycleBinFetch, Boolean isRequiredToCheckTopLevelRootDeletion) {
		try {
			Query query = getSession().createQuery("FROM HIEfwdConnection where hiResourceEFWD.parentResource.resourceId in :resourceId");
			query.setParameterList("resourceId", resourceIds);
			List<HIEfwdConnection> connections = (List<HIEfwdConnection> )query.list();
			Map<Integer, List<PlainConnDTO>> connectionMap = prepareConnectionMap(connections, "all");
			return prepareEfwdConnectionOpt(connections,connectionMap, isRequiredForRecycleBinFetch,isRequiredToCheckTopLevelRootDeletion);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public void addHiHcrConnection(HIHcrConnections hiHcrConnections) {
		try {
			getSession().save(hiHcrConnections);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void addHiHcrEfwdConnection(HIHcrConnectionsEfwd hiHcrConnectionsEfwd) {
		try {
			getSession().save(hiHcrConnectionsEfwd);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void addHiHcrQuery(HiHcrQuery hcrQuery) {
		try {
			getSession().save(hcrQuery);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void addHiHcrQueryParams(HIHcrQueryParameters hiHcrQueryParameters) {
		try {
			getSession().save(hiHcrQueryParameters);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void deleteHiHcrConnection(HIHcrConnections hiHcrConnections) {
		try {
			getSession().delete(hiHcrConnections);
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public List<HIHcrConnections> fetchAllHcrConnectionsByResourceId(Integer hcrResId) {
		List<HIHcrConnections> hcrConnections=new ArrayList<>();
		try {
			Session currentSession=getSession();
			Query query=currentSession.createQuery("FROM HIHcrConnections hcrCon WHERE hcrCon.hiResourceHcr.resourceId=:hcrResId");
			query.setParameter("hcrResId", hcrResId);
			hcrConnections=query.list();
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return hcrConnections;
	}

	@Override
	public HIHcrConnections fetchHIHcrConnectionsById(Integer hcrResId) {
		try {
			HIHcrConnections connd=null;
			Session currentSession=getSession();
			Query query=currentSession.createQuery("FROM HIHcrConnections hcrCon WHERE hcrCon.id=:hcrResId");
			query.setParameter("hcrResId", hcrResId);
			connd=  (HIHcrConnections)query.uniqueResult();
			return connd;
		} catch (HibernateException ex) {
			ex.printStackTrace();
		}
		return  null;
	}
	
	
	@Override
	public PlainConnDTO findPlainConnection(Integer connectionId) {
		try {
			Session session = getSession();
			String sqlQuery = """
					SELECT new com.helicalinsight.admin.dto.PlainConnDTO (
					    c.id,
					    c.database,
					    c.driver,
					    c.efwdConnId,
					    c.name,
					    c.pass,
					    c.url,
					    c.userName,
					    new com.helicalinsight.admin.dto.HIEfwdDTO(
				            r.resourceId,
				            r.resourceURL,
				            efwd.createdBy,
				            r.isDeleted,
				            r.title,
				            r.resourcePath
				        )
					    )
					FROM EFWDConnSqlJDBC c
					JOIN c.hiEfwdConnection conn
				    JOIN conn.hiResourceEFWD efwd
				    JOIN efwd.parentResource r
					WHERE c.efwdConnId =:connectionId
					""";
			SelectionQuery<PlainConnDTO> query = session.createSelectionQuery(sqlQuery,PlainConnDTO.class);
			query.setParameter("connectionId", connectionId);
			PlainConnDTO plainConnection =  query.uniqueResult();
			return plainConnection;
		} catch (Exception e) {
			log.error("Error occurred while fetching plain connection  : {} due to : ", connectionId, e);
		}
		return null;
	}
	
	@Override
	public PlainConnDTO findGroovyConnection(Integer connectionId) {
		try {
			Session session = getSession();
			String groovyQuery = """
					SELECT new com.helicalinsight.admin.dto.PlainConnDTO(
					    c.id,
					    c.database,
					    c.driver,
					    c.efwdConnId,
					    c.name,
					    c.pass,
					    c.url,
					    c.userName,
					    new com.helicalinsight.admin.dto.HIEfwdDTO(
				            r.resourceId,
				            r.resourceURL,
				            efwd.createdBy,
				            r.isDeleted,
				            r.title,
				            r.resourcePath
				        ),
					    c.condition
					    )
					FROM EFWDConnGroovy c
					JOIN c.hiEfwdConnection conn
					JOIN conn.hiResourceEFWD efwd
				    JOIN efwd.parentResource r
					WHERE c.efwdConnId = :connectionId
					""";
			SelectionQuery<PlainConnDTO> query = session.createSelectionQuery(groovyQuery,PlainConnDTO.class);
			query.setParameter("connectionId", connectionId);
			return  query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occurred while fetching groovy connection  : {} due to : ", connectionId, e);
		}
		return null;
	}

	@Override
	public void restoreConnection(Integer connectionId) {
		try {
			String hql = """
					update HIEfwdConnection
					set deleted = false
					where id = :id
					  and deleted = true
					""";
			Session session = getSession();
			MutationQuery mutationQuery = session.createMutationQuery(hql);
			mutationQuery.setParameter("id", connectionId);
			int count = mutationQuery.executeUpdate();
			log.debug("No of updated connection(s) : {}", count);
			
		}
		catch (Exception e) {
			log.error("Error occurred while restoring EFWD connection : {}" , connectionId , e);
		}
	}


}
